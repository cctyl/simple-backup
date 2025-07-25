<template>
  <!-- 内容区域 -->
  <div class="content">


    <!-- 文件树容器 -->
    <div class="file-tree-container">

      <!-- 文件树列表 -->
      <div class="file-tree">

        <!-- 根目录 -->
        <div class="tree-item level-0" style="background-color: #f5f5f5;" @click="intoParent"

        >
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
        <div class="tree-item level-0"
             :style="{ backgroundColor: item.checked ? 'rgba(26, 115, 232, 0.1)' : 'white' }"

             v-for="item in files" :key="item.relativePath" @click="toChild(item)">
          <div class="tree-icon" :class="{
            folder: item.isDirectory,
          }">
            <i class="material-icons">{{ getGoogleIconNameFromMimeType(item.mimeType) }}</i>
          </div>
          <div class="tree-content">
            <div class="tree-name">{{ item.name }}</div>
          </div>
          <div class="tree-checkbox" v-if="item.isDirectory" @click.stop="select(item)">
            <input type="checkbox" :checked="item.checked">
          </div>


        </div>

      </div>
    </div>

    <Loading :show="showLoading" text="数据加载中..."/>
  </div>

</template>
<script>
import { mapMutations} from 'vuex'
import Loading from "@/components/Loading.vue";

