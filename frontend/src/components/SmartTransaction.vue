<!-- frontend/src/components/SmartTransaction.vue -->
<template>
  <div class="smart-transaction" role="region" aria-labelledby="transaction-title">
    <h2 id="transaction-title">{{ isMerchant ? 'Proses Transaksi' : 'Mulai Transaksi' }}</h2>
    
    <!-- Pesan Error -->
    <div v-if="errorMessage" class="error-message" role="alert">
      {{ errorMessage }}
      <button @click="clearError" aria-label="Tutup pesan error">✕</button>
    </div>

    <!-- Form Transaksi -->
    <form @submit.prevent="createTransaction" :aria-busy="isLoading">
      <div v-if="isLoading" class="loading-overlay">
        <div class="spinner" aria-label="Memproses..."></div>
      </div>
      <input
        v-model="merchantId"
        placeholder="ID Merchant"
        required
        v-if="!isMerchant"
        :disabled="isLoading"
        aria-label="ID Merchant"
        @input="validateMerchantId"
      />
      <input
        v-model="userId"
        placeholder="ID Pelanggan"
        required
        v-if="isMerchant"
        :disabled="isLoading"
        aria-label="ID Pelanggan"
        @input="validateUserId"
      />
      <input
        v-model.number="amountInPi"
        type="number"
        placeholder="Jumlah Pi"
        step="0.0001"
        min="0.0001"
        required
        :disabled="isLoading"
        aria-label="Jumlah Pi"
        @input="validateAmount"
      />
      <select v-model="source" :disabled="isLoading" aria-label="Sumber Pi">
        <option value="mining">Pi dari Penambangan</option>
        <option value="exchange">Pi dari Bursa</option>
      </select>
      <p>
        Nilai: ${{ amountInUSD.toLocaleString('en-US', { minimumFractionDigits: 2 }) }} USD
        ({{ source === 'mining' ? 'Konsensus' : 'Bursa' }})
      </p>
      <div v-if="isMerchant && source === 'mining'" class="conversion-option">
        <label>
          <input type="checkbox" v-model="autoConvert" :disabled="isLoading" />
          Konversi otomatis ke nilai bursa untuk fiat
        </label>
      </div>
      <button type="submit" :disabled="isLoading || !isFormValid" aria-label="Buat transaksi">
        {{ isLoading ? 'Memproses...' : 'Buat Transaksi' }}
      </button>
    </form>

    <!-- Detail Transaksi -->
    <div v-if="transaction" class="transaction-details" role="group">
      <p>ID Transaksi: {{ transaction.id }}</p>
      <p>
        Jumlah: {{ transaction.amount }} Pi
        (${{ transaction.usdAmount.toLocaleString('en-US', { minimumFractionDigits: 2 }) }})
      </p>
      <p>
        Sumber: {{ transaction.source === 'mining' ? 'Penambangan' : 'Bursa' }}
        <span v-if="transaction.source === 'mining'" class="purity-badge" aria-label="Pi Murni">
          🌟 Pi Murni
        </span>
      </p>
      <p>Status: {{ transaction.status }}</p>
      <button
        v-if="transaction.status === 'pending'"
        @click="executeTransaction"
        :disabled="isLoading"
        aria-label="Konfirmasi transaksi"
      >
        {{ isMerchant ? 'Konfirmasi Penerimaan' : 'Konfirmasi Pembayaran' }}
      </button>
    </div>

    <!-- Volatility Alert -->
    <div v-if="showVolatilityAlert" class="volatility-alert" role="alert">
      <p>
        Peringatan: Harga Pi di bursa berubah {{ volatilityChange.toFixed(2) }}%! Sekarang
        ${{ externalRate.toFixed(4) }}/Pi.
      </p>
      <button @click="dismissVolatilityAlert" aria-label="Tutup peringatan">Tutup</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { mapState, mapActions } from 'vuex';

