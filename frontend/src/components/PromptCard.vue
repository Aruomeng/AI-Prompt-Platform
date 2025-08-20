<template>
  <el-card class="prompt-card" shadow="hover" body-style="height: 100%; display: flex; flex-direction: column;">
    <template #header>
      <div class="card-header">
        <h3>{{ prompt.title }}</h3>
        <div class="card-actions">
          <el-button
            :type="isLiked ? 'primary' : 'default'"
            circle
            size="small"
            @click="handleLike"
          >
            <el-icon>
              <CollectionTag />
            </el-icon>
          </el-button>
          <el-button
            :type="isFavorited ? 'warning' : 'default'"
            circle
            size="small"
            @click="handleFavorite"
          >
            <el-icon>
              <star />
            </el-icon>
          </el-button>
          <el-button
            type="danger"
            circle
            size="small"
            @click="handleDelete"
            v-if="showDelete"
          >
            <el-icon>
              <delete />
            </el-icon>
          </el-button>
        </div>
      </div>
    </template>
    
    <div class="prompt-content" style="flex: 1;">
      <p class="description">{{ prompt.description }}</p>
      <el-tag
        v-for="tag in (typeof prompt.tags === 'string' ? prompt.tags.split(',').map(t => t.trim()).filter(t => t) : prompt.tags)"
        :key="tag"
        size="small"
        class="tag"
      >
        {{ tag }}
      </el-tag>
    </div>
    
    <div class="prompt-footer" style="margin-top: auto;">
      <div class="audit-info" v-if="prompt.status" :data-status="prompt.status">
        <el-tag 
          :type="getStatusType(prompt.status)" 
          size="small"
          class="audit-status"
          effect="dark"
        >
          {{ getStatusText(prompt.status) }}
        </el-tag>
        <div v-if="prompt.status === 'REJECTED' && prompt.reviewComment" class="review-comment">
          <el-tooltip :content="prompt.reviewComment" placement="top">
            <span class="comment-text">{{ truncateComment(prompt.reviewComment) }}</span>
          </el-tooltip>
        </div>
      </div>
      <div class="stats">
        <span>👍 {{ prompt.likeCount }}</span>
        <span>⭐ {{ prompt.favoriteCount }}</span>
        <span>👀 {{ prompt.usageCount }}</span>
      </div>
      <div class="footer-actions">
          <el-button type="success" size="small" @click="copyPrompt">
            <el-icon><DocumentCopy /></el-icon>
            复制
          </el-button>
          <el-button 
            v-if="prompt.status === 'REJECTED'"
            type="warning" 
            size="small" 
            @click="editPrompt"
          >
            <el-icon><Edit /></el-icon>
            修改
          </el-button>
          <el-button 
            v-else-if="prompt.status === 'APPROVED'"
            type="primary" 
            size="small" 
            @click="viewDetail"
          >
            <el-icon><Tickets /></el-icon>
            详情
          </el-button>
          <el-button 
            v-else
            type="info" 
            size="small" 
            disabled
          >
            <el-icon><Clock /></el-icon>
            待审核
          </el-button>
        </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { DocumentCopy, Tickets, Edit, Clock } from '@element-plus/icons-vue'

import { promptsApi } from '../api/prompts'

const props = defineProps({
  prompt: {
    type: Object,
    required: true
  },
  showDelete: {
    type: Boolean,
    default: false
  }
})

const router = useRouter()
const isLiked = ref(false)
const isFavorited = ref(false)

const handleLike = async () => {
  try {
    if (isLiked.value) {
      await promptsApi.unlikePrompt(props.prompt.id)
      isLiked.value = false
      props.prompt.likeCount--
    } else {
      await promptsApi.likePrompt(props.prompt.id)
      isLiked.value = true
      props.prompt.likeCount++
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const handleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await promptsApi.unfavoritePrompt(props.prompt.id)
      isFavorited.value = false
      props.prompt.favoriteCount--
    } else {
      await promptsApi.favoritePrompt(props.prompt.id)
      isFavorited.value = true
      props.prompt.favoriteCount++
    }
  } catch (error) {
    console.error('收藏失败:', error)
  }
}

const viewDetail = () => {
  router.push(`/prompts/${props.prompt.id}`)
}

const editPrompt = () => {
  router.push(`/prompts/${props.prompt.id}/edit`)
}

const copyPrompt = async () => {
  try {
    await navigator.clipboard.writeText(props.prompt.content)
    ElMessage.success('提示词已复制到剪贴板')
  } catch (error) {
    console.error('复制失败:', error)
    // 降级方案：使用传统的复制方法
    const textArea = document.createElement('textarea')
    textArea.value = props.prompt.content
    document.body.appendChild(textArea)
    textArea.select()
    try {
      document.execCommand('copy')
      ElMessage.success('提示词已复制到剪贴板')
    } catch (err) {
      ElMessage.error('复制失败，请手动复制')
    }
    document.body.removeChild(textArea)
  }
}

const getStatusType = (status) => {
  const statusMap = {
 'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已拒绝'
  }
  return statusMap[status] || status
}

const truncateComment = (comment) => {
  if (!comment) return ''
  return comment.length > 20 ? comment.substring(0, 20) + '...' : comment
}

const emit = defineEmits(['delete'])

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个提示词吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await promptsApi.deletePrompt(props.prompt.id)
    ElMessage.success('删除成功')
    emit('delete', props.prompt.id)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(async () => {
  try {
    const [likeRes, favoriteRes] = await Promise.all([
      promptsApi.checkLikeStatus(props.prompt.id),
      promptsApi.checkFavoriteStatus(props.prompt.id)
    ])
    isLiked.value = likeRes.data
    isFavorited.value = favoriteRes.data
  } catch (error) {
    console.error('检查状态失败:', error)
  }
})
</script>

<style scoped>
.prompt-card {
  margin-bottom: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.prompt-content {
  margin-bottom: 16px;
  flex: 1;
}

.description {
  margin-bottom: 12px;
  color: #666;
  line-height: 1.5;
  min-height: 45px;
}

.tag {
  margin-right: 8px;
  margin-bottom: 4px;
}

.prompt-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.stats {
  display: flex;
  gap: 16px;
  color: #999;
  font-size: 14px;
}

.footer-actions {
  display: flex;
  gap: 8px;
}

.audit-info {
  margin-top: 10px;
  padding: 8px 0;
}

.audit-status {
  margin-bottom: 4px;
  font-weight: bold;
}

.review-comment {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

.comment-text {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>