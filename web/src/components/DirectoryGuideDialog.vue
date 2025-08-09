<template>
  <div class="popup-container">
    <div class="popup-header">
      <h2>请授权访问您的文件</h2>
      <p>为确保完整备份，我们需要访问您的存储空间</p>
      <p>选择错误可以重新选择，请放心大胆的尝试</p>
    </div>

    <div class="popup-content">
      <div class="explanation">
        <p>
          您的手机像一个大文件柜，照片、文档等存放在不同抽屉中。我们需要您授权访问整个文件柜，才能找到所有需要备份的文件。</p>
      </div>

      <div class="visual-compare">
        <div class="option bad-option">
          <div class="option-icon">📁</div>
          <div class="option-title">仅授权小文件夹</div>
          <div>可能遗漏重要文件</div>
        </div>

        <div class="option good-option">
          <div class="option-icon">🗂️</div>
          <div class="option-title">授权根目录</div>
          <div>确保完整备份</div>
        </div>
      </div>

      <div class="step-guide">
        <div class="step">
          <div class="step-number">1</div>
          <div class="step-content">
            <strong>在系统选择器中：</strong>
            <p>找到并点击"内部存储空间"（不要进入子文件夹）</p>

          </div>
        </div>
        <div class="privacy-note">
          如果确实无法选择内部存储空间，请至少选择一个目录，该目录下 存在您需要备份的文件。

        </div>


        <div class="folder-visual">
          <div class="folder-item main">内部存储空间</div>
          <div class="folder-item">Android</div>
          <div class="folder-item">相册DCIM</div>
          <div class="folder-item">图片Pictures</div>
          <div class="folder-item">下载Download</div>
          <div class="folder-item">文档documents</div>
          <p style="margin-top: 8px; font-size: 0.8rem;">请选择"内部存储空间"</p>
        </div>
        <div style="display: flex;justify-content: center">
          <img width="80%" src="img/tutorial/root-dir.jpg" alt="">
        </div>


        <div style="display: flex;justify-content: center">
          <img width="80%" src="img/tutorial/1754402504326.jpg" alt="">
        </div>
        <div class="step">
          <div class="step-number">2</div>
          <div class="step-content">
            <strong>授权成功后：</strong>
            <p>选择你需要备份的文件和目录</p>
          </div>



        </div>

        <div style="display: flex;justify-content: center">
          <img width="80%" src="img/tutorial/1754402504308.jpg" alt="">
        </div>
      </div>




    </div>
  </div>
</template>
<style scoped>


.popup-container {
  width: 100%;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.popup-header {
  background: linear-gradient(135deg, #4285f4, #34a853);
  color: white;
  padding: 15px 20px;
  text-align: center;
}

.popup-header h2 {
  font-size: 1.3rem;
  margin-bottom: 5px;
}

.popup-header p {
  font-size: 0.85rem;
  opacity: 0.9;
}

.popup-content {
  padding: 15px 20px;
  max-height: 60vh;
  overflow-y: auto;
}

.explanation {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 15px;
  font-size: 0.9rem;
  line-height: 1.5;
}

.visual-compare {
  display: flex;
  gap: 10px;
  margin: 15px 0;
}

.option {
  flex: 1;
  padding: 10px;
  border-radius: 8px;
  font-size: 0.8rem;
  text-align: center;
}

.bad-option {
  background: #ffebee;
  border: 1px solid #ef9a9a;
}

.good-option {
  background: #e8f5e9;
  border: 1px solid #a5d6a7;
}

.option-icon {
  font-size: 1.8rem;
  margin: 5px 0;
}

.bad-option .option-icon {
  color: #ef5350;
}

.good-option .option-icon {
  color: #4caf50;
}

.option-title {
  font-weight: bold;
  margin: 5px 0;
}

.step-guide {
  margin-top: 15px;
  font-size: 0.85rem;
}

.step {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
}

.step-number {
  width: 22px;
  height: 22px;
  background: #4285f4;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  margin-right: 10px;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
}

.folder-visual {
  background: #f5f5f5;
  border-radius: 8px;
  padding: 10px;
  margin: 15px 0;
  text-align: center;
}

.folder-item {
  display: inline-block;
  width: 80%;
  padding: 8px;
  margin: 3px 0;
  background: #e3f2fd;
  border-radius: 4px;
  font-size: 0.8rem;
}

.folder-item.main {
  background: #bbdefb;
  font-weight: bold;
}

.buttons {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  font-size: 0.9rem;
  cursor: pointer;
  text-align: center;
}

.btn-primary {
  background: #4285f4;
  color: white;
}

.btn-secondary {
  background: #f1f3f4;
  color: #5f6368;
}

.privacy-note {
  font-size: 0.75rem;
  color: #757575;
  text-align: center;
  margin-top: 15px;
  padding: 0 10px;
}
</style>
<script setup lang="ts">
</script>