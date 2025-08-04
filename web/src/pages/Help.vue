<template>

  <!-- 内容区域 -->
  <div class="content">
    <!-- 视频教程 -->
    <div class="video-container">
      <!-- Bilibili视频嵌入 -->
      <iframe :src="`//player.bilibili.com/player.html?bvid=${bvid}&cid=339262048&page=1&high_quality=1&danmaku=0`" allowfullscreen="allowfullscreen" width="100%" height="500" scrolling="no" frameborder="0" sandbox="allow-top-navigation allow-same-origin allow-forms allow-scripts"></iframe>
      <div class="video-overlay" @click="toBlibili">
        <div class="video-title">云备份应用使用指南</div>
        <div class="video-title" ><a>跳转b站观看</a></div>
      </div>
    </div>

    <!-- 文字教程 -->
    <div class="tutorial-card">
      <div class="card-header">
        <i class="material-icons">list_alt</i>
        快速入门指南
      </div>
      <div class="card-body">
        <div class="tutorial-steps">
          <div class="step-item">
            <div class="step-number">1</div>
            <div class="step-content">
              <div class="step-title">配置服务器连接</div>
              <div class="step-desc">
                在设置中配置您的服务器地址和访问令牌，确保应用可以连接到您的备份服务器。
              </div>
              <div class="step-image">配置服务器界面截图</div>
            </div>
          </div>

          <div class="step-item">
            <div class="step-number">2</div>
            <div class="step-content">
              <div class="step-title">选择备份源</div>
              <div class="step-desc">
                在备份源管理中选择您需要备份的文件夹，支持多选和排除特定文件夹。
              </div>
              <div class="step-image">文件夹选择界面截图</div>
            </div>
          </div>

          <div class="step-item">
            <div class="step-number">3</div>
            <div class="step-content">
              <div class="step-title">设置备份规则</div>
              <div class="step-desc">
                配置自动备份计划、网络限制和电源要求，优化您的备份体验。
              </div>
              <div class="step-image">备份规则设置截图</div>
            </div>
          </div>

          <div class="step-item">
            <div class="step-number">4</div>
            <div class="step-content">
              <div class="step-title">执行首次备份</div>
              <div class="step-desc">
                返回主界面点击"立即备份"按钮开始您的首次完整备份，过程可在活动日志中查看。
              </div>
              <div class="step-image">备份进行中界面截图</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 常见问题 -->
    <div class="tutorial-card">
      <div class="card-header">
        <i class="material-icons">help</i>
        常见问题解答
      </div>
      <div class="card-body">
        <div class="faq-item">
          <div class="faq-question">
            <i class="material-icons">live_help</i>
            备份过程中断会怎样？
          </div>
          <div class="faq-answer">
            应用支持任意情况的中断、暂停。未完成的备份在下次将会继续进行
          </div>
        </div>



        <div class="faq-item">
          <div class="faq-question">
            <i class="material-icons">live_help</i>
            应用在后台会中断备份吗？
          </div>
          <div class="faq-answer">
            不会，只要应用没有被杀死，备份会正常进行。进度可以查看通知栏。为了防止系统杀死进程，可以将应用加入白名单
          </div>
        </div>

        <div class="faq-item">
          <div class="faq-question">
            <i class="material-icons">live_help</i>
            不会用了怎么办？
          </div>
          <div class="faq-answer">
            查看页面顶部的视频教程，或点击进入b站视频下方评论，作者会进行回复
          </div>
        </div>
      </div>
    </div>

    <!-- 更多帮助 -->
    <div class="tutorial-card">
      <div class="card-header">
        <i class="material-icons">support_agent</i>
        获取更多帮助
      </div>
      <div class="card-body">
        <div class="step-item" @click="toBrowser(githubUserProfileUrl)">
          <div class="step-number">
            <i class="material-icons">forum</i>
          </div>
          <div class="step-content">
            <div class="step-title">作者github主页</div>
            <div class="step-desc">
              查看更多好玩的应用
            </div>
          </div>
        </div>

        <div class="step-item" @click="toBilibiliUserProfile(authUid)">
          <div class="step-number">
            <i class="material-icons">mail</i>
          </div>
          <div class="step-content">
            <div class="step-title">作者bilibili</div>
            <div class="step-desc">
              看看有什么新东西
            </div>
          </div>
        </div>

        <div class="step-item" @click="toBrowser(githubSourceUrl)">
          <div class="step-number">
            <i class="material-icons">menu_book</i>
          </div>
          <div class="step-content">
            <div class="step-title">源代码</div>
            <div class="step-desc">
              本应用完全开源免费，点击这里访问源代码：{{githubSourceUrl}}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'help-view',
  data(){

    return{
      bvid:"BV181hLzdEtG",
      githubSourceUrl:'https://github.com/cctyl/bili-rec-web',
      githubUserProfileUrl:'https://github.com/cctyl',
      bilibiliUserProfileUrl:'https://space.bilibili.com/8427106',
      authUid:'8427106'
    }
  },
  methods:{
    toBlibili(){
      window.Android.toBilibili(this.bvid);
    },
    toBrowser(url){
      window.Android.openInBrowser(url)
    },
    toBilibiliUserProfile(uid){
      window.Android.toBilibiliUserProfile(uid)
    }
  }
};
</script>
<style scoped>

/* 内容区域 */
.content {
  padding: 16px;
  padding-top: 40px; /* 状态栏+应用栏高度 */
  max-width: 500px;
  margin: 0 auto;
}

/* 视频容器 */
.video-container {
  position: relative;
  width: 100%;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.video-container iframe {
  width: 100%;
  height: 210px;
  border: none;
}

.video-overlay {

  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 16px;
}

.video-title {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
}

.video-meta {
  display: flex;
  align-items: center;
  font-size: 14px;
  opacity: 0.9;
}

.video-meta span {
  margin-right: 16px;
  display: flex;
  align-items: center;
}

.video-meta i {
  margin-right: 4px;
}

/* 教程卡片 */
.tutorial-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
  overflow: hidden;
}

.card-header {
  padding: 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #f1f3f4;
  font-weight: 500;
  font-size: 16px;
  color: #202124;
  display: flex;
  align-items: center;
}

.card-header i {
  margin-right: 8px;
  color: #1a73e8;
}

.card-body {
  padding: 16px;
}

/* 教程步骤 */
.tutorial-steps {
  counter-reset: step-counter;
}

.step-item {
  display: flex;
  margin-bottom: 24px;
  position: relative;
}

.step-item:last-child {
  margin-bottom: 0;
}

.step-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e8f0fe;
  color: #1a73e8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  margin-right: 16px;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
}

.step-title {
  font-weight: 500;
  font-size: 16px;
  margin-bottom: 8px;
  color: #202124;
}

.step-desc {
  font-size: 14px;
  color: #5f6368;
  line-height: 1.5;
}

.step-image {
  width: 100%;
  border-radius: 12px;
  margin-top: 12px;
  background: #f8f9fa;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5f6368;
  font-style: italic;
}

/* 常见问题 */
.faq-item {
  padding: 16px 0;
  border-bottom: 1px solid #f1f3f4;
}

.faq-item:last-child {
  border-bottom: none;
}

.faq-question {
  font-weight: 500;
  font-size: 16px;
  margin-bottom: 8px;
  display: flex;
  align-items: flex-start;
}

.faq-question .material-icons {
  color: #1a73e8;
  margin-right: 8px;
  font-size: 20px;
}

.faq-answer {
  font-size: 14px;
  color: #5f6368;
  padding-left: 32px;
  line-height: 1.5;
}

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