<template>
  <div>
    <el-tree
      :data="tree"
      :props="defaultProps"
      @node-click="handleNodeClick"
    ></el-tree>

    <div>
      <el-button
        size="mini"
        type="primary"
        @click="showAddDialog"
        >新增</el-button
      >
      <el-table
        :data="tableData"
        style="width: 100%; margin-bottom: 20px"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column
          prop="comment"
          label="名称"
          sortable
          width="180"
        ></el-table-column>
        <el-table-column
          label="图标"
          width="66"
        >
          <template slot-scope="scope">
            <!-- 直接通过class绑定 -->
            <div style="text-align: center">
              <i
                :class="scope.row.icon"
                style="font-size: 20px"
              ></i>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="组件名"
          sortable
          width="180"
        ></el-table-column>
        <el-table-column
          prop="path"
          label="路径"
        ></el-table-column>

        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="success"
              @click="showEditDialog(scope.row)"
              >编辑</el-button
            >
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      title="添加权限菜单"
      :visible.sync="addDialogVisible"
      width="40%"
      center
    >
      <el-form
        :model="newForm"
        ref="newForm"
        label-width="80px"
        size="small"
        :rules="rules"
      >
        <el-form-item
          label="权限名称"
          prop="comment"
        >
          <el-input
            v-model="newForm.comment"
            autocomplete="off"
          ></el-input>
        </el-form-item>

        <!-- 单选框选择权限菜单类型 -->
        <el-form-item
          label="菜单类型"
          prop="menuType"
        >
          <el-radio-group v-model="newForm.menuType">
            <el-radio label="普通权限菜单">普通权限菜单</el-radio>
            <el-radio label="分类权限菜单">分类权限菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          v-if="newForm.menuType === '普通权限菜单'"
          label="组件名"
          prop="name"
        >
          <el-input
            v-model="newForm.name"
            autocomplete="off"
            placeholder="Permission"
          ></el-input>
        </el-form-item>

        <el-form-item
          v-if="newForm.menuType === '普通权限菜单'"
          label="路径"
          prop="path"
        >
          <el-input
            v-model="newForm.path"
            autocomplete="off"
            placeholder="/permission"
          ></el-input>
        </el-form-item>
        <el-form-item
          v-model="newForm.parentId"
          label="父节点"
          prop="parentId"
        >
          <div class="block">
            <el-cascader
              v-model="selectedValue"
              :options="cascaderOptions"
              :props="{ expandTrigger: 'click', checkStrictly: true }"
              @change="handleChange"
              size="large"
              clearable
            ></el-cascader>
          </div>
        </el-form-item>
        <el-form-item
          label="图标"
          prop="icon"
        >
          <el-select
            v-model="newForm.icon"
            clearable
            placeholder="请选择图标"
            v-if="iconDictionary && iconDictionary.length"
          >
            <el-option
              v-for="item in iconDictionary"
              :key="item.label"
              :label="item.label"
              :value="item.value"
            >
              <i
                :class="item.value"
                style="margin-right: 30px"
              ></i
              >{{ item.label }}
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          @click="handleAdd"
          >确 定</el-button
        >
      </span>
    </el-dialog>

    <el-dialog
      title="编辑权限菜单"
      :visible.sync="editDialogVisible"
      width="40%"
      center
    >
      <el-form
        :model="editForm"
        ref="editForm"
        label-width="80px"
        size="small"
        :rules="rules"
      >
        <el-form-item
          label="权限名称"
          prop="comment"
        >
          <el-input
            v-model="editForm.comment"
            autocomplete="off"
          ></el-input>
        </el-form-item>

        <el-form-item
          v-if="editForm.menuType === '普通权限菜单'"
          label="组件名"
          prop="name"
        >
          <el-input
            v-model="editForm.name"
            autocomplete="off"
          ></el-input>
        </el-form-item>

        <el-form-item
          v-if="editForm.menuType === '普通权限菜单'"
          label="路径"
          prop="path"
        >
          <el-input
            v-model="editForm.path"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item
          v-model="editForm.parentId"
          label="父节点"
          prop="parentId"
        >
          <div class="block">
            <el-cascader
              v-model="editForm.parentId"
              :options="cascaderOptions"
              :props="{ expandTrigger: 'click', checkStrictly: true }"
              size="large"
              clearable
            ></el-cascader>
          </div>
        </el-form-item>
        <el-form-item
          label="图标"
          prop="icon"
        >
          <el-select
            v-model="editForm.icon"
            clearable
            placeholder="请选择图标"
            v-if="iconDictionary && iconDictionary.length"
          >
            <el-option
              v-for="item in iconDictionary"
              :key="item.label"
              :label="item.label"
              :value="item.value"
            >
              <i
                :class="item.value"
                style="margin-right: 15px"
              ></i
              >{{ item.label }}
            </el-option>
            <!-- 选中后将图标与文字一起回显 -->
            <template
              slot="prefix"
              v-if="editForm.icon"
            >
              <i
                :class="editForm.icon"
                style="margin-left: 10px"
              ></i>
            </template>
          </el-select>
        </el-form-item>
      </el-form>
      <span
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          @click="handleEdit"
          >确 定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { reloadActiveRotes } from "@/utils/permission";
