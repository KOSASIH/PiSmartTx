<!-- frontend/src/components/ARMerchantDiscovery.vue -->
<template>
  <div class="ar-container">
    <a-scene embedded arjs="sourceType: webcam;">
      <a-marker v-for="merchant in merchants" :key="merchant.id" type="pattern" :url="`/patterns/${merchant.id}.patt`">
        <a-entity
          position="0 0 0"
          scale="0.1 0.1 0.1"
          text="value: {{ merchant.name }}\n{{ merchant.promo }}; align: center; color: white;"
        ></a-entity>
      </a-marker>
      <a-entity camera></a-entity>
    </a-scene>
    <button @click="startAR">Start AR</button>
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
  methods: {
    async startAR() {
      try {
        const position = await this.getUserLocation();
        const response = await axios.get('/api/ar/merchants', {
          params: {
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
            radius: 1000, // dalam meter
          },
        });
        this.merchants = response.data;
      } catch (error) {
        console.error('Error fetching AR merchants:', error);
      }
    },
    getUserLocation() {
      return new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(resolve, reject);
      });
    },
  },
};
</script>

<style scoped>
.ar-container {
  width: 100%;
  height: 100vh;
}
</style>
