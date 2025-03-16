<template>
  <div>
    <!-- 显示/隐藏按钮 -->
    <el-button
      type="primary"
      @click="toggleTableVisibility"
    >
      {{ isTableVisible ? "隐藏申请表格" : "显示申请表格" }}
    </el-button>
    <el-table
      v-if="isTableVisible"
      :data="applicationForm"
      border
      style="width: 100%"
    >
      <el-table-column
        prop="userName"
        label="申请人"
        width="180"
        :formatter="formatUserName"
      >
      </el-table-column>
      <el-table-column
        prop="name"
        label="店铺名称"
        width="180"
      >
      </el-table-column>
      <el-table-column
        prop="image"
        label="图片"
      >
        <template slot-scope="scope">
          <el-image
            style="width: 95px; height: 95px"
            :src="scope.row.image"
            fit="cover"
          >
          </el-image>
        </template>
      </el-table-column>
      <el-table-column
        prop="address"
        label="地址"
      >
      </el-table-column>
      <el-table-column
        prop="status"
        label="审批状态"
      >
        <template slot-scope="scope">
          <span :style="getStatusStyle(scope.row.status)">
            {{ scope.row.status }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="applyTime"
        label="申请时间"
      >
        <!-- <template slot-scope="scope">
          {{ formatTime(scope.row.applyTime) }}
        </template> -->
      </el-table-column>
      <el-table-column
        prop="approveTime"
        label="审批时间"
      >
        <!-- <template slot-scope="scope">
          {{ formatTime(scope.row.approveTime) }}
        </template> -->
      </el-table-column>
      <el-table-column
        prop="adminUserName"
        label="审批人员"
        :formatter="formatAdminName"
      >
      </el-table-column>
      <el-table-column
        prop="comment"
        label="批语"
      ></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <!-- 如果状态是 PENDING， 显示通过和拒绝按钮 -->
          <template v-if="scope.row.status === 'PENDING'">
            <el-button
              size="mini"
              type="success"
              @click="approve(scope.row)"
              >通过</el-button
            >
            <el-button
              size="mini"
              type="danger"
              @click="reject(scope.row)"
              >拒绝</el-button
            >
          </template>

          <el-button
            v-else
            size="mini"
            type="warning"
            disabled
            >已审批</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <div style="margin: 10px 0">
      <el-input
        style="width: 200px; margin-right: 20px"
        placeholder="请输入店铺名称"
        suffix-icon="el-icon-search"
        v-model="name"
      ></el-input>
      <el-input
        style="width: 200px; margin-right: 20px"
        placeholder="请输入用户名"
        suffix-icon="el-icon-message"
        v-model="username"
      ></el-input>
      <el-input
        style="width: 200px; margin-right: 20px"
        placeholder="请输入地址"
        suffix-icon="el-icon-position"
        v-model="address"
      ></el-input>
      <el-button
        type="primary"
        @click="load"
        >搜索</el-button
      >
      <el-button @click="reset">重置</el-button>
    </div>

    <el-table
      :data="tableData"
      style="width: 100%"
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        prop="name"
        label="店铺名称"
      ></el-table-column>
      <el-table-column
        prop="image"
        label="图片"
      >
        <template slot-scope="scope">
          <el-image
            style="width: 95px; height: 95px"
            :src="scope.row.image"
            fit="cover"
          >
          </el-image>
        </template>
      </el-table-column>
      <el-table-column
        prop="address"
        label="地址"
      ></el-table-column>
      <el-table-column
        prop="username"
        label="用户名"
      ></el-table-column>
    </el-table>
    <div style="padding: 10px 0">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[5, 10, 15, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: "ShopManagement",
  data() {
    return {
      tableData: [],
      applicationForm: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      multipleSelection: [],
      name: "",
      username: "",
      address: "",
      adminUserId: sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user")).id
        : null,
      isTableVisible: true,
    };
  },
  created() {
    this.load();
    this.listApplicationInfo();
    console.log(
      "Vuex updated, new value:",
      this.$store.getters.isApplicationRejected
    ); // 确认状态更新
  },
  methods: {
    // 切换表格显示/隐藏
    toggleTableVisibility() {
      this.isTableVisible = !this.isTableVisible;
    },
    reject(row) {
      this.$prompt("请输入拒绝原因", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      })
        .then(({ value }) => {
          this.request
            .put("/application/rejectApplication/" + row.id, null, {
              params: {
                status: "REJECTED",
                comment: value,
                adminUserId: this.adminUserId,
                userId: row.userId,
              },
            })
            .then((response) => {
              if (response.code === "200") {
                this.listApplicationInfo();
                // 当申请被拒绝时，更新VueX状态
                this.$store.dispatch("updateApplicationStatus", true);
                console.log(
                  "Vuex updated, new value:",
                  this.$store.getters.isApplicationRejected
                ); // 确认状态更新
              } else {
                this.$message.error("审批失败");
              }
            });
          this.$message({
            type: "info",
            message: "您已经拒绝该申请",
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "取消操作",
          });
        });
    },
    approve(row) {
      this.$prompt("请输入批语", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      })
        .then(({ value }) => {
          this.request
            .put("/application/" + row.id, null, {
              params: {
                status: "APPROVED",
                comment: value,
                adminUserId: this.adminUserId,
                userId: row.userId,
              },
            })
            .then((response) => {
              if (response.code === "200") {
                this.listApplicationInfo();
              } else {
                this.$message.error("审批失败");
              }
            });
          this.$message({
            type: "success",
            message: "您已批准该申请",
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "取消操作",
          });
        });
    },
    // formatTime(isoTime) {
    //   if (!isoTime) return "";
    //   return isoTime.replace("T", " ");
    // },
    // formatTime(isoTime) {
    //   if (!isoTime) return "";
    //   if (Array.isArray(isoTime)) {
    //     isoTime = isoTime[0]; // 取第一个值
    //   }
    //   return isoTime.replace("T", " ");
    // },
    getStatusStyle(status) {
      if (status == "APPROVED") {
        return { color: "green" };
      } else if (status == "REJECTED") {
        return { color: "red" };
      } else if (status == "PENDING") {
        return { color: "orange" };
      }
      return {};
    },
    formatUserName(row, column, cellValue, index) {
      return `${cellValue} (${row.userNickName})`; // userName 和 userNickName 结合，NickName 用括号括起来
    },
    formatAdminName(row, column, cellValue, index) {
      return `${cellValue} (${row.adminNickName})`;
    },
    listApplicationInfo() {
      this.request.get("/application/listApplicationInfo").then((res) => {
        if (res.code !== "200") {
          this.$message.error("获取申请列表失败");
          return;
        }
        if (res.data) {
          this.applicationForm = res.data;
          console.log("this.applicationForm: ", this.applicationForm);
        }
      });
    },
    reset() {
      (this.username = ""), (this.address = ""), (this.name = ""), this.load();
    },
    handleSelectionChange(val) {
      console.log(val);
      this.multipleSelection = val;
    },
    load() {
      this.request
        .get("/shop/shops/page", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            name: this.name,
            username: this.username,
            address: this.address,
          },
        })
        .then((res) => {
          if (res.data && res.data.records) {
            this.tableData = res.data.records;
            this.total = res.data.total;
          } else {
            this.tableData = [];
            this.total = 0;
          }
        })
        .catch((error) => {
          this.$message.info("请求失败，请稍后重试！");
          console.error(error);
        });
    },
  },
};
// var websocket = null;
// var clientId = Math.random().toString(36).substr(2);

