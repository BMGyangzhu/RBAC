import router from "@/router";
import Layout from "@/layout/Layout.vue";
import Vue from "vue";
export function activeRouter(permissions) {
  let root = {
      path: '/',
      name: 'Layout',
      component: Layout,
      redirect: '/home',
      children: []
  };

  function addRoutesFromPermissions(nodes, parent) {
      nodes.sort((a, b) => a.path.localeCompare(b.path, undefined, { sensitivity: 'base' }));
      console.log(nodes);

      nodes.forEach(p => {
          if (p.path) { // 如果当前节点有path，添加到路由中
            console.log("p.path" + p.path);
              let obj = {
                  path: p.path,
                  name: p.name,
                  component: () => import(`@/views/${p.name}`)
              };
              parent.children.push(obj);
          }

          // 如果当前节点有子节点，则递归添加子节点
          if (p.children && p.children.length > 0) {
              addRoutesFromPermissions(p.children, parent);
          }
      });
  }

  // 开始递归添加路由
  addRoutesFromPermissions(permissions, root);

  // 将动态路由添加到 Vue Router 中
  router.addRoute(root);

  // 打印当前的路由配置，查看路由是否已正确添加
  console.log("当前的路由根路径", root);
  console.log("当前的路由配置：", router.options.routes);
}

export function reloadActiveRotes() {
    const item = sessionStorage.getItem('user');
    let username = null;
    if (item) {
      const user = JSON.parse(item);
      username = user.username;
    }

    Vue.prototype.request.get("/permission/" + username).then(response => {
      if (response && response.code === '200') {
        // 覆盖掉原来的项
        const newUser = response.data;
        sessionStorage.setItem('user', JSON.stringify(newUser));
        activeRouter(newUser.permissions);
        
      }
    }).catch(error => {
      Vue.prototype.$message.info("请求失败，请稍后重试？？？？？！");
      console.log(error);
    })
    setTimeout(() => {
        window.location.reload();
      }, 500); // 延迟 500ms 后刷新页面
}
