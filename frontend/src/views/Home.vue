<template>
    <div class="home-container">
      <el-row class="home-header" type="flex" justify="center">
        <el-col :span="24">
          <h1>欢迎来到后台管理系统</h1>
        </el-col>
      </el-row>
  
      <el-row class="home-content" type="flex" justify="center">
        <el-col :span="16">
          <p class="intro-text">这里是您的管理面板。</p>
        </el-col>
      </el-row>
  
      <el-row class="home-footer" type="flex" justify="center">
        <el-col :span="24">
          <p>&copy; 2025 后台管理系统 版权所有</p>
        </el-col>
      </el-row>

      <div style="text-align:center;">
        <img src="..\src\assets\images.jpg" alt="欢迎图片" style="width: 366px"  />
      </div>
    


    </div>
  </template>
  
  <script>
  export default {
    name: 'Home',
    data() {
      return {
        // 在这里可以添加一些数据
      };
    },
    methods: {
      // 在这里可以添加一些方法
    }
  }
  </script>
  
  <style scoped>
  .home-container {
    padding: 20px;
  }
  
  .home-header h1 {
    text-align: center;
    color: #333;
  }
  
  .home-content .intro-text {
    text-align: center;
    font-size: 16px;
    color: #666;
  }
  
  .home-footer {
    margin-top: 50px;
    text-align: center;
    color: #999;
  }
  </style>
  