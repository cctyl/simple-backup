import Vue from 'vue'
import App from './App.vue'
import router from './router'
Vue.config.productionTip = false;
import store from './store'
import Loading from '@/components/Loading.vue'
Vue.component('loading-view', Loading)
window.vue={};
new Vue({
  render: h => h(App),
  router,
  store,
  beforeCreate() {
    Vue.prototype.$bus  = this
  }
}).$mount('#app')
