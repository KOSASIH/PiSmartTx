<!-- frontend/src/components/MerchantDashboard.vue -->
<template>
  <div class="merchant-dashboard">
    <h2>Dashboard Merchant</h2>
    <h3>Transaksi Terbaru</h3>
    <div v-for="transaction in transactions" :key="transaction.id" class="transaction">
      <p>ID Transaksi: {{ transaction.id }}</p>
      <p>Jumlah: {{ transaction.amount }} Pi (${{ transaction.usdAmount.toLocaleString() }})</p>
      <p>Sumber: {{ transaction.source === 'mining' ? 'Penambangan' : 'Bursa' }}</p>
      <p>Status: {{ transaction.status }}</p>
    </div>
    <h3>Total Pendapatan</h3>
    <p>Konsensus: {{ totalPi }} Pi (${{ totalInternalUSD.toLocaleString() }})</p>
    <p>Bursa (OKX/Bitget/Pionex/MEXC/Gate.io): {{ totalPi }} Pi (${{ totalExternalUSD.toLocaleString() }})</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      transactions: [],
      internalRate: 0,
      externalRate: 0,
      totalPi: 0,
      totalInternalUSD: 0,
      totalExternalUSD: 0,
    };
  },
  async mounted() {
    await this.fetchRates();
    await this.fetchTransactions();
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
      } catch (error) {
        console.error('Gagal mengambil nilai:', error);
        this.internalRate = 314159.0;
        this.externalRate = 0.8111;
      }
    },
    async fetchTransactions() {
      try {
        const response = await axios.get('/api/transactions/merchant', {
          params: { merchantId: this.$store.state.merchantId },
        });
        this.transactions = response.data;
        this.totalPi = this.transactions.reduce((sum, tx) => sum + tx.amount, 0);
        this.totalInternalUSD = this.totalPi * this.internalRate;
        this.totalExternalUSD = this.totalPi * this.externalRate;
      } catch (error) {
        console.error('Gagal mengambil transaksi:', error);
      }
    },
  },
};
</script>

<style scoped>
.merchant-dashboard {
  padding: 20px;
}
.transaction {
  border: 1px solid #ccc;
  padding: 10px;
  margin: 10px 0;
}
</style>
