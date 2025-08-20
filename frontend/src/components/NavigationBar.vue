<template>
  <nav class="floating-nav">
    <div class="nav-container">
      <div class="nav-brand">
        <div class="cube-container" @click="$router.push('/')">
          <div class="logo-icon">
            <div class="ai-cube">
              <div class="cube-face front"></div>
              <div class="cube-face back"></div>
              <div class="cube-face right"></div>
              <div class="cube-face left"></div>
              <div class="cube-face top"></div>
              <div class="cube-face bottom"></div>
            </div>
            <div class="logo-particles">
              <span class="particle particle-1"></span>
              <span class="particle particle-2"></span>
              <span class="particle particle-3"></span>
            </div>
          </div>
        </div>
        <div class="logo-container" @click="$router.push('/')">
          <div class="logo-text">
            <span class="logo-main">
              <span class="ai-badge">AI</span>
              Prompt
            </span>
          </div>
        </div>
      </div>
      
      <div class="nav-center">
        <div class="nav-links">
          <router-link 
            v-for="item in allNavItems" 
            :key="item.path"
            :to="item.path" 
            class="nav-item"
            :class="{ active: isActive(item.path) }"
            :style="{ minWidth: item.minWidth + 'px' }"
          >
            <span class="nav-text">{{ item.name }}</span>
            <div class="nav-indicator"></div>
          </router-link>
        </div>
      </div>
      
      <div class="nav-user">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown trigger="click" placement="bottom-end">
            <div class="user-avatar">
              <div class="avatar-wrapper">
                <el-avatar 
                  :size="36" 
                  :src="userStore.userInfo?.avatar"
                  class="gradient-avatar"
                >
                  {{ (userStore.userInfo?.nickname || userStore.userInfo?.username || '用').charAt(0).toUpperCase() }}
                </el-avatar>
              </div>
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <div class="auth-buttons">
            <el-button type="text" @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')">注册</el-button>
          </div>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { SwitchButton, ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()

const allNavItems = computed(() => {
  const baseItems = [
    { name: '首页', path: '/', minWidth: 60 },
    { name: '提示词', path: '/prompts', minWidth: 60 },
    { name: '我的提示词', path: '/my-prompts', minWidth: 80 },
    { name: '我的收藏', path: '/my-favorites', minWidth: 80 },
    { name: '创建提示词', path: '/create-prompt', minWidth: 80 }
  ]
  
  if (userStore.isAdmin) {
    baseItems.push({ name: '后台管理', path: '/admin/prompt-audit', minWidth: 80 })
  }
  
  return baseItems
})

const isActive = (path) => {
  return route.path === path || (path !== '/' && route.path.startsWith(path))
}

const handleLogout = () => {
  userStore.logout()
  window.location.href = '/'
}
</script>

<style scoped>
.floating-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  padding: 12px 0;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: relative;
}

.nav-brand {
  display: flex;
  align-items: center;
  flex: 1;
  gap: 20px;
}

.cube-container {
  cursor: pointer;
  transition: transform 0.3s ease;
  margin-left: -8px;
}

.cube-container:hover {
  transform: scale(1.1);
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.logo-container:hover {
  transform: scale(1.05);
}

.logo-icon {
  position: relative;
  width: 40px;
  height: 40px;
}

.ai-cube {
  position: relative;
  width: 100%;
  height: 100%;
  transform-style: preserve-3d;
  animation: rotateCube 8s infinite linear;
}

@keyframes rotateCube {
  0% { transform: rotateX(0deg) rotateY(0deg); }
  100% { transform: rotateX(360deg) rotateY(360deg); }
}

.cube-face {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 1px solid rgba(255, 255, 255, 0.3);
  opacity: 0.8;
}

.cube-face.front { transform: translateZ(20px); }
.cube-face.back { transform: rotateY(180deg) translateZ(20px); }
.cube-face.right { transform: rotateY(90deg) translateZ(20px); }
.cube-face.left { transform: rotateY(-90deg) translateZ(20px); }
.cube-face.top { transform: rotateX(90deg) translateZ(20px); }
.cube-face.bottom { transform: rotateX(-90deg) translateZ(20px); }

.logo-particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  animation: float 3s infinite ease-in-out;
}

.particle-1 {
  top: 20%;
  left: 20%;
  animation-delay: 0s;
}

.particle-2 {
  top: 60%;
  right: 20%;
  animation-delay: 1s;
}

.particle-3 {
  bottom: 20%;
  left: 60%;
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) scale(1); opacity: 0.7; }
  50% { transform: translateY(-10px) scale(1.2); opacity: 1; }
}

.logo-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.logo-main {
  font-size: 24px;
  font-weight: 800;
  color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 7px;
}

.ai-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 6px 6px;
  border-radius: 6px;
  font-size: 24px;
  font-weight: 800;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  border: none;
  line-height: 1;
}

.logo-sub {
  font-size: 12px;
  color: #8b92a9;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.nav-center {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
}

.nav-links {
  display: flex;
  align-items: center;
  background: rgba(240, 240, 240, 0.3);
  border-radius: 12px;
  padding: 4px;
  position: relative;
  gap: 0;
}

.nav-item {
  position: relative;
  padding: 12px 16px;
  text-decoration: none;
  color: #666;
  font-weight: 500;
  transition: all 0.3s ease;
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.nav-user {
  display: flex;
  align-items: center;
  flex: 1;
  justify-content: flex-end;
}

.nav-item:hover {
  color: #667eea;
  transform: translateY(-2px);
}

.nav-item.active {
  color: #667eea;
}

.nav-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateX(-50%);
}

.nav-item.active .nav-indicator {
  width: 80%;
}

.nav-item:hover .nav-indicator {
  width: 80%;
}

.nav-user {
  display: flex;
  align-items: center;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  background: rgba(240, 240, 240, 0.5);
}

.username {
  font-weight: 500;
  color: #333;
}

.auth-buttons {
  display: flex;
  gap: 12px;
  align-items: center;
}

.auth-buttons .el-button {
  border-radius: 8px;
  font-weight: 500;
}

/* 滑动动效 */
.nav-item {
  position: relative;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.nav-item:hover::before {
  opacity: 1;
}

.nav-item.active::before {
  opacity: 1;
}

/* 用户头像样式 */
.avatar-wrapper {
  position: relative;
}

.gradient-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 600;
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.gradient-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  background: rgba(240, 240, 240, 0.5);
}

.username {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.dropdown-icon {
  color: #666;
  font-size: 12px;
  transition: transform 0.3s ease;
}

.user-avatar:hover .dropdown-icon {
  transform: translateY(2px);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .nav-container {
    padding: 0 16px;
  }
  
  .nav-item {
    padding: 10px 12px;
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .nav-container {
    padding: 0 12px;
    flex-direction: column;
    gap: 12px;
  }
  
  .nav-center {
    position: static;
    left: auto;
    transform: none;
    width: 100%;
    justify-content: center;
  }
  
  .nav-links {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .nav-item {
    padding: 8px 10px;
    font-size: 13px;
    min-width: auto !important;
  }
  
  .brand-text {
    font-size: 16px;
  }
  
  .nav-brand, .nav-user {
    flex: none;
  }
}
</style>