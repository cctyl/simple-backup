<!-- src/components/ConfirmModal.vue -->
<template>
  <transition name="modal-fade">
    <div class="modal-overlay" v-if="visible" @click="closeModal">
      <transition name="modal-scale" @click.stop>
        <div class="modal-content" v-if="visible" @click.stop>
          <div class="modal-header">
            <div class="modal-icon">
              <i class="material-icons warning-icon">warning</i>
            </div>
            <h2 class="modal-title">{{ title }}</h2>
          </div>

          <div class="modal-body">
            <p class="modal-message">{{ message }}</p>
          </div>

          <div class="modal-footer">
            <button class="modal-button cancel-button" @click="cancelAction">
              {{ cancelText }}
            </button>
            <button class="modal-button confirm-button" @click="confirmAction">
              {{ confirmText }}
            </button>
          </div>
        </div>
      </transition>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'confirm-modal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '确认操作'
    },
    message: {
      type: String,
      default: '您确定要执行此操作吗？'
    },
    confirmText: {
      type: String,
      default: '确认'
    },
    cancelText: {
      type: String,
      default: '取消'
    }
  },
  methods: {
    closeModal() {
      this.$emit('cancel');
    },
    confirmAction() {
      this.$emit('confirm');
    },
    cancelAction() {
      this.$emit('cancel');
    }
  }
}
</script>

<style scoped>
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.225s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-scale-enter-active,
.modal-scale-leave-active {
  transition: transform 0.225s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-scale-enter-from {
  transform: scale(0.8);
}

.modal-scale-leave-to {
  transform: scale(0.8);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.32);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 28px;
  width: 90%;
  max-width: 360px;
  box-shadow:
    0 11px 15px -7px rgba(0, 0, 0, 0.2),
    0 24px 38px 3px rgba(0, 0, 0, 0.14),
    0 9px 46px 8px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 32px 24px 16px;
  text-align: center;
}

.modal-icon {
  margin-bottom: 16px;
}

.warning-icon {
  font-size: 56px;
  color: #fb8c00;
  width: auto;
  height: auto;
}

.modal-title {
  font-size: 24px;
  font-weight: 500;
  color: #202124;
  margin: 0;
  line-height: 1.33;
}

.modal-body {
  padding: 0 24px 24px;
  text-align: center;
  flex: 1;
}

.modal-message {
  font-size: 16px;
  color: #5f6368;
  line-height: 1.5;
  margin: 0;
}

.modal-footer {
  display: flex;
  padding: 8px;
  justify-content: flex-end;
  gap: 12px;
}

.modal-button {
  border-radius: 50px;
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  min-width: 80px;
  text-transform: uppercase;
  letter-spacing: 1.25px;
  transition: all 0.15s ease;
}

.cancel-button {
  background: transparent;
  color: #1a73e8;
}

.cancel-button:hover {
  background: rgba(26, 115, 232, 0.08);
}

.confirm-button {
  background: transparent;
  color: #1a73e8;
}

.confirm-button:hover {
  background: rgba(26, 115, 232, 0.08);
}
</style>
