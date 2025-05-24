import Vue from 'vue';
import App from './App.vue';
import uView from 'uview-ui';
import i18n from './i18n';
import store from './store';

Vue.use(uView);
Vue.config.productionTip = false;

new Vue({
  i18n,
  store,
  render: h => h(App),
}).$mount('#app');