export default {
  name: 'dir-select-view',
  components: {Loading},

  data() {
    return {
      showLoading: false,
      currentPath: null,
      root: null,
      files: [
        {
          name: "aaaaa",
          mimeType: "vnd.android.document/directory",
          isDirectory: true
        },
        {
          name: "bbb",
          mimeType: "vnd.android.document/directory"
        },
        {
          name: "ccc",
          mimeType: "vnd.android.document/directory"
        },
        {
          name: "dddd",
          mimeType: "vnd.android.document/directory"
        },
      ],
      selectedDir: [],
    }
  },
  created() {

    window.vue.receiveFileList = this.receiveFileList;
    window.vue.receiveRoot = this.receiveRoot;
  },
  mounted() {
    this.$bus.$on('onAppBackPressed', this.onAppBackPressed);
    this.$bus.$on('iconCallBack', this.intoParent);
    this.selectedDir = this.$store.state.selectedDir;
    window.Android.init();
  },
  watch: {
    currentPath() {
      //把数组：this.currentPath 用/ 拼接，得到一个字符串
      let result = '/';
      if (this.currentPath && this.currentPath.length > 0) {
        result = this.currentPath[this.currentPath.length - 1].relativePath
      }

      if (!result.startsWith('/')) {
        result = '/' + result
      }
      this.$store.commit('SET_HEADER_TITLE', result)
    }
  },
  computed: {},
  methods: {
    ...mapMutations(['SET_SELECTED_DIR']),
    isSelected(selectedDir, list) {

      //遍历list，然后判断list中元素的relativePath是否在selectedDir元素的relativePath字段相同，若相同则把list中这个元素加上一个字段checked
      list.forEach(item => {
        item.checked = selectedDir.some(i => i.relativePath === item.relativePath);
      })

    },
    getGoogleIconNameFromMimeType(mimeType) {
      const mimeTypeMap = {
        // 文件夹
        'vnd.android.document/directory': 'folder',

        // 文本文件
        'text/plain': 'description',
        'text/html': 'code',
        'text/css': 'code',
        'text/javascript': 'code',
        'application/json': 'code',

        // PDF
        'application/pdf': 'picture_as_pdf',

        // 图片
        'image/*': 'image',

        // 视频
        'video/*': 'video_file',

        // 音频
        'audio/*': 'audio_file',

        // Word 文档
        'application/msword': 'article',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'article',

        // Excel 表格
        'application/vnd.ms-excel': 'table_chart',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'table_chart',

        // 压缩包
        'application/zip': 'archive',
        'application/x-rar-compressed': 'archive',
        'application/x-7z-compressed': 'archive',
        'application/x-tar': 'archive',
        'application/gzip': 'archive',

        // 可执行文件 / 未知
        'application/octet-stream': 'insert_drive_file',
        'application/x-executable': 'dangerous',
        'application/vnd.android.package-archive': 'android',
      };

      // 精确匹配
      if (mimeTypeMap[mimeType]) {
        return mimeTypeMap[mimeType];
      }

      // 通配符匹配 image/*, video/* 等
      const wildcardKey = Object.keys(mimeTypeMap).find(key => key.endsWith('/*') && mimeType.startsWith(key.split('/*')[0]));
      if (wildcardKey) {
        return mimeTypeMap[wildcardKey];
      }

      // console.log(`${mimeType} 没有对应图标 `)

      // 默认图标
      return 'insert_drive_file';
    },
    receiveRoot(root) {
      this.root = root;
    },
    receiveFileList(dirList) {
      this.handleData(dirList)
    },

    scrollTop() {
      window.scrollTo(0, 0);
      // 或者如果需要滚动特定容器，可以使用:
      // this.$el.querySelector('.file-tree').scrollTop = 0;

    },
    handleData(dirList) {
      dirList.forEach(item => {
        item.checked = false;
      });
      this.files = dirList;
      this.isSelected(this.selectedDir, this.files);
    },

    select(item) {
      item.checked = !item.checked;

      if (item.checked) {
        //判断是否已经存在，不存在才添加
        if (this.selectedDir.some(i => i.relativePath === item.relativePath)) {
          return;
        }
        this.selectedDir.push(item);
      } else {
        this.selectedDir = this.selectedDir.filter(i => i.relativePath !== item.relativePath);
      }
      this.SET_SELECTED_DIR(this.selectedDir);
    },


    toChild(item) {

      if (!item.isDirectory) {
        return;
      }
      this.showLoading = true;


      setTimeout(() => {
        if (!this.currentPath) {
          this.currentPath = [this.root];
        } else {
          this.currentPath.push(item);
        }
        let dirList = JSON.parse(window.Android.intoChild(JSON.stringify(item)));
        // console.log(" toChild dirList length=" + dirList.length);
        this.handleData(dirList);
        this.scrollTop();
        if (dirList.length < 20) {
          // console.log("dirList length=" + dirList.length + ", 直接关闭 this.showLoading = "+this.showLoading)
          this.showLoading = false;
        } else {
          this.$nextTick(function () {
            // console.log("渲染后 直接关闭 this.showLoading = "+this.showLoading)
            this.showLoading = false;

          })
        }
      }, 50);
    },
    intoParent() {

      if (!this.currentPath || this.currentPath.length === 0) {
        this.$router.back();

      }
      this.showLoading = true;
      setTimeout(() => {

        if (this.currentPath && this.currentPath.length > 0) {
          this.currentPath.pop();
        }
        let dirList = [];
        if (!this.currentPath || this.currentPath.length < 1) {
          // console.log("回到顶级:" + this.root.relativePath)
          dirList = JSON.parse(window.Android.intoParent(JSON.stringify(this.root)));
          this.handleData(dirList)
        } else if (this.currentPath.length > 0) {
          let item = this.currentPath[this.currentPath.length - 1];
          // console.log("回到上一级:" + item.relativePath)
          item = JSON.stringify(item);
          dirList = JSON.parse(window.Android.intoParent(item))
          this.handleData(dirList)
        }
        this.scrollTop();
        if (dirList.length < 20) {
          // console.log("dirList length=" + dirList.length + ", 直接关闭")
          this.showLoading = false;
        } else {
          this.$nextTick(function () {
            this.showLoading = false;
          })
        }

      }, 50);
    },
    onAppBackPressed(){
      console.log("dir select 接收到返回，保存一次数据")
      this.intoParent()
    },


  },
  beforeDestroy() {
    this.$bus.$off([
        'iconCallBack',

      'onAppBackPressed'])
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
  min-height: 70px;
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
  word-wrap: break-word;
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
