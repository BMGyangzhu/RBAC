import Layout from "@/layout/Layout.vue";
import Vue from "vue";
import VueRouter from "vue-router";
Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "Layout",
    component: Layout,
    redirect: "/home",
    children: [
      {
        path: "home",
        name: "Home",
        component: () => import("@/views/Home.vue"),
      },
    ],
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login"),
    meta: { requiresAuth: false }, // 不需要验证的路由
  },
  {
    path: "/person",
    name: "Person",
    component: () => import("@/views/Person.vue"),
    meta: { requiresAuth: false },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/Register"),
    meta: { requiresAuth: false }, // 不需要验证的路由
  },
  {
    path: "/password",
    name: "Password",
    component: () => import("@/views/Password"),
    meta: { requiresAuth: false },
  }
];
const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

// 路由守卫
// router.beforeEach((to, from, next) => {
//     localStorage.setItem("currentPathName", to.name)  //设置当前的路由名称，为了在Header组件中去使用
//     store.commit("setPath") // 触发store的数据更新
//     next() // 放行路由
// })

// router.beforeEach((to, from, next) => {
//     const user = JSON.parse(localStorage.getItem("user"));

//     if(!user && to.path !== '/login' && to.path !== '/register'){
//         next('/login');
//     }else{
//         next();
//     }
// })
router.beforeEach((to, from, next) => {
  // 从 localStorage 或 sessionStorage 中获取 user 对象
  const user =
    JSON.parse(localStorage.getItem("user")) ||
    JSON.parse(sessionStorage.getItem("user"));

  // 获取 token
  const token = user ? user.token : null;
  

  // 如果目标路由需要验证身份（即需要验证 token），或者没有设置 `meta.requiresAuth`（默认需要验证）
  if (to.matched.some((record) => record.meta.requiresAuth === false)) {
    // 如果设置了 `requiresAuth: false`，不需要验证 token，直接进入
    next();
   
  } else if (to.matched.some((record) => record.meta.requiresAuth !== false)) {
    // 如果没有设置 `requiresAuth: false`（即需要验证）
    
    if (!token) {
      // 如果没有 token，跳转到登录页面
      next({
        path: "/login",
        query: { redirect: to.fullPath }, // 登录后可以跳转到原页面
      });
    } else {
      // 如果有 token，继续路由
      next();
    }
  } else {
    next(); // 如果没有 `meta.requiresAuth`，直接进入
  }
});
export default router;