// //判断当前浏览器是否支持WebSocket
// if ("WebSocket" in window) {
//   //连接WebSocket节点
//   websocket = new WebSocket("ws://localhost:8080/ws/" + clientId);
// } else {
//   alert("Not support websocket");
// }

// //连接发生错误的回调方法
// websocket.onerror = function () {
//   // setMessageInnerHTML("error");
//   console.log("error");
// };

// //连接成功建立的回调方法
// websocket.onopen = function () {
//   // setMessageInnerHTML("连接成功");
//   console.log("连接成功")
// };

// //接收到消息的回调方法
// websocket.onmessage = function (event) {
//   // setMessageInnerHTML(event.data);
//   console.log("websocket接收到的信息", event.data);
// };

// //连接关闭的回调方法
// websocket.onclose = function () {
//   // setMessageInnerHTML("close");
// };

// //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
// window.onbeforeunload = function () {
//   websocket.close();
// };

// //将消息显示在网页上
// // function setMessageInnerHTML(innerHTML) {
// //   document.getElementById("message").innerHTML += innerHTML + "<br/>";
// // }

// //发送消息
// // function send() {
// //   var message = document.getElementById("text").value;
// //   websocket.send(message);
// // }

// //关闭连接
// function closeWebSocket() {
//   websocket.close();
// }
</script>
<style>
.status-passed {
  color: green !important;
}
.status-rejected {
  color: red !important;
}
.status-pending {
  color: orange !important;
}
</style>
