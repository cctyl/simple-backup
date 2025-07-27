<!-- src/components/AnimatedTransition.vue -->
<template>
  <transition
    name="slide-fade"
    mode="out-in"
  >
    <slot></slot>
  </transition>
</template>

<script>
export default {
  name: 'AnimatedTransition'
}
</script>

<style scoped>
.slide-fade-enter-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-fade-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-fade-enter-from {
  transform: translateY(20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}
</style>
