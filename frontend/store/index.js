// frontend/src/store/index.js
import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';

Vue.use(Vuex);

/**
 * Vuex store untuk Pi Store frontend, mengelola state global untuk Dual Value System,
 * transaksi, WebSocket, dan dictionary.
 */
export default new Vuex.Store({
  state: {
    // Dual Value System
    internalRate: 314159.0, // Nilai konsensus: 1 Pi = $314,159 USD
    externalRate: 0.8111, // Nilai bursa (default OKX, Mei 2025)
    userId: localStorage.getItem('userId') || '',
    merchantId: localStorage.getItem('merchantId') || '',
    jwt: localStorage.getItem('jwt') || '',
    volatilityAlert: {
      show: false,
      changePercentage: 0,
      message: '',
    },
    autoConvert: false, // Opsi konversi otomatis untuk merchant

    // Fitur asli
    statusBarHeight: 0,
    socket: null,
    isShowIMChat: false,
    isContact: false,
    isNoReadMsg: false,
    dictArrs: {},
    errorMessage: '', // Untuk pesan error global
  },

  mutations: {
    /**
     * Memperbarui rates untuk Pi Coin (internal dan eksternal).
     * @param {Object} state - Vuex state
     * @param {Object} payload - { internalRate, externalRate }
     */
    setRates(state, { internalRate, externalRate }) {
      state.internalRate = internalRate;
      state.externalRate = externalRate;
    },

    /**
     * Memperbarui external rate saja (untuk volatilitas).
     * @param {Object} state - Vuex state
     * @param {number} rate - Harga eksternal baru
     */
    setExternalRate(state, rate) {
      state.externalRate = rate;
    },

    /**
     * Memperbarui userId dan menyimpan ke localStorage.
     * @param {Object} state - Vuex state
     * @param {string} userId - ID pengguna
     */
    setUserId(state, userId) {
      state.userId = userId;
      localStorage.setItem('userId', userId);
    },

    /**
     * Memperbarui merchantId dan menyimpan ke localStorage.
     * @param {Object} state - Vuex state
     * @param {string} merchantId - ID merchant
     */
    setMerchantId(state, merchantId) {
      state.merchantId = merchantId;
      localStorage.setItem('merchantId', merchantId);
    },

    /**
     * Memperbarui JWT dan menyimpan ke localStorage.
     * @param {Object} state - Vuex state
     * @param {string} jwt - Token JWT
     */
    setJwt(state, jwt) {
      state.jwt = jwt;
      localStorage.setItem('jwt', jwt);
    },

    /**
     * Memperbarui status volatilitas untuk notifikasi.
     * @param {Object} state - Vuex state
     * @param {Object} payload - { show, changePercentage, message }
     */
    setVolatilityAlert(state, payload) {
      state.volatilityAlert = payload;
    },

    /**
     * Memperbarui opsi autoConvert untuk merchant.
     * @param {Object} state - Vuex state
     * @param {boolean} value - Status autoConvert
     */
    setAutoConvert(state, value) {
      state.autoConvert = value;
    },

    /**
     * Menyimpan data dictionary ke state.
     * @param {Object} state - Vuex state
     * @param {Object} dicts - Objek dictionary
     */
    saveDictArrs(state, dicts) {
      Object.entries(dicts).forEach(([key, value]) => {
        state.dictArrs[key] = value;
      });
    },

    /**
     * Menyimpan data dropdown dictionary.
     * @param {Object} state - Vuex state
     * @param {Object} payload - { key, val }
     */
    saveSelectList(state, { key, val }) {
      state.dictArrs[key] = val;
    },

    /**
     * Memperbarui tinggi status bar.
     * @param {Object} state - Vuex state
     * @param {number} height - Tinggi status bar
     */
    setStatusHeight(state, height) {
      state.statusBarHeight = parseInt(height);
    },

    /**
     * Memperbarui status koneksi WebSocket.
     * @param {Object} state - Vuex state
     * @param {boolean} isContact - Status koneksi
     */
    setChatIfContact(state, isContact) {
      state.isContact = isContact;
    },

    /**
     * Memperbarui status tampilan chat.
     * @param {Object} state - Vuex state
     * @param {boolean} isShow - Status tampilan
     */
    setChatIfShow(state, isShow) {
      state.isShowIMChat = isShow;
    },

    /**
     * Memperbarui status pesan belum dibaca.
     * @param {Object} state - Vuex state
     * @param {boolean} hasUnread - Status pesan
     */
    setChatIfRead(state, hasUnread) {
      state.isNoReadMsg = hasUnread;
    },

    /**
     * Menyimpan instance WebSocket.
     * @param {Object} state - Vuex state
     * @param {WebSocket} websocket - Instance WebSocket
     */
    setSocket(state, websocket) {
      state.socket = websocket;
    },

    /**
     * Memperbarui pesan error global.
     * @param {Object} state - Vuex state
     * @param {string} message - Pesan error
     */
    setErrorMessage(state, message) {
      state.errorMessage = message;
    },
  },

  actions: {
    /**
     * Mengambil rates internal dan eksternal dari backend.
     * @param {Object} context - Vuex context
     * @returns {Promise}
     */
    async fetchRatesAction({ commit, state }) {
      try {
        const [internalResponse, externalResponse] = await Promise.all([
          axios.get('/api/rate/pi?type=internal', {
            headers: { Authorization: `Bearer ${state.jwt}` },
          }),
          axios.get('/api/rate/pi?type=external', {
            headers: { Authorization: `Bearer ${state.jwt}` },
          }),
        ]);
        commit('setRates', {
          internalRate: internalResponse.data.rate,
          externalRate: externalResponse.data.rate,
        });
      } catch (error) {
        const message = error.response?.data?.message || 'Gagal mengambil nilai Pi';
        commit('setErrorMessage', message);
        commit('setRates', { internalRate: 314159.0, externalRate: 0.8111 });
        console.error('Fetch rates error:', error);
      }
    },

    /**
     * Memeriksa volatilitas harga eksternal.
     * @param {Object} context - Vuex context
     */
    async checkVolatility({ commit, state }) {
      try {
        const response = await axios.get('/api/rate/pi?type=external', {
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

    /**
     * Autentikasi pengguna atau merchant.
     * @param {Object} context - Vuex context
     * @param {Object} payload - { email, password, isMerchant }
     * @returns {Promise}
     */
    async authenticate({ commit }, { email, password, isMerchant }) {
      try {
        const endpoint = isMerchant ? '/api/auth/merchant' : '/api/auth/user';
        const response = await axios.post(endpoint, { email, password });
        const { userId, merchantId, token } = response.data;
        if (userId) {
          commit('setUserId', userId);
        }
        if (merchantId) {
          commit('setMerchantId', merchantId);
        }
        commit('setJwt', token);
        return response.data;
      } catch (error) {
        const message = error.response?.data?.message || 'Gagal autentikasi';
        commit('setErrorMessage', message);
        throw error;
      }
    },

    /**
     * Mengambil data dictionary berdasarkan tipe.
     * @param {Object} context - Vuex context
     * @param {string} params - Tipe dictionary
     * @returns {Promise}
     */
    async getDictlists({ commit }, params) {
      try {
        const response = await axios.get(`/cert/dict/queryByType/${params}`, {
          headers: { Authorization: `Bearer ${state.jwt}` },
        });
        if (response.data && response.data.code === 0) {
          commit('saveDictArrs', response.data.data);
          return response.data.data;
        } else {
          throw new Error('Respons dictionary tidak valid');
        }
      } catch (error) {
        const message = error.response?.data?.message || 'Gagal mengambil dictionary';
        commit('setErrorMessage', message);
        console.error('Get dictlists error:', error);
        throw error;
      }
    },

    /**
     * Mengambil data dropdown dictionary berdasarkan nilai.
     * @param {Object} context - Vuex context
     * @param {string} params - Nilai dictionary
     */
    async getSelectLists({ commit }, params) {
      try {
        const response = await axios.get(`/cert/dict/selectByValue/${params}`, {
          headers: { Authorization: `Bearer ${state.jwt}` },
        });
        if (response.data && response.data.code === 0) {
          commit('saveSelectList', { key: params, val: response.data.data });
        } else {
          throw new Error('Respons dropdown tidak valid');
        }
      } catch (error) {
        const message = error.response?.data?.message || 'Gagal mengambil dropdown';
        commit('setErrorMessage', message);
        console.error('Get select lists error:', error);
      }
    },

    /**
     * Menginisialisasi dan mengelola koneksi WebSocket.
     * @param {Object} context - Vuex context
     * @param {string} wsUrl - URL WebSocket
     */
    async initWebSocket({ commit, state }) {
      const wsUrl = 'wss://pi-store-api.example.com/ws';
      let reconnectAttempts = 0;
      const maxReconnectAttempts = 5;

      const connect = () => {
        const socket = new WebSocket(wsUrl);
        socket.onopen = () => {
          commit('setChatIfContact', true);
          commit('setSocket', socket);
          reconnectAttempts = 0;
          console.debug('WebSocket connected');
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
          }
          if (data.type === 'message') {
            commit('setChatIfRead', true);
          }
        };
        socket.onclose = () => {
          commit('setChatIfContact', false);
          if (reconnectAttempts < maxReconnectAttempts) {
            setTimeout(() => {
              reconnectAttempts++;
              console.debug(`Reconnecting WebSocket, attempt ${reconnectAttempts}`);
              connect();
            }, 5000);
          } else {
            commit('setErrorMessage', 'Gagal menghubungkan WebSocket');
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
