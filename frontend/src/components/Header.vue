<template>
  <div style="align-items: center; display: flex; height: 60px">
    <div style="flex: 1">
      <span
        :class="collapseBtnClass"
        style="cursor: pointer; font-size: 18px"
        @click="collapse"
      ></span>

      <!-- <el-breadcrumb separator="/" style="display: inline-block; margin-left: 10px">
                <el-breadcrumb-item :to="'/home'">首页</el-breadcrumb-item>
                <el-breadcrumb-item>{{ currentPathName }}</el-breadcrumb-item>
            </el-breadcrumb> -->
    </div>

    <div v-if="isAdministrator">
      <el-badge :value="badgeValue">
        <el-button size="small" @click="goToShopManagement">入驻申请</el-button>
      </el-badge>
    </div>

    <el-dropdown style="width: 100px; cursor: pointer; text-align: right">
      <div style="display: inline-block">
        <span>{{ user.nickname }}</span
        ><i
          class="el-icon-arrow-down"
          style="margin-left: 5px"
        ></i>
      </div>

      <el-dropdown-menu
        slot="dropdown"
        style="width: 100px; text-align: center"
      >
        <el-dropdown-item style="font-size: 14px; padding: 5px 0">
          <router-link to="/person">个人信息</router-link>
        </el-dropdown-item>
        <el-dropdown-item style="font-size: 14px; padding: 5px 0">
          <span
            style="text-decoration: none"
            @click="logout"
            >退出</span
          >
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
export default {
  name: "Header",
  props: {
    collapseBtnClass: String,
  },
  data() {
    return {
      user: sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user"))
        : {},
      userId: sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user")).id
        : null,
      isAdministrator: false,
      badgeValue: null,
      websocket: null,
      clientId: Math.random().toString(36).substr(2)
    };
  },
  
  created() {
    this.checkAdministrator();
    this.checkBadgeValue();
    this.initWebSocket();
  },
  beforeDestroy(){
    this.closeWebSocket(); // 组件销毁时关闭WebSocket
  },
  methods: {
    initWebSocket(){
        if ("WebSocket" in window){
            this.websocket = new WebSocket("ws://localhost:8080/ws/" + this.clientId);

            this.websocket.onerror = () => {
                console.log("WebSocket Error");
            };
            this.websocket.onopen = () => {
                console.log("WebSocket 连接成功");
            };
            this.websocket.onmessage = (event) =>{
                if(event.data){
                    const message = JSON.parse(event.data);
                    if(message.badgeValue){
                        this.badgeValue = message.badgeValue; 
                    }
                }
            };
            this.websocket.onclose = () => {
                console.log("WebSocket 连接关闭");
            };
        } else {
            alert("This browser not support websocket");
        }
    },
    closeWebSocket(){
        if (this.websocket){
            this.websocket.close();
        }
    },
    goToShopManagement(){
        if (this.$route.path !== "/shopManagement"){
            this.$router.push("/shopManagement");
        } 
    },
    checkAdministrator() {
      this.request
        .get("/role/verifyAdministrator/" + this.userId)
        .then((response) => {
          if (response.code != "200") {
            this.$message.error("验证身份失败");
            return;
          }
          if (response.data) {
            this.isAdministrator = true;
          }
        });
    },
    checkBadgeValue() {
      this.request
        .get("/application/checkPendingApplication")
        .then((response) => {
          if (response.code != "200") {
            this.$message.error("获取待审批申请失败");
            return;
          }
          if (response.data) {
            this.badgeValue = response.data;
          }
        });
    },
    collapse() {
      this.$emit("asideCollapse");
    },
    logout() {
      this.$router.push("/login");
      sessionStorage.removeItem("user");
      this.$message.success("退出成功");
    },
  },
};
</script>
<style></style>
