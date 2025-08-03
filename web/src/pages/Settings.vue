<template>
  <div>
    <!-- 内容区域 -->
    <div class="content">
      <!-- 备份内容设置组 -->
      <div class="settings-group">
        <div class="group-title">备份选项</div>

        <router-link to="/settings/source" class="setting-item">
          <div class="setting-content">
            <div class="setting-icon">
              <i class="material-icons">folder</i>
            </div>
            <div class="setting-info">
              <div class="setting-name">备份源管理</div>
              <div class="setting-desc">选择要备份的文件夹</div>

            </div>
          </div>
          <div class="setting-action">
            <i class="material-icons">chevron_right</i>
          </div>
        </router-link>

        <router-link to="/settings/server-config" class="setting-item">
          <div class="setting-content">
            <div class="setting-icon">
              <i class="material-icons">backup</i>
            </div>
            <div class="setting-info">
              <div class="setting-name">服务器设置</div>
              <div class="setting-desc">设置服务器地址和密钥信息</div>
            </div>
          </div>
          <div class="setting-action">
            <i class="material-icons">chevron_right</i>
          </div>
        </router-link>
      </div>

      <!-- 杂项 -->
      <div class="advanced-header">
        <span>.</span>
      </div>

      <div class="settings-group">
        <div class="group-title">杂项</div>

        <div class="setting-item">
          <div class="setting-content">
            <div class="setting-icon">
              <i class="material-icons">folder</i>
            </div>
            <div class="setting-info">
              <div class="setting-name">md5校验</div>
              <div class="setting-desc">开启后可防止上传错误文件，但会增加上传时间</div>

            </div>
          </div>
          <div class="setting-action">
            <label class="setting-switch">
              <input type="checkbox" v-model="md5Check" >
              <span class="setting-slider"></span>
            </label>
          </div>
        </div>


      </div>


    </div>


  </div>
</template>
<script>
import {mapActions} from "vuex";

export default {
  name: 'settings-view',
  data(){

    return {
      md5Check:false
    }
  },
  watch:{
    md5Check(){
      console.log("md5Check变化了")
      let config = this.$store.state.serverConfig;
      config.checkMd5 = this.md5Check;
      this.setServerConfig( config)
    }
  },
  mounted() {
    this.md5Check = this.$store.state.serverConfig.checkMd5;

  },methods:{
    ...mapActions(['setServerConfig']),
  }
}
</script>
<style scoped>


/* 高级设置 */
.advanced-header {
  display: flex;
  align-items: center;
  margin: 24px 0 16px;
  color: #5f6368;
  font-weight: 500;
}

.advanced-header::before, .advanced-header::after {
  content: "";
  flex: 1;
  height: 1px;
  background: #dadce0;
}

.advanced-header span {
  margin: 0 16px;
}

/* 高级选项 */
.advanced-options {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
  margin-bottom: 24px;
}

.option-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
}

.option-label {
  font-size: 16px;
  color: #202124;
}

.option-input {
  width: 120px;
  padding: 12px;
  border: 1px solid #dadce0;
  border-radius: 12px;
  font-size: 16px;
}

/* 设置卡片 */
.settings-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
  margin-bottom: 24px;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
}

.setting-label {
  display: flex;
  align-items: center;
}

.setting-label i {
  margin-right: 16px;
  color: #5f6368;
}

.setting-text {
  font-size: 16px;
}

.setting-switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
}

.setting-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.setting-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 34px;
}

.setting-slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .setting-slider {
  background-color: #1a73e8;
}

input:checked + .setting-slider:before {
  transform: translateX(24px);
}


.content {
  padding: 16px;
}

/* 设置组 */
.settings-group {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
  overflow: hidden;
}

.group-title {
  padding: 16px;
  font-weight: 500;
  font-size: 16px;
  color: #202124;
  background: #f8f9fa;
  border-bottom: 1px solid #f1f3f4;
}

/* 设置项 */
.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #f1f3f4;
  position: relative;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-content {
  display: flex;
  align-items: center;
  flex: 1;
}

.setting-icon {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  background: #e8f0fe;
  color: #1a73e8;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.setting-icon i {
  font-size: 20px;
}

.setting-info {
  flex: 1;
}

.setting-name {
  font-size: 16px;
  margin-bottom: 4px;
  color: #202124;
}

.setting-desc {
  font-size: 14px;
  color: #5f6368;
}

.setting-action {
  margin-left: 16px;
  color: #5f6368;
}

.setting-switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
}

.setting-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.setting-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 34px;
}

.setting-slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .setting-slider {
  background-color: #1a73e8;
}

input:checked + .setting-slider:before {
  transform: translateX(24px);
}

/* 备份计划预览 */
.backup-preview {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 12px 16px;
  margin-top: 8px;
  font-size: 14px;
  color: #5f6368;
}

/* 底部操作栏 */
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 16px;
  display: flex;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 90;
}

.action-button {
  flex: 1;
  border-radius: 24px;
  padding: 14px;
  font-size: 16px;
  font-weight: 500;
  text-align: center;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
  background: #1a73e8;
  color: white;
  box-shadow: 0 2px 4px rgba(26, 115, 232, 0.3);
}


</style>
