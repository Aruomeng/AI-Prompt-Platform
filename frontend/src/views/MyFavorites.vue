<template>
  <div class="my-favorites">
    <div class="page-header">
      <h1>我的收藏</h1>
      <p>查看和管理您收藏的提示词</p>
    </div>
    
    <div v-loading="loading" class="favorites-content">
      <div v-if="favorites.length === 0" class="empty-state">
        <el-empty 
          description="暂无收藏的提示词" 
          :image-size="200"
        >
          <template #description>
            <div>
              <p>您还没有收藏任何提示词</p>
              <el-button type="primary" @click="$router.push('/prompts')">
                去发现精彩提示词
              </el-button>
            </div>
          </template>
        </el-empty>
      </div>
      
      <div v-else>
        <div class="favorites-grid">
          <PromptCard
            v-for="prompt in favorites"
            :key="prompt.id"
            :prompt="prompt"
            @remove-favorite="removeFavorite"
          />
        </div>
        
        <el-pagination
          v-if="total > pageSize"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PromptCard from '../components/PromptCard.vue'
import { promptsApi } from '../api/prompts'

const loading = ref(false)
const favorites = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const loadFavorites = async () => {
  loading.value = true
  try {
    const response = await promptsApi.getFavorites({
      page: currentPage.value,
      size: pageSize.value
    })
    favorites.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('加载收藏失败:', error)
    ElMessage.error('加载收藏失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadFavorites()
}

const removeFavorite = async (promptId) => {
  try {
    await promptsApi.unfavoritePrompt(promptId)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (error) {
    console.error('取消收藏失败:', error)
    ElMessage.error('取消收藏失败')
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.my-favorites {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 32px;
  color: #2c3e50;
  margin-bottom: 8px;
}

.page-header p {
  font-size: 16px;
  color: #666;
}

.favorites-content {
  min-height: 400px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

@media (max-width: 1200px) {
  .favorites-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }
}

.el-pagination {
  margin-top: 30px;
  justify-content: center;
}
</style>