export default {
  name: 'SmartTransaction',
  props: {
    isMerchant: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      merchantId: '',
      userId: '',
      amountInPi: 0,
      source: 'mining',
      amountInUSD: 0,
      transaction: null,
      isLoading: false,
      errorMessage: '',
      autoConvert: false,
      showVolatilityAlert: false,
      volatilityChange: 0,
      isFormValid: false,
      validationErrors: {
        merchantId: '',
        userId: '',
        amountInPi: '',
      },
    };
  },
  computed: {
    ...mapState(['internalRate', 'externalRate', 'userId', 'merchantId']),
  },
  async mounted() {
    await this.fetchRates();
    this.checkVolatility();
    setInterval(this.checkVolatility, 3600000); // Cek volatilitas setiap jam
  },
  methods: {
    ...mapActions(['fetchRatesAction']),
    async fetchRates() {
      this.isLoading = true;
      try {
        await this.fetchRatesAction();
        this.updateUSDAmount();
      } catch (error) {
        this.errorMessage = 'Gagal mengambil nilai Pi. Menggunakan nilai default.';
        this.$store.commit('setRates', { internalRate: 314159.0, externalRate: 0.8111 });
        this.updateUSDAmount();
        console.error('Gagal mengambil nilai:', error);
      } finally {
        this.isLoading = false;
      }
    },
    updateUSDAmount() {
      this.amountInUSD = this.amountInPi * (this.source === 'mining' ? this.internalRate : this.externalRate);
      this.validateForm();
    },
    validateMerchantId() {
      this.validationErrors.merchantId = this.merchantId.length >= 3 ? '' : 'ID Merchant minimal 3 karakter';
      this.validateForm();
    },
    validateUserId() {
      this.validationErrors.userId = this.userId.length >= 3 ? '' : 'ID Pelanggan minimal 3 karakter';
      this.validateForm();
    },
    validateAmount() {
      this.validationErrors.amountInPi =
        this.amountInPi >= 0.0001 ? '' : 'Jumlah Pi minimal 0.0001';
      this.updateUSDAmount();
    },
    validateForm() {
      this.isFormValid =
        (!this.isMerchant || (this.userId && !this.validationErrors.userId)) &&
        (this.isMerchant || (this.merchantId && !this.validationErrors.merchantId)) &&
        this.amountInPi >= 0.0001 &&
        !this.validationErrors.amountInPi;
    },
    async createTransaction() {
      if (!this.isFormValid) {
        this.errorMessage = 'Harap lengkapi formulir dengan benar.';
        return;
      }
      this.isLoading = true;
      this.errorMessage = '';
      try {
        const payload = this.isMerchant
          ? {
              userId: this.userId,
              merchantId: this.$store.state.merchantId,
              amountInPi: this.amountInPi,
              source: this.source,
              autoConvert: this.autoConvert,
            }
          : {
              userId: this.$store.state.userId,
              merchantId: this.merchantId,
              amountInPi: this.amountInPi,
              source: this.source,
            };
        const response = await axios.post('/api/transactions/create-transaction', payload, {
          headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` },
        });
        this.transaction = response.data;
      } catch (error) {
        this.errorMessage =
          error.response?.data?.message || 'Gagal membuat transaksi. Coba lagi nanti.';
        console.error('Gagal membuat transaksi:', error);
      } finally {
        this.isLoading = false;
      }
    },
    async executeTransaction() {
      this.isLoading = true;
      this.errorMessage = '';
      try {
        const response = await axios.post(
          '/api/transactions/execute-transaction',
          { transactionId: this.transaction.id },
          { headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` } }
        );
        this.transaction = response.data;
      } catch (error) {
        this.errorMessage =
          error.response?.data?.message || 'Gagal mengeksekusi transaksi. Coba lagi nanti.';
        console.error('Gagal mengeksekusi transaksi:', error);
      } finally {
        this.isLoading = false;
      }
    },
    async checkVolatility() {
      try {
        const response = await axios.get('/api/rate/pi?type=external');
        const newRate = response.data.rate;
        if (this.externalRate && Math.abs((newRate - this.externalRate) / this.externalRate) * 100 > 5) {
          this.volatilityChange = ((newRate - this.externalRate) / this.externalRate) * 100;
          this.showVolatilityAlert = true;
        }
        this.$store.commit('setExternalRate', newRate);
      } catch (error) {
        console.error('Gagal memeriksa volatilitas:', error);
      }
    },
    dismissVolatilityAlert() {
      this.showVolatilityAlert = false;
    },
    clearError() {
      this.errorMessage = '';
    },
  },
  watch: {
    amountInPi() {
      this.validateAmount();
    },
    source() {
      this.updateUSDAmount();
    },
    merchantId() {
      this.validateMerchantId();
    },
    userId() {
      this.validateUserId();
    },
  },
};
</script>

<style scoped>
.smart-transaction {
  padding: 20px;
  max-width: 500px;
  margin: 0 auto;
  background: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  color: #333;
}

input,
select {
  display: block;
  width: 100%;
  margin: 10px 0;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 16px;
  box-sizing: border-box;
}

input:focus,
select:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.3);
}

button {
  width: 100%;
  padding: 12px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

button:disabled {
  background: #cccccc;
  cursor: not-allowed;
}

button:hover:not(:disabled) {
  background: #0056b3;
}

.error-message {
  background: #ff4444;
  color: white;
  padding: 10px;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.error-message button {
  background: none;
  color: white;
  font-weight: bold;
  padding: 0;
  width: auto;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.transaction-details {
  margin-top: 20px;
  padding: 15px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.purity-badge {
  background: #28a745;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  margin-left: 5px;
}

.volatility-alert {
  position: fixed;
  top: 10px;
  right: 10px;
  background: #ff4444;
  color: white;
  padding: 10px;
  border-radius: 5px;
  z-index: 1000;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.volatility-alert button {
  margin-left: 10px;
  background: white;
  color: #ff4444;
  border: none;
  padding: 5px 10px;
  border-radius: 3px;
  cursor: pointer;
  width: auto;
}

.conversion-option {
  margin: 10px 0;
}

@media (max-width: 600px) {
  .smart-transaction {
    padding: 15px;
  }
  input,
  select,
  button {
    font-size: 14px;
  }
}
</style>
