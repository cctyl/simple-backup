<template>


  <!-- 内容区域 -->
  <div class="transition-container">
    <AnimatedTransition>
      <BackupRunning v-if="$store.state.backupStatus !== 0" :key="1"></BackupRunning>
      <BackupReady v-else  :key="0"></BackupReady>
    </AnimatedTransition>


    <MaterialDialog
        :visible="showCancel"
        :title="title"
        :icon="icon"
        :show-cancel="false"
        @confirm="handleReturnHome"
    >
      <p>

        {{ cancelHint }}

      </p>
    </MaterialDialog>
  </div>


</template>

<script>

import BackupReady from "./home/BackupReady";
import BackupRunning from "./home/BackupRunning";
import AnimatedTransition from "@/components/AnimatedTransition.vue";
import MaterialDialog from "@/components/Dialog.vue";

export default {
  name: 'home-view',
  components: {MaterialDialog, AnimatedTransition, BackupReady, BackupRunning},
  data() {
    return {
      running: true,
      cancelHint: '',
      showCancel: false,
      title:'温馨提示',
      icon:'notifications_active'

    }
  },
  created() {

  },
  mounted() {
    window.vue.receiveNotNeedBackup = this.receiveNotNeedBackup;
    window.vue.receiveServerAlreadyLatest = this.receiveServerAlreadyLatest;
    window.vue.receiveUploadError = this.receiveUploadError;

  },

  computed: {},
  methods: {

    receiveUploadError(msg) {
      this.title = '出现错误！'
      this.cancelHint = '备份错误：'+msg;
      this.icon='error';
      this.showCancel = true;

    },
    receiveServerAlreadyLatest() {
      this.title = '温馨提示'
      this.cancelHint = '经过与服务器的比较，您的文件与服务器文件完全一致，无需进行备份，本次备份不会进行';
      this.icon = 'notifications_active';
      this.showCancel = true;

    },

    receiveNotNeedBackup() {
      this.title = '温馨提示'
      this.cancelHint = '扫描后没有发现需要备份的文件，本次备份不会进行';
      this.icon = 'notifications_active';
      this.showCancel = true;
    },

    handleReturnHome() {
      this.showCancel = false;
    },


  }
}
</script>

<style scoped>
.transition-container {
  position: relative;
  min-height: 500px; /* 根据实际内容调整 */
}
</style>