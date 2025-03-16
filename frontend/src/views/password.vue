<template>
    <el-card class="person-card">
            <el-form :model="user" :rules="rules" ref="userForm" label-width="100px" size="default" class="form-container">

               <el-form-item prop="oldPassword">
                <el-input placeholder="请输入原密码" size="medium" 
                show-password v-model="user.oldPassword"></el-input>
               </el-form-item>

                <el-form-item prop="newPassword">
                <el-input placeholder="请输入新密码" size="medium" 
                 show-password v-model="user.newPassword"></el-input>
                </el-form-item>

                <el-form-item prop="confirmNewPassword">
                <el-input placeholder="请确认新密码" size="medium"
                show-password v-model="user.confirmNewPassword" @keyup.enter.native="changePassword"></el-input>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" size="small" autocomplete="off" @click="changePassword">修改</el-button>
                    <el-button type="warning" size="small" autocomplete="off"
                        @click="$router.push('/person')">返回</el-button>
                </el-form-item>

            </el-form>
        </el-card>
</template>



<script>
export default {
    name: 'Register',
    data() {
        return {
            user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {},
            rules: {
                username: [
                    { required: true, message: '请输入原密码', trigger: 'blur' },
                    { min: 1, max: 20, message: '长度在1到20个字符', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入新密码', trigger: 'blur' },
                    { min: 1, max: 20, message: '长度在1到20个字符', trigger: 'blur' }
                ],
                confirmPassword: [
                    { required: true, message: '请确认新密码', trigger: 'blur' },
                    { min: 1, max: 20, message: '长度在1到20个字符', trigger: 'blur' }
                ]
            }
        }
    },
    methods: {
        changePassword() {
            
            this.$refs['userForm'].validate((valid) => {
                if (valid) {  // 表单校验合法
                    if (this.user.newPassword !== this.user.confirmNewPassword) {
                        this.$message.info("两次输入的密码不一致")
                        return false
                    }
                    this.request.post("/user/password", this.user).then(res => {
                        if (res.code === '200') {
                            this.$message.success("修改成功");
                            this.$router.push('/person')
                        } else {
                            this.$message.info(res.msg)
                        }
                    })
                }
            });
        }
    }
}
</script>

<style scoped>
.person-card {
    max-width: 600px;
    margin: 50px auto;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
}

.form-container {
    margin-top: 20px;
}


.el-form-item label {
    font-weight: bold;
    color: #333;
}
</style>

