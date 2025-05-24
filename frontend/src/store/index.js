// frontend/src/store/index.js
import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    internalRate: 314159.0,
    externalRate: 0.8111,
    userId: localStorage.getItem('userId') || '',
    merchantId: localStorage.getItem('merchantId') || '',
    jwt: localStorage.getItem('jwt') || '',
    volatilityAlert: { show: false, changePercentage: 0, message: '' },
    autoConvert: false,
    errorMessage: '',
    piPurityBadge: false,
    isConnected: false,
  },
  mutations: {
    setRates(state, { internalRate, externalRate }) {
      state.internalRate = internalRate;
      state.externalRate = externalRate;
    },
    setExternalRate(state, rate) {
      state.externalRate = rate;
    },
    setUserId(state, userId) {
      state.userId = userId;
      localStorage.setItem('userId', userId);
    },
    setMerchantId(state, merchantId) {
      state.merchantId = merchantId;
      localStorage.setItem('merchantId', merchantId);
    },
    setJwt(state, jwt) {
      state.jwt = jwt;
      localStorage.setItem('jwt', jwt);
    },
    setVolatilityAlert(state, payload) {
      state.volatilityAlert = payload;
    },
    setAutoConvert(state, value) {
      state.autoConvert = value;
    },
    setErrorMessage(state, message) {
      state.errorMessage = message;
    },
    setPiPurityBadge(state, status) {
      state.piPurityBadge = status;
    },
    setIsConnected(state, isConnected) {
      state.isConnected = isConnected;
    },
  },
  actions: {
    async fetchRatesAction({ commit, state }) {
      try {
        const [internalResponse, externalResponse] = await Promise.all([
          axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/rate/pi?type=internal`, {
            headers: { Authorization: `Bearer ${state.jwt}` },
          }),
          axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/rate/pi?type=external`, {
            headers: { Authorization: `Bearer ${state.jwt}` },
          }),
        ]);
        commit('setRates', {
          internalRate: internalResponse.data.rate,
          externalRate: externalResponse.data.rate,
        });
      } catch (error) {
        commit('setErrorMessage', 'Failed to fetch Pi rates. Using defaults.');
        commit('setRates', { internalRate: 314159.0, externalRate: 0.8111 });
        console.error('Fetch rates error:', error);
      }
    },
    async checkVolatility({ commit, state }) {
      try {
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/rate/pi?type=external`, {
          headers: { Authorization: `Bearer ${state.jwt}` },
        });
        const newRate = response.data.rate;
        if (state.externalRate && Math.abs((newRate - state.externalRate) / state.externalRate) * 100 > 5) {
          const changePercentage = ((newRate - state.externalRate) / state.externalRate) * 100;
          commit('setVolatilityAlert', {
            show: true,
            changePercentage,
            message: `Harga Pi berubah ${changePercentage.toFixed(2)}%! Sekarang $${newRate.toFixed(4)}/Pi.`,
          });
        }
        commit('setExternalRate', newRate);
      } catch (error) {
        console.error('Check volatility error:', error);
      }
    },
    async authenticate({ commit }, { email, password, isMerchant }) {
      try {
        const endpoint = isMerchant ? '/api/auth/merchant' : '/api/auth/user';
        const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}${endpoint}`, { email, password });
        const { userId, merchantId, token } = response.data;
        if (userId) commit('setUserId', userId);
        if (merchantId) commit('setMerchantId', merchantId);
        commit('setJwt', token);
        return response.data;
      } catch (error) {
        const message = error.response?.data?.message || 'Gagal autentikasi';
        commit('setErrorMessage', message);
        throw error;
      }
    },
    async initWebSocket({ commit, state }) {
      const wsUrl = process.env.VUE_APP_WS_URL || 'wss://api.mainnet.minepi.com/ws';
      let reconnectAttempts = 0;
      const maxReconnectAttempts = 5;

      const connect = () => {
        const socket = new WebSocket(wsUrl);
        socket.onopen = () => {
          socket.send(JSON.stringify({ type: 'auth', token: state.jwt }));
          commit('setIsConnected', true);
          reconnectAttempts = 0;
          console.debug('WebSocket terhubung ke Pi Network');
        };
        socket.onmessage = (event) => {
          const data = JSON.parse(event.data);
          if (data.type === 'priceUpdate') {
            commit('setExternalRate', data.rate);
            if (Math.abs((data.rate - state.externalRate) / state.externalRate) * 100 > 5) {
              commit('setVolatilityAlert', {
                show: true,
                changePercentage: ((data.rate - state.externalRate) / state.externalRate) * 100,
                message: `Harga Pi berubah! Sekarang $${data.rate.toFixed(4)}/Pi.`,
              });
            }
          } else if (data.type === 'transaction') {
            commit('setErrorMessage', `Transaksi ${data.txHash} dikonfirmasi`);
          }
        };
        socket.onclose = () => {
          commit('setIsConnected', false);
          if (reconnectAttempts < maxReconnectAttempts) {
            setTimeout(() => {
              reconnectAttempts++;
              console.debug(`Menyambung ulang WebSocket, percobaan ${reconnectAttempts}`);
              connect();
            }, 5000);
          } else {
            commit('setErrorMessage', 'Gagal menghubungkan ke Pi Network');
          }
        };
        socket.onerror = (error) => {
          console.error('WebSocket error:', error);
        };
      };

      connect();
    },
  },
});
