<!-- frontend/src/components/VolatilityAlert.vue -->
<template>
  <div class="volatility-alert" v-if="showAlert">
    <p>Peringatan: Harga Pi di bursa berubah {{ changePercentage.toFixed(2) }}%! Sekarang ${{ currentRate.toFixed(4) }}/Pi.</p>
    <button @click="dismissAlert">Tutup</button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      showAlert: false,
      currentRate: 0,
      previousRate: 0,
      changePercentage: 0,
    };
  },
  async mounted() {
    await this.fetchRate();
    setInterval(this.checkVolatility, 3600000); // Periksa setiap jam
  },
  methods: {
    async fetchRate() {
      try {
        const response = await axios.get('/api/rate/pi?type=external');
        this.previousRate = this.currentRate;
        this.currentRate = response.data.rate;
        this.checkVolatility();
      } catch (error) {
        console.error('Gagal mengambil nilai:', error);
      }
    },
    checkVolatility() {
      if (this.previousRate === 0) return;
      this.changePercentage = ((this.currentRate - this.previousRate) / this.previousRate) * 100;
      if (Math.abs(this.changePercentage) > 5) {
        this.showAlert = true;
      }
    },
    dismissAlert() {
      this.showAlert = false;
    },
  },
};
</script>

<style scoped>
.volatility-alert {
  position: fixed;
  top: 10px;
  right: 10px;
  background: #ff4444;
  color: white;
  padding: 10px;
  border-radius: 5px;
}
button {
  margin-left: 10px;
  background: white;
  color: #ff4444;
  border: none;
  cursor: pointer;
}
</style>
