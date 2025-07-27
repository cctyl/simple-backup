<template>
  <!-- 内容区域 -->
  <div class="content">
    <!-- 搜索栏 -->
    <div class="search-container">
      <i class="material-icons">search</i>
      <input type="text" placeholder="搜索文件夹..." v-model="filter">
    </div>


    <SelectFolder :filter="filter" title="已选文件夹" :update="true"></SelectFolder>
    <!-- 底部操作栏 -->
    <div class="action-bar">

      <router-link to="/settings/source/select" tag="button" class="action-button save-button" v-if="hasPermission">
        添加文件夹
      </router-link>
      <button class="action-button  cancel-button" @click="saveSelectDir" :disabled="!hasPermission"
              v-if="hasPermission">保存设置
      </button>
      <button class="action-button authorization-button"
              :class="{secondary: hasPermission}"
              @click="applyPermission">
        {{ hasPermission ? '重新选择根目录' : '请授权app访问目录' }}
      </button>

    </div>
  </div>
</template>
<script>

import SelectFolder from "@/components/SelectFolder.vue";
import {mapActions} from "vuex";

export default {
  name: 'backup-source-view',
  components: {SelectFolder},
  data() {
    return {
      filter: '',
      hasPermission: false,
    }
  },
  created() {

  },
  mounted() {
    window.vue.checkPermission = this.checkPermission;
    this.$bus.$on('onAppBackPressed', this.onAppBackPressed);
    console.log("backup mounted")
    this.checkPermission();
  },
  methods: {
    ...mapActions(['setSelectedDir']),
    saveSelectDir() {
      this.setSelectedDir(this.$store.state.selectedDir)
    },
    checkPermission() {
      this.hasPermission = Boolean(window.Android.getDirUri());
    },
    applyPermission() {

      window.Android.applyPermission(this.hasPermission);
    },
    onAppBackPressed(){
      this.saveSelectDir()
      this.$router.back();
    },

  },
  beforeDestroy() {
    this.$bus.$off(['onAppBackPressed']);
    this.saveSelectDir();
  }
}
</script>
<style scoped>


.authorization-button {
  background: linear-gradient(45deg, #4CAF50, #8BC34A);
  color: white;
  box-shadow: 0 2px 4px rgba(76, 175, 80, 0.3);
  border: none;
  font-weight: 600;
}

.authorization-button:hover {
  background: linear-gradient(45deg, #43A047, #7CB342);
  box-shadow: 0 4px 8px rgba(76, 175, 80, 0.4);
}

.authorization-button.secondary {
  background: #f1f3f4;
  color: #5f6368;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  font-weight: normal;
}

.authorization-button.secondary:hover {
  background: #e8eaed;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.authorization-button {
  background: linear-gradient(45deg, #4CAF50, #8BC34A);
  color: white;
  box-shadow: 0 2px 4px rgba(76, 175, 80, 0.3);
  border: none;
  font-weight: 600;
}

.authorization-button:hover {
  background: linear-gradient(45deg, #43A047, #7CB342);
  box-shadow: 0 4px 8px rgba(76, 175, 80, 0.4);
}

/* 搜索栏 */
.search-container {
  background: white;
  border-radius: 24px;
  padding: 8px 16px;
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.search-container i {
  color: #5f6368;
  margin-right: 8px;
}

.search-container input {
  flex: 1;
  border: none;
  background: none;
  font-size: 16px;
  padding: 8px 0;
  outline: none;
}

/* 底部操作栏 */
.action-bar {


  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.action-button {
  margin-bottom: 12px;
  flex: 1;
  border-radius: 24px;
  padding: 14px;
  font-size: 16px;
  font-weight: 500;
  text-align: center;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.cancel-button {
  background: #e8f0fe;
  color: #1a73e8;
  border: 1px solid #dadce0;
}

.save-button {
  background: #1a73e8;
  color: white;
  box-shadow: 0 2px 4px rgba(26, 115, 232, 0.3);
}


/* 已选文件夹 */
.selected-folders {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
  margin-bottom: 24px;
}

.selected-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  border-bottom: 1px solid #f1f3f4;
  padding: 10px;
  padding-top: 0px;

}

.selected-title {
  font-weight: 500;
  font-size: 16px;
  color: #202124;
}

.selected-count {
  color: #5f6368;
  font-size: 14px;
}

.selected-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.selected-item {
  background: #f1f3f4;
  border-radius: 24px;
  padding: 8px 12px;
  display: flex;
  align-items: center;
  flex-shrink: 0;
  margin: 5px;
}

.selected-name {
  margin-right: 8px;
  font-size: 14px;
  color: #202124;
}

.selected-remove {
  color: #5f6368;
  font-size: 18px;
  cursor: pointer;
}


/* 内容区域 */
.content {
  padding: 16px;
  margin: 0 auto;
}


</style>
