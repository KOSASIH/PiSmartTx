<!-- frontend/src/components/MerchantDashboard.vue -->
<template>
  <div class="merchant-dashboard">
    <h2>Merchant Dashboard</h2>
    <h3>Recent Transactions</h3>
    <div v-for="transaction in transactions" :key="transaction.id" class="transaction">
      <p>Transaction ID: {{ transaction.id }}</p>
      <p>Amount: {{ transaction.amount }} Pi (${{ transaction.usdAmount.toLocaleString() }})</p>
      <p>Status: {{ transaction.status }}</p>
    </div>
    <h3>Total Earnings</h3>
    <p>{{ totalPi }} Pi (${{ totalUSD.toLocaleString() }})</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      transactions: [],
      piRate: 0,
      totalPi: 0,
      totalUSD: 0,
    };
  },
  async mounted() {
    await this.fetchPiRate();
    await this.fetchTransactions();
  },
  methods: {
    async fetchPiRate() {
      try {
        const response = await axios.get('/api/rate/pi');
        this.piRate = response.data.rate;
      } catch (error) {
        console.error('Error fetching Pi rate:', error);
        this.piRate = 314159.0; // Fallback
      }
    },
    async fetchTransactions() {
      try {
        const response = await axios.get('/api/transactions/merchant', {
          params: { merchantId: this.$store.state.merchantId },
        });
        this.transactions = response.data;
        this.totalPi = this.transactions.reduce((sum, tx) => sum + tx.amount, 0);
        this.totalUSD = this.totalPi * this.piRate;
      } catch (error) {
        console.error('Error fetching transactions:', error);
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
