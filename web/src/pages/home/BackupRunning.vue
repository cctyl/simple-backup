<template>
  <!-- 内容区域 -->
  <div class="content">
    <!-- 进度卡片 -->
    <div class="progress-card pulse">
      <div class="progress-title" :style="{
           color: $store.state.backupStatus ===1?'#1a73e8': '#f97316'
      }">

        <!--  $store.state.backupStatus === 1 ? '备份中' : '已暂停'       -->
        {{
         circleTitle
        }}
      </div>
      <div class="progress-subtitle">

        <!--    $store.state.backupStatus === 1 ? '请不要关闭应用或断开网络' : '点击下方按钮继续备份'      -->
        {{
          circleDesc
        }}
      </div>

      <!-- 圆形进度条 -->
      <div style="display: flex;flex-direction: row;justify-content: center;align-items: center;margin-bottom: 20px">

        <CircularProgress :status="$store.state.backupStatus" :size="170" :progress="progressData.totalPercent"></CircularProgress>
      </div>

      <div class="progress-text">已上传 {{ formatSize(progressData.alreadyUploadFileSize) }}</div>
    </div>

    <!-- 当前文件信息 -->
    <div class="file-info">
      <div class="file-header">
        <div class="file-icon">
          <i class="material-icons">{{ getGoogleIconNameFromMimeType(progressData.currentFile.mimeType) }}</i>
        </div>
        <div class="file-details">
          <div class="file-name">{{ progressData.currentFile.name }}</div>
          <div class="file-path">{{ progressData.currentFile.relativePath }}</div>
        </div>
      </div>
      <div class="file-progress-container">
        <div class="file-progress-bar" :style="{
          width:progressData.currentFile.percent+'%',
          background: $store.state.backupStatus ===1?'#1a73e8': '#f97316'
        }"></div>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="stats-container">
      <div class="stat-card">
        <div class="stat-value">{{ formatSize(progressData.speed) }}/s</div>
        <div class="stat-label">上传速度</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ formatRelativeTime(progressData.startTime) }}</div>
        <div class="stat-label">已用时间</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ formatNumberWithCommas(progressData.needUploadFileNum) }}</div>
        <div class="stat-label">需要备份的文件数量</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ formatNumberWithCommas(progressData.alreadyUploadFileNum) }}</div>
        <div class="stat-label">已处理文件</div>
      </div>
    </div>

    <!-- 按钮区域 -->
    <div class="button-container">
      <button class="button pause-button" @click="pauseBackup" v-if="$store.state.backupStatus===1">
        <i class="material-icons" style="vertical-align: middle; margin-right: 5px;">pause</i>
        暂停备份
      </button>

      <button class="button pause-button" @click="resumeBackup" v-else>
        <i class="material-icons" style="vertical-align: middle; margin-right: 5px;">work</i>
        继续备份
      </button>

      <button class="button cancel-button" @click="completeBackup">
        <i class="material-icons" style="vertical-align: middle; margin-right: 5px;">cancel</i>
        结束备份
      </button>
    </div>

    <!-- 网络状态 -->
    <div class="network-status">
      <span class="material-icons network-icon">wifi</span>
      通过 Wi-Fi 连接 • 安全备份
    </div>

    <!-- 在根元素内添加 -->
<!--    <ConfirmModal
        :visible="showConfirmModal"
        title="结束备份"
        message="您确定要结束备份吗？未完成的备份将需要重新开始。"
        confirm-text="结束备份"
        cancel-text="继续备份"
        @confirm="handleConfirmComplete"
        @cancel="handleCancelComplete">
    </ConfirmModal>-->


    <MaterialDialog
        :visible="showConfirmModal"
        title="结束备份"
        icon="warning"
        :show-cancel="true"
        @confirm="handleConfirmComplete"
        @cancel="handleCancelComplete"
    >
      <p>您确定要结束备份吗？未完成的备份将需要重新开始。</p>
    </MaterialDialog>


    <MaterialDialog
        :visible="showCancel"
        title="温馨提示"
        icon="notifications_active"
        :show-cancel="false"
        @confirm="handleReturnHome"
    >
      <p>

        {{cancelHint}}

      </p>
    </MaterialDialog>
  </div>
</template>


<script>
import CircularProgress from "@/components/CircularProgress.vue";
import MaterialDialog from "@/components/Dialog.vue";

