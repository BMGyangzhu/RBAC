<template>
    <div class="wrapper">
        <div
            style="margin: 100px auto; background-color: #fff; width: 350px; height: 400px; padding: 20px; border-radius: 10px;">
            <div style="margin: 20px 0; text-align: center; font-size: 24px"><b>注册</b></div>
            <el-form :model="user" :rules="rules" ref="userForm">

                <el-form-item prop="username">
                    <el-input placeholder="请输入用户名" size="medium" style="margin: 5px 0" prefix-icon="el-icon-user"
                        v-model="user.username"></el-input>
                </el-form-item>

                <el-form-item prop="password">
                    <el-input placeholder="请输入密码" size="medium" style="margin: 5ps 0" prefix-icon="el-icon-lock"
                        show-password v-model="user.password"></el-input>
                </el-form-item>

                <el-form-item prop="confirmPassword">
                    <el-input placeholder="请确认密码" size="medium" style="margin: 5px 0" prefix-icon="el-icon-lock"
                        show-password v-model="user.confirmPassword" @keyup.enter.native="register"></el-input>
                </el-form-item>

                <el-form-item style="margin: 5px 0; text-align: right">
                    <el-button type="primary" size="small" autocomplete="off" @click="register">注册</el-button>
                    <el-button type="warning" size="small" autocomplete="off"
                        @click="$router.push('/login')">返回登录</el-button>
                </el-form-item>

            </el-form>
        </div>
    </div>
</template>

<script>
export default {
    name: 'Register',
    data() {
        return {
            user: {},
            rules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' },
                    { min: 1, max: 20, message: '长度在1到20个字符', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 1, max: 20, message: '长度在1到20个字符', trigger: 'blur' }
                ],
                confirmPassword: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 1, max: 20, message: '长度在1到20个字符', trigger: 'blur' }
                ]
            }
        }
    },
    methods: {
        register() {
            this.$refs['userForm'].validate((valid) => {
                if (valid) {  // 表单校验合法
                    if (this.user.password !== this.user.confirmPassword) {
                        this.$message.info("两次输入的密码不一致")
                        return false
                    }
                    this.request.post("/user/register", this.user).then(res => {
                        if (res.code === '200') {
                            this.$message.success("注册成功");
                            this.$router.push('/login')
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

<style>
.wrapper{
    height: 100vh;
    background-image: linear-gradient(to bottom right, #FC466B, #3F5EFB);
}
</style>