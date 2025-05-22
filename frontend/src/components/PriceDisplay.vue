<!-- frontend/src/components/PriceDisplay.vue -->
<template>
  <div class="price-display">
    <p>Harga: {{ priceInPi }} Pi</p>
    <p>Nilai Konsensus (Mining): ${{ priceInInternalUSD.toLocaleString() }} USD</p>
    <p>Nilai Bursa (OKX/Bitget/Pionex/MEXC/Gate.io): ${{ priceInExternalUSD.toLocaleString() }} USD</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    priceInPi: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      internalRate: 0,
      externalRate: 0,
      priceInInternalUSD: 0,
      priceInExternalUSD: 0,
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
        this.priceInInternalUSD = this.priceInPi * this.internalRate;
        this.priceInExternalUSD = this.priceInPi * this.externalRate;
      } catch (error) {
        console.error('Gagal mengambil nilai:', error);
        this.internalRate = 314159.0;
        this.externalRate = 0.8111; // Fallback ke harga OKX
        this.priceInInternalUSD = this.priceInPi * this.internalRate;
        this.priceInExternalUSD = this.priceInPi * this.externalRate;
      }
    },
  },
};
</script>

<style scoped>
.price-display {
  padding: 10px;
  border: 1px solid #ccc;
  margin: 10px;
  text-align: center;
}
</style>
