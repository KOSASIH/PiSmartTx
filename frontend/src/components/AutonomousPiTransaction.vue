<!-- frontend/src/components/AutonomousPiTransaction.vue -->
<template>
  <div class="autonomous-pi-transaction">
    <u-navbar :title="$t('transaction.title')" />
    <u-gap height="10" />

    <!-- Status dan Rates -->
    <u-card class="status-card">
      <template #header>
        <h3>{{ $t('transaction.statusTitle') }}</h3>
      </template>
      <p>{{ $t('transaction.internalRate') }}: ${{ internalRate.toLocaleString('en-US', { minimumFractionDigits: 2 }) }} USD/Pi</p>
      <p>{{ $t('transaction.externalRate') }}: ${{ externalRate.toFixed(4) }} USD/Pi</p>
      <u-tag v-if="piPurityBadge" type="success" class="purity-badge">
        {{ $t('transaction.purityBadge') }}
      </u-tag>
      <u-notice-bar v-if="volatilityAlert.show" type="error" :text="volatilityAlert.message">
        <template #right>
          <u-button size="mini" @click="dismissVolatilityAlert">{{ $t('transaction.close') }}</u-button>
        </template>
      </u-notice-bar>
      <u-notice-bar v-if="errorMessage" type="error" :text="errorMessage">
        <template #right>
          <u-button size="mini" @click="clearError">✕</u-button>
        </template>
      </u-notice-bar>
    </u-card>

    <!-- Form Transaksi -->
    <u-form @submit="submitTransaction" :model="form" :rules="rules" ref="transactionForm">
      <u-loading-overlay :show="isLoading" :text="$t('transaction.processing')" />
      <u-form-item :label="$t('transaction.amount')" prop="amountInPi">
        <u-input
          v-model.number="form.amountInPi"
          type="number"
          :step="0.0001"
          :min="0.0001"
          :disabled="isLoading || !isConnected"
          :placeholder="$t('transaction.amountPlaceholder')"
          @input="validateAmount"
        />
      </u-form-item>
      <u-form-item :label="$t('transaction.mode')" prop="transactionMode">
        <u-tooltip :content="$t('transaction.modeTooltip')">
          <u-select
            v-model="form.transactionMode"
            :disabled="isLoading || !isConnected"
            :options="modeOptions"
            :placeholder="$t('transaction.modePlaceholder')"
          />
        </u-tooltip>
      </u-form-item>
      <u-form-item v-if="isMerchant">
        <u-checkbox v-model="form.autoConvert" :disabled="isLoading || !isConnected">
          {{ $t('transaction.autoConvert') }}
        </u-checkbox>
      </u-form-item>
      <p class="amount-usd">
        {{ $t('transaction.value') }}: ${{ amountInUSD.toLocaleString('en-US', { minimumFractionDigits: 2 }) }} USD
        ({{ $t(`transaction.${effectiveRateType.toLowerCase()}`) }})
      </p>
      <u-button
        type="primary"
        :disabled="isLoading || !isFormValid || !isConnected"
        native-type="submit"
        block
      >
        {{ isLoading ? $t('transaction.processing') : $t('transaction.submit') }}
      </u-button>
    </u-form>

    <!-- Hasil Transaksi -->
    <u-collapse v-if="transaction" class="transaction-details">
      <u-collapse-item :title="$t('transaction.detailsTitle')">
        <p>{{ $t('transaction.txId') }}: {{ transaction.id }}</p>
        <p>
          {{ $t('transaction.amount') }}: {{ transaction.amount }} Pi
          (${{ transaction.usdAmount.toLocaleString('en-US', { minimumFractionDigits: 2 }) }})
        </p>
        <p>{{ $t('transaction.mode') }}: {{ $t(`transaction.${transaction.mode}`) }}</p>
        <p>{{ $t('transaction.status') }}: {{ $t(`transaction.${transaction.status}`) }}</p>
      </u-collapse-item>
    </u-collapse>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import Web3 from 'web3';
import PiStoreABI from '@/abis/PiStore.json'; // ABI dari PiStore.sol

