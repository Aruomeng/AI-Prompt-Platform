<template>
  <div class="create-prompt">
    <el-card>
      <template #header>
        <h2>创建提示词</h2>
      </template>
      
      <el-form
        ref="promptFormRef"
        :model="promptForm"
        :rules="rules"
        label-width="100px"
        @submit.prevent="handleCreate"
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
            创建提示词
          </el-button>
          <el-button @click="$router.push('/prompts')">
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { promptsApi } from '../api/prompts'
import { categoriesApi } from '../api/categories'

const router = useRouter()
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
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入描述', trigger: 'blur' },
    { min: 10, max: 500, message: '长度在 10 到 500 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入提示词内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '长度在 10 到 2000 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ]
}

const loadCategories = async () => {
  try {
    const res = await categoriesApi.getAllCategories()
    categories.value = res.data
  } catch (error) {
    ElMessage.error('加载分类失败')
  }
}

const handleCreate = async () => {
  if (!promptFormRef.value) return
  
  await promptFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 转换数据格式以匹配后端要求
        const submitData = {
          title: promptForm.value.title,
          content: promptForm.value.content,
          description: promptForm.value.description,
          categoryId: Number(promptForm.value.categoryId),
          tags: promptForm.value.tags.join(',')
        }
        await promptsApi.createPrompt(submitData)
        ElMessage.success('创建成功')
        router.push('/my-prompts')
      } catch (error) {
        console.error('创建失败:', error)
        const errorMsg = error.response?.data?.message || '创建失败'
        ElMessage.error(errorMsg)
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.create-prompt {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.create-prompt h2 {
  margin: 0;
  color: #333;
}
</style>