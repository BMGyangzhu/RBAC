// // src/utils/websocket.js

// class WebSocketService {
//     constructor(url) {
//       this.url = url;
//       this.socket = null;
//       this.listeners = {};
//     }
  
//     // 创建连接
//     connect() {
//       this.socket = new WebSocket(this.url);
  
//       this.socket.onopen = () => {
//         console.log('WebSocket 连接已打开');
//       };
  
//       this.socket.onclose = () => {
//         console.log('WebSocket 连接已关闭');
//       };
  
//       this.socket.onerror = (error) => {
//         console.error('WebSocket 错误:', error);
//       };
  
//       this.socket.onmessage = (event) => {
//         const data = JSON.parse(event.data);
//         this._triggerEvent(data.type, data);  // 根据事件类型触发不同的回调
//       };
//     }
  
//     // 注册事件监听器
//     on(eventType, callback) {
//       if (!this.listeners[eventType]) {
//         this.listeners[eventType] = [];
//       }
//       this.listeners[eventType].push(callback);
//     }
  
//     // 触发事件回调
//     _triggerEvent(eventType, data) {
//       if (this.listeners[eventType]) {
//         this.listeners[eventType].forEach((callback) => callback(data));
//       }
//     }
  
//     // 发送消息
//     send(message) {
//       if (this.socket && this.socket.readyState === WebSocket.OPEN) {
//         this.socket.send(JSON.stringify(message));
//       } else {
//         console.error('WebSocket 连接未打开');
//       }
//     }
  
//     // 断开连接
//     disconnect() {
//       if (this.socket) {
//         this.socket.close();
//       }
//     }
//   }
  
//   const socketService = new WebSocketService('ws://localhost:8080');  // 替换为你的 WebSocket 服务器地址
  
//   export default socketService;
  