<template>

  <!-- 应用栏 -->
  <div class="app-bar">
    <span class="material-icons" @click="iconCallBack">{{icon}}</span>
    <div class="title">
      {{ $store.state.headerTitle? $store.state.headerTitle:currentTitle}}

<!--      /Android/data/com.tencent.mm/.fileexplorer/aa/bb/cc/dd/ee/ff/bb/cc/dd/ee/ff/bb/cc/dd/ee/ff-->
    </div>
    <div style="flex: 1;"></div>
    <span class="material-icons" @click="clickSync" style="margin-left: 16px; "

    :style="{
      animation: spin?'spin 1s linear infinite':''
    }"
    >settings_backup_restore</span>
  </div>


</template>

<script>
export default {
  name: 'header-view',
  data() {
    return {
      spin: false,
      currentTitle:'',
      icon:'',

    }
  },
  watch: {
    '$route'() {
      this.$store.commit("SET_HEADER_TITLE",null);
      this.refresh()
    }
  },
  mounted() {
   this.refresh()
  },
  methods:{

    iconCallBack(){
      console.log('iconCallBack')
      this.$bus.$emit("iconCallBack")
    },
    refresh(){
      this.currentTitle = this.$route.meta.headerTitle || '默认标题'
      this.icon= this.$route.meta.icon || 'cloud_done'

    },
    clickSync() {
      this.spin = true;

      setTimeout(() => {
        this.spin = false;
      },1000)
    },

  }
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
  width: 80%;
  overflow: hidden;
  font-size: 20px;
  font-weight: 500;
  margin-left: 16px;
}

</style>