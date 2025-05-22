<!-- frontend/src/components/RecommendedMerchants.vue -->
<template>
  <div class="recommended-merchants">
    <h2>Recommended Merchants</h2>
    <div v-for="merchant in merchants" :key="merchant.id" class="merchant-card">
      <h3>{{ merchant.name }}</h3>
      <p>Distance: {{ merchant.distance }} km</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      merchants: [],
    };
  },
  async mounted() {
    try {
      const position = await this.getUserLocation();
      const response = await axios.get('/api/recommendations/merchants', {
        params: {
          userId: this.$store.state.userId,
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        },
      });
      this.merchants = response.data;
    } catch (error) {
      console.error('Error fetching recommendations:', error);
    }
  },
  methods: {
    getUserLocation() {
      return new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(resolve, reject);
      });
    },
  },
};
</script>

<style scoped>
.recommended-merchants {
  padding: 20px;
}
.merchant-card {
  border: 1px solid #ccc;
  padding: 10px;
  margin: 10px 0;
}
</style>
