<template>
  <div class="edit-prompt">
    <el-card>
      <template #header>
        <h2>修改提示词</h2>
      </template>
      
      <el-form
        ref="promptFormRef"
        :model="promptForm"
        :rules="rules"
        label-width="100px"
        @submit.prevent="handleUpdate"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="promptForm.title"
            placeholder="请输入提示词标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="分类" prop="categoryId">
          <el-select
            v-model="promptForm.categoryId"
            placeholder="请选择分类"
            style="width: 100%"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="promptForm.description"
            type="textarea"
            :rows="3"
            placeholder="请描述这个提示词的用途和效果"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="提示词内容" prop="content">
          <el-input
            v-model="promptForm.content"
            type="textarea"
            :rows="10"
            placeholder="请输入完整的提示词内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="promptForm.tags"
            multiple
            filterable
            allow-create
            placeholder="请输入标签，按回车确认"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            :loading="loading"
          >
            保存修改
          </el-button>
          <el-button @click="$router.push('/my-prompts')">
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { promptsApi } from '../api/prompts'
import { categoriesApi } from '../api/categories'

const router = useRouter()
const route = useRoute()
const promptFormRef = ref()
const loading = ref(false)

const promptForm = ref({
  title: '',
  description: '',
  content: '',
  categoryId: '',
  tags: []
})

const categories = ref([])

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入提示词内容', trigger: 'blur' },
    { min: 10, message: '提示词内容至少 10 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ]
}

const loadPrompt = async () => {
  const promptId = route.params.id
  if (!promptId) {
    ElMessage.error('提示词ID不存在')
    router.push('/my-prompts')
    return
  }
  
  try {
    const res = await promptsApi.getMyPrompts({ page: 1, size: 100 })
    const prompt = res.data.records.find(p => p.id.toString() === promptId.toString())
    
    if (!prompt) {
      ElMessage.error('提示词不存在')
      router.push('/my-prompts')
      return
    }
    
    promptForm.value = {
      title: prompt.title,
      description: prompt.description,
      content: prompt.content,
      categoryId: prompt.categoryId,
      tags: prompt.tags ? prompt.tags.split(',').filter(tag => tag.trim()) : []
    }
  } catch (error) {
    ElMessage.error('加载提示词失败')
    router.push('/my-prompts')
  }
}

const loadCategories = async () => {
  try {
    const res = await categoriesApi.getAllCategories()
    categories.value = res.data
  } catch (error) {
    ElMessage.error('加载分类失败')
  }
}

const handleUpdate = async () => {
  const valid = await promptFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const promptId = route.params.id
    const data = {
      ...promptForm.value,
      tags: promptForm.value.tags.join(',')
    }
    
    await promptsApi.updatePrompt(promptId, data)
    ElMessage.success('修改成功，提示词已重新提交审核')
    router.push('/my-prompts')
  } catch (error) {
    ElMessage.error('修改失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCategories()
  loadPrompt()
})
</script>

<style scoped>
.edit-prompt {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

h2 {
  margin: 0;
  color: #333;
}
</style>