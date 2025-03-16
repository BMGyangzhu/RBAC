<template>
  <div>
    <div style="margin: 10px 0">
      <el-input
        style="width: 200px"
        placeholder="请输入名称"
        suffix-icon="el-icon-search"
        v-model="name"
      ></el-input>
      <!--      <el-input style="width: 200px" placeholder="请输入邮箱" suffix-icon="el-icon-message" class="ml-5" v-model="email"></el-input>-->
      <!--      <el-input style="width: 200px" placeholder="请输入地址" suffix-icon="el-icon-position" class="ml-5" v-model="address"></el-input>-->
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleAdd">
        新增
        <i class="el-icon-circle-plus-outline"></i>
      </el-button>
      <el-popconfirm
        class="ml-5"
        confirm-button-text="确定"
        cancel-button-text="我再想想"
        icon="el-icon-info"
        icon-color="red"
        title="您确定批量删除这些数据吗？"
        @confirm="delBatch"
      >
        <el-button type="danger" slot="reference">
          批量删除
          <i class="el-icon-remove-outline"></i>
        </el-button>
      </el-popconfirm>
      <!--      <el-upload action="http://localhost:9090/user/import" :show-file-list="false" accept="xlsx" :on-success="handleExcelImportSuccess" style="display: inline-block">-->
      <!--        <el-button type="primary" class="ml-5">导入 <i class="el-icon-bottom"></i></el-button>-->
      <!--      </el-upload>-->
      <!--      <el-button type="primary" @click="exp" class="ml-5">导出 <i class="el-icon-top"></i></el-button>-->
    </div>

    <el-table
      :data="tableData"
      border
      stripe
      :header-cell-class-name="'headerBg'"
      :default-sort="{ prop: 'id', order: 'ascending' }"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column
        prop="id"
        label="ID"
        width="80"
        sortable
      ></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="comment" label="描述"></el-table-column>
      <el-table-column label="操作" width="280" align="center">
        <template slot-scope="scope">
          <el-button type="info" @click="showPermissionAssignDialog(scope.row)">
            分配权限
          </el-button>
          <el-button type="success" @click="handleEdit(scope.row)">
            编辑
            <i class="el-icon-edit"></i>
          </el-button>
          <el-popconfirm
            class="ml-5"
            confirm-button-text="确定"
            cancel-button-text="我再想想"
            icon="el-icon-info"
            icon-color="red"
            title="您确定删除吗？"
            @confirm="del(scope.row.id)"
          >
            <el-button type="danger" slot="reference">
              删除
              <i class="el-icon-remove-outline"></i>
            </el-button>
          </el-popconfirm>
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
      ></el-pagination>
    </div>

    <el-dialog title="角色信息" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-width="80px" size="small">
        <el-form-item label="名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.comment" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="权限分配"
      :visible.sync="permissionDialogVisible"
      width="30%"
    >
      <el-tree
        :data="permissionData"
        show-checkbox
        node-key="id"
        default-expand-all
        ref="tree"
        :props="defaultProps"
      ></el-tree>
      <div slot="footer" class="dialog-footer">
        <el-button @click="permissionDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveRolePermission">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { reloadActiveRotes } from '@/utils/permission'
export default {
  name: 'Role',
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: '',
      form: {},
      dialogFormVisible: false,
      permissionDialogVisible: false,
      multipleSelection: [],
      permissionData: [],
      expends: [],
      checks: [],
      roleId: 0,
      roleFlag: '',
      ids: [],
      defaultProps: {
        children: 'children',
        label: 'comment'
      }
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request
        .get('/role/page', {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            name: this.name
          }
        })
        .then((res) => {
          this.tableData = res.data.records
          this.total = res.data.total
        })
    },
    save() {
      this.request.post('/role/save', this.form).then((res) => {
        if (res.code === '200') {
          this.$message.success('保存成功')
          reloadActiveRotes(this)
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error('保存失败')
        }
      })
    },
    saveRolePermission() {
      const checkedKeys = this.$refs.tree.getCheckedKeys()
      const halfCheckedKeys = this.$refs.tree.getHalfCheckedKeys() // 半勾选的节点 ID
      // 合并为一个数组传递到后端
      const permissionIds = [...checkedKeys, ...halfCheckedKeys]
      console.log('permissionIds', this.permissionIds)
      this.request
        .post('/role/saveRolePermission/' + this.roleId, permissionIds)
        .then((res) => {
          if (res.code === '200') {
            this.$message.success('绑定成功')
            reloadActiveRotes(this)

            this.permissionDialogVisible = false
          } else {
            this.$message.error(res.msg)
          }
        })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete('/role/' + id).then((res) => {
        if (res.code === '200') {
          this.$message.success('删除成功')
          this.load()
        } else {
          this.$message.error('删除失败')
        }
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      let ids = this.multipleSelection.map((v) => v.id) // [{}, {}, {}] => [1,2,3]
      this.request.post('/role/del/batch', ids).then((res) => {
        if (res.code === '200') {
          this.$message.success('批量删除成功')
          this.load()
        } else {
          this.$message.error('批量删除失败')
        }
      })
    },
    reset() {
      this.name = ''
      this.load()
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
                : []
          }))
      }

      this.permissionData = formatData(this.permissionData)
    },
    showPermissionAssignDialog(role) {
      this.roleId = role.id

      // 请求当前角色的权限 IDs
      this.request
        .get('/role/listPermissionIdsByRoleId/' + this.roleId)
        .then((res) => {
          this.checks = res.data

          // 请求菜单数据
          this.request.get('/permission').then((res) => {
            this.permissionData = res.data

            // 通过 $nextTick 确保树形数据已经渲染
            this.$nextTick(() => {
              // 通过 checks 设置勾选状态
              this.checks.forEach((id) => {
                this.$refs.tree.setChecked(id, true) // 设置为勾选状态
              })
            })
          })
          this.permissionDialogVisible = true
        })
    }
  }
}
</script>

<style>
.headerBg {
  background: #eee !important;
}
</style>
