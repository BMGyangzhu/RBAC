<template>
  <div>
    <!-- <div style="margin: 10px 0">
      <el-input
        style="width: 200px"
        placeholder="请输入菜品名称"
        suffix-icon="el-icon-search"
        v-model="username"
      ></el-input>
      <el-input
        style="width: 200px"
        placeholder="请输入分类名称"
        suffix-icon="el-icon-message"
        class="ml-5"
        v-model="email"
      ></el-input>
      <el-button
        class="ml-5"
        type="primary"
        @click="load"
        >搜索</el-button
      >
      <el-button
        type="warning"
        @click="reset"
        >重置</el-button
      >
    </div> -->

    <div style="margin: 10px 0">
      <el-button
        type="primary"
        @click="showAddDialogFormVisible"
        >新增 <i class="el-icon-circle-plus-outline"></i
      ></el-button>
      <el-popconfirm
        class="ml-5"
        confirm-button-text="确定"
        cancel-button-text="我再想想"
        icon="el-icon-info"
        icon-color="red"
        title="您确定批量删除这些数据吗？"
        @confirm="delBatch"
      >
        <el-button
          type="danger"
          slot="reference"
          >批量删除 <i class="el-icon-remove-outline"></i
        ></el-button>
      </el-popconfirm>
      <!-- <el-button type="primary" class="ml-5">导入 <i class="el-icon-bottom"></i></el-button> -->
      <!-- <el-button type="primary">导出 <i class="el-icon-top"></i></el-button> -->
    </div>

    <el-table
      :data="tableData"
      border
      stripe
      :header-cell-class-name="'headerBg'"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        type="selection"
        width="55"
      ></el-table-column>
      <el-table-column
        prop="id"
        label="ID"
        width="80"
      ></el-table-column>
      <el-table-column
        prop="name"
        label="菜品名称"
        width="140"
      ></el-table-column>
      <el-table-column
        prop="categoryName"
        label="分类名称"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="image"
        label="图片"
        width="120"
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
      prop="flavors"
      label="口味"
      width="200"
    >
      <template slot-scope="scope">
        <div v-if="scope.row.flavors && scope.row.flavors.length > 0">
          <el-tag
            v-for="(flavor, index) in scope.row.flavors"
            :key="index"
            size="mini"
            style="margin-right: 5px; display: inline-block;"
          >
          {{ flavor.name }}: {{ flavor.value.replace(/[$$$$"]/g, '') }}
          </el-tag>
        </div>
        <div v-else>
          默认
        </div>
      </template>
    </el-table-column>
      <el-table-column
        prop="price"
        label="价格"
        width="200"
      ></el-table-column>
      <el-table-column
        prop="description"
        label="描述"
      ></el-table-column>
      <el-table-column
        label="状态"
        width="80"
      >
        <template slot-scope="scope">
          <div
            class="tableColumn-status"
            :class="{
              'status-enabled': String(scope.row.status) === '1',
              'status-disabled': String(scope.row.status) === '0',
            }"
          >
            <!-- 小圆点 -->
            <span class="status-dot"></span>
            {{ String(scope.row.status) === "0" ? "下架" : "上架" }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        width="200"
        align="center"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            class="non"
            :class="{
              blueBug: scope.row.status == '0',
              delBut: scope.row.status != '0',
            }"
            @click="changeStatus(scope.row)"
          >
            {{ scope.row.status == "1" ? "下架" : "上架" }}
          </el-button>
          <el-button
            type="text"
            size="small"
            class="blueBug"
            @click="showEditDialog(scope.row)"
          >
            编辑
          </el-button>
          <el-button
            type="text"
            size="small"
            class="delBut"
            @click="deleteHandle(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
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
      >
      </el-pagination>
    </div>

    <el-dialog
      title="菜品信息"
      :visible.sync="addDialogFormVisible"
      width="80%"
    >
      <el-form
        label-width="80px"
        size="small"
      >
        <el-form-item label="菜品名称">
          <el-input
            v-model="newForm.name"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="分类名称">
          <!-- <el-input
            v-model="newForm.categoryName"
            autocomplete="off"
          ></el-input> -->
          <el-select
            v-model="newForm.categoryId"
            placeholder="请选择"
          >
            <el-option
              v-for="item in categories"
              :key="item.sort"
              :label="item.name"
              :value="item.id"
            >
            </el-option>
          </el-select>
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
              v-if="newForm.image"
              :src="newForm.image"
              class="avatar"
              alt="图片预览"
            />
            <i
              v-else
              class="el-icon-plus avatar-uploader-icon"
            ></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="口味">
          <div class="flavorBox">
            <!-- <flavor-select
              :dishFlavors="dishFlavors"
              :initialFlavors="newForm.flavors"
              @update:selectedFlavors="handleFlavorsUpdate"
            ></flavor-select> -->

            <!-- 动态渲染 flavor-select 组件 -->
            <div
              v-for="fs in flavorSelects"
              :key="fs.id"
              class="flavor-select-container"
            >
              <flavor-select
                :dishFlavors="dishFlavors"
                :initialFlavors="fs"
                @update:selectedFlavors="
                  (newValue) => handleFlavorsUpdate(newValue, fs.id)
                "
              >
              </flavor-select>

              <el-button
                type="text"
                size="small"
                class="removeFlavorButton"
                @click="removeFlavorSelect(fs.id)"
                >删除</el-button
              >
            </div>

            <el-button
              type="warning"
              @click="addFlavorSelect"
              style="margin-left: 10px; margin-top: 10px; margin-bottom: 10px"
              >添加口味</el-button
            >

            <!-- <div class="flavorSelect">
              <el-select
                v-model="newForm.flavors"
                placeholder="请选择"
              >
                <el-option
                  v-for="item in dishFlavors"
                  :key="item.name"
                  :label="item.name"
                  :value="item.value"
                >
                </el-option>
              </el-select>

              <div class="flavorValue">
                <el-tag
                  v-for="(flavor, index) in newForm.flavors"
                  :key="index"
                  closable
                  @close="removeTag(index)"
                >
                  {{ flavor }}
                </el-tag>
              </div>

              <div class="cleanFlavorSelect">
                <el-button
                  type="danger"
                  icon="el-icon-delete"
                  circle
                ></el-button>
              </div>
            </div> -->
          </div>
        </el-form-item>
        <el-form-item label="价格">
          <el-input
            v-model="newForm.price"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="newForm.description"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="addDialogFormVisible = false">取 消</el-button>
        <el-button
          type="primary"
          @click="add"
          >确 定</el-button
        >
      </div>
    </el-dialog>

    <el-dialog
      title="菜品信息"
      :visible.sync="dialogFormVisible"
      width="30%"
    >
      <el-form
        label-width="80px"
        size="small"
      >
        <el-form-item label="菜品名称">
          <el-input
            v-model="editForm.name"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="分类名称">
          <el-input
            v-model="editForm.categoryName"
            autocomplete="off"
          ></el-input>
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
              v-if="editForm.image"
              :src="editForm.image"
              class="avatar"
              alt="图片预览"
            />
            <i
              v-else
              class="el-icon-plus avatar-uploader-icon"
            ></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="价格">
          <el-input
            v-model="editForm.price"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="editForm.description"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button
          type="primary"
          @click="save"
          >确 定</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import FlavorSelect from "@/components/FlavorSelect.vue";
export default {
  name: "Dish",
  components: {
    FlavorSelect,
  },
  data() {
    return {
      userId: sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user")).id
        : null,
      shopId: null,
      tableData: [],
      dishFlavors: [
        { name: "甜味", value: ["无糖", "少糖", "半糖", "多糖", "全糖"] },
        { name: "温度", value: ["热饮", "常温", "去冰", "少冰", "多冰"] },
        { name: "忌口", value: ["不要葱", "不要蒜", "不要香菜", "不要辣"] },
        { name: "辣度", value: ["不辣", "微辣", "中辣", "重辣"] },
      ],
      flavorSelects: [], // 存储多个 flavor-select 组件的状态
      nextFlavorSelectId: 1, // 用于生成唯一的 ID
      total: 0,
      pageNum: 1,
      pageSize: 5,
      name: "",
      categories: {},
      editForm: {},
      newForm: {
        flavor: [],
      },
      multipleSelection: [],
      addDialogFormVisible: false,
      dialogFormVisible: false,

      uploadUrl: "http://200022-top-idvpdlc.qiniudns.com", // 上传接口地址
    };
  },
  created() {
    this.getShopId();
  
  },
  methods: {
    handleSelectionChange(val) {
      console.log(val);
      this.multipleSelection = val;
    },
    deleteHandle(id){
      this.request
        .delete("/dish/" + id)
        .then((response) => {
          if (response.code === "200") {
            this.$message.info("删除成功");
            this.load();
          }
        })
    },
    addFlavorSelect() {
      this.flavorSelects.push({
        id: this.nextFlavorSelectId++,
        name: "",
        value: [], // 初始化选中的口味
      });
    },
    handleFlavorsUpdate(newValue, id) {
      // 更新指定 ID 的 flavor-select 组件的选中口味

      const index = this.flavorSelects.findIndex((fs) => fs.id === id);
      if (index !== -1) {
        this.flavorSelects[index] = newValue;
      }
      console.log("this.flavorSelects: ", this.flavorSelects[index]);
    },

    removeFlavorSelect(id) {
      // 根据 ID 移除对应的 flavor-select 组件
      this.flavorSelects = this.flavorSelects.filter((fs) => fs.id !== id);
    },
    removeTag(index) {
      this.newForm.flavors.splice(index, 1);
    },
    getShopId() {
      this.request.get("/shop/getShopId/" + this.userId).then((response) => {
        if (response.data) {
          this.shopId = response.data;
          this.load();
          this.getAllCategories();
        }
      });
    },
    getAllCategories() {
      this.request.get("/category/listAll/" + this.userId).then((response) => {
        if (response.data) {
          this.categories = response.data;
        }
      });
    },
    flavorSelectAggregation() {
   

      // 清空之前的聚合结果
      this.newForm.flavors = [];

      // 遍历 flavorSelects 数组，构建新的 flavor 对象并添加到 newForm.flavors 中
      this.flavorSelects.forEach((fs) => {
        // 假设每个 fs 对象包含 name 和 flavor 属性
        // 并且 flavor 属性是一个数组，例如 ["无糖", "少糖", "半糖"]
        this.newForm.flavors.push({
          name: fs.name, // 这里需要确保 fs.name 是你想要的口味名称
          value: JSON.stringify(fs.value), // 这里假设 fs.flavor 是选中口味的数组
        });
      });
      console.log(
        "flavorSelectAggregation() this.flavorSelects: ",
        this.flavorSelects
      );
    },
    load() {
      this.request
        .get("/dish/page", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            shopId: this.shopId
          },
        })
        .then((res) => {
          console.log(res);
          if (res.data && res.data.records) {
            this.tableData = res.data.records;
           console.log("tableData: ",this.tableData);

            this.total = res.data.total;
            console.log("total: ", this.total);
          } else {
            this.tableData = [];
            this.total = 0;
            this.$message.info("加载数据没有成功。登录可能已经过期");
          }
        })
        .catch((error) => {
          // 如果请求出错，处理错误
          this.$message.info("请求失败，请稍后重试！");
          console.error(error);
        });
    },
    save() {
      this.request.post("/dish/edit", this.editForm).then((res) => {
        if (res.code === "200") {
          this.$message.success("保存成功");
          this.dialogFormVisible = false;
          this.load();
        } else {
          this.$message.error("保存失败");
        }
      });
    },
    add() {
      this.newForm.shopId = this.shopId;
      this.flavorSelectAggregation();
      console.log("add() this.newForm: ", this.newForm);
      this.request.post("/dish/add", this.newForm).then((res) => {
        if (res.code === "200") {
          this.$message.success("添加成功");
          this.addDialogFormVisible = false;
          this.load();
        } else {
          this.$message.error("添加失败");
        }
      });
    },
    showAddDialogFormVisible() {
      this.newForm = {};
      this.addDialogFormVisible = true;
    },
    showEditDialog(row) {
      this.editForm = row;
      this.dialogFormVisible = true;
    },
    del(id) {
      this.request.delete("/dish/" + id).then((res) => {
        if (res.code === "200") {
          this.$message.success("删除成功");
          this.load();
        } else {
          this.$message.error("删除失败");
        }
      });
    },
    delBatch() {
      let ids = this.multipleSelection.map((v) => v.id); // [{}, {}, {}] => [1,2,3]
      this.request.post("/dish/del/batch", ids).then((res) => {
        if (res.code === "200") {
          this.$message.success("批量删除成功");
          this.load();
        } else {
          this.$message.error("批量删除失败");
        }
      });
    },
    reset() {
      this.username = "";
      this.email = "";
      this.address = "";
      this.load();
    },
    handleSizeChange(pageSize) {
      console.log(pageSize);
      this.pageSize = pageSize;
      this.load();
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum);
      this.pageNum = pageNum;
      this.load();
    },
    changeStatus(row) {
      let status = row.status === 1 ? 0 : 1; // Toggle the status
      console.log(this.tableData.id);

      // 修改为通过 query 参数传递 status
      this.request
        .post("/dish/status/" + row.id, null, {
          params: { status: status },
        })
        .then((res) => {
          if (res.code === "200") {
            this.$message.success("修改成功");
            this.load(); // 重新加载数据
          } else {
            this.$message.error("修改失败");
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
                this.editForm.image = res.data; // 赋值图片 URL
                this.newForm.image = res.data;
                this.$message.success("上传成功！");
              } else {
                this.$message.error("上传失败：" + (res.msg || "未知错误"));
              }
            })
            .catch(() => {
              this.$message.error("上传失败！");
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
.headerBg {
  background: #eee !important;
}

.tableColumn-status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 10px;
}

.status-enabled .status-dot {
  background-color: green;
  /* 启用状态的绿色小圆点 */
}

.status-disabled .status-dot {
  background-color: red;
  /* 禁用状态的红色小圆点 */
}

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
  width: 150px;
  height: 150px;
  line-height: 150px;
  text-align: center;
}
.avatar {
  width: 150px;
  height: 150px;
  object-fit: contain;
  object-position: center;
  display: block;
}
.flavorBox {
  min-width: 400px;
  min-height: 100px;
  background-color: #f5f5f5;
  border-radius: 5px;

  .flavor-select-container {
    display: flex;

    .removeFlavorButton {
      margin-left: 30px;
    }
  }
}
</style>
