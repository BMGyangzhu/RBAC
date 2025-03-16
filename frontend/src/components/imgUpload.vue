<template>
    <div class="parent-component">
      <!-- 父组件内容 -->
      <div class="upload-item">
        <el-upload ref="uploadfiles"
                   :accept="type"
                   :class="{ borderNone: imageUrl }"
                   class="avatar-uploader"
                   action="/api/common/upload"
                   :show-file-list="false"
                   :on-success="handleAvatarSuccess"
                   :on-remove="handleRemove"
                   :on-error="handleError"
                   :before-upload="beforeAvatarUpload"
                   :headers="headers">
          <img v-if="imageUrl"
               :src="imageUrl"
               class="avatar">
  
          <i v-else
             class="el-icon-plus avatar-uploader-icon" />
          <span v-if="imageUrl"
                class="el-upload-list__item-actions">
            <span class="el-upload-span"
                  @click.stop="oploadImgDel">
              删除图片
            </span>
            <span class="el-upload-span"> 重新上传 </span>
          </span>
        </el-upload>
        <p class="upload-tips">
          <slot />
        </p>
      </div>
      <!-- 其他父组件内容 -->
    </div>
  </template>
  
  <script lang="ts">
  import { Vue } from 'vue-property-decorator';
  import { getToken } from '@/utils/cookies';
  
  export default class ParentComponent extends Vue {
    type: string = '.jpg,.jpeg,.png';
    size: number = 2;
    propImageUrl: string = '';
    imageUrl: string = '';
  
    headers = {
      token: getToken()
    };
  
    handleRemove() {}
  
    handleError(err: any, file: any, fileList: any) {
      console.log(err, file, fileList, 'handleError');
      this.$message({
        message: '图片上传失败',
        type: 'error'
      });
    }
  
    handleAvatarSuccess(response: any, file: any, fileList: any) {
      this.imageUrl = `${response.data}`;
      this.$emit('imageChange', this.imageUrl);
    }
  
    oploadImgDel() {
      this.imageUrl = '';
      this.$emit('imageChange', this.imageUrl);
    }
  
    beforeAvatarUpload(file: any) {
      const isLt2M = file.size / 1024 / 1024 < this.size;
      if (!isLt2M) {
        this.$message({
          message: `上传文件大小不能超过${this.size}M!`,
          type: 'error'
        });
        return false;
      }
    }
  }
  </script>
  
  <style lang="scss">
  .upload-item {
    display: flex;
    align-items: center;
    .el-form-item__content {
      width: 500px !important;
    }
  }
  
  .upload-tips {
    font-size: 12px;
    color: #666666;
    display: inline-block;
    line-height: 17px;
    margin-left: 36px;
  }
  
  .el-upload-list__item-actions {
    position: absolute;
    width: 100%;
    height: 100%;
    left: 0;
    top: 0;
    cursor: default;
    text-align: center;
    color: #fff;
    opacity: 0;
    font-size: 20px;
    background-color: rgba(0, 0, 0, 0.5);
    transition: opacity 0.3s;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
  }
  
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  
  .avatar-uploader {
    display: inline-block;
  }
  
  .avatar-uploader .el-upload:hover {
    border-color: #ffc200;
  }
  
  .el-upload-span {
    width: 100px;
    height: 30px;
    border: 1px solid #ffffff;
    border-radius: 4px;
    font-size: 14px;
    text-align: center;
    line-height: 30px;
  }
  
  .el-upload-span:first-child {
    margin-bottom: 20px;
  }
  
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 200px;
    height: 160px;
    line-height: 160px;
    text-align: center;
  }
  
  .avatar {
    width: 200px;
    height: 160px;
    display: block;
  }
  </style>
  