export default {
  name: 'AutonomousPiTransaction',
  props: {
    isMerchant: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      form: {
        amountInPi: 0,
        transactionMode: 'auto',
        autoConvert: false,
      },
      isLoading: false,
      transaction: null,
      isFormValid: false,
      web3: null,
      piContract: null,
      modeOptions: [
        { label: this.$t('transaction.auto'), value: 'auto' },
        { label: this.$t('transaction.internal'), value: 'internal' },
        { label: this.$t('transaction.external'), value: 'external' },
      ],
      rules: {
        amountInPi: [
          { required: true, message: this.$t('transaction.amountRequired'), trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value < 0.0001) {
                callback(new Error(this.$t('transaction.amountMin')));
              } else {
                callback();
              }
            },
            trigger: 'blur',
          },
        ],
        transactionMode: [
          { required: true, message: this.$t('transaction.modeRequired'), trigger: 'change' },
        ],
      },
    };
  },
  computed: {
    ...mapState([
      'internalRate',
      'externalRate',
      'userId',
      'merchantId',
      'jwt',
      'volatilityAlert',
      'errorMessage',
      'piPurityBadge',
      'isConnected',
    ]),
    amountInUSD() {
      const rate = this.effectiveRateType === 'internal' ? this.internalRate : this.externalRate;
      return this.form.amountInPi * rate;
    },
    effectiveRateType() {
      if (this.form.transactionMode === 'auto') {
        // Logika otonom: Prioritaskan internal jika Pi murni atau volatilitas >5%
        return this.piPurityBadge || this.volatilityAlert.changePercentage > 5 ? 'internal' : 'external';
      }
      return this.form.transactionMode;
    },
  },
  async created() {
    await this.initWeb3();
    await this.fetchRatesAction();
    await this.initWebSocket();
    await this.validatePiPurity();
    setInterval(() => this.checkVolatility(), 3600000); // Cek volatilitas setiap jam
  },
  methods: {
    ...mapActions(['fetchRatesAction', 'initWebSocket', 'checkVolatility']),
    async initWeb3() {
      try {
        // Inisialisasi Web3 dengan Pi Network node atau wallet
        this.web3 = new Web3(process.env.VUE_APP_PI_NODE_URL || 'https://api.mainnet.minepi.com');
        if (window.ethereum) {
          this.web3.setProvider(window.ethereum);
          await window.ethereum.request({ method: 'eth_requestAccounts' });
        }

        // Inisialisasi kontrak PiStore.sol
        const contractAddress = process.env.VUE_APP_PI_CONTRACT_ADDRESS;
        if (!contractAddress) {
          throw new Error('Contract address not configured');
        }
        this.piContract = new this.web3.eth.Contract(PiStoreABI, contractAddress);
        this.$store.commit('setIsConnected', true);
      } catch (error) {
        this.$store.commit('setErrorMessage', this.$t('transaction.web3Error'));
        console.error('Web3 init error:', error);
      }
    },
    async validatePiPurity() {
      try {
        const accounts = await this.web3.eth.getAccounts();
        if (!accounts.length) {
          this.$store.commit('setErrorMessage', this.$t('transaction.noAccount'));
          return;
        }
        const isPure = await this.piContract.methods.validatePiPurity(accounts[0]).call();
        this.$store.commit('setPiPurityBadge', isPure);
      } catch (error) {
        this.$store.commit('setErrorMessage', this.$t('transaction.purityError'));
        console.error('Pi Purity error:', error);
      }
    },
    validateAmount() {
      this.$refs.transactionForm.validateField('amountInPi', (error) => {
        this.isFormValid = !error && this.form.amountInPi >= 0.0001;
      });
    },
    async submitTransaction() {
      try {
        await this.$refs.transactionForm.validate();
        this.isLoading = true;
        this.$store.commit('setErrorMessage', '');

        const accounts = await this.web3.eth.getAccounts();
        if (!accounts.length) {
          throw new Error(this.$t('transaction.noAccount'));
        }

        // Logika otonom
        const paymentType = this.form.transactionMode === 'auto' ? this.effectiveRateType : this.form.transactionMode;

        // Kirim transaksi ke PiStore.sol
        const amountInWei = this.web3.utils.toWei(this.form.amountInPi.toString(), 'ether');
        const merchantAddress = this.isMerchant ? this.merchantId : '0x0000000000000000000000000000000000000000';
        await this.piContract.methods
          .submitTransaction(accounts[0], merchantAddress, amountInWei, paymentType, this.form.autoConvert)
          .send({ from: accounts[0] });

        // Log transaksi ke backend
        const response = await this.$axios.post(
          '/api/transactions/submit',
          {
            userId: this.userId,
            merchantId: this.isMerchant ? this.merchantId : null,
            amountInPi: this.form.amountInPi,
            paymentType,
            autoConvert: this.form.autoConvert,
          },
          { headers: { Authorization: `Bearer ${this.jwt}` } }
        );

        this.transaction = {
          id: response.data.txHash,
          amount: this.form.amountInPi,
          usdAmount: this.amountInUSD,
          mode: paymentType,
          status: 'confirmed',
        };
        this.$u.message.success(this.$t('transaction.success'));
      } catch (error) {
        const message = error.response?.data?.message || this.$t('transaction.error');
        this.$store.commit('setErrorMessage', message);
        console.error('Transaction error:', error);
      } finally {
        this.isLoading = false;
      }
    },
    dismissVolatilityAlert() {
      this.$store.commit('setVolatilityAlert', { show: false, changePercentage: 0, message: '' });
    },
    clearError() {
      this.$store.commit('setErrorMessage', '');
    },
  },
  watch: {
    'form.amountInPi': 'validateAmount',
    'form.transactionMode'() {
      this.validateAmount();
    },
    'form.autoConvert'(value) {
      this.$store.commit('setAutoConvert', value);
    },
  },
};
</script>

<style scoped>
.autonomous-pi-transaction {
  padding: 0 16px;
  background: #f5f5f5;
}

.status-card {
  margin-bottom: 16px;
}

.purity-badge {
  margin-top: 8px;
}

.amount-usd {
  margin: 16px 0;
  font-size: 16px;
  color: #333;
}

.transaction-details {
  margin-top: 16px;
}
</style>
