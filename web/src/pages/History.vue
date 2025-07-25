<template>
  <!-- 内容区域 -->
  <div class="content">
    <!-- 时间轴 -->
    <div class="timeline" v-if="backupList.length>0">


      <div class="log-item"  v-for="item in backupList" :key="item.id">
        <div class="timeline-node"></div>
        <div class="log-header">
          <div class="log-status "
               :class="{
            'status-success':item.success,
            'status-error':!item.success
          }"
          >
            <i class="material-icons">check_circle</i>
          </div>
          <div class="log-title">{{ item.backupResult }}</div>
          <div class="log-time">{{ formatRelativeTime(item.backUpTime ) }}</div>
        </div>
        <div class="log-content">
          {{ item.backupDetail }}
        </div>
        <div class="log-stats">
          <div class="stat-badge">📁 {{ formatNumberWithCommas(item.backUpNum) }} 个文件</div>
          <div class="stat-badge">📊 {{ formatSize(item.totalFileSize) }} 数据</div>
          <div class="stat-badge">⏱️ {{ formatCostTime(item.backUpCostTime) }}</div>
        </div>
        <div class="log-details">
          <div class="log-details-list">
            <div class="log-files" v-for="p in item.backUpPathArr.split(',')" :key="p">
              <i class="material-icons" style="font-size: 16px; vertical-align: middle;">folder</i>
              {{p}}
            </div>

          </div>


          <div style="display: flex;justify-content: center">
            <router-link to="/settings/source" tag="div" class="detail-button">
              查看配置 <i class="material-icons" style="font-size: 18px;">chevron_right</i>
            </router-link>
          </div>

        </div>
      </div>
      <!--
            &lt;!&ndash; 日志项1 - 成功 &ndash;&gt;
            <div class="log-item">
              <div class="timeline-node"></div>
              <div class="log-header">
                <div class="log-status status-success">
                  <i class="material-icons">check_circle</i>
                </div>
                <div class="log-title">备份完成</div>
                <div class="log-time">今天 10:30</div>
              </div>
              <div class="log-content">
                设备数据备份成功完成，所有文件已安全上传至云端。
              </div>
              <div class="log-stats">
                <div class="stat-badge">📁 1,274 个文件</div>
                <div class="stat-badge">📊 4.7 GB 数据</div>
                <div class="stat-badge">⏱️ 12分45秒</div>
              </div>
              <div class="log-details">
                <div class="log-files">
                  <i class="material-icons" style="font-size: 16px; vertical-align: middle;">folder</i>
                  内部存储/DCIM, 内部存储/Documents...
                </div>
                <div class="detail-button">
                  查看详情 <i class="material-icons" style="font-size: 18px;">chevron_right</i>
                </div>
              </div>
            </div>

            &lt;!&ndash; 日志项2 - 警告 &ndash;&gt;
            <div class="log-item">
              <div class="timeline-node"></div>
              <div class="log-header">
                <div class="log-status status-warning">
                  <i class="material-icons">warning</i>
                </div>
                <div class="log-title">暂停</div>
                <div class="log-time">昨天 14:20</div>
              </div>
              <div class="log-content">
                备份过程中跳过了3个被排除的文件，其他文件备份成功。
              </div>
              <div class="log-stats">
                <div class="stat-badge">✅ 1,271 个文件成功</div>
                <div class="stat-badge">⚠️ 3 个文件跳过</div>
              </div>
              <div class="log-details">
                <div class="log-files">
                  <i class="material-icons" style="font-size: 16px; vertical-align: middle;">info</i>
                  跳过: cache.tmp, temp_file.log...
                </div>
                <div class="detail-button">
                  查看详情 <i class="material-icons" style="font-size: 18px;">chevron_right</i>
                </div>
              </div>
            </div>

            &lt;!&ndash; 日志项3 - 错误 &ndash;&gt;
            <div class="log-item">
              <div class="timeline-node"></div>
              <div class="log-header">
                <div class="log-status status-error">
                  <i class="material-icons">error</i>
                </div>
                <div class="log-title">备份失败</div>
                <div class="log-time">昨天 02:15</div>
              </div>
              <div class="log-content">
                自动备份过程中断，网络连接丢失导致上传失败。
              </div>
              <div class="log-stats">
                <div class="stat-badge">📁 784/1,274 个文件</div>
                <div class="stat-badge">❌ 网络错误</div>
              </div>
              <div class="log-details">
                <div class="log-files">
                  <i class="material-icons" style="font-size: 16px; vertical-align: middle;">wifi_off</i>
                  网络连接中断，剩余文件未上传
                </div>
                <div class="detail-button">
                  查看详情 <i class="material-icons" style="font-size: 18px;">chevron_right</i>
                </div>
              </div>
            </div>

            &lt;!&ndash; 日志项4 - 信息 &ndash;&gt;
            <div class="log-item">
              <div class="timeline-node"></div>
              <div class="log-header">
                <div class="log-status status-info">
                  <i class="material-icons">info</i>
                </div>
                <div class="log-title">存储空间警告</div>
                <div class="log-time">2023-08-18 09:45</div>
              </div>
              <div class="log-content">
                云端存储空间已使用85%，建议清理旧版本或升级存储计划。
              </div>
              <div class="log-stats">
                <div class="stat-badge">💾 8.5 GB/10 GB</div>
                <div class="stat-badge">🔄 7个备份版本</div>
              </div>
              <div class="log-details">
                <div class="log-files">
                  <i class="material-icons" style="font-size: 16px; vertical-align: middle;">storage</i>
                  存储空间即将用尽
                </div>
                <div class="detail-button">
                  查看详情 <i class="material-icons" style="font-size: 18px;">chevron_right</i>
                </div>
              </div>
            </div>

            &lt;!&ndash; 日志项5 - 成功 &ndash;&gt;
            <div class="log-item">
              <div class="timeline-node"></div>
              <div class="log-header">
                <div class="log-status status-success">
                  <i class="material-icons">check_circle</i>
                </div>
                <div class="log-title">备份完成</div>
                <div class="log-time">2023-08-17 02:30</div>
              </div>
              <div class="log-content">
                自动备份成功完成，所有文件已安全上传至云端。
              </div>
              <div class="log-stats">
                <div class="stat-badge">📁 1,250 个文件</div>
                <div class="stat-badge">📊 4.5 GB 数据</div>
              </div>
              <div class="log-details">
                <div class="log-files">
                  <i class="material-icons" style="font-size: 16px; vertical-align: middle;">folder</i>
                  内部存储/DCIM, 内部存储/Documents...
                </div>
                <div class="detail-button">
                  查看详情 <i class="material-icons" style="font-size: 18px;">chevron_right</i>
                </div>
              </div>
            </div>

            -->
    </div>
    <div  v-else>

      <!--  没有备份历史时的显示    -->
      <div class="empty-state">
        <div class="empty-icon">
          <i class="material-icons">history</i>
        </div>
        <div class="empty-title">暂无备份记录</div>
        <div class="empty-text">您还没有任何备份历史记录，快去开始备份吧！</div>
      </div>
    </div>

    <!-- 空状态（隐藏） -->
    <div class="empty-state" style="display: none;">
      <div class="empty-icon">
        <i class="material-icons">history</i>
      </div>
      <div class="empty-title">暂无活动记录</div>
      <div class="empty-text">您还没有任何备份或恢复活动记录</div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'history-view',
  data() {
    return {
      backupList: [],
    }
  },

  mounted() {
    this.backupList = JSON.parse(window.Android.getBackupHistory());
    console.log(this.backupList)
  },
  methods: {
    formatNumberWithCommas(num) {
      // 处理非数字输入
      if (typeof num !== 'number' || isNaN(num)) {
        return '0';
      }

      // 四舍五入取整
      const roundedNum = Math.round(num);

      // 使用toLocaleString自动添加千位分隔符
      return roundedNum.toLocaleString('en-US');
    },
    formatRelativeTime(isoDateTime) {
      console.log(isoDateTime)
      // 解析输入时间
      const inputDate = new Date(isoDateTime);
      const now = new Date();
      // 计算时间差（毫秒）
      const diffMs = now - inputDate;
      const diffMinutes = Math.round(diffMs / (1000 * 60));
      const diffHours = Math.round(diffMs / (1000 * 60 * 60));
      const diffDays = Math.round(diffMs / (1000 * 60 * 60 * 24));

      // 判断时间范围并返回相应格式
      if (diffMinutes < 60) {
        return `${diffMinutes}分钟前`;
      } else if (diffHours < 24) {
        const hours = Math.floor(diffHours);
        const minutes = diffMinutes % 60;
        return `${hours}小时${minutes}分钟前`;
      } else if (diffDays <= 7) {
        return `${diffDays}天前`;
      } else {
        // 超过7天显示具体日期
        const year = inputDate.getFullYear();
        const month = inputDate.getMonth() + 1; // 月份从0开始
        const day = inputDate.getDate();
        return `${year}年${month}月${day}日`;
      }
    },

    formatStorage(mbValue) {
      if (mbValue >= 1024) {
        // 转换为GB并保留1位小数
        const gbValue = Math.round(mbValue / 102.4) / 10; // 避免浮点运算问题
        return `${gbValue}GB`;
      }
      return `${Math.round(mbValue)}MB`; // 不足1024MB直接显示
    },

    formatSize(size) {
      if (size < 1024) return size + ' B';
      else if (size < 1024 * 1024) return (size / 1024).toFixed(1) + ' KB';
      else if (size < 1024 * 1024 * 1024)
        return (size / (1024 * 1024)).toFixed(1) + ' MB';
      else return (size / (1024 * 1024 * 1024)).toFixed(1) + ' GB';
    },

    formatCostTime(seconds) {
      if (typeof seconds !== 'number' || seconds < 0 || !isFinite(seconds)) {
        return '无效时间';
      }

      const day = Math.floor(seconds / 86400);
      seconds %= 86400;
      const hour = Math.floor(seconds / 3600);
      seconds %= 3600;
      const minute = Math.floor(seconds / 60);
      const second = seconds % 60;

      const parts = [];

      if (day > 0) {
        parts.push(`${day}天`);
      }
      if (hour > 0) {
        parts.push(`${hour}小时`);
      }
      if (minute > 0) {
        parts.push(`${minute}分钟`);
      }
      if (day === 0 && hour === 0 && minute === 0 && second > 0) {
        parts.push(`${second}秒`);
      }

      // 如果超过一天，显示秒
      if (day > 0 && second > 0) {
        parts.push(`${second}秒`);
      }

      return parts.join('');
    }
  },
}
</script>
<style scoped>


