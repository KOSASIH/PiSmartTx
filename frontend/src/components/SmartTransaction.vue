<!-- frontend/src/components/SmartTransaction.vue -->
<template>
  <div class="smart-transaction">
    <h2>{{ isMerchant ? 'Proses Transaksi' : 'Mulai Transaksi' }}</h2>
    <form @submit.prevent="createTransaction">
      <input v-model="merchantId" placeholder="ID Merchant" required v-if="!isMerchant" />
      <input v-model="userId" placeholder="ID Pelanggan" required v-if="isMerchant" />
      <input v-model.number="amountInPi" type="number" placeholder="Jumlah Pi" step="0.0001" required />
      <select v-model="source">
        <option value="mining">Pi dari Penambangan</option>
        <option value="exchange">Pi dari Bursa</option>
      </select>
      <p>Nilai: ${{ amountInUSD.toLocaleString() }} USD ({{ source === 'mining' ? 'Konsensus' : 'Bursa' }})</p>
      <button type="submit">Buat Transaksi</button>
    </form>
    <div v-if="transaction">
      <p>ID Transaksi: {{ transaction.id }}</p>
      <p>Jumlah: {{ transaction.amount }} Pi (${{ transaction.usdAmount.toLocaleString() }})</p>
      <p>Sumber: {{ transaction.source === 'mining' ? 'Penambangan' : 'Bursa' }}</p>
      <p>Status: {{ transaction.status }}</p>
      <button v-if="transaction.status === 'pending'" @click="executeTransaction">
        {{ isMerchant ? 'Konfirmasi Penerimaan' : 'Konfirmasi Pembayaran' }}
      </button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
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
      internalRate: 0,
      externalRate: 0,
      amountInUSD: 0,
      transaction: null,
    };
  },
  async mounted() {
    await this.fetchRates();
  },
  methods: {
    async fetchRates() {
      try {
        const [internalResponse, externalResponse] = await Promise.all([
          axios.get('/api/rate/pi?type=internal'),
          axios.get('/api/rate/pi?type=external'),
        ]);
        this.internalRate = internalResponse.data.rate;
        this.externalRate = externalResponse.data.rate;
        this.updateUSDAmount();
      } catch (error) {
        console.error('Gagal mengambil nilai:', error);
        this.internalRate = 314159.0;
        this.externalRate = 0.8111;
        this.updateUSDAmount();
      }
    },
    updateUSDAmount() {
      this.amountInUSD = this.amountInPi * (this.source === 'mining' ? this.internalRate : this.externalRate);
    },
    async createTransaction() {
      try {
        const payload = this.isMerchant
          ? { userId: this.userId, merchantId: this.$store.state.merchantId, amountInPi: this.amountInPi, source: this.source }
          : { userId: this.$store.state.userId, merchantId: this.merchantId, amountInPi: this.amountInPi, source: this.source };
        const response = await axios.post('/api/transactions/create-transaction', payload);
        this.transaction = response.data;
      } catch (error) {
        console.error('Gagal membuat transaksi:', error);
      }
    },
    async executeTransaction() {
      try {
        const response = await axios.post('/api/transactions/execute-transaction', this.transaction.id);
        this.transaction = response.data;
      } catch (error) {
        console.error('Gagal mengeksekusi transaksi:', error);
      }
    },
  },
  watch: {
    amountInPi() {
      this.updateUSDAmount();
    },
    source() {
      this.updateUSDAmount();
    },
  },
};
</script>

<style scoped>
.smart-transaction {
  padding: 20px;
}
input, select {
  display: block;
  margin: 10px 0;
  padding: 8px;
}
button {
  padding: 10px;
  background: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}
</style>
