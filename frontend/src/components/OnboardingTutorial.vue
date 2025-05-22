<!-- frontend/src/components/OnboardingTutorial.vue -->
<template>
  <div class="onboarding-modal" v-if="showModal">
    <div class="modal-content">
      <h2>Selamat Datang di Pi Store!</h2>
      <div v-if="step === 1">
        <p>Pahami Dual Value System:</p>
        <p><strong>Pi Hasil Penambangan</strong>: Bernilai $314,159/Pi untuk transaksi eksklusif di Pi Store!</p>
        <button @click="nextStep">Lanjut</button>
      </div>
      <div v-if="step === 2">
        <p><strong>Pi dari Bursa</strong>: Mengikuti harga pasar (misalnya, $0.8111) untuk transaksi praktis.</p>
        <button @click="nextStep">Lanjut</button>
      </div>
      <div v-if="step === 3">
        <p>Pilih sumber Pi saat bertransaksi dan lihat nilai USD-nya!</p>
        <button @click="completeTutorial">Selesai</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      showModal: !localStorage.getItem('tutorialCompleted'),
      step: 1,
    };
  },
  methods: {
    nextStep() {
      this.step++;
    },
    completeTutorial() {
      localStorage.setItem('tutorialCompleted', 'true');
      this.showModal = false;
    },
  },
};
</script>

<style scoped>
.onboarding-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  animation: slideIn 0.5s ease;
}
@keyframes slideIn {
  from { transform: translateY(-50px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
button {
  margin-top: 10px;
  padding: 10px;
  background: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}
</style>
