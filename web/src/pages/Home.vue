<template>


  <!-- 内容区域 -->
  <div class="content">
    <!-- 状态切换按钮 -->
    <div class="state-toggle" v-if="!isReady">
      <router-link to="/settings/server-config" tag="button" class="toggle-btn highlight" v-if="!serverSettingReady">
        你还没有设置服务器信息，点我设置
      </router-link>

      <router-link to="/settings/source" tag="button" class="toggle-btn highlight" v-if="!sourceSettingReady">
        你还没有添加备份源，点我添加
      </router-link>
    </div>

    <!-- 备份状态卡片 (默认显示有备份状态) -->
    <div class="card backup-status" id="backupCard">
      <div class="status-indicator" :class="{'warning': !hasBackup}" id="statusIndicator"></div>
      <div class="card-header">
        <div class="card-title">备份状态</div>
        <span class="material-icons" style="color: #5f6368; ">info</span>
      </div>
      <div class="card-body " v-if="hasBackup">
        <div class="backup-info">
          <div class="device-icon">
            <i class="material-icons">smartphone</i>
          </div>
          <div class="device-info">
            <div class="device-name">我的手机</div>
            <div class="device-model">{{ phoneDetail }}</div>
          </div>
        </div>
        <div class="backup-stats">
          <div class="stat-item">
            <div class="stat-label">上次备份</div>
            <div class="stat-value">{{ formatRelativeTime(backupList[0].backUpTime) }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">备份文件数</div>
            <div class="stat-value">{{ formatNumberWithCommas(backupList[0].backUpNum) }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">总大小</div>
            <div class="stat-value">{{ formatSize(backupList[0].totalFileSize) }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">花费时长</div>
            <div class="stat-value">{{ backupList[0].backUpCostTime }} 秒</div>
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
        <div class="card-title">本地存储空间</div>
        <span class="material-icons" style="color: #5f6368;">mobile_camera</span>
      </div>
      <div class="card-body">
        <div class="storage-progress">
          <div class="storage-bar" id="storageBar" :style="{
            width: progress+'%',
           backgroundColor: progress >= 90 ? '#f44336' :
                     progress >= 80 ? '#ff9800' :
                     progress >= 70 ? '#ffeb3b' :
                     progress >= 50 ? '#1a73e8'
                     : '#4caf50'


          }"></div>
        </div>
        <div class="storage-info">
          <div class="storage-label">已使用</div>
          <div class="storage-value" id="storageValue">{{ formatSize(available) }} / {{ formatSize(total) }}</div>
        </div>
      </div>
    </div>

    <!-- 主操作按钮 -->
    <button class="primary-button"

            :class="{ 'warning': !isReady }"
            :style="{
              background: hasBackup?'#1a73e8':'#34a853'
            }"
            @click="startBackup"
    >
      <i class="material-icons" :style="{animation:hasBackup? 'spin 1s linear infinite':''}">
        {{ isReady ? (hasBackup ? 'autorenew' : 'cloud_upload') : 'error' }}
      </i>
      <span>
        {{ isReady ? (hasBackup ? '立即备份' : '开始首次备份') : '请配置相关信息（点我）' }}
      </span>

    </button>
    <SelectFolder :show-count="false" title="将要备份的文件夹" @click.native="toSource"></SelectFolder>




  </div>


</template>

<script>

import SelectFolder from "@/components/SelectFolder.vue";

export default {
  name: 'home-view',
  components: {SelectFolder},
  data() {
    return {
      hasBackup: false,
      backupList: [],
      phoneDetail: '',
      total: 0,
      available: 0,
      progress: 0,
      selectedDir: [],
      showLoading: true,


      serverSettingReady: true,
      sourceSettingReady: true,
    }
  },
  created() {

  },
  mounted() {
    this.sourceSettingReady = this.$store.state.selectedDir.length > 0;
    this.serverSettingReady = this.$store.state.serverConfig.addr && this.$store.state.serverConfig.secret;
    this.getBackupList();
    this.getPhoneDetail();
    this.getStorageInfo();

    setTimeout(() => {
      this.getProgress()
    }, 500)

  },
  computed: {

    isReady: function () {
      return this.serverSettingReady && this.sourceSettingReady;
    },


  },
  methods: {


    startBackup() {
      if (!this.isReady) {
        this.scrollTop();
        return;
      }


      window.Android.addTestHistory();
    },
    scrollTop() {
      window.scrollTo(0, 0);


    },
    toSource() {
      this.$router.push('/settings/source')
    },
    getProgress() {
      let a = this.available / this.total;
      //不保留小数
      a = 100 - Math.floor(a * 100);
      this.progress = a;
    },

    getStorageInfo() {

      this.total = window.Android.getTotalStorage();
      this.available = window.Android.getAvailableStorage();
      console.log(this.total, this.available)
    },

    getPhoneDetail() {
      this.phoneDetail = window.Android.getPhoneDetail();
    },
    getBackupList() {

      this.backupList = JSON.parse(window.Android.getBackupHistory());
      console.log(this.backupList.length)
      console.log(this.backupList)


      if (this.backupList.length > 0) {
        this.hasBackup = true;
      }else {
        this.hasBackup = false;
      }
    },

    formatNumberWithCommas(num) {
      // 处理非数字输入
      if (typeof num !== 'number' || isNaN(num)) {
        return '0';
      }

      // 四舍五入取整
      const roundedNum = Math.round(num);

      // 使用toLocaleString自动添加千位分隔符
      return roundedNum.toLocaleString('en-US');
    },
    formatRelativeTime(isoDateTime) {
      // 解析输入时间
      const inputDate = new Date(isoDateTime);
      console.log(inputDate)
      const now = new Date();

      // 计算时间差（毫秒）
      const diffMs = now - inputDate;
      const diffMinutes = Math.round(diffMs / (1000 * 60));
      const diffHours = Math.round(diffMs / (1000 * 60 * 60));
      const diffDays = Math.round(diffMs / (1000 * 60 * 60 * 24));

      // 判断时间范围并返回相应格式
      if (diffMinutes < 60) {
        return `${diffMinutes}分钟前`;
      } else if (diffHours < 24) {
        const hours = Math.floor(diffHours);
        const minutes = diffMinutes % 60;
        return `${hours}小时${minutes}分钟前`;
      } else if (diffDays <= 7) {
        return `${diffDays}天前`;
      } else {
        // 超过7天显示具体日期
        const year = inputDate.getFullYear();
        const month = inputDate.getMonth() + 1; // 月份从0开始
        const day = inputDate.getDate();
        return `${year}年${month}月${day}日`;
      }
    },
    changeState() {

      this.hasBackup = !this.hasBackup;
    },

    // 格式化文件大小（字节转 KB/MB）
    formatSize(size) {
      if (size < 1024) return size + ' B';
      else if (size < 1024 * 1024) return (size / 1024).toFixed(1) + ' KB';
      else if (size < 1024 * 1024 * 1024)
        return (size / (1024 * 1024)).toFixed(1) + ' MB';
      else return (size / (1024 * 1024 * 1024)).toFixed(1) + ' GB';
    },
    formatStorage(mbValue) {
      if (mbValue >= 1024) {
        // 转换为GB并保留1位小数
        const gbValue = Math.round(mbValue / 102.4) / 10; // 避免浮点运算问题
        return `${gbValue}GB`;
      }
      return `${Math.round(mbValue)}MB`; // 不足1024MB直接显示
    },


  }
}
</script>

<style scoped>
/* 警告状态的主按钮样式 */
.primary-button.warning {
  background: #fbbc04 !important; /* 警告黄色背景 */
  color: #202124 !important; /* 深色文字确保可读性 */
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3) !important;
}

