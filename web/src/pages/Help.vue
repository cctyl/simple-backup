<template>

  <!-- 内容区域 -->
  <div class="content">
    <!-- 视频教程 -->
    <div class="video-container">
      <!-- Bilibili视频嵌入 -->
      <iframe :src="`https://player.bilibili.com/player.html?bvid=${bvid}&cid=339262048&page=1&high_quality=1&danmaku=0`" allowfullscreen="allowfullscreen" width="100%" height="500" scrolling="no" frameborder="0" sandbox="allow-top-navigation allow-same-origin allow-forms allow-scripts"></iframe>
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

            <div class="step-content">
              <div class="step-number">1</div>
              <div>
                <div class="step-title">启动服务器</div>
                <div class="step-desc">
                  在pc端，双击下载好的可执行文件 simple-backup.exe,
                  如果电脑弹出防火墙提示，请允许。

                </div>

                <div class="step-desc">
                  然后目录下会产生一个 “扫我.png”,打开图片，用于后续扫码。
                </div>

                <div class="step-desc">
                  （注意，电脑网络更换后ip也会更换，你可以重启软件来刷新二维码）
                </div>
              </div>

            </div>
            <div class="step-image">

              <img src="img/tutorial/服务器启动.png" alt="">
            </div>
          </div>



          <div class="step-item">

            <div class="step-content">
              <div class="step-number">2</div>
              <div>
                <div class="step-title">观察首页提示</div>
                <div class="step-desc">
                  查看需要配置的两个项目
                </div>

              </div>

            </div>
            <div class="step-image">

              <img src="img/tutorial/首页截图.jpg" alt="">
            </div>
          </div>


          <div class="step-item">

            <div class="step-content">
              <div class="step-number">3</div>
              <div>
                <div class="step-title">配置服务器连接</div>






              </div>

            </div>
            <div class="step-image">
              <div class="step-desc">
                点击第一个黄色图标，跳转到服务器设置页面，
              </div>
              <img src="img/tutorial/首页第一个黄色按钮.jpg" alt="">
              <div class="step-desc">
                在设置中配置您的服务器地址和访问令牌，你可以点击右上角，打开相机扫描服务器的二维码，
              </div>
              <img src="img/tutorial/1754402504392.jpg" alt="">
              <div class="step-desc">
                扫码完成后，点击测试链接，显示如下则表示链接成功。
              </div>
              <img src="img/tutorial/1754402504378.jpg" alt="">
              <div class="step-desc">
                注意，当您的电脑同时链接了多个网络，那么这个二维码扫出来的ip可能会链接失败，您可能需要自己在电脑上输入ipconfig来展示所有ip，
                然后手动在app上输入ip，然后点击测试链接。
              </div>
            </div>
          </div>



          <div class="step-item">

            <div class="step-content">
              <div class="step-number">4</div>
              <div>
                <div class="step-title">选择备份源</div>



              </div>

            </div>
            <div class="step-image">
              <div class="step-desc" >
                点击首页第二个黄色按钮, 跳转到备份源管理页面，
              </div>
              <img src="img/tutorial/1754402504360.jpg" alt="">
              <div class="step-desc" >

                第一步授权：
                首先点击绿色“授权”按钮，选择根目录。
              </div>
              <img src="img/tutorial/1754402504341.jpg" alt="">
              <div class="step-desc" >

                找到根目录后，点击允许访问
              </div>
              <img src="img/tutorial/1754402504326.jpg" alt="">
              <div class="step-desc">
                第二步：选择需要备份的文件夹。
                授权成功后，在展示出的文件夹列表中，勾选需要备份的文件夹，并点击右上角的√进行确认，完成配置
                支持多选

              </div>
              <img src="img/tutorial/1754402504308.jpg" alt="">
              <div class="step-desc">
                第三步：选择完毕后，点击右上角打勾，回到设置界面
              </div>
              <img src="img/tutorial/1754402504291.jpg" alt="">
            </div>
          </div>



          <div class="step-item">

            <div class="step-content">
              <div class="step-number">5</div>
              <div>
                <div class="step-title">开始第一次备份</div>
                <div class="step-desc">
                  点击左下角“首页”按钮，回到首页，点击开始首次备份，进入备份界面。等待备份完成。
                </div>

              </div>

            </div>
            <div class="step-image">

              <img src="img/tutorial/1754402504277.jpg" alt="">
              <img src="img/tutorial/1754402504260.jpg" alt="">
              <img src="img/tutorial/1754402504240.jpg" alt="">

            </div>
          </div>

          <div class="step-item">

            <div class="step-content">
              <div class="step-number">6</div>
              <div>
                <div class="step-title">查看备份历史</div>
                <div class="step-desc">
                  点击底部“历史”按钮，可查看历史备份数据信息
                </div>

              </div>

            </div>
            <div class="step-image">

              <img src="img/tutorial/1754402504222.jpg" alt="">

            </div>
          </div>



          <div class="step-item">

            <div class="step-content">
              <div class="step-number">7</div>
              <div>
                <div class="step-title">再次查看教程</div>
                <div class="step-desc">
                  你可以从首页右上角的按钮进入教程界面再次查看教程
                </div>

              </div>

            </div>
            <div class="step-image">

              <img src="img/tutorial/首页再次查看教程.jpg" alt="">

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
        <div class="step-item-i" @click="toBrowser(githubUserProfileUrl)">
          <div class="step-number-i">
            <i class="material-icons">forum</i>
          </div>
          <div class="step-content-i">
            <div class="step-title">作者github主页</div>
            <div class="step-desc">
              查看更多好玩的应用
            </div>
          </div>
        </div>

        <div class="step-item-i" @click="toBilibiliUserProfile(authUid)">
          <div class="step-number-i">
            <i class="material-icons">mail</i>
          </div>
          <div class="step-content-i">
            <div class="step-title">作者bilibili</div>
            <div class="step-desc">
              看看有什么新东西
            </div>
          </div>
        </div>

        <div class="step-item-i" @click="toBrowser(githubSourceUrl)">
          <div class="step-number-i">
            <i class="material-icons">menu_book</i>
          </div>
          <div class="step-content-i">
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
      bvid:"BV1L2tAzdEMk",
      githubSourceUrl:'https://github.com/cctyl/simple-backup',
      githubUserProfileUrl:'https://github.com/cctyl',
      bilibiliUserProfileUrl:'https://space.bilibili.com/8427106',
      authUid:'8427106'
    }
  },
  created() {
    this.$bus.$on('onAppBackPressed', this.onAppBackPressed);
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
    },
    onAppBackPressed() {
      this.$router.back();
    },
  },
  beforeDestroy() {
    this.$bus.$off(['onAppBackPressed']);
  }
};
</script>
<style scoped>


.step-content-i {
  flex: 1;
}
.step-item-i {
  display: flex;
  margin-bottom: 24px;
  position: relative;
}

.step-item-i:last-child {
  margin-bottom: 0;
}

.step-number-i{
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

  margin-bottom: 24px;
  position: relative;
}

.step-item:last-child {
  margin-bottom: 0;
}

.step-number {
  width: 25px;
  height: 25px;
  border-radius: 50%;
  background: #e8f0fe;
  color: #1a73e8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  margin-right: 10px;
  flex-shrink: 0;
}

.step-content {
  display: flex;
  width: 100%;
}

.step-title {
  font-weight: 500;
  font-size: 16px;
  margin-bottom: 8px;
  color: #202124;
}

.step-desc {
  margin-top: 10px;
  font-size: 14px;
  color: #5f6368;
  line-height: 1.5;
}

.step-image {
  width: 100%;
  border-radius: 12px;
  margin-top: 12px;
  background: #f8f9fa;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #5f6368;
  font-style: italic;
  overflow: auto;
}

.step-image img{
  width: 100%;
  height: auto;
  object-fit: contain;
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