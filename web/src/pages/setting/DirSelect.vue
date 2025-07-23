<template>
  <!-- 内容区域 -->
  <div class="content">


    <!-- 文件树容器 -->
    <div class="file-tree-container">

      <!-- 文件树列表 -->
      <div class="file-tree">

        <!-- 根目录 -->
        <div class="tree-item level-0">
          <div class="tree-icon folder">
            <i class="material-icons">folder</i>
          </div>
          <div class="tree-content">
            <div class="tree-name">上级目录</div>
          </div>

          <div class="tree-expand">
            <i class="material-icons">expand_more</i>
          </div>
        </div>




        <!-- 一级目录 -->
        <div class="tree-item level-0"  v-for="(item, index) in files" :key="index">
          <div class="tree-icon folder">
            <i class="material-icons">folder</i>
          </div>
          <div class="tree-content">
            <div class="tree-name">{{ item.name }}</div>
          </div>
          <div class="tree-checkbox">
            <input type="checkbox" >
          </div>


          <div class="tree-expand">
            <i class="material-icons">chevron_right</i>
          </div>
        </div>

      </div>
    </div>


  </div>

</template>
<script>
export default {
  name: 'dir-select-view',

  data() {
    return {
      currentPath: null,

      root: null,
      files: [
        {
          name:"aaaaa",
        },
        {
          name:"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
        },
        {
          name:"ccc",
        },

        {
          name:"dddd",
        },
      ],
    }
  },
  created() {

    window.vue.receiveFileList = this.receiveFileList;
    window.vue.receiveRoot = this.receiveRoot;
  },
  mounted() {

    window.Android.init();
  },
  computed: {},
  methods: {
    receiveRoot(root) {
      this.root = root;
    },
    receiveFileList(dirList) {
      this.files = dirList;
    },
    toChild(item) {
      if (!this.currentPath) {
        this.currentPath = [this.root];
      } else {
        this.currentPath.push(item);
      }
      window.Android.intoChild(JSON.stringify(item))
    },
    intoParent() {


      if (this.currentPath && this.currentPath.length > 1) {

        this.currentPath.pop();
      }

      console.log(this.currentPath)

      if (!this.currentPath || this.currentPath.length < 1) {
        console.log("回到根目录")
        window.Android.intoParent(JSON.stringify(this.root));
      } else if (this.currentPath.length > 0) {
        let item = this.currentPath[this.currentPath.length - 1];
        item = JSON.stringify(item);

        console.log("返回：" + item)
        window.Android.intoParent(item)
      }

    },


  }

}
</script>
<style scoped>

/* 内容区域 */
.content {
  margin: 0 auto;
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

/* 文件树容器 */
.file-tree-container {
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 文件树标题 */
.tree-header {
  padding: 16px;
  border-bottom: 1px solid #f1f3f4;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tree-title {
  font-weight: 500;
  font-size: 16px;
  color: #202124;
}

.tree-action {
  color: #1a73e8;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

/* 文件树列表 */
.file-tree {
  overflow-y: auto;
  width: 100%;

}

.tree-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f3f4;
  width: 100%;
  justify-content: space-between;
}

.tree-item:last-child {
  border-bottom: none;
}

.tree-item.level-0 {
  padding-left: 16px;
}

.tree-item.level-1 {
  padding-left: 48px;
}

.tree-item.level-2 {
  padding-left: 80px;
}

.tree-item.level-3 {
  padding-left: 112px;
}

.tree-icon {
  color: #5f6368;
  margin-right: 16px;
  width: 24px;
  text-align: center;
}

.tree-icon.folder {
  color: #fbbc04;
}

.tree-content {
  display: flex;
  flex-direction: column;
  min-width: 0;
  flex: 1;
}

.tree-name {
  font-size: 16px;
  margin-bottom: 2px;
  overflow: hidden;
  word-wrap:break-word;
}

.tree-info {
  font-size: 14px;
  color: #5f6368;
}

.tree-checkbox {
  margin-left: 16px;
}

.tree-checkbox input[type="checkbox"] {
  width: 20px;
  height: 20px;
  accent-color: #1a73e8;
}



.custom-checkbox {
  position: relative;
  cursor: pointer;
  margin-left: 16px;
}

.custom-checkbox input {
  opacity: 0;
  width: 0;
  height: 0;
}

.checkmark {
  position: absolute;
  top: 0;
  left: 0;
  height: 20px;
  width: 20px;
  background-color: #eee;
  border-radius: 4px;
}

.checkmark:after {
  content: "";
  position: absolute;
  display: none;
  left: 7px;
  top: 3px;
  width: 5px;
  height: 10px;
  border: solid #1a73e8;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.custom-checkbox input:checked ~ .checkmark {
  background-color: #1a73e8;
}

.custom-checkbox input:checked ~ .checkmark:after {
  display: block;
}



.tree-expand {
  margin-left: 16px;
  color: #5f6368;
  cursor: pointer;
}

/* 设置卡片 */
.settings-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
  margin-bottom: 24px;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
}

.setting-label {
  display: flex;
  align-items: center;
}

.setting-label i {
  margin-right: 16px;
  color: #5f6368;
}

.setting-text {
  font-size: 16px;
}

.setting-switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
}

.setting-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.setting-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 34px;
}

.setting-slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .setting-slider {
  background-color: #1a73e8;
}

input:checked + .setting-slider:before {
  transform: translateX(24px);
}

/* 底部操作栏 */
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 90;
}

.action-button {
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
  background: #f8f9fa;
  color: #5f6368;
  margin-right: 12px;
}

.save-button {
  background: #1a73e8;
  color: white;
  box-shadow: 0 2px 4px rgba(26, 115, 232, 0.3);
}

</style>