.primary-button.warning:hover {
  background: #f9ab00 !important; /* 悬停时稍深的黄色 */
  transform: scale(1.02); /* 轻微放大效果 */
}

.primary-button.warning:active {
  background: #f2994a !important; /* 按下时的背景颜色 */
  transform: scale(0.98); /* 轻微缩小效果 */
}

.primary-button.warning i {
  color: #202124 !important; /* 图标也使用深色 */
  animation: warning-pulse 1.5s infinite; /* 添加脉冲动画 */
}

/* 警告图标脉冲动画 */
@keyframes warning-pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

/* 警告状态下的禁用样式（如果需要） */
.primary-button.warning.disabled {
  background: #f1f3f4 !important;
  color: #9aa0a6 !important;
  cursor: not-allowed !important;
  box-shadow: none !important;
  animation: none !important;
}

.primary-button.warning.disabled i {
  animation: none !important;
  color: #9aa0a6 !important;
}









/* 禁用状态的主按钮样式 */
.primary-button.disabled {
  background: #bdc1c6 !important; /* 灰色背景 */
  color: #9aa0a6 !important;
  cursor: not-allowed !important;
  box-shadow: none !important;
  animation: none !important; /* 移除动画 */
}

.primary-button.disabled:hover {
  background: #bdc1c6 !important; /* 悬停时保持灰色 */
  transform: none !important; /* 移除变换效果 */
}

