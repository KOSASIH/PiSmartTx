<!-- frontend/src/components/AutonomousPiTransaction.vue -->
<template>
  <div class="autonomous-pi-transaction">
    <h2>Transaksi Otonom Pi Store</h2>
    
    <!-- Status dan Rates -->
    <div class="status">
      <p>Internal Rate: ${{ internalRate.toLocaleString('en-US', { minimumFractionDigits: 2 }) }} USD/Pi</p>
      <p>External Rate: ${{ externalRate.toFixed(4) }} USD/Pi</p>
      <p v-if="piPurityBadge" class="purity-badge">🌟 Pi Murni Terverifikasi</p>
      <p v-if="volatilityAlert.show" class="volatility-alert" role="alert">
        {{ volatilityAlert.message }}
        <button @click="dismissVolatilityAlert" aria-label="Tutup peringatan">Tutup</button>
      </p>
      <p v-if="errorMessage" class="error-message" role="alert">
        {{ errorMessage }}
        <button @click="clearError" aria-label="Tutup pesan error">✕</button>
      </p>
    </div>

    <!-- Form Transaksi -->
    <form @submit.prevent="submitTransaction" :aria-busy="isLoading">
      <div v-if="isLoading" class="loading-overlay">
        <div class="spinner" aria-label="Memproses..."></div>
      </div>
      <label>
        Jumlah Pi:
        <input
          v-model.number="amountInPi"
          type="number"
          step="0.0001"
          min="0.0001"
          required
          :disabled="isLoading || !isConnected"
          aria-label="Jumlah Pi"
          @input="validateAmount"
        />
      </label>
      <label>
        Mode Transaksi:
        <select v-model="transactionMode" :disabled="isLoading || !isConnected" aria-label="Mode transaksi">
          <option value="auto">Otonom (Pilih Otomatis)</option>
          <option value="internal">Internal (Konsensus)</option>
          <option value="external">Eksternal (Bursa)</option>
        </select>
      </label>
      <label v-if="isMerchant">
        <input
          type="checkbox"
          v-model="autoConvert"
          :disabled="isLoading || !isConnected"
          aria-label="Konversi otomatis ke fiat"
        />
        Konversi otomatis ke nilai bursa untuk fiat
      </label>
      <p>
        Nilai: ${{ amountInUSD.toLocaleString('en-US', { minimumFractionDigits: 2 }) }} USD
        ({{ effectiveRateType }})
      </p>
      <button
        type="submit"
        :disabled="isLoading || !isFormValid || !isConnected"
        aria-label="Proses transaksi"
      >
        {{ isLoading ? 'Memproses...' : 'Proses Transaksi' }}
      </button>
    </form>

    <!-- Hasil Transaksi -->
    <div v-if="transaction" class="transaction-details" role="group">
      <p>ID Transaksi: {{ transaction.id }}</p>
      <p>Jumlah: {{ transaction.amount }} Pi (${{ transaction.usdAmount.toLocaleString('en-US', { minimumFractionDigits: 2 }) }})</p>
      <p>Mode: {{ transaction.mode }}</p>
      <p>Status: {{ transaction.status }}</p>
    </div>
  </div>
</template>

<script>
import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import Web3 from 'web3';

Vue.use(Vuex);

