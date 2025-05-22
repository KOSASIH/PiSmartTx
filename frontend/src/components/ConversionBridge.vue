<!-- frontend/src/components/ConversionBridge.vue -->
<template>
  <div class="conversion-bridge">
    <h3>Konversi Pi ke Fiat</h3>
    <input v-model.number="amountInPi" type="number" placeholder="Jumlah Pi" step="0.0001" />
    <select v-model="source">
      <option value="mining">Pi dari Penambangan</option>
      <option value="exchange">Pi dari Bursa</option>
    </select>
    <p>Nilai: ${{ amountInUSD.toLocaleString() }} USD</p>
    <button @click="convert">Konversi</button>
    <p v-if="result">Konversi Berhasil: {{ result.amountInPi }} Pi → ${{ result.amountInUSD.toLocaleString() }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      amountInPi: 0,
      source: 'mining',
      externalRate: 0,
      amountInUSD: 0,
      result: null,
    };
  },
  async mounted() {
    await this.fetchRate();
  },
  methods: {
    async fetchRate() {
      try {
        const response = await axios.get('/api/rate/pi?type=external');
        this.externalRate = response.data.rate;
        this.updateUSDAmount();
      } catch (error) {
        console.error('Gagal mengambil nilai:', error);
        this.externalRate = 0.8111;
        this.updateUSDAmount();
      }
    },
    updateUSDAmount() {
      this.amountInUSD = this.amountInPi * this.externalRate;
    },
    async convert() {
      try {
        const response = await axios.post('/api/conversions/convert-pi', {
          merchantId: this.$store.state.merchantId,
          amountInPi: this.amountInPi,
          source: this.source,
        });
        this.result = response.data;
      } catch (error) {
        console.error('Gagal mengkonversi:', error);
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
.conversion-bridge {
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