.primary-button.disabled:active {
  background: #bdc1c6 !important; /* 点击时保持灰色 */
  transform: none !important;
}

.primary-button.disabled i {
  animation: none !important; /* 移除图标动画 */
  color: #9aa0a6 !important;
}


/* 已选文件夹 */
.selected-folders {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
  margin-bottom: 24px;
}

.selected-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  border-bottom: 1px solid #f1f3f4;
  padding-bottom: 10px;

}

.selected-title {
  font-size: 18px;
  font-weight: 500;
  color: #202124;

}

.selected-count {
  color: #5f6368;
  font-size: 14px;

}

.selected-list {
  display: flex;
  height: 150px;
  overflow: auto;
}

.selected-item {
  background: #f1f3f4;
  border-radius: 24px;
  padding: 8px 20px;
  display: flex;
  align-items: center;
  flex-shrink: 0;
  height: 20%;
}

.selected-name {
  margin-right: 8px;
  font-size: 14px;
  color: #202124;
}

.selected-remove {
  color: #5f6368;
  font-size: 18px;
  cursor: pointer;
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
  padding-top: 20px;

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
  width: 0;
  background: #1a73e8;
  border-radius: 4px;
  transition: width 1s ease-out, background-color 0.5s ease-out;
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
  outline: none; /* 去除默认的焦点轮廓 */
  -webkit-tap-highlight-color: transparent; /* 去除点击时的高亮颜色 */

}

.primary-button i {
  margin-right: 8px;
}

.primary-button:hover {
  background: #1967d2;
}

.primary-button:active {
  background-color: #185abc; /* 按下时的背景颜色 */
  transform: scale(0.98); /* 可选：轻微缩小按钮，模拟按下效果 */
}

/* 状态切换 */
.toggle-btn {
  background: #e8f0fe;
  color: #1a73e8;
  border: none;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  margin: 5px;


}

.toggle-btn.highlight {
  background: #fde293; /* 醒目的黄色背景 */
  color: #e37400;
  box-shadow: 0 0 0 2px #ffa000;
  animation: pulse 0.8s infinite; /* 添加脉冲动画 */
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(255, 160, 0, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(255, 160, 0, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(255, 160, 0, 0);
  }
}

.state-toggle {
  display: flex;
  justify-content: center;
  flex-direction: column;
  margin: 16px 0px;
}

/* 响应式调整 */
@media (max-width: 480px) {

  .backup-stats {
    grid-template-columns: 1fr;
  }
}
</style>