<template>
  <div>
    <div v-if="isBusinessmen">
      <el-switch
        style="display: block"
        v-model="shopStatus"
        active-color="#13ce66"
        inactive-color="#ff4949"
        active-text="营业"
        inactive-text="打烊"
        @change="handleStatusChange"
      >
      </el-switch>
      <el-descriptions
        class="margin-top"
        :column="3"
        :size="size"
        border
      >
        <template slot="extra">
          <el-button
            type="primary"
            size="small"
            @click="() => (this.showHistoryOrders = !this.showHistoryOrders)"
          >
            {{ showHistoryOrders ? "进行中的订单" : "历史订单" }}
          </el-button>
        </template>
        <template slot="extra">
          <el-button
            type="primary"
            size="small"
            @click="handleEdit"
          >
            操作
          </el-button>
        </template>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-user"></i>
            用户名
          </template>
          {{ form.username }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-tickets"></i>
            店铺名称
          </template>
          <el-tag size="small">{{ form.name }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-mobile-phone"></i>
            手机号
          </template>
          {{ form.phone }}
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            店铺地址
          </template>
          {{ form.address }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-picture"></i>
            图片
          </template>
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :http-request="handleUpload"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <img
              v-if="form.image"
              :src="form.image"
              class="avatar"
              alt="图片预览"
            />
            <i
              v-else
              class="el-icon-plus avatar-uploader-icon"
            ></i>
          </el-upload>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 历史订单表格 -->
      <el-card
        v-if="showHistoryOrders === true"
        v-for="(order, index) in processedHistoryOrders"
        :key="index"
        class="order-card"
      >
        <div
          slot="header"
          class="clearfix"
        >
          订单编号: {{ order.orderCode }}
          <span style="float: right; color: #8492a6; font-size: 13px">
            总金额: ¥{{ order.totalPrice }}
          </span>
        </div>

        <!-- 菜单列表表格 -->
        <el-table
          :data="order.menuItems"
          style="width: 100%"
        >
          <el-table-column
            prop="dishId"
            label="菜品ID"
            width="120"
          ></el-table-column>
          <el-table-column
            prop="name"
            label="菜品名称"
            width="180"
          >
          </el-table-column>
          <el-table-column
            prop="number"
            label="数量"
            width="120"
          ></el-table-column>
          <el-table-column
            prop="amount"
            label="价格（元）"
            width="120"
          ></el-table-column>
          <el-table-column
            label="口味"
            width="200"
          >
            <template slot-scope="scope">
              <span
                v-for="(flavor, fIndex) in scope.row.flavors"
                :key="fIndex"
              >
                {{ flavor }}
                <template v-if="fIndex < scope.row.flavors.length - 1"
                  >,
                </template>
              </span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 订单表格 -->
      <el-card
        v-if="showHistoryOrders === false"
        v-for="(order, index) in processedOrders"
        :key="index"
        class="order-card"
      >
        <div
          slot="header"
          class="clearfix"
        >
          订单编号: {{ order.orderCode }}
          <span style="float: right; color: #8492a6; font-size: 13px">
            总金额: ¥{{ order.totalPrice }}
          </span>
        </div>

        <!-- 菜单列表表格 -->
        <el-table
          :data="order.menuItems"
          style="width: 100%"
        >
          <el-table-column
            prop="dishId"
            label="菜品ID"
            width="120"
          ></el-table-column>
          <el-table-column
            prop="name"
            label="菜品名称"
            width="180"
          >
          </el-table-column>
          <el-table-column
            prop="number"
            label="数量"
            width="120"
          ></el-table-column>
          <el-table-column
            prop="amount"
            label="价格（元）"
            width="120"
          ></el-table-column>
          <el-table-column
            label="口味"
            width="200"
          >
            <template slot-scope="scope">
              <span
                v-for="(flavor, fIndex) in scope.row.flavors"
                :key="fIndex"
              >
                {{ flavor }}
                <template v-if="fIndex < scope.row.flavors.length - 1"
                  >,
                </template>
              </span>
            </template>
          </el-table-column>
        </el-table>
        <!-- 完成按钮 -->
        <div style="margin-top: 10px; text-align: center">
          <el-popconfirm
            title="确定要将此订单标记为完成吗？"
            confirm-button-text="确定"
            cancel-button-text="取消"
            icon="el-icon-question"
            icon-color="#606266"
            @confirm="handleComplete(index)"
          >
            <el-button
              type="success"
              slot="reference"
              icon="el-icon-check"
            ></el-button>
          </el-popconfirm>
        </div>
      </el-card>
      <!-- 如果没有订单，显示提示信息 -->
      <el-alert
        v-if="processedOrders.length === 0"
        type="info"
        show-icon
      >
        当前没有新订单
      </el-alert>
    </div>
    <div v-else>
      <h3 style="margin: 10px 0">
        {{
          isPending
            ? "您已提交入驻申请，请等待管理员审批。"
            : "您现在还未开通商家功能，若您有意成为商家，请填写相关信息并等待管理员审批。"
        }}
      </h3>
      <el-button
        :type="isPending ? 'warning' : 'primary'"
        :disabled="isPending"
        @click="showApplicationDialog"
      >
        {{ isPending ? "待审批" : "申请入驻" }}</el-button
      >
    </div>

    <!-- 申请入驻对话框 -->
    <el-dialog
      title="申请入驻"
      :visible.sync="applicationVisible"
      :append-to-body="true"
      width="30%"
    >
      <el-form
        :model="applicationForm"
        label-width="80px"
      >
        <el-form-item label="店铺名称">
          <el-input v-model="applicationForm.name"></el-input>
        </el-form-item>
        <el-form-item label="店铺地址">
          <el-input v-model="applicationForm.address"></el-input>
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :http-request="handleUpload"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <img
              v-if="applicationForm.image"
              :src="applicationForm.image"
              class="avatar"
              alt="图片预览"
            />
            <i
              v-else
              class="el-icon-plus avatar-uploader-icon"
            ></i>
          </el-upload>
        </el-form-item>
      </el-form>
      <span
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="handleApplicationCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleApplication"
          >申请</el-button
        >
      </span>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      title="编辑信息"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-form
        :model="form"
        label-width="80px"
      >
        <el-form-item label="店铺名称">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="店铺地址">
          <el-input v-model="form.address"></el-input>
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :http-request="handleUpload"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <img
              v-if="form.image"
              :src="form.image"
              class="avatar"
              alt="图片预览"
            />
            <i
              v-else
              class="el-icon-plus avatar-uploader-icon"
            ></i>
          </el-upload>
        </el-form-item>
      </el-form>
      <span
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSave"
          >保存</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "MyShop",
  data() {
    return {
      size: "medium",
      uploadUrl: "http://200022-top-idvpdlc.qiniudns.com", // 上传接口地址
      dialogVisible: false,
      applicationVisible: false,
      userName: sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user")).nickname
        : "",
      userId: sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user")).id
        : null,
      shopId: null,
      shopStatus: null,
      form: {
        username: "",
        name: "",
        address: "",
        image: "",
        phone: "",
      },
      applicationForm: {
        name: "",
        address: "",
        image: "",
      },

      

      orderData: [],
      historyOrderData: [],
      // 处理后的订单数据
      processedOrders: [],
      processedHistoryOrders: [],
      showHistoryOrders: false,

      isBusinessmen: false,
      isPending: false, // 是否已申请入驻

      websocket: null,
      orderWebSocket: null,
    };
  },
  computed: {
    isRejected() {
      console.log(
        "Checking isRejected:",
        this.$store.getters.isApplicationRejected
      );

      return this.$store.getters.isApplicationRejected;
    },
   
  },
  watch: {
    isRejected(newStatus) {
      console.log("isRejected changed:", newStatus);
      if (newStatus) {
        this.showRejectedPopup(); // 显示弹窗
      }
    },
  },
  created() {
    this.load();
    this.authentication();
    this.checkApplication();
    this.checkRejectedApplication();
  },
  watch: {
    shopId(newVal) {
      if (newVal) {
        this.initOrderWebSocket();
      }
    },
  },

  mounted() {
    this.initWebSocket();
    // this.initOrderWebSocket()
  },
  beforeDestroy() {
    this.closeWebSocket(); // 组件销毁时关闭WebSocket
  },
  methods: {
    initWebSocket() {
      if ("WebSocket" in window) {
        this.websocket = new WebSocket(
          "ws://localhost:8080/ws/" + this.clientId
        );

        this.websocket.onerror = () => {
          console.log("WebSocket Error");
        };
        this.websocket.onopen = () => {
          console.log("WebSocket 连接成功");
        };
        this.websocket.onmessage = (event) => {
          if (event.data) {
            const message = JSON.parse(event.data);
            if (message.result == 1 && message.userId == this.userId) {
              this.isPending = false;
              this.showRejectedPopup();
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
    initOrderWebSocket() {
      if ("WebSocket" in window) {
        this.orderWebSocket = new WebSocket(
          "ws://localhost:8080/ws/order/" + this.shopId
        );

        this.orderWebSocket.onerror = () => {
          console.log("WebSocket Error");
        };
        this.orderWebSocket.onopen = () => {
          console.log("WebSocket 连接成功");
        };
        this.orderWebSocket.onmessage = (event) => {
          if (event.data) {
            const message = JSON.parse(event.data);
            this.load();
            console.log("orderWebsocket: ", message);
          }
        };
        this.orderWebSocket.onclose = () => {
          console.log("WebSocket 连接关闭");
        };
      } else {
        alert("This browser not support websocket");
      }
    },
    handleStatusChange() {
      this.request.get("/shop/status/" + this.shopId)
      .then((response => {
        if(response.code === "200"){
          console.log("切换营业状态成功");
        }
      }))
    },
    getOrders() {
      this.request
        .get("/shoppingCart/order/businessmen/" + this.shopId)
        .then((response) => {
          if (response.code === "200") {
            this.orderData = response.data.orderItems;
            this.historyOrderData = response.data.historyOrderItems;
            this.processOrderData();
            this.processHistoryData();
          }
        });
    },
    processOrderData() {
      this.processedOrders = this.orderData.map((order) => {
        const shoppingCart = order.shoppingCart;
        // 提取取单码 orderCode
        const orderCode = shoppingCart[0].id;
        const totalPrice = shoppingCart.reduce(
          (sum, item) => sum + item.amount,
          0
        );
        // 处理菜单项
        const menuItems = shoppingCart.map((item) => ({
          dishId: item.dishId,
          name: item.name,
          number: item.number,
          amount: item.amount,
          flavors: JSON.parse(item.flavors),
        }));
        return {
          orderCode,
          totalPrice,
          menuItems,
        };
      });
      console.log("this.processOrderData() executed: ", this.processedOrders);
    },
    processHistoryData() {
      this.processedHistoryOrders = this.historyOrderData.map((order) => {
        const shoppingCart = order.shoppingCart;
        // 提取取单码 orderCode、
        const orderCode = shoppingCart[0].id;
        const totalPrice = shoppingCart.reduce(
          (sum, item) => sum + item.amount,
          0
        );
        // 处理菜单项
        // 处理菜单项
        const menuItems = shoppingCart.map((item) => ({
          dishId: item.dishId,
          name: item.name,
          number: item.number,
          amount: item.amount,
          flavors: JSON.parse(item.flavors),
        }));
        return {
          orderCode,
          totalPrice,
          menuItems,
        };
      });
    },
    handleComplete(index) {
      if (index === undefined || index === null) {
        console.log("index未定义");
        return;
      }
      const order = this.processedOrders[index];
      console.log("order", order);
      if (!order) {
        console.warn("无法找到要完成的订单:", order);
      }

      // 从当前订单列表中移除该订单
      this.request
        .get("/shoppingCart/order/setHistory/" + order.orderCode)
        .then((res) => {
          console.log("res: ", res);
          if (res.code === "200") {
            this.$message.success("已完成订单");
            this.load();
          } else {
            this.$message.error("处理订单失败");
          }
        });
    },
    showRejectedPopup() {
      window.alert("您的申请已被拒绝");
    },
    handleApplication() {
      this.applicationVisible = false;
      this.applicationForm.userId = this.userId;
      this.request
        .post("/application/apply", this.applicationForm)
        .then((res) => {
          if (res.code === "200") {
            this.$message.success("申请成功，等待管理员审批");
            this.isPending = true; // 设置为已申请，按钮禁用
          } else {
            this.$message.error("申请失败，请重试");
          }
        });
    },
    showApplicationDialog() {
      this.applicationVisible = true;
    },
    handleApplicationCancel() {
      this.applicationVisible = false;
      this.load();
    },
    authentication() {
      this.request
        .get("/role/verifyBusinessmenByUserId/" + this.userId)
        .then((response) => {
          if (response.code != "200") {
            this.$message.error("验证身份失败");
            return;
          }
          if (response.data) {
            this.isBusinessmen = true;
          }
        });
    },
    checkApplication() {
      this.request.get("/application/" + this.userId).then((response) => {
        if (response.code !== "200") {
          this.$message.error("请求失败");
          return;
        }
        this.isPending = response.data;
      });
    },
    checkRejectedApplication() {
      this.request
        .get("/application/checkRejectedApplication/" + this.userId)
        .then((response) => {
          if (response.code !== "200") {
            this.$message.error("请求失败");
            return;
          }

          this.$store.dispatch("updateApplicationStatus", response.data);
        });
    },
    load() {
      this.request.get("/shop/" + this.userId).then((response) => {
        if (response.code !== "200") {
          this.$message.error("获取店铺信息失败");
          return;
        }
        if (response.data) {
          this.form = response.data;
          if(this.form.status === 1) this.shopStatus = true;
          else this.shopStatus = false;
          this.shopId = response.data.id;
          this.getOrders();
          console.log("this.form.status: ", this.form.status);
          console.log("this.shopId: ", this.shopId);
        }
      });
    },
    // 点击操作按钮
    handleEdit() {
      this.dialogVisible = true;
    },
    handleCancel() {
      this.dialogVisible = false; // 关闭对话框
      this.load();
    },
    // 保存修改
    handleSave() {
      this.dialogVisible = false;
      this.request.post("/shop/edit", this.form).then((res) => {
        if (res.code === "200") {
          this.$message.success("保存成功");
          this.dialogFormVisible = false;
          this.load();
        } else {
          this.$message.error("保存失败");
        }
      });
    },
    handleUpload(params) {
      // 获取上传 Token
      this.request
        .get("/upload/getToken")
        .then((tokenRes) => {
          if (tokenRes.code !== "200") {
            this.$message.error("获取上传 Token 失败！");
            return;
          }
          const uploadToken = tokenRes.data;

          // 构造 FormData 上传
          const formData = new FormData();
          formData.append("file", params.file);
          formData.append("token", uploadToken);

          this.request
            .post("/upload/image", formData, {
              headers: { "Content-Type": "multipart/form-data" },
            })
            .then((res) => {
              if (res.code === "200") {
                this.form.image = res.data; // 赋值图片 URL
                this.applicationForm.image = res.data;
                this.$message.success("上传成功！");
              } else {
                this.$message.error("上传失败：" + (res.msg || "未知错误"));
              }
            })
            .catch(() => {
              this.$message.error("上传失败!");
            });
        })
        .catch(() => {
          this.$message.error("获取 Token 失败！");
        });
    },
    beforeUpload(file) {
      const isImage = file.type.startsWith("image/");
      if (!isImage) {
        this.$message.error("只能上传图片文件！");
        return false;
      }
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 2MB!");
        return false;
      }
      return true;
    },
  },
};
</script>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}
.avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style>
