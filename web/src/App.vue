<template>
  <div id="app">
    <Header :style="{
      paddingTop: statusBarHeight+'px'
    }"></Header>
    <div style="padding-bottom: 100px;padding-top: 60px;  ">
      <router-view></router-view>
    </div>

    <Footer v-show="!$route.meta.hideFooter"></Footer>
  </div>
</template>

<script>

import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import {mapMutations} from "vuex";

export default {
  name: 'App',
  components: {
    Header, Footer
  },
  data() {

    return {
      statusBarHeight: 45
    }
  },
  created() {
    console.log("当前状态="+window.Android.getStatus());
    this.$store.commit("SET_BACKUP_STATUS", window.Android.getStatus());



    this.statusBarHeight = window.Android.getStatusBarHeight();
    this.initDataFromAndroid();
  },
  mounted() {
    window.vue.onAppBackPressed = this.onAppBackPressed;
    window.vue.receiveBackupStatus = this.receiveBackupStatus;
  },
  methods: {
    ...mapMutations(['SET_SERVER_CONFIG', 'SET_SELECTED_DIR']),

    receiveBackupStatus(status) {
      console.log("收到android的备份状态="+status)
      // window.Android.toast("收到android的备份状态="+status);
      this.$store.commit("SET_BACKUP_STATUS", status);
    },
    /**
     * 向android请求数据然后存入vuex
     */
    initDataFromAndroid() {
      this.getServerConfig();
      this.getSelectDir();
    },


    getServerConfig() {
      let config = JSON.parse(window.Android.getServerConfig());
      // console.log("App getServerConfig=", config)
      this.SET_SERVER_CONFIG(config);
    },

    getSelectDir() {
      let arr = JSON.parse(window.Android.getSelectDir());
      // console.log("App getSelectDir=", arr)
      this.SET_SELECTED_DIR(arr)
    },

    onAppBackPressed() {
      // console.log("app 发送返回信号")
      this.$bus.$emit("onAppBackPressed")
    }
  },
}
</script>

<style>

html body {
  height: 100%;
}

#app {

  height: 100%;
}

body {
  font-family: 'Roboto', sans-serif;
  background-color: #f5f5f5;
  color: #333;
  /*min-height: 100vh;*/
  padding: 0;

}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  -webkit-tap-highlight-color: transparent;
}

button {
  outline: none; /* 去除默认的焦点轮廓 */
  -webkit-tap-highlight-color: transparent; /* 去除点击时的高亮颜色 */
}
</style>
