<template>
  <div class="my-prompts">
    <div class="header">
      <h1>我的提示词</h1>
      <el-button type="primary" @click="$router.push('/create-prompt')">
        创建新提示词
      </el-button>
    </div>
    
    <div v-loading="loading" class="content">
      <div v-if="prompts.length === 0" class="empty">
        <el-empty description="暂无提示词">
          <el-button type="primary" @click="$router.push('/create-prompt')">
            立即创建
          </el-button>
        </el-empty>
      </div>
      
      <div v-else>
        <div class="prompt-grid">
          <PromptCard
          v-for="prompt in prompts"
          :key="prompt.id"
          :prompt="prompt"
          :show-delete="true"
          @delete="handleDelete"
          class="prompt-item"
        />
        </div>
        
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { promptsApi } from '../api/prompts'
import PromptCard from '../components/PromptCard.vue'

const prompts = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadMyPrompts = async () => {
  loading.value = true
  try {
    const res = await promptsApi.getMyPrompts({
      page: currentPage.value,
      size: pageSize.value
    })
    prompts.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('加载提示词失败')
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个提示词吗？此操作不可恢复。',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await promptsApi.deletePrompt(id)
    ElMessage.success('删除成功')
    loadMyPrompts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const viewDetail = (id) => {
  window.open(`/prompts/${id}`, '_blank')
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1 // 重置到第一页
  loadMyPrompts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadMyPrompts()
}

onMounted(() => {
  loadMyPrompts()
})
</script>

<style scoped>
.my-prompts {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h1 {
  margin: 0;
  color: #333;
}

.content {
  min-height: 400px;
}

.empty {
  text-align: center;
  padding: 60px 0;
}

.prompt-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.prompt-item {
  height: 100%;
}

.el-pagination {
  margin-top: 30px;
  justify-content: center;
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