<template>
  <transition name="dialog-fade">
    <div v-if="visible" class="dialog-overlay" @click.self="handleOverlayClick">
      <div class="dialog-container" :style="{ width: width }">
        <!-- 标题区域 -->
        <div class="dialog-header">
          <div class="dialog-title">
            <i v-if="icon" class="material-icons">{{ icon }}</i>
            <span>{{ title }}</span>
          </div>
          <button v-if="closable" class="dialog-close" @click="handleClose">
            <i class="material-icons">close</i>
          </button>
        </div>

        <!-- 内容区域 -->
        <div class="dialog-content" :class=" {'no-padding': !contentPadding}">
          <slot></slot>
        </div>

        <!-- 操作按钮区域 -->
        <div class="dialog-actions">
          <button
              v-if="showCancel"
              class="dialog-button cancel-button"
              @click="handleCancel"
          >
            {{ cancelText }}
          </button>
          <button
              class="dialog-button confirm-button"
              @click="handleConfirm"
              :disabled="confirmDisabled"
          >
            {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'MaterialDialog',
  props: {
    contentPadding:{
      type: Boolean,
      default: true
    },
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '提示'
    },
    icon: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: '500px'
    },
    confirmText: {
      type: String,
      default: '确定'
    },
    cancelText: {
      type: String,
      default: '取消'
    },
    showCancel: {
      type: Boolean,
      default: true
    },
    closable: {
      type: Boolean,
      default: true
    },
    closeOnClickOverlay: {
      type: Boolean,
      default: true
    },
    confirmDisabled: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    handleClose() {
      this.$emit('update:visible', false)
      this.$emit('close')
    },
    handleConfirm() {
      this.$emit('confirm')
    },
    handleCancel() {
      this.$emit('cancel')
    },
    handleOverlayClick() {
      if (this.closeOnClickOverlay) {
        this.handleClose()
      }
    }
  }
}
</script>

<style scoped>
/* 引入 Material Icons */
@import url('https://fonts.googleapis.com/icon?family=Material+Icons');

/* 过渡动画 */
.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: opacity 0.3s;
}
.dialog-fade-enter,
.dialog-fade-leave-to {
  opacity: 0;
}

/* 遮罩层 */
.dialog-overlay {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 1000;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 对话框容器 */
.dialog-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 11px 15px -7px rgba(0, 0, 0, 0.2),
  0 24px 38px 3px rgba(0, 0, 0, 0.14), 0 9px 46px 8px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  max-width: 90%;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

/* 标题区域 */
.dialog-header {
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

.dialog-title {
  font-size: 1.25rem;
  font-weight: 500;
  letter-spacing: 0.02em;
  display: flex;
  align-items: center;
}

.dialog-title .material-icons {
  margin-right: 16px;
  color: #1a73e8;
}

.dialog-close {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  margin: -8px;
  color: rgba(0, 0, 0, 0.54);
  transition: color 0.2s;
}

.dialog-close:hover {
  color: rgba(0, 0, 0, 0.87);
}

/* 内容区域 */
.dialog-content {
  padding: 20px 24px;
  flex: 1;
  overflow-y: auto;
  line-height: 2;
}

.no-padding{
  padding: 0;
}


/* 操作按钮区域 */
.dialog-actions {
  padding: 8px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid rgba(0, 0, 0, 0.12);
}

.dialog-button {
  min-width: 64px;
  padding: 8px 16px;
  margin: 0 8px;
  border: none;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background-color 0.2s, box-shadow 0.2s;
}

.dialog-button:focus {
  outline: none;
}

.cancel-button {
  color: #1a73e8;
  background-color: transparent;
}

.cancel-button:hover {
  background-color: rgba(26, 115, 232, 0.04);
}

.confirm-button {
  color: white;
  background-color: #1a73e8;
  box-shadow: 0 2px 4px -1px rgba(0, 0, 0, 0.2),
  0 4px 5px 0 rgba(0, 0, 0, 0.14), 0 1px 10px 0 rgba(0, 0, 0, 0.12);
}

.confirm-button:hover {
  background-color: #1765cc;
  box-shadow: 0 3px 5px -1px rgba(0, 0, 0, 0.2),
  0 6px 10px 0 rgba(0, 0, 0, 0.14), 0 1px 18px 0 rgba(0, 0, 0, 0.12);
}

.confirm-button:disabled {
  background-color: rgba(0, 0, 0, 0.12);
  color: rgba(0, 0, 0, 0.26);
  box-shadow: none;
  cursor: not-allowed;
}
</style>