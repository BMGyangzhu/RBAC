<template>
    <div class="wrapper">
        <div
            style="margin: 200px auto; background-color: #fff; width: 350px; height: 300px; padding: 20px; border-radius: 10px;">
            <div style="margin: 20px 0; text-align: center; font-size: 24px"><b>登录</b></div>
            <el-form :model="user" :rules="rules" ref="userForm" >
                <el-form-item prop="username">
                    <el-input size="medium" style="margin: 10px 0" prefix-icon="el-icon-user"
                        v-model="user.username"
                        placeholder="请输入用户名"
                        ></el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input size="medium" style="margin: 10px 0" prefix-icon="el-icon-lock" show-password
                        v-model="user.password"
                        @keyup.enter.native="login"
                        placeholder="请输入密码"></el-input>
                </el-form-item>
                <el-button type="primary" size="small" autocomplete="off" @click="login" html-type="submit" >登录</el-button>
                <el-button type="warning" size="small" autocomplete="off"
                    @click="$router.push('/register')">注册</el-button>
            </el-form>
        </div>
    </div>
</template>

<script>
import { activeRouter } from '@/utils/permission';


export default {
    name: "Login",
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
            }
        }
    },
    methods: {
        // login() {
        //   this.$refs['userForm'].validate((valid) => {
        //     if (valid) { // 表单校验合法
        //         this.request.post("/user/rbac", this.user).then(res => {
        //             if (res.code == '200') {
        //                 localStorage.setItem("user", JSON.stringify(res.data)) // 存储用户信息到浏览器
        //                 localStorage.setItem("menus", JSON.stringify(res.data.permissions)) 
        //                 this.$router.push("/home/user")
        //                 this.$message.success("登录成功")
        //             } else {
        //                 this.$message.error;
        //             }
        //         })
        //     }
        //   })
        //  },
        login() {
            this.$refs['userForm'].validate(() => {

                this.request.post("/user/login", this.user).then(res => {
                    if (res.code === '200') {
                        this.$message({
                            type: "success",
                            message: "登录成功"
                        })
                        sessionStorage.setItem("user", JSON.stringify(res.data)) // 缓存用户信息

                        const permissions = res.data.permissions;
                        // 初始化路由信息
                        activeRouter(permissions)

                        sessionStorage.removeItem('selectedMenu');
                        sessionStorage.removeItem('openedMenus');
                        // 获取 `redirect` 参数，跳转到之前的页面
                        const redirect = this.$route.query.redirect || '/'; // 默认跳转到首页
                        if (this.$route.path !== redirect ) {
                            this.$router.push(redirect);  // 登录后跳转回目标页面
                        } else {
                            this.$router.push('/'); // 如果已经在目标页面，直接跳转到其他页面（避免重复跳转）
                        }

                    } else {
                        this.$message({
                            type: 'info',
                            message: res.msg
                        })
                    }
                })
            })
        }

    }
}

</script>

<style>
.wrapper {
    height: 100vh;
    background-image: linear-gradient(to bottom right, #FC466B, #3F5EFB);
    overflow: hidden;
}
</style>