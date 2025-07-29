<template>
  <!-- 内容区域 -->
  <div class="content">
    <!-- 服务器设置卡片 -->
    <div class="settings-card">
      <div class="card-header">服务器连接设置(或右上角扫码)</div>
      <div class="card-body">
        <div class="form-group">
          <label class="form-label">服务器地址</label>
          <div class="input-container">
            <span class="input-icon material-icons">dns</span>
            <input
              type="text"
              class="form-input"
              :class="{ 'input-error': validationErrors.addr }"
              placeholder="192.168.1.25"
              v-model="addr">
          </div>
          <div class="form-hint">
            请输入完整的服务器ip（192.168.1.1）
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">访问令牌</label>
          <div class="input-container">
            <span class="input-icon material-icons">vpn_key</span>
            <input
              :type="showPassword ? 'text' : 'password'"
              class="form-input"
              :class="{ 'input-error': validationErrors.accessToken }"
              placeholder="输入您的访问令牌"
              v-model="secret">
            <span class="password-toggle material-icons" @click="togglePasswordVisibility">
              {{ showPassword ? 'visibility_off' : 'visibility' }}
            </span>
          </div>
          <div class="form-hint">
            在服务端配置的密码，<a href="#">如何获取密码？</a>
          </div>
        </div>

        <div class="button-group">
          <button class="button test-button" @click="checkConnection">
            <i class="material-icons" style="vertical-align: middle; margin-right: 5px;">wifi_tethering</i>
            测试连接
          </button>
          <button class="button save-button" @click="saveConfig">
            <i class="material-icons" style="vertical-align: middle; margin-right: 5px;">save</i>
            保存设置
          </button>
        </div>

        <div class="connection-status "
             :class="{'status-error':!checkSuccess, 'status-success':checkSuccess}"
             v-if="showTestResult">
          <div class="status-icon material-icons">{{checkSuccess?'check_circle':'close'}}</div>
          <div class="status-text">
            {{ testMsg }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapActions, mapMutations} from "vuex";

export default {
  name: 'server-config-view',
  data() {
    return {
      showPassword: false,
      secret: '',
      addr: '',
      showTestResult: false,
      testMsg: '',
      validationErrors: {
        addr: false,
        accessToken: false
      },

      checkSuccess: false,
    }
  },

  mounted() {
    this.addr = this.$store.state.serverConfig.addr;
    this.secret = this.$store.state.serverConfig.secret;
    this.$bus.$on('rightIconCallBack', this.scanQrCode);
    window.vue.receiveServerConfig = this.receiveServerConfig;
  },
  methods: {
    ...mapActions(['setServerConfig']),
    ...mapMutations(['SET_SERVER_CONFIG']),
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword;
    },
    checkConnection() {
      this.testMsg = ' 连接成功！';
      this.showTestResult = true;
      this.checkSuccess=true;
    },
    saveConfig() {
      // 重置验证错误状态
      this.validationErrors.addr = false;
      this.validationErrors.accessToken = false;

      let hasError = false;

      if (!this.addr) {
        this.validationErrors.addr = true;
        hasError = true;
      }

      if (!this.secret) {
        this.validationErrors.accessToken = true;
        hasError = true;
      }

      if (hasError) {
        this.testMsg = ' 请填写完整的设置！';
        this.showTestResult = true;
        this.checkSuccess=false;
        return;
      }

      this.setServerConfig({
        addr: this.addr,
        secret: this.secret
      })


      this.testMsg = ' 保存设置成功！';

      this.checkSuccess=true;
      this.showTestResult = true;

    },
    scanQrCode(){
      window.Android.startScan();
    },

    /**
     * 接收结果
     * @param config
     */
    receiveServerConfig(config){

      //console.log("ServerConfig receiveServerConfig=", config)
      this.SET_SERVER_CONFIG( config)
      this.addr = config.addr;
      this.secret = config.secret;
    }


  },
  beforeDestroy() {
    this.$bus.$off(['rightIconCallBack'])
  }
}
</script>

<style scoped>
/* 内容区域 */
.content {
  padding: 24px 16px;
  margin: 0 auto;
}

/* 设置卡片 */
.settings-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
  overflow: hidden;
}

.card-header {
  padding: 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #f1f3f4;
  font-weight: 500;
  font-size: 16px;
  color: #202124;
}

.card-body {
  padding: 16px;
}

/* 表单组 */
.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  font-size: 16px;
  color: #202124;
}

.input-container {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #5f6368;
}

.form-input {
  width: 100%;
  padding: 16px 16px 16px 48px;
  border: 1px solid #dadce0;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.2s;
  background: white;
}

.form-input:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.2);
}

.form-input.input-error {
  border-color: #ea4335;
  box-shadow: 0 0 0 2px rgba(234, 67, 53, 0.2);
}

.form-input.input-error:focus {
  border-color: #ea4335;
  box-shadow: 0 0 0 2px rgba(234, 67, 53, 0.2);
}

.form-hint {
  margin-top: 8px;
  font-size: 14px;
  color: #5f6368;
  line-height: 1.5;
}

.form-hint a {
  color: #1a73e8;
  text-decoration: none;
}

/* 密码切换按钮 */
.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #5f6368;
  cursor: pointer;
}

/* 按钮组 */
.button-group {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

.button {
  flex: 1;
  border-radius: 24px;
  padding: 16px;
  font-size: 16px;
  font-weight: 500;
  text-align: center;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.test-button {
  background: #e8f0fe;
  color: #1a73e8;
  border: 1px solid #dadce0;
}

.test-button:hover {
  background: #d2e3fc;
}

.save-button {
  background: #1a73e8;
  color: white;
  box-shadow: 0 2px 4px rgba(26, 115, 232, 0.3);
}

.save-button:hover {
  background: #1967d2;
}

/* 连接状态 */
.connection-status {
  display: flex;
  align-items: center;
  padding: 16px;
  border-radius: 12px;
  margin-top: 24px;
}

.status-success {
  background: #e6f4ea;
  color: #34a853;
}

.status-error {
  background: #fce8e6;
  color: #ea4335;
}

.status-icon {
  margin-right: 12px;
  font-size: 24px;
}

.status-text {
  flex: 1;
  font-size: 16px;
}

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

/* 底部导航 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  display: flex;
  justify-content: space-around;
  padding: 8px 0;
  box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.nav-item {
  text-align: center;
  padding: 8px;
  color: #5f6368;
  font-size: 12px;
  flex: 1;
}

.nav-item.active {
  color: #1a73e8;
}

.nav-icon {
  font-size: 24px;
  margin-bottom: 4px;
}
</style>
