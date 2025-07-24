<template>
  <div v-show="show" class="loading-overlay">
    <div class="loading-spinner">
      <div class="spinner"></div> <!-- 简化结构 -->
    </div>
    <p class="loading-text" v-show="text">{{ text }}</p>
  </div>
</template>

<script>
export default {
  name: 'loading-view',
  props: {
    show: Boolean,
    text: String
  }
}
</script>

<style scoped>
/* 修复后的 Material Design 旋转动画 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  position: relative;
}

.spinner {
  width: 100%;
  height: 100%;
  border: 3px solid rgba(26, 115, 232, 0.2); /* 浅色底圈 */
  border-radius: 50%;
  border-top-color: #1a73e8; /* 主色调用于动画部分 */
  animation: spin 1s linear infinite;
}

.loading-text {
  margin-top: 16px;
  color: #1a73e8;
  font-family: 'Roboto', sans-serif;
  font-size: 14px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>