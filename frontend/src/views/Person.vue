<template>
    <el-card class="person-card">
        <el-form label-width="100px" size="default" class="form-container">

            <el-form-item label="用户名">
                <el-input v-model="form.username" disabled autocomplete="off" placeholder="请输入用户名" clearable></el-input>
            </el-form-item>

            <el-form-item label="昵称">
                <el-input v-model="form.nickname" autocomplete="off" placeholder="请输入昵称" clearable></el-input>
            </el-form-item>

            <el-form-item label="邮箱">
                <el-input v-model="form.email" autocomplete="off" placeholder="请输入邮箱" clearable></el-input>
            </el-form-item>

            <el-form-item label="地址">
                <el-input
                    type="textarea"
                    v-model="form.address"
                    autocomplete="off"
                    placeholder="请输入地址"
                    clearable
                    :autosize="{ minRows: 2, maxRows: 4 }">
                </el-input>
            </el-form-item>

            <div class="button-group">
                <el-button type="primary" icon="el-icon-check" @click="save">保存</el-button>
                <el-button icon="el-icon-refresh" @click="resetForm">重置</el-button>
            </div>

        </el-form>
    </el-card>
</template>

<script>
export default {
    name: "Person",
    data() {
        return {
            form: {
                username: "",
                nickname: "",
                email: "",
                address: "",
            },
            user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
        };
    },
    created() {
        // 初始化表单数据
        this.initializeForm();
    },
    methods: {
        async initializeForm() {
            const userData = await this.getUser();
            this.form = {
                ...userData,
                username: this.user.username,
            };
        },
        async getUser() {
            const response = await this.request.get("user/username/" + this.user.username);
            return response.data;
        },
        save() {
            this.request.post("/user", this.form).then(res => {
                if (res.code === '200') {
                    this.$message.success("保存成功");

                    // 更新本地存储的用户信息
                    this.getUser().then(res => {
                        res.token = JSON.parse(localStorage.getItem("user")).token;
                        localStorage.setItem("user", JSON.stringify(res));
                    });

                    this.$router.push("/home/user");
                } else {
                    this.$message.error("保存失败");
                }
            });
        },
        resetForm() {
            this.initializeForm();
            this.$message.info("表单已重置");
        }
    }
};
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

.button-group {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.el-form-item label {
    font-weight: bold;
    color: #333;
}
</style>
