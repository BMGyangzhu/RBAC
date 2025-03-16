<template>
    <div class="layout">

        <el-container style="min-height: 100vh">


            <el-aside width="sideWidth + 'px'" style=" box-shadow: 2px 0 6px rgb(0 21 41 / 35%);">
                <!-- Aside 侧边栏部分 -->
                <Aside :isCollapse="isCollapse" :logoTextShow="logoTextShow" style="padding-bottom: 20px" />
            </el-aside>


            <el-container>
                <!-- Header 部分 -->
                <el-header style="border-bottom: 1px solid #ccc; box-shadow: 2px 0 6px rgb(0 21 41 / 35%);">
                    <Header :collapseBtnClass="collapseBtnClass" @asideCollapse="toggleCollapse" />
                </el-header>

                <el-main>
                    <!-- 主要内容部分 -->
                            <router-view />
                </el-main>
            </el-container>
        </el-container>




    </div>
</template>

<script>
import Header from '@/components/Header.vue';
import Aside from '@/components/Aside.vue';
export default {
    name: 'Layout',
    components: {
        Header,
        Aside
    },
    data() {
        return {
            // 控制侧边栏是否收缩
            isCollapse: false,
            // 控制 logo 文字显示与否
            logoTextShow: true,
            // 控制收缩按钮样式
            collapseBtnClass: 'el-icon-s-fold',
            sideWidth: 200,

        };
    },
    methods: {
        toggleCollapse() {

            this.isCollapse = !this.isCollapse;
            if (this.isCollapse){  //收缩
                this.sideWidth = 64;
                this.collapseBtnClass = 'el-icon-s-unfold';
                this.logoTextShow = false;
            } else {  // 展开
                this.sideWidth = 200;
                this.collapseBtnClass = 'el-icon-s-fold';
                this.logoTextShow = true;
            }

        },
    }

}
</script>





