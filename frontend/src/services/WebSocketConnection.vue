<template>
    <div>
      <h2>Socket.io 通信</h2>
      <div v-if="message">来自服务器的消息: {{ message }}</div>
    </div>
  </template>
  
  <script>
  import io from 'socket.io-client';
  
  export default {
    data() {
      return {
        socket: null,     // Socket 实例
        message: '',      // 保存从服务器收到的消息
      };
    },
    methods: {
      // 连接 Socket.io 服务器
      connectSocket() {
        this.socket = io('http://localhost:8080');  // 连接到服务器地址
  
        // 监听服务器的消息
        this.socket.on('message', (data) => {
          console.log('接收到消息: ', data);
          this.message = data;  // 将消息保存到组件状态
        });
        
        // 发送消息到服务器
        this.socket.emit('message', 'Hello from Vue client!');
      },
  
      // 断开 Socket.io 连接
      disconnectSocket() {
        if (this.socket) {
          this.socket.disconnect();
          console.log('Socket.io 连接已断开');
        }
      },
    },
    mounted() {
      this.connectSocket();  // 组件挂载后自动连接 Socket.io
    },
    beforeDestroy() {
      this.disconnectSocket();  // 组件销毁时断开连接
    }
  };
  </script>
  