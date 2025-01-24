<template>
  <el-menu style="min-height: 100%; min-width: 300;overflow-x: hidden"  text-color="black"
    active-text-color="#4169E1" :default-active="selectedMenu" :collapse-transition="false" :collapse="isCollapse"
    router>
    <div style="height: 60px; line-height: 60px; text-align: center">
      <img src="../assets/logo.png" alt="" style="width: 20px; position: relative; top: 5px;">
      <b style="color: black; margin-left: 5px" v-show="logoTextShow">后台管理系统</b>
    </div>

    <!-- 渲染菜单项 -->
    <menu-item v-for="item in user.permissions" :key="item.id" :item="item"></menu-item>
  </el-menu>
</template>

<script>
import MenuItem from './MenuItem.vue'; // 引入递归组件

export default {
  name: "Aside",
  props: {
    isCollapse: Boolean,
    logoTextShow: Boolean
  },

  data() {
    return {
      user: {},
      selectedMenu: ''  // 存储当前选中的菜单项
    }
  },
  
  created() {
    let userStr = sessionStorage.getItem("user");
    this.user = userStr ? JSON.parse(userStr) : {};

    // 尝试获取当前选中的菜单项
    this.selectedMenu = sessionStorage.getItem("selectedMenu");
  },

  watch: {
    // 监听路由变化，更新当前选中的菜单项
    '$route.path'(newPath) {
      this.selectedMenu = newPath;
      sessionStorage.setItem('selectedMenu', newPath);  // 保存选中的菜单项
    }
  },

  components: {
    MenuItem  // 注册递归组件
  }
}
</script>
<style>

</style>

