<template>
  <div class="admin-layout">
    <div class="admin-header">
      <nav class="admin-nav">
        <div class="nav-tabs">
          <div class="nav-slider" :style="sliderStyle"></div>
          <router-link 
            v-for="(tab, index) in navTabs" 
            :key="tab.path"
            :to="tab.path" 
            class="nav-tab"
            :class="{ active: route.path === tab.path }"
          >
            <el-icon><component :is="tab.icon" /></el-icon>
            <span class="tab-label">{{ tab.label }}</span>
          </router-link>
        </div>
      </nav>
    </div>
    
    <div class="admin-content">
      <div class="content-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import { Document, Management, ChatDotRound } from '@element-plus/icons-vue'

const route = useRoute()

const navTabs = [
  { path: '/admin/prompt-audit', label: '审核', icon: Document },
  { path: '/admin/prompt-management', label: '管理', icon: Management },
  { path: '/admin/comment-management', label: '评论', icon: ChatDotRound }
]

const activeIndex = computed(() => 
  navTabs.findIndex(tab => route.path === tab.path)
)

const sliderStyle = computed(() => {
  const index = activeIndex.value
  if (index === -1) return {}
  
  return {
    transform: `translateX(${index * 100}%)`
  }
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.admin-header {
  background: transparent;
  padding: 12px 0;
  flex-shrink: 0;
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  justify-content: center;
  align-items: center;
}

.admin-nav {
  margin-top: -10px;
  background: #fff;
  border-radius: 25px;
  padding: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
}

.nav-tabs {
  display: flex;
  gap: 3px;
  position: relative;
}

.nav-slider {
  position: absolute;
  top: 0;
  left: 0;
  width: calc(100% / 3);
  height: 100%;
  background: #409EFF;
  border-radius: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1;
  opacity: 1;
}

.nav-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  min-width: 90px;
  height: 45px;
  border-radius: 22px;
  text-decoration: none;
  color: #909399;
  font-size: 16px;
  transition: all 0.3s ease;
  background: transparent;
  gap: 3px;
  position: relative;
  z-index: 2;
}

.nav-tab .tab-label {
  font-size: 13px;
  font-weight: 500;
}

.nav-tab:hover {
  color: #409EFF;
}

.nav-tab.active {
  color: #fff;
}

.admin-content {
  flex: 1;
  padding: 0 24px 24px;
  background-color: #f5f7fa;
  overflow-y: auto;
}

.content-main {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 21, 41, 0.08);
  margin-top: 10px;
}
</style>