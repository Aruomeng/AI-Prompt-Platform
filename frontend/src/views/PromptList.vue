<template>
  <div class="prompt-list">
    <div class="header">
      <h1>提示词列表</h1>
      <div class="filters">
        <el-select
          v-model="filters.categoryId"
          placeholder="选择分类"
          clearable
          style="width: 200px; margin-right: 20px"
        >
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
        
        <el-input
          v-model="filters.keyword"
          placeholder="搜索提示词"
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="'Search'" @click="handleSearch" />
          </template>
        </el-input>
      </div>
    </div>
    
    <div v-loading="loading">
      <div v-if="prompts.length === 0" class="empty">
        <el-empty description="暂无提示词" />
      </div>
      <div v-else>
        <div class="prompt-grid">
          <PromptCard
            v-for="prompt in prompts"
            :key="prompt.id"
            :prompt="prompt"
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
import { ref, onMounted, watch } from 'vue'
import { promptsApi } from '../api/prompts'
import { categoriesApi } from '../api/categories'
import PromptCard from '../components/PromptCard.vue'

const prompts = ref([])
const categories = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filters = ref({
  categoryId: '',
  keyword: ''
})

const loadPrompts = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...filters.value
    }
    const res = await promptsApi.getPrompts(params)
    prompts.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载提示词失败:', error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await categoriesApi.getAllCategories()
    categories.value = res.data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadPrompts()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1 // 重置到第一页
  loadPrompts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadPrompts()
}

watch(() => filters.value.categoryId, () => {
  currentPage.value = 1
  loadPrompts()
})

onMounted(() => {
  loadPrompts()
  loadCategories()
})
</script>

<style scoped>
.prompt-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  margin-bottom: 30px;
}

.header h1 {
  margin-bottom: 20px;
  color: #333;
}

.filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.empty {
  text-align: center;
  padding: 60px 0;
}

.el-pagination {
  margin-top: 30px;
  justify-content: center;
}

.prompt-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
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