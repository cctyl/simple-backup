<template>
  <div class="circular-progress">
    <svg
        class="progress-ring"
        :width="size"
        :height="size"
        viewBox="0 0 100 100"
    >
      <!-- 背景圆 -->
      <circle
          class="progress-ring__bg"
          stroke="#f1f3f4"
          stroke-width="8"
          fill="transparent"
          r="46"
          cx="50"
          cy="50"
      />
      <!-- 进度圆 -->
      <circle
          class="progress-ring__progress"
          :stroke=" status===1?'#1a73e8': '#f97316'"
          stroke-width="8"
          fill="transparent"
          :stroke-dasharray="circumference"
          :stroke-dashoffset="strokeDashoffset"
          r="46"
          cx="50"
          cy="50"
      />
    </svg>
    <div class="progress-text">
      <div class="progress-percent" :style="{
        color: status===1?'#1a73e8': '#f97316'
      }">{{ status===1?progress:(100-progress) }}%
      </div>
      <div class="progress-label" :style="{
        color: status===1?'#1a73e8': '#f97316'
      }">
        {{status===1?'已完成':'剩余'}}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'circular-progress',
  props: {
    progress: {
      type: Number,
      default: 0,
    },
    size: {
      type: Number,
      default: 120
    },
    status: Number
  },
  data() {
    return {
      radius: 46,
      circumference: 2 * Math.PI * 46
    }
  },
  computed: {
    strokeDashoffset() {
      return this.circumference - (this.progress / 100) * this.circumference
    }
  }
}
</script>

<style scoped>
.circular-progress {
  position: relative;
  width: fit-content;
  height: fit-content;
}

.progress-ring {
  transform: rotate(-90deg);
}

.progress-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.progress-percent {
  font-size: 24px;
  font-weight: bold;
  color: #1a73e8;
}

.progress-label {
  font-size: 14px;
  color: #5f6368;
  margin-top: 4px;
}
</style>