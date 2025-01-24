import router from '@/router';
import axios from 'axios'

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000
})

// request拦截器
// 统一加token，对请求参数进行统一加密
request.interceptors.request.use(config =>{
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    let user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : null;
    if (user) {
        config.headers['token'] = user.token; // 设置
    }

    return config
    }, error => {
        // return Promise.reject(error)

});

// response拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
      let res = response.data;
  
      // 如果是返回的文件
      if (response.config.responseType === 'blob') {
        return res;
      }
  
      // 兼容服务器返回的字符串数据
      if (typeof res === 'string') {
        res = res ? JSON.parse(res) : res;
      }
  
      console.log(res.code);
  
      // 执行错误逻辑
      // if (res.code !== '200') {
      //   alert(res.msg || '操作失败！');  // 如果服务器返回了错误消息，可以显示
      //   router.push('/login');
      //   return Promise.reject(new Error(res.msg || '操作失败！')); // 抛出错误，保证链式调用中可以捕获
      // }
      switch(res.code){
        case '200':{
          return res;
        };
        case '403':{
          alert(res.msg || '操作失败');
          router.push('/login');
          break;
        };
        case '400 BAD_REQUEST':{
          alert(res.msg);
          break;
        }
        

      }
  
      // 正常返回
      return res;
    },
    error => {
      // 发生网络错误或请求失败的逻辑
      return Promise.reject(error);  // 这里要返回 rejected promise
    }
  );


// 
export default request

