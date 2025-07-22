<template>


  <!-- 内容区域 -->
  <div class="content">
    <!-- 状态切换按钮 -->
    <!--      <div class="state-toggle">-->
    <!--        <button class="toggle-btn" @click="changeState">切换备份状态</button>-->
    <!--      </div>-->

    <!-- 备份状态卡片 (默认显示有备份状态) -->
    <div class="card backup-status" id="backupCard">
      <div class="status-indicator" :class="{'warning': !hasBackup}" id="statusIndicator"></div>
      <div class="card-header">
        <div class="card-title">备份状态</div>
        <span class="material-icons" style="color: #5f6368;">info</span>
      </div>
      <div class="card-body " v-if="hasBackup">
        <div class="backup-info">
          <div class="device-icon">
            <i class="material-icons">smartphone</i>
          </div>
          <div class="device-info">
            <div class="device-name">我的手机</div>
            <div class="device-model">Android 13 • Galaxy S23</div>
          </div>
        </div>
        <div class="backup-stats">
          <div class="stat-item">
            <div class="stat-label">上次备份</div>
            <div class="stat-value">今天 10:30</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">备份文件</div>
            <div class="stat-value">1,274</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">总大小</div>
            <div class="stat-value">4.7 GB</div>
          </div>
        </div>
      </div>
      <div class="card-body" v-else>
        <div class="no-backup">
          <div class="no-backup-icon">
            <i class="material-icons">cloud_off</i>
          </div>
          <div class="no-backup-title">尚未进行备份</div>
          <div class="no-backup-text">您的设备数据尚未备份，请立即备份以防止数据丢失</div>
        </div>
      </div>
    </div>

    <!-- 存储卡片 -->
    <div class="card">
      <div class="card-header">
        <div class="card-title">存储空间</div>
        <span class="material-icons" style="color: #5f6368;">cloud</span>
      </div>
      <div class="card-body">
        <div class="storage-progress">
          <div class="storage-bar" id="storageBar"></div>
        </div>
        <div class="storage-info">
          <div class="storage-label">已使用</div>
          <div class="storage-value" id="storageValue">6.5 GB / 10 GB</div>
        </div>
      </div>
    </div>

    <!-- 主操作按钮 -->
    <button class="primary-button" v-if="hasBackup" style="background: #1a73e8">
      <i class="material-icons">cloud_upload</i>
      <span>立即备份</span>

    </button>
    <button class="primary-button" v-else style="background: #34a853">
      <i class="material-icons">cloud_upload</i>
      <span>开始首次备份</span>

    </button>


  </div>


</template>

<script>

export default {
  name: 'home-view',
  data() {
    return {
      hasBackup: false,

    }
  },
  created() {

  },
  mounted() {


  },
  computed: {},
  methods: {


    changeState() {

      this.hasBackup = !this.hasBackup;
    },

    // 格式化文件大小（字节转 KB/MB）
    formatSize(size) {
      if (size < 1024) return size + ' B';
      else if (size < 1024 * 1024) return (size / 1024).toFixed(1) + ' KB';
      else return (size / (1024 * 1024)).toFixed(1) + ' MB';
    },

    // 格式化时间戳为可读格式
    formatDate(timestamp) {
      const date = new Date(timestamp);
      return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    },


  }
}
</script>

<style scoped>
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}


/* Android状态栏 */
.status-bar {
  background-color: #1a73e8;
  color: white;
  padding: 8px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;

  height: 24px;
}


/* 内容区域 */
.content {
  margin: 0 auto;
  padding: 16px;
  padding-top: 0px;

  height: 100%;

  //display: flex;
  //flex-direction: column;
  //justify-content: space-evenly;
}

/* 卡片样式 */
.card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
  overflow: hidden;

}

.card-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 500;
  color: #202124;
}

.card-body {
  padding: 16px;
}

/* 备份状态卡片 */
.backup-status {
  position: relative;
}

.status-indicator {
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background-color: #34a853;
}

.status-indicator.warning {
  background-color: #fbbc04;
}

.status-indicator.error {
  background-color: #ea4335;
}

.backup-info {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.device-icon {
  width: 48px;
  height: 48px;
  border-radius: 24px;
  background-color: #e8f0fe;
  color: #1a73e8;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.device-icon i {
  font-size: 24px;
}

.device-info {
  flex: 1;
}

.device-name {
  font-weight: 500;
  font-size: 16px;
  margin-bottom: 4px;
}

.device-model {
  font-size: 14px;
  color: #5f6368;
}

.backup-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-top: 8px;
}

.stat-item {
  text-align: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-label {
  font-size: 12px;
  color: #5f6368;
  margin-bottom: 4px;
}

.stat-value {
  font-weight: 500;
  font-size: 16px;
}

/* 无备份状态 */
.no-backup {
  text-align: center;
  padding: 24px 16px;
}

.no-backup-icon {
  font-size: 64px;
  color: #dadce0;
  margin-bottom: 16px;
}

.no-backup-title {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #202124;
}

.no-backup-text {
  font-size: 14px;
  color: #5f6368;
  margin-bottom: 24px;
}

/* 存储卡片 */
.storage-progress {
  height: 8px;
  background: #e8eaed;
  border-radius: 4px;
  overflow: hidden;
  margin: 16px 0;
}

.storage-bar {
  height: 100%;
  width: 65%;
  background: #1a73e8;
  border-radius: 4px;
}

.storage-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.storage-label {
  color: #5f6368;
}

.storage-value {
  font-weight: 500;
}

/* 主按钮 */
.primary-button {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #1a73e8;
  color: white;
  border: none;
  border-radius: 24px;
  padding: 16px 24px;
  font-size: 16px;
  font-weight: 500;
  width: 100%;
  margin: 24px 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: background 0.2s;
}

.primary-button i {
  margin-right: 8px;
}

.primary-button:hover {
  background: #1967d2;
}

.primary-button:active {
  background: #185abc;
}

/*

.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.action-item {
  background: white;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:hover {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  background: #e8f0fe;
  color: #1a73e8;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
}

.action-icon i {
  font-size: 20px;
}

.action-title {
  font-size: 14px;
  font-weight: 500;
}
*/

/* 状态切换 */
.state-toggle {
  display: flex;
  justify-content: center;
  margin: 16px 0;
}

.toggle-btn {
  background: #e8f0fe;
  color: #1a73e8;
  border: none;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
}

/* 响应式调整 */
@media (max-width: 480px) {

  .backup-stats {
    grid-template-columns: 1fr;
  }
}
</style>