export default {
  name: "Permission",
  data() {
    return {
      tree: [],
      tableData: [],
      defaultProps: {
        children: "children",
        label: "comment",
      },
      newForm: {
        comment: "",
        name: "",
        path: "",
        parentId: null,
        icon: "",
        menuType: "普通权限菜单", // 默认选择普通菜单
      },
      addDialogVisible: false,
      // 存储级联选择的值
      selectedValue: [],
      // 级联组件的数据
      cascaderOptions: [],
      editForm: {
        comment: "",
        name: "",
        path: "",
        parentId: null,
        icon: "",
        menuType: "普通权限菜单", // 默认选择普通菜单
      },
      editDialogVisible: false,
      rules: {
        comment: [
          { required: true, message: "权限名称不能为空", trigger: "blur" },
          {
            min: 1,
            max: 20,
            message: "长度在 1 到 10 个字符",
            trigger: "blur",
          },
        ],
        name: [
          { required: true, message: "组件名称不能为空", trigger: "blur" },
          {
            min: 1,
            max: 20,
            message: "长度在 1 到 10 个字符",
            trigger: "blur",
          },
        ],
        path: [
          { required: true, message: "路径不能为空", trigger: "blur" },
          {
            min: 1,
            max: 20,
            message: "长度在 1 到 10 个字符",
            trigger: "blur",
          },
        ],
        parentId: [
          { required: true, message: "父节点不能为空", trigger: "blur" },
        ],
      },
      iconDictionary: [
        { label: "主页", value: "el-icon-house" },
        { label: "用户", value: "el-icon-user" },
        { label: "菜单", value: "el-icon-menu" },
        { label: "编辑", value: "el-icon-edit" },
        { label: "删除", value: "el-icon-delete" },
        { label: "搜索", value: "el-icon-search" },
        { label: "文档", value: "el-icon-document" },
        { label: "书籍", value: "el-icon-notebook-1" },
        { label: "设置", value: "el-icon-setting" },
        { label: "文件夹", value: "el-icon-files" },
      ],
    };
  },

  created() {
    this.load();
  },

  methods: {
    handleNodeClick(tree) {
      console.log(tree);
    },

    load() {
      this.request
        .get("/permission")
        .then((response) => {
          console.log("加载的数据:", response.data);
          if (response && response.data) {
            this.tree = response.data;
            this.tableData = response.data;
            this.formatTreeData();
            console.log(
              "cascaderOptions",
              JSON.stringify(this.cascaderOptions)
            );
          }
        })
        .catch((error) => {
          this.$message.info("请求失败，请稍后重试！");
          console.log(error);
        });
    },

    // 删除
    handleDelete(row) {
      this.$confirm("确定删除该项吗?", "删除确认", {
        type: "warning",
      })
        .then(() => {
          this.request
            .delete("/permission/delete/" + row.id)
            .then((response) => {
              if (response && response.code === "200") {
                console.log("tableData");
                console.log(JSON.stringify(this.tableData));
                this.deleteFromTree(this.tableData, row.id); // 从嵌套的树结构中删除
                reloadActiveRotes(this);

                this.$message.success("删除成功");
              } else {
                this.$message.error("删除失败");
              }
            })
            .catch((error) => {
              this.$message.error(error);
              console.log(error);
            });
        })
        .catch(() => {});
    },
    // 递归删除树结构中的指定项
    deleteFromTree(tree, id) {
      for (let i = 0; i < tree.length; i++) {
        const item = tree[i];
        if (item.id === id) {
          tree.splice(i, 1); // 找到并删除当前项
          return; // 删除后退出
        }
        if (item.children && item.children.length > 0) {
          // 如果有子菜单，递归删除
          this.deleteFromTree(item.children, id);
        }
      }
    },

    // 编辑
    handleEdit() {
      // 赋值给form是为了解决执行编辑时父节点选择框被清空的问题。
      // 因为对象赋值默认是引用赋值，所以需要特殊的浅拷贝处理，
      // 否则会修改this.editForm本身的值，选择框仍然会被清空
      let form = Object.assign({}, this.editForm); // 创建 `editForm` 的深拷贝
      form.parentId = this.editForm.parentId[0]; // 修改副本

      this.request
        .post("/permission/saveOrUpdate", form)
        .then((response) => {
          if (response && response.code === "200") {
            this.editDialogVisible = false;
            this.$message.success("编辑成功");

            // shift + F5
            // window.location.reload(true);
            // this.load();
            reloadActiveRotes(this);
          } else {
            if (
              response.msg &&
              response.msg.includes("已存在，请选择其他名称")
            ) {
              this.$message.info(response.msg);
              // 恢复到被更改前的状态，否则v-model会保留错误的值
              this.$refs["editForm"].resetFields();
            } else this.$message.error("编辑失败");
          }
        })
        .catch((error) => {
          if (error.msg && error.msg.includes("已存在，请选择其他名称"))
            this.$message.info(error.msg);
          console.log(error);
        });
    },
    showEditDialog(row) {
      this.editForm = row;
      let item = row;
      if (item.path === "" || item.path === null || item.path === undefined) {
        this.editForm.menuType = "分类权限菜单";
      } else {
        this.editForm.menuType = "普通权限菜单";
      }
      // 确保 parentId 为数组格式
      // 级联的选择是依靠cascaderOptions数组的。
      // 因为javascript对0会有类似空数组的处理，它会被视为一个假值，需要明确地将其转换为数组格式[0]
      // 才能够让级联选取到数组的[0]（第一个值）
      if (!Array.isArray(this.editForm.parentId)) {
        this.editForm.parentId = [this.editForm.parentId];
      }
      console.log("editForm:", this.editForm);
      this.editDialogVisible = true;
    },

    // 新增
    showAddDialog() {
      console.log(this.tableData);
      console.log("cascaderOptions", JSON.stringify(this.cascaderOptions));
      this.addDialogVisible = true;
    },
    handleAdd() {
      if (this.selectedValue.length > 0) {
        this.newForm.parentId =
          this.selectedValue[this.selectedValue.length - 1]; // 取最后一个值作为父节点ID
      }
      this.$refs.newForm.validate((valid) => {
        if (this.newForm.menuType === "分类权限菜单") {
          this.newForm.name = "";
          this.newForm.path = "";
        }
        if (valid) {
          this.request
            .post("/permission/saveOrUpdate", this.newForm)
            .then((response) => {
              if (response && response.code === "200") {
                reloadActiveRotes(this);
                this.addDialogVisible = false;
                this.$message.success("添加成功");
                this.load();
              } else {
                this.$message.info(response.msg);
              }
            })
            .catch((error) => {
              const errorMessage =
                error.response && error.response.data && error.response.data.msg
                  ? error.response.data.message
                  : "操作失败，请稍后重试";
              // 直接显示后端的错误信息
              this.$message.error(errorMessage);
              console.log(error);
            });
        } else {
          this.$message.info("请填写完整的表单");
        }
      });
    },
    reloadSessionStorage() {
      this.request.get("/");
    },
    formatTreeData() {
      const formatData = (data) => {
        return data
          .filter((item) => !item.path) // 仅筛选path为空的项
          .map((item) => ({
            value: item.id,
            label: item.comment, // 显示权限名称
            children:
              item.children && item.children.length > 0
                ? formatData(item.children)
                : [],
          }));
      };

      // 将tableData格式化为el-cascader所需格式
      this.cascaderOptions = formatData(this.tableData);
      let parent = {
        value: 0,
        label: "无",
        children: [],
      };
      // 向数组添加一个值，表示没有父节点
      this.cascaderOptions.unshift(parent);
    },
    handleChange(value) {
      console.log("选中的值:", value); // 打印选中的值
      console.log("selectedValue:", this.selectedValue);
    },
  },
};
</script>

<style>
/* 修改cascader的size属性不生效，所以在style中修改宽度*/
.el-cascader {
  width: 80%;
}
</style>
