import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import './assets/global.css'
import 'element-ui/lib/theme-chalk/index.css';
import request from './utils/request';
import store from '@/store/index'
import { activeRouter } from '@/utils/permission'; 
Vue.config.productionTip = false

Vue.use(ElementUI, { size: "small"});

Vue.prototype.request = request;

// 页面加载时，检查是否有保存的用户信息 刷新页面时自动动态添加路由
const storedUser = sessionStorage.getItem('user');
if (storedUser) {
    const user = JSON.parse(storedUser);
    activeRouter(user.permissions);  // 动态添加路由
}


new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')

