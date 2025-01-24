<template>
    <div>
      <h1>书籍管理</h1>
      <!-- 书籍管理的内容可以放在这里 -->
      <el-table :data="books" style="width: 100%">
        <el-table-column prop="name" label="书籍名称"></el-table-column>
        <el-table-column prop="author" label="作者"></el-table-column>
        <el-table-column prop="category" label="分类"></el-table-column>
      </el-table>
    </div>
  </template>
  
  <script>
  export default {
    name: "Book",
    data() {
      return {
        books: [
          { name: "书籍1", author: "作者1", category: "分类1" },
          { name: "书籍2", author: "作者2", category: "分类2" },
          { name: "书籍3", author: "作者3", category: "分类3" },
        ],
      };
    },
  };
  </script>
  
  <style scoped>
  /* 在这里可以加一些自定义的样式 */
  </style>
  