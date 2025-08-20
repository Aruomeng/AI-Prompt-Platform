<template>
  <div id="app">
    <el-container>
      <el-header v-if="!isAuthPage">
        <NavigationBar />
      </el-header>
      <el-main :class="{ 'auth-main': isAuthPage }">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from './stores/user'
import NavigationBar from './components/NavigationBar.vue'

const route = useRoute()
const userStore = useUserStore()

const isAuthPage = computed(() => {
  return route.path === '/login' || route.path === '/register'
})

onMounted(() => {
  userStore.initializeUser()
})
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

body {
  margin: 0;
  padding: 0;
}

.el-header {
  padding: 0;
  height: auto !important;
  line-height: 1 !important;
  box-shadow: none;
}

.el-main {
  background-color: #f5f5f5;
  min-height: calc(100vh - 76px);
  padding: 20px;
  margin-top: 76px;
}

.auth-main {
  margin-top: 0;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>