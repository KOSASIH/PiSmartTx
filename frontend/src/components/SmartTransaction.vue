<!-- frontend/src/components/SmartTransaction.vue -->
<template>
  <div class="smart-transaction">
    <h2>Initiate Transaction</h2>
    <form @submit.prevent="createTransaction">
      <input v-model="merchantId" placeholder="Merchant ID" required />
      <input v-model.number="amount" type="number" placeholder="Amount in Pi" required />
      <button type="submit">Create Transaction</button>
    </form>
    <div v-if="transaction">
      <p>Transaction ID: {{ transaction.id }}</p>
      <p>Status: {{ transaction.status }}</p>
      <button v-if="transaction.status === 'pending'" @click="executeTransaction">Confirm Payment</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      merchantId: '',
      amount: 0,
      transaction: null,
    };
  },
  methods: {
    async createTransaction() {
      try {
        const response = await axios.post('/api/smartcontract/create-transaction', {
          userId: this.$store.state.userId,
          merchantId: this.merchantId,
          amount: this.amount,
        });
        this.transaction = response.data;
      } catch (error) {
        console.error('Error creating transaction:', error);
      }
    },
    async executeTransaction() {
      try {
        const response = await axios.post('/api/smartcontract/execute-transaction', this.transaction.id);
        this.transaction = response.data;
      } catch (error) {
        console.error('Error executing transaction:', error);
      }
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
