<template>
  <!-- 已选文件夹 -->
  <div class="selected-folders">
    <div class="selected-header ">
      <div class="selected-title">{{ title }}</div>
      <div class="selected-count" v-if="showCount">共{{ this.selectedDir.length }}个</div>
      <div class="selected-count" v-else>
        <span class="material-icons" style="color: #5f6368; ">drive_folder_upload</span>
      </div>
    </div>
    <div class="selected-list" v-if="  selectedDir.length>0">

      <div class="selected-item" v-for="item in selectedDir" :key="item.relativePath">
        <div class="selected-name">{{ item.relativePath }}</div>
        <i v-if="update" class="material-icons selected-remove" @click="removeDir(item)">close</i>
      </div>

    </div>
    <div v-else style="padding: 10px;color: #5f6368;font-size: 14px">
      你还没有添加文件夹，快去添加吧
    </div>


  </div>

</template>
<script>
import {mapActions} from "vuex";

export default {
  name: 'select-folder-view',
  components: {},
  props: {
    showCount: Boolean,
    update: Boolean,
    title: String,
  },
  data() {
    return {
      selectedDir: [],

    }
  },
  mounted() {
    this.selectedDir = this.$store.state.selectedDir;
  },
  methods: {

    ...mapActions(['setAddr','setSecret']),

    removeDir(dir) {
      this.selectedDir = this.selectedDir.filter(item => item.relativePath !== dir.relativePath);
      this.setSelectedDir(this.selectedDir);
    }
  }
}
</script>
<style scoped>


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

</style>
