<!-- frontend/src/components/SmartTransaction.vue -->
<template>
  <div class="smart-transaction">
    <h2>{{ isMerchant ? 'Process Transaction' : 'Initiate Transaction' }}</h2>
    <form @submit.prevent="createTransaction">
      <input v-model="merchantId" placeholder="Merchant ID" required v-if="!isMerchant" />
      <input v-model="userId" placeholder="Customer ID" required v-if="isMerchant" />
      <input v-model.number="amountInPi" type="number" placeholder="Amount in Pi" step="0.0001" required />
      <p>Equivalent: ${{ amountInUSD.toLocaleString() }} USD</p>
      <button type="submit">Create Transaction</button>
    </form>
    <div v-if="transaction">
      <p>Transaction ID: {{ transaction.id }}</p>
      <p>Amount: {{ transaction.amount }} Pi (${{ transaction.usdAmount.toLocaleString() }})</p>
      <p>Status: {{ transaction.status }}</p>
      <button v-if="transaction.status === 'pending'" @click="executeTransaction">
        {{ isMerchant ? 'Confirm Receipt' : 'Confirm Payment' }}
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
      piRate: 0,
      amountInUSD: 0,
      transaction: null,
    };
  },
  async mounted() {
    await this.fetchPiRate();
  },
  methods: {
    async fetchPiRate() {
      try {
        const response = await axios.get('/api/rate/pi');
        this.piRate = response.data.rate;
        this.updateUSDAmount();
      } catch (error) {
        console.error('Error fetching Pi rate:', error);
        this.piRate = 314159.0; // Fallback
        this.updateUSDAmount();
      }
    },
    updateUSDAmount() {
      this.amountInUSD = this.amountInPi * this.piRate;
    },
    async createTransaction() {
      try {
        const payload = this.isMerchant
          ? { userId: this.userId, merchantId: this.$store.state.merchantId, amountInPi: this.amountInPi }
          : { userId: this.$store.state.userId, merchantId: this.merchantId, amountInPi: this.amountInPi };
        const response = await axios.post('/api/transactions/create-transaction', payload);
        this.transaction = response.data;
      } catch (error) {
        console.error('Error creating transaction:', error);
      }
    },
    async executeTransaction() {
      try {
        const response = await axios.post('/api/transactions/execute-transaction', this.transaction.id);
        this.transaction = response.data;
      } catch (error) {
        console.error('Error executing transaction:', error);
      }
    },
  },
  watch: {
    amountInPi() {
      this.updateUSDAmount();
    },
  },
};
</script>

<style scoped>
.smart-transaction {
  padding: 20px;
}
input {
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
