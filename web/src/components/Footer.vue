<template>

  <!-- 底部导航 -->
  <div class="bottom-nav" >


    <router-link to="/home" class="nav-item " active-class="active" replace>
      <div class="nav-icon material-icons">home</div>
      <div>首页</div>
    </router-link>

    <router-link to="/history" class="nav-item " active-class="active" replace>
      <div class="nav-icon material-icons">history</div>
      <div>历史</div>
    </router-link>

    <router-link to="/settings" class="nav-item " active-class="active" replace>
      <div class="nav-icon material-icons">settings</div>
      <div>设置</div>
    </router-link>
  </div>

</template>

<script>
export default {
  name: 'footer-view'
}

</script>

<style scoped>

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