<!-- frontend/src/components/PriceDisplay.vue -->
<template>
  <div class="price-display">
    <p>Price: {{ priceInPi }} Pi</p>
    <p>Equivalent: ${{ priceInUSD.toLocaleString() }} USD</p>
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
      piRate: 0,
      priceInUSD: 0,
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
        this.priceInUSD = this.priceInPi * this.piRate;
      } catch (error) {
        console.error('Error fetching Pi rate:', error);
        this.piRate = 314159.0; // Fallback
        this.priceInUSD = this.priceInPi * this.piRate;
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
