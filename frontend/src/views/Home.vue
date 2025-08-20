<template>
  <div class="home">
    <div class="hero-section">
      <h1>AI提示词共享平台</h1>
      <p>发现、分享和创建高质量的AI提示词，提升您的AI交互体验</p>
      <el-button type="primary" size="large" @click="$router.push('/prompts')">
        开始探索
      </el-button>
    </div>
    
    <div class="features">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="feature-card">
            <template #header>
              <h3>🔍 发现优质提示词</h3>
            </template>
            <p>浏览社区分享的精选提示词，找到最适合您需求的AI交互方案</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card">
            <template #header>
              <h3>🚀 提升AI效果</h3>
            </template>
            <p>使用经过验证的提示词，显著提高AI模型的响应质量和准确性</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card">
            <template #header>
              <h3>🤝 社区分享</h3>
            </template>
            <p>分享您的创意提示词，与全球AI爱好者共同构建知识库</p>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <div class="recent-prompts">
      <h2>热门提示词</h2>
      <div v-loading="loading" class="prompt-grid">
        <PromptCard
          v-for="prompt in recentPrompts"
          :key="prompt.id"
          :prompt="prompt"
          class="prompt-item"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { promptsApi } from '../api/prompts'
import PromptCard from '../components/PromptCard.vue'

const recentPrompts = ref([])
const loading = ref(false)

const loadRecentPrompts = async () => {
  loading.value = true
  try {
    const res = await promptsApi.getPrompts({
      page: 1,
      size: 6,
      sortBy: 'usageCount'
    })
    recentPrompts.value = res.data.records
  } catch (error) {
    console.error('加载提示词失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRecentPrompts()
})
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.hero-section {
  text-align: center;
  padding: 60px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
  margin-bottom: 40px;
}

.hero-section h1 {
  font-size: 48px;
  margin-bottom: 20px;
}

.hero-section p {
  font-size: 20px;
  margin-bottom: 30px;
  opacity: 0.9;
}

.features {
  margin-bottom: 40px;
}

.feature-card {
  margin-bottom: 20px;
  text-align: center;
}

.feature-card h3 {
  margin: 0;
  color: #333;
}

.recent-prompts h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.prompt-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.prompt-item {
  height: 100%;
}

@media (max-width: 1200px) {
  .prompt-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .prompt-grid {
    grid-template-columns: 1fr;
  }
}
</style>