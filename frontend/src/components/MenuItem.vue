<template>
    <div>
      <!-- 如果当前菜单项有子菜单 -->
      <template v-if="item.children && item.children.length > 0">
        <el-submenu :index="item.id + ''">
          <template slot="title">
            <i :class="item.icon" class="menu-icon"></i>
            <span class="menu-text">{{ item.comment }}</span>
          </template>
  
          <!-- 递归渲染子菜单 -->
          <menu-item v-for="subItem in item.children" :key="subItem.id" :item="subItem"></menu-item>
        </el-submenu>
      </template>
  
      <!-- 如果没有子菜单，直接渲染 el-menu-item -->
      <el-menu-item v-else :index="item.path">
        <i :class="item.icon" class="menu-icon"></i>
        <span class="menu-text">{{ item.comment }}</span>
      </el-menu-item>
    </div>
  </template>
  
  <script>
  export default {
    name: 'MenuItem',
    props: {
      item: Object  // 传入一个菜单项
    }
  }
  </script>
  
  <style scoped>
/* 自定义图标样式 */
.menu-icon {
  margin-right: 10px; /* 设置图标与文字之间的距离 */
  font-size: 18px; /* 调整图标大小 */
}

/* 收缩时的菜单文字样式 */
.menu-text {
  display: inline-block;
  transition: all 0.3s;
  opacity: 1; /* 确保文字在展开时完全显示 */
}

/* 收缩状态下隐藏文字 */
.el-menu--collapse .menu-text {
  opacity: 0;
  width: 0;
  overflow: hidden;
}
</style>
  