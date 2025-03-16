<template>
  <div class="item">
    <el-select
      v-model="selectedFlavors"
      @change="handleChange"
      placeholder="请选择"
    >
      <el-option
        v-for="item in dishFlavors"
        :key="item.name"
        :label="item.name"
        :value="item"
      >
      </el-option>
    </el-select>

    <div class="flavorValue">
      <el-tag
        v-for="(flavor, index) in selectedFlavors.value"
        :key="index"
        closable
        @close="removeTag(index)"
      >
        {{ flavor }}
      </el-tag>
    </div>
  </div>
</template>

<script>
export default {
  name: "FlavorSelect",
  props: {
    dishFlavors: {
      type: Array,
      required: true,
    },
    initialFlavors: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      selectedFlavors: this.initialFlavors,
    };
  },
  
  methods: {
    handleChange() {
      // 当 el-select 的值发生变化时，发出 update:selectedFlavors 事件
      this.$emit("update:selectedFlavors", this.selectedFlavors);
    },
    removeTag(index) {
      console.log("this.selectedFlavors: ", this.selectedFlavors);
      this.selectedFlavors.value.splice(index, 1);
      console.log("after slice: ", this.selectedFlavors);
      this.$emit("update:selectedFlavors", this.selectedFlavors);
    },
  },
};
</script>
<style scoped>
.item {
  display: flex;
  margin-top: 10px;
  margin-left: 10px;

  .flavorSelect {
    padding: 15px;
  }

  .flavorValue {
    border-radius: 5px;
    margin-left: 10px;
    padding-left: 5px;
    padding-right: 5x;
    background-color: aliceblue;
  }
}
</style>
