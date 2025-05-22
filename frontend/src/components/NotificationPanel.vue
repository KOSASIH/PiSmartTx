<!-- frontend/src/components/NotificationPanel.vue -->
<template>
  <div class="notification-panel">
    <h3>Notifications</h3>
    <ul>
      <li v-for="(notification, index) in notifications" :key="index">
        {{ notification }}
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  data() {
    return {
      notifications: [],
      socket: null,
    };
  },
  mounted() {
    this.connectWebSocket();
  },
  methods: {
    connectWebSocket() {
      this.socket = new WebSocket('ws://api.pi-store.app/notifications');
      this.socket.onmessage = (event) => {
        this.notifications.push(event.data);
      };
      this.socket.onopen = () => {
        console.log('WebSocket connected');
      };
      this.socket.onerror = (error) => {
        console.error('WebSocket error:', error);
      };
    },
  },
  beforeDestroy() {
    if (this.socket) {
      this.socket.close();
    }
  },
};
</script>

<style scoped>
.notification-panel {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: white;
  border: 1px solid #ccc;
  padding: 10px;
  max-width: 300px;
}
</style>
