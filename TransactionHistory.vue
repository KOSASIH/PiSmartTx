<template>
    <div>
        <h2>Transaction History</h2>
        <table>
            <tr v-for="transaction in transactions" :key="transaction.id">
                <td>{{ transaction.date }}</td>
                <td>{{ transaction.amount }}</td>
                <td>{{ transaction.status }}</td>
            </tr>
        </table>
    </div>
</template>

<script>
export default {
    data() {
        return {
            transactions: []
        };
    },
    mounted() {
        this.fetchTransactions();
    },
    methods: {
        fetchTransactions() {
            // Fetch transaction data from the backend
        }
    }
};
</script>