// Vuex Store terintegrasi dalam komponen
const store = new Vuex.Store({
  state: {
    internalRate: 314159.0, // Nilai konsensus
    externalRate: 0.8111, // Nilai bursa (default OKX, Mei 2025)
    userId: localStorage.getItem('userId') || '',
    merchantId: localStorage.getItem('merchantId') || '',
    jwt: localStorage.getItem('jwt') || '',
    volatilityAlert: {
      show: false,
      changePercentage: 0,
      message: '',
    },
    autoConvert: false,
    errorMessage: '',
    piPurityBadge: false,
    isConnected: false, // Status koneksi ke Pi Network
  },
  mutations: {
    setRates(state, { internalRate, externalRate }) {
      state.internalRate = internalRate;
      state.externalRate = externalRate;
    },
    setExternalRate(state, rate) {
      state.externalRate = rate;
    },
    setUserId(state, userId) {
      state.userId = userId;
      localStorage.setItem('userId', userId);
    },
    setMerchantId(state, merchantId) {
      state.merchantId = merchantId;
      localStorage.setItem('merchantId', merchantId);
    },
    setJwt(state, jwt) {
      state.jwt = jwt;
      localStorage.setItem('jwt', jwt);
    },
    setVolatilityAlert(state, payload) {
      state.volatilityAlert = payload;
    },
    setAutoConvert(state, value) {
      state.autoConvert = value;
    },
    setErrorMessage(state, message) {
      state.errorMessage = message;
    },
    setPiPurityBadge(state, status) {
      state.piPurityBadge = status;
    },
    setIsConnected(state, isConnected) {
      state.isConnected = isConnected;
    },
  },
  actions: {
    async fetchRatesAction({ commit, state }) {
      try {
        const [internalResponse, externalResponse] = await Promise.all([
          axios.get('/api/rate/pi?type=internal', {
            headers: { Authorization: `Bearer ${state.jwt}` },
          }),
          axios.get('/api/rate/pi?type=external', {
            headers: { Authorization: `Bearer ${state.jwt}` },
          }),
        ]);
        commit('setRates', {
          internalRate: internalResponse.data.rate,
          externalRate: externalResponse.data.rate,
        });
      } catch (error) {
        commit('setErrorMessage', 'Gagal mengambil nilai Pi. Menggunakan default.');
        commit('setRates', { internalRate: 314159.0, externalRate: 0.8111 });
        console.error('Fetch rates error:', error);
      }
    },
    async checkVolatility({ commit, state }) {
      try {
        const response = await axios.get('/api/rate/pi?type=external', {
          headers: { Authorization: `Bearer ${state.jwt}` },
        });
        const newRate = response.data.rate;
        if (state.externalRate && Math.abs((newRate - state.externalRate) / state.externalRate) * 100 > 5) {
          const changePercentage = ((newRate - state.externalRate) / state.externalRate) * 100;
          commit('setVolatilityAlert', {
            show: true,
            changePercentage,
            message: `Harga Pi berubah ${changePercentage.toFixed(2)}%! Sekarang $${newRate.toFixed(4)}/Pi.`,
          });
        }
        commit('setExternalRate', newRate);
      } catch (error) {
        console.error('Check volatility error:', error);
      }
    },
    async authenticate({ commit }, { email, password, isMerchant }) {
      try {
        const endpoint = isMerchant ? '/api/auth/merchant' : '/api/auth/user';
        const response = await axios.post(endpoint, { email, password });
        const { userId, merchantId, token } = response.data;
        if (userId) commit('setUserId', userId);
        if (merchantId) commit('setMerchantId', merchantId);
        commit('setJwt', token);
        return response.data;
      } catch (error) {
        const message = error.response?.data?.message || 'Gagal autentikasi';
        commit('setErrorMessage', message);
        throw error;
      }
    },
    async initWebSocket({ commit, state }) {
      const wsUrl = process.env.VUE_APP_WS_URL || 'wss://api.mainnet.minepi.com/ws';
      let reconnectAttempts = 0;
      const maxReconnectAttempts = 5;

      const connect = () => {
        const socket = new WebSocket(wsUrl);
        socket.onopen = () => {
          socket.send(JSON.stringify({ type: 'auth', token: state.jwt }));
          commit('setIsConnected', true);
          reconnectAttempts = 0;
          console.debug('WebSocket terhubung ke Pi Network');
        };
        socket.onmessage = (event) => {
          const data = JSON.parse(event.data);
          if (data.type === 'priceUpdate') {
            commit('setExternalRate', data.rate);
            if (Math.abs((data.rate - state.externalRate) / state.externalRate) * 100 > 5) {
              commit('setVolatilityAlert', {
                show: true,
                changePercentage: ((data.rate - state.externalRate) / state.externalRate) * 100,
                message: `Harga Pi berubah! Sekarang $${data.rate.toFixed(4)}/Pi.`,
              });
            }
          } else if (data.type === 'transaction') {
            commit('setErrorMessage', `Transaksi ${data.txHash} dikonfirmasi`);
          }
        };
        socket.onclose = () => {
          commit('setIsConnected', false);
          if (reconnectAttempts < maxReconnectAttempts) {
            setTimeout(() => {
              reconnectAttempts++;
              console.debug(`Menyambung ulang WebSocket, percobaan ${reconnectAttempts}`);
              connect();
            }, 5000);
          } else {
            commit('setErrorMessage', 'Gagal menghubungkan ke Pi Network');
          }
        };
        socket.onerror = (error) => {
          console.error('WebSocket error:', error);
        };
      };

      connect();
    },
  },
});

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
      amountInPi: 0,
      transactionMode: 'auto',
      autoConvert: false,
      isLoading: false,
      transaction: null,
      isFormValid: false,
      validationErrors: {
        amountInPi: '',
      },
      web3: null,
      piContract: null,
    };
  },
  computed: {
    ...Vuex.mapState([
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
      const rate = this.effectiveRateType === 'Internal' ? this.internalRate : this.externalRate;
      return this.amountInPi * rate;
    },
    effectiveRateType() {
      if (this.transactionMode === 'auto') {
        // Logika otonom: Pilih internal jika volatilitas tinggi (>5%) atau Pi murni
        return this.volatilityAlert.changePercentage > 5 || this.piPurityBadge
          ? 'Internal'
          : 'Eksternal';
      }
      return this.transactionMode === 'internal' ? 'Internal' : 'Eksternal';
    },
  },
  async created() {
    await this.initWeb3();
    await this.$store.dispatch('fetchRatesAction');
    await this.$store.dispatch('initWebSocket');
    await this.validatePiPurity();
    setInterval(() => this.$store.dispatch('checkVolatility'), 3600000); // Cek volatilitas setiap jam
  },
  methods: {
    ...Vuex.mapActions(['fetchRatesAction']),
    async initWeb3() {
      try {
        // Inisialisasi Web3 dengan Pi Network node
        if (window.ethereum) {
          this.web3 = new Web3(window.ethereum);
          await window.ethereum.request({ method: 'eth_requestAccounts' });
        } else {
          this.web3 = new Web3('https://api.mainnet.minepi.com');
        }

        // Inisialisasi smart contract (contoh ABI, sesuaikan dengan kontrak sebenarnya)
        const contractAddress = '0xYourPiStoreContractAddress'; // Ganti dengan alamat kontrak
        const contractABI = [
          {
            inputs: [{ internalType: 'address', name: 'user', type: 'address' }],
            name: 'validatePiPurity',
            outputs: [{ internalType: 'bool', name: '', type: 'bool' }],
            stateMutability: 'view',
            type: 'function',
          },
          {
            inputs: [
              { internalType: 'address', name: 'user', type: 'address' },
              { internalType: 'uint256', name: 'amount', type: 'uint256' },
              { internalType: 'string', name: 'paymentType', type: 'string' },
            ],
            name: 'submitTransaction',
            outputs: [],
            stateMutability: 'nonpayable',
            type: 'function',
          },
        ];
        this.piContract = new this.web3.eth.Contract(contractABI, contractAddress);
        this.$store.commit('setIsConnected', true);
      } catch (error) {
        this.$store.commit('setErrorMessage', 'Gagal menginisialisasi Web3');
        console.error('Web3 init error:', error);
      }
    },
    async validatePiPurity() {
      try {
        const accounts = await this.web3.eth.getAccounts();
        if (accounts.length === 0) {
          this.$store.commit('setErrorMessage', 'Tidak ada akun terhubung');
          return;
        }
        const isPure = await this.piContract.methods
          .validatePiPurity(accounts[0])
          .call();
        this.$store.commit('setPiPurityBadge', isPure);
      } catch (error) {
        this.$store.commit('setErrorMessage', 'Gagal memvalidasi Pi Purity');
        console.error('Pi Purity error:', error);
      }
    },
    validateAmount() {
      this.validationErrors.amountInPi =
        this.amountInPi >= 0.0001 ? '' : 'Jumlah Pi minimal 0.0001';
      this.isFormValid = this.amountInPi >= 0.0001 && !this.validationErrors.amountInPi;
    },
    async submitTransaction() {
      if (!this.isFormValid) {
        this.$store.commit('setErrorMessage', 'Harap masukkan jumlah Pi yang valid');
        return;
      }
      this.isLoading = true;
      this.$store.commit('setErrorMessage', '');

      try {
        const accounts = await this.web3.eth.getAccounts();
        if (accounts.length === 0) {
          throw new Error('Tidak ada akun terhubung');
        }

        // Logika otonom untuk memilih mode transaksi
        const paymentType = this.transactionMode === 'auto' ? this.effectiveRateType.toLowerCase() : this.transactionMode;

        // Kirim transaksi ke smart contract Pi Network
        const amountInWei = this.web3.utils.toWei(this.amountInPi.toString(), 'ether'); // Sesuaikan unit
        await this.piContract.methods
          .submitTransaction(accounts[0], amountInWei, paymentType)
          .send({ from: accounts[0] });

        // Kirim ke backend untuk logging dan konversi (jika autoConvert)
        const response = await axios.post(
          '/api/transactions/submit',
          {
            userId: this.userId,
            merchantId: this.isMerchant ? this.merchantId : null,
            amountInPi: this.amountInPi,
            paymentType,
            autoConvert: this.autoConvert,
          },
          { headers: { Authorization: `Bearer ${this.jwt}` } }
        );

        this.transaction = {
          id: response.data.txHash,
          amount: this.amountInPi,
          usdAmount: this.amountInUSD,
          mode: paymentType,
          status: 'confirmed',
        };
      } catch (error) {
        const message = error.response?.data?.message || 'Gagal memproses transaksi';
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
    amountInPi() {
      this.validateAmount();
    },
    transactionMode() {
      this.validateAmount();
    },
    autoConvert(value) {
      this.$store.commit('setAutoConvert', value);
    },
  },
};
</script>

<style scoped>
.autonomous-pi-transaction {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
  background: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  color: #333;
}

.status {
  margin-bottom: 20px;
}

.purity-badge {
  display: inline-block;
  background: #28a745;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.volatility-alert {
  background: #ff4444;
  color: white;
  padding: 10px;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.volatility-alert button {
  background: white;
  color: #ff4444;
  border: none;
  padding: 5px 10px;
  border-radius: 3px;
  cursor: pointer;
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
}

label {
  display: block;
  margin: 10px 0;
}

input,
select {
  width: 100%;
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
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.transaction-details {
  margin-top: 20px;
  padding: 15px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
}

@media (max-width: 600px) {
  .autonomous-pi-transaction {
    padding: 15px;
  }
  input,
  select,
  button {
    font-size: 14px;
  }
}
</style>