.log-details-list {

  height: 20vh;
  overflow: auto;

}

/* 内容区域 */
.content {
  padding-top: 16px;
  margin: 0 auto;
}

/* 时间轴容器 */
.timeline {
  position: relative;
  padding-left: 13px;
  margin-left: 14px;
  border-left: 2px solid #e8eaed;
}

/* 时间轴节点 */
.timeline-node {
  position: absolute;
  left: -9px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #e8eaed;
  z-index: 2;
}

/* 日志项 */
.log-item {
  position: relative;
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
  margin-bottom: 24px;
  transition: all 0.2s;
  width: 95%;
}

.log-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.log-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.log-status {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.status-success {
  background: #e6f4ea;
  color: #34a853;
}

.status-error {
  background: #fce8e6;
  color: #ea4335;
}

.status-warning {
  background: #fef7e0;
  color: #fbbc04;
}

.status-info {
  background: #e8f0fe;
  color: #1a73e8;
}

.log-title {
  flex: 1;
  font-weight: 500;
  font-size: 16px;
}

.log-time {
  font-size: 14px;
  color: #5f6368;
}

.log-content {
  margin-bottom: 16px;
  line-height: 1.5;
}

.log-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.stat-badge {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 8px 12px;
  font-size: 14px;
}

.log-details {
  display: flex;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #f1f3f4;
  flex-direction: column;
}

.log-files {
  font-size: 14px;
  color: #5f6368;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: wrap;
  flex: 1;
  border-bottom: 1px solid #f1f3f4;
  padding: 10px 0px;
}

.detail-button {
  color: #1a73e8;
  font-weight: 500;
  font-size: 14px;
  display: flex;
  align-items: center;
  cursor: pointer;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 48px 16px;
}

.empty-icon {
  font-size: 64px;
  color: #dadce0;
  margin-bottom: 24px;
}

.empty-title {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #202124;
}

.empty-text {
  font-size: 14px;
  color: #5f6368;
  margin-bottom: 24px;
}


</style>
