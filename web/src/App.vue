<template>
  <div id="app">
    <Header></Header>
    <div style="padding-bottom: 100px;padding-top: 60px;  ">
      <router-view></router-view>
    </div>

    <Footer ></Footer>
  </div>
</template>

<script>

import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
export default {
  name: 'App',
  components: {
  Header,Footer
  }
}
</script>

<style>
html body {
  height: 100%;
}
#app{
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
button{
  outline: none; /* 去除默认的焦点轮廓 */
  -webkit-tap-highlight-color: transparent; /* 去除点击时的高亮颜色 */
}
</style>
