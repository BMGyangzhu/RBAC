<template>
  <div>
    <el-button size="mini" type="primary" @click="showAddDialogFormVisible">新增</el-button>
    <el-table :data="tableData" style="width: 100%;margin-bottom: 20px;">
      <el-table-column prop="id" label="id" sortable width="180"></el-table-column>
      <el-table-column prop="name" label="分类名称" sortable width="180"></el-table-column>
      <el-table-column prop="sort" label="排序" sortable width="180"></el-table-column>
      <!-- 状态列 -->
      <el-table-column label="状态" width="180">
        <template slot-scope="scope">
          <div class="tableColumn-status" :class="{
            'status-enabled': String(scope.row.status) === '1',
            'status-disabled': String(scope.row.status) === '0'
          }">
            <!-- 小圆点 -->
            <span class="status-dot"></span>
            {{ String(scope.row.status) === '0' ? '禁用' : '启用' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button type="text" size="small" class="non" :class="{
            blueBug: scope.row.status == '0',
            delBut: scope.row.status != '0'
          }" @click="changeStatus(scope.row)">
            {{ scope.row.status == '1' ? '禁用' : '启用' }}
          </el-button>
          <el-button type="text" size="small" class="blueBug" @click="showEditDialog(scope.row)">
            修改
          </el-button>
          <el-button type="text" size="small" class="delBut" @click="deleteHandle(scope.row.id)">
            删除
          </el-button>

        </template>
      </el-table-column>
    </el-table>
    <div style="padding: 10px 0">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
        :page-sizes="[5, 10, 15, 20]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>

    <el-dialog title="分类信息" :visible.sync="addDialogFormVisible" width="30%">
      <el-form label-width="80px" size="small">
        <el-form-item label="分类名称">
          <el-input v-model="newForm.name" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="add">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="分类信息" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-width="80px" size="small">
        <el-form-item label="分类名称">
          <el-input v-model="editForm.name" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { reloadActiveRotes } from '@/utils/permission';
export default {
  name: "Category",
  data() {
    return {
      shopId: null,
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      editForm: {},
      newForm: {},
      addDialogFormVisible: false,
      dialogFormVisible: false,

    }
  },
  created() {
    this.getShopId();
    this.load();
  },
  methods: {
    getShopId() {
      const user = sessionStorage.getItem('user');
      const shopId = user ? JSON.parse(user).id : null; // 确保获取到user对象并从中取出id
      console.log('shopId:', shopId); // 调试输出，查看shopId是否正确
      this.shopId = shopId;
    },
    load() {
      this.request.get("/category/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          shopId: this.shopId
        }
      }).then(res => {
        console.log(res)
        if (res.data && res.data.records) {
          this.tableData = res.data.records
          this.total = res.data.total
        } else {
          this.tableData = [];
          this.total = 0;
          this.$message.info("加载数据没有成功。登录可能已经过期");
        }
      }).catch(error => {
        // 如果请求出错，处理错误
        this.$message.info("请求失败，请稍后重试！");
        console.error(error);
      })
    },
    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    },
    showAddDialogFormVisible() {
      this.newForm = {};
      this.addDialogFormVisible = true;
    },
    showEditDialog(row) {
      this.editForm = row;
      this.dialogFormVisible = true
    },
    save() {
      this.request.post("/category", this.editForm).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功");
          // reloadActiveRotes(this);
          this.dialogFormVisible = false;
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    add() {
      this.newForm.shopId = this.shopId;
      this.newForm.status = 1;
      this.request.post("/category/add", this.newForm).then(res => {
        if (res.code === '200') {
          this.$message.success("添加成功");
          this.addDialogFormVisible = false;
          this.load()
        } else {
          this.$message.error("添加失败")
        }
      })
    },
    changeStatus(row) {
      let status = row.status === 1 ? 0 : 1; // Toggle the status
      console.log(this.tableData.id);

      // 修改为通过 query 参数传递 status
      this.request.post("/category/status/" + row.id, null, {
        params: { status: status }
      }).then(res => {
        if (res.code === '200') {
          this.$message.success("修改成功");
          this.load(); // 重新加载数据
        } else {
          this.$message.error("修改失败");
        }
      })
    },
    deleteHandle(id){
      this.request.delete("/category/" + id).then(res => {
        if ( res.code === '200') {
          this.$message.success("删除成功");
          this.load();
          reloadActiveRotes(this);
        } else {
          this.$message.error("删除失败");
        }
      })
    }
  }
}
</script>

<style scoped>
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
</style>