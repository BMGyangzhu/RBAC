<template>
    <div style="line-height: 60px; display: flex">
        <div style="flex: 1;">
            <span :class="collapseBtnClass" style="cursor: pointer; font-size: 18px" @click="collapse"></span>

            <!-- <el-breadcrumb separator="/" style="display: inline-block; margin-left: 10px">
                <el-breadcrumb-item :to="'/home'">首页</el-breadcrumb-item>
                <el-breadcrumb-item>{{ currentPathName }}</el-breadcrumb-item>
            </el-breadcrumb> -->

        </div>

        <el-dropdown style="width: 100px; cursor: pointer; text-align: right">
            <div style="display: inline-block">
                <span>{{ user.nickname }}</span><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
            </div>
            <el-dropdown-menu slot="dropdown" style="width: 100px; text-align: center">
                <el-dropdown-item style="font-size: 14px; padding: 5px 0">
                    <router-link to="/person">个人信息</router-link>
                </el-dropdown-item>
                <el-dropdown-item style="font-size: 14px; padding: 5px 0">
                    <span style="text-decoration: none" @click="logout">退出</span>
                </el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
    </div>
</template>

<script>
export default {
    name: "Header",
    props: {
        collapseBtnClass: String,
        
    },
    data() {
        return {
            user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
        }
    },
    // computed: {
    //     currentPathName() {
    //         return this.$store.state.currentPathName; //需要监听的数据
    //     }
    // },
    // watch: {
    //     currentPathName(newVal, oldVal) {
    //         console.log(newVal)
    //     }
    // },
    methods: {
        collapse() {
            this.$emit("asideCollapse");
        },
        logout() {
            this.$router.push("/login");
            sessionStorage.removeItem("user")
            this.$message.success("退出成功")
        }
    }

}
</script>