<template>


  <!-- 内容区域 -->
  <div class="transition-container">
    <AnimatedTransition>
      <BackupReady v-if="$store.state.backupStatus===0" :key="0"></BackupReady>
      <BackupRunning v-else :key="1"></BackupRunning>
    </AnimatedTransition>
  </div>


</template>

<script>

import BackupReady from "./home/BackupReady";
import BackupRunning from "./home/BackupRunning";
import AnimatedTransition from "@/components/AnimatedTransition.vue";

export default {
  name: 'home-view',
  components: {AnimatedTransition, BackupReady, BackupRunning},
  data() {
    return {
      running: true
    }
  },
  created() {

  },
  mounted() {


  },

  computed: {},
  methods: {}
}
</script>

<style scoped>
.transition-container {
  position: relative;
  min-height: 500px; /* 根据实际内容调整 */
}
</style>