<template>

  <!-- 应用栏 -->
  <div class="app-bar">
    <span class="material-icons">cloud_done</span>
    <div class="title">云备份</div>
    <div style="flex: 1;"></div>
    <span class="material-icons" style="margin-left: 16px;">settings_backup_restore</span>
  </div>


</template>

<script>
export default {
  name: 'header-view'
}

</script>

<style scoped>
/* 应用栏 */
.app-bar {
  background-color: #1a73e8;
  color: white;
  padding: 16px;
  display: flex;
  align-items: center;
  position: fixed;
  top: 0px;
  left: 0;
  right: 0;
  z-index: 100;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  height: 56px;
}

.app-bar .title {
  font-size: 20px;
  font-weight: 500;
  margin-left: 16px;
}

</style>