export default {
  name: 'backup-running-view',
  components: {MaterialDialog, CircularProgress},
  data() {
    return {

      progressData:{
        totalPercent: 30,
        needUploadFileNum:0,
        alreadyUploadFileSize: 72,
        currentFile: {
          mimeType: 'text/html',
          name: 'IMG_20230715_184523.jpg',
          relativePath: '内部存储/DCIM/Camera',
          percent: 90,
        },
        speed: 20481,//上传速度
        startTime: '2025-07-26T18:45:23Z', //ISO 8601 格式的时间字符串 示例："2023-07-15T18:45:23Z"
        alreadyUploadFileNum: 1,
        checkFinish:false,
        circleTitle: '',
        circleDesc: '',
      },

      cancelHint:'',
      showCancel:false,
      showConfirmModal: false
    }
  },
  created() {
    //console.log("backupRunning.vue created")
    window.vue.receiveProgressData = this.receiveProgressData;
    window.vue.receiveNotNeedBackup = this.receiveNotNeedBackup;
    window.vue.receiveServerAlreadyLatest = this.receiveServerAlreadyLatest;
  },
  mounted() {
    window.scrollTo(0, 0);
    //console.log("backupRunning.vue startBackup")
    window.Android.startBackup();

  },
  computed: {


    circleTitle(){

      if (this.$store.state.backupStatus === 1){

        if (this.progressData.checkFinish){
         return  '备份中'
        }else {
          return '准备中'
        }
      }else {
        return '已暂停';
      }

    },

    circleDesc(){

      if (this.$store.state.backupStatus === 1){

        if (this.progressData.checkFinish){
          return  '请不要关闭应用或断开网络'
        }else {
          return '请稍后，正在检查哪些文件需要备份...'
        }
      }else {
        return '点击下方按钮继续备份';
      }

    },
  },
  methods: {

    receiveServerAlreadyLatest(){
      this.cancelHint = '经过与服务器的比较，您的文件与服务器文件完全一致，无需进行备份，本次备份不会进行';
      this.showCancel = true;

    },

    receiveNotNeedBackup(){
      this.cancelHint = '扫描后没有发现需要备份的文件，本次备份不会进行';
      this.showCancel = true;
    },

    handleReturnHome(){
      this.$store.commit("SET_BACKUP_STATUS",0);
      this.showCancel = false;
    },


    resumeBackup() {
      this.$store.commit('SET_BACKUP_STATUS', 1)
      window.Android.resumeBackup();
    },
    pauseBackup() {
      this.$store.commit('SET_BACKUP_STATUS', 2)
      window.Android.pauseBackup();
    },
    completeBackup() {
      this.showConfirmModal = true;
      //console.log("准备结束")

    },
    handleConfirmComplete() {
      //console.log("修改状态")
      this.showConfirmModal = false;
      this.$store.commit('SET_BACKUP_STATUS', 0)
      window.Android.completeBackup();
    },
    handleCancelComplete() {
      this.showConfirmModal = false;
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
    formatRelativeTime(isoDate) {
      // 解析输入的 ISO 时间
      const inputTime = new Date(isoDate).getTime();

      // 获取当前时间
      const currentTime = new Date().getTime();

      // 计算时间差（毫秒）
      let diff = Math.abs(currentTime - inputTime);

      // 计算小时、分钟和秒
      const hours = Math.floor(diff / (1000 * 60 * 60));
      diff -= hours * (1000 * 60 * 60);

      const minutes = Math.floor(diff / (1000 * 60));
      diff -= minutes * (1000 * 60);

      const seconds = Math.floor(diff / 1000);

      // 格式化输出
      return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;


    },
    formatSize(size) {
      if (size < 1024) return size + ' B';
      else if (size < 1024 * 1024) return (size / 1024).toFixed(1) + ' KB';
      else if (size < 1024 * 1024 * 1024)
        return (size / (1024 * 1024)).toFixed(1) + ' MB';
      else return (size / (1024 * 1024 * 1024)).toFixed(1) + ' GB';
    },
    getGoogleIconNameFromMimeType(mimeType) {
      const mimeTypeMap = {
        // 文件夹
        'vnd.android.document/directory': 'folder',

        // 文本文件
        'text/plain': 'description',
        'text/html': 'code',
        'text/css': 'code',
        'text/javascript': 'code',
        'application/json': 'code',

        // PDF
        'application/pdf': 'picture_as_pdf',

        // 图片
        'image/*': 'image',

        // 视频
        'video/*': 'video_file',

        // 音频
        'audio/*': 'audio_file',

        // Word 文档
        'application/msword': 'article',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'article',

        // Excel 表格
        'application/vnd.ms-excel': 'table_chart',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'table_chart',

        // 压缩包
        'application/zip': 'archive',
        'application/x-rar-compressed': 'archive',
        'application/x-7z-compressed': 'archive',
        'application/x-tar': 'archive',
        'application/gzip': 'archive',

        // 可执行文件 / 未知
        'application/octet-stream': 'insert_drive_file',
        'application/x-executable': 'dangerous',
        'application/vnd.android.package-archive': 'android',
      };

      // 精确匹配
      if (mimeTypeMap[mimeType]) {
        return mimeTypeMap[mimeType];
      }

      // 通配符匹配 image/*, video/* 等
      const wildcardKey = Object.keys(mimeTypeMap).find(key => key.endsWith('/*') && mimeType.startsWith(key.split('/*')[0]));
      if (wildcardKey) {
        return mimeTypeMap[wildcardKey];
      }

      // console.log(`${mimeType} 没有对应图标 `)

      // 默认图标
      return 'insert_drive_file';
    },
    receiveProgressData(progressInfo) {
      // this.totalPercent = progressInfo.totalPercent;
      // this.alreadyUploadFileSize = progressInfo.alreadyUploadFileSize;
      // this.currentFile = progressInfo.currentFile;
      // this.speed = progressInfo.speed;
      // this.startTime = progressInfo.startTime;
      // this.alreadyUploadFileNum = progressInfo.alreadyUploadFileNum;
      // this.circleTitle = progressInfo.


      this.progressData = progressInfo;
    },
  }
}
</script>
<style scoped>
/* 内容区域 */
.content {
  padding: 24px 16px;
  max-width: 500px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 进度卡片 */
.progress-card {
  background: white;
  border-radius: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  width: 100%;
  padding: 32px;
  margin-bottom: 24px;
  text-align: center;
}

.progress-title {
  font-size: 22px;
  font-weight: 500;
  color: #202124;
  margin-bottom: 8px;
}

.progress-subtitle {
  font-size: 16px;
  color: #5f6368;
  margin-bottom: 32px;
}


.progress-text {
  font-size: 14px;
  color: #5f6368;
}

/* 文件信息 */
.file-info {
  background: #f8f9fa;
  border-radius: 16px;
  padding: 16px;
  width: 100%;
  margin-bottom: 24px;
  text-align: left;
}

.file-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.file-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #e8f0fe;
  color: #1a73e8;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.file-details {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-weight: 500;
  font-size: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}

.file-path {
  font-size: 14px;
  color: #5f6368;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding-top: 10px;
}

.file-progress-container {
  height: 4px;
  background: #e8eaed;
  border-radius: 2px;
  overflow: hidden;
}

.file-progress-bar {
  height: 100%;
  width: 45%;
  background: #1a73e8;
  border-radius: 2px;
}

/* 统计信息 */
.stats-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  width: 100%;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.stat-value {
  font-size: 20px;
  font-weight: 500;
  color: #1a73e8;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #5f6368;
}

/* 按钮区域 */
.button-container {
  display: flex;
  gap: 16px;
  width: 100%;
  margin-top: 8px;
  justify-content: space-evenly;
}

.button {
  border-radius: 24px;
  padding: 16px 28px;
  font-size: 16px;
  font-weight: 500;
  text-align: center;
  cursor: pointer;
  border: none;
  transition: all 0.2s;

}

.pause-button {
  background: #f8f9fa;
  color: #1a73e8;
  border: 1px solid #dadce0;
}

.pause-button:hover {
  background: #e8f0fe;
}

.cancel-button {
  background: #fef0f0;
  color: #ea4335;
  border: 1px solid #fad2cf;
}

.cancel-button:hover {
  background: #fce8e6;
}

/* 网络状态 */
.network-status {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 24px;
  color: #5f6368;
  font-size: 14px;
}

.network-icon {
  margin-right: 8px;
  color: #34a853;
}

/* 动画 */
@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.02);
    opacity: 0.9;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.pulse {
  animation: pulse 2s infinite;
}


</style>