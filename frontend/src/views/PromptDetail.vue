<template>
  <div class="prompt-detail">
    <el-card v-if="prompt">
      <template #header>
        <div class="detail-header">
          <h1>{{ prompt.title }}</h1>
          <div class="header-actions">
            <el-button
              :type="isLiked ? 'danger' : 'default'"
              :icon="isLiked ? 'HeartFilled' : 'Heart'"
              @click="handleLike"
            >
              {{ isLiked ? '已点赞' : '点赞' }} ({{ prompt.likeCount }})
            </el-button>
            <el-button
              :type="isFavorited ? 'warning' : 'default'"
              :icon="isFavorited ? 'StarFilled' : 'Star'"
              @click="handleFavorite"
            >
              {{ isFavorited ? '已收藏' : '收藏' }} ({{ prompt.favoriteCount }})
            </el-button>
            <el-button 
              type="primary" 
              @click="copyToClipboard"
            >
              <el-icon><DocumentCopy /></el-icon>
              复制提示词
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="prompt-content">
        <div class="prompt-meta-info">
          <div class="info-grid">
            <div class="info-card">
              <el-icon class="info-icon"><Folder /></el-icon>
              <div class="info-content">
                <span class="info-label">分类</span>
                <span class="info-value">{{ prompt.categoryName }}</span>
              </div>
            </div>
            <div class="info-card">
              <el-icon class="info-icon"><User /></el-icon>
              <div class="info-content">
                <span class="info-label">作者</span>
                <span class="info-value">{{ prompt.user?.nickname || prompt.authorName }}</span>
              </div>
            </div>
            <div class="info-card">
              <el-icon class="info-icon"><View /></el-icon>
              <div class="info-content">
                <span class="info-label">使用次数</span>
                <span class="info-value">{{ prompt.usageCount }} 次</span>
              </div>
            </div>
            <div class="info-card" v-if="prompt.tags && prompt.tags.length > 0">
              <el-icon class="info-icon"><CollectionTag /></el-icon>
              <div class="info-content">
                <span class="info-label">标签</span>
                <div class="info-value tags-container">
                  <el-tag
                    v-for="tag in (typeof prompt.tags === 'string' ? prompt.tags.split(',').map(t => t.trim()).filter(t => t) : prompt.tags)"
                    :key="tag"
                    size="small"
                    type="primary"
                    effect="light"
                    style="margin-right: 4px; margin-bottom: 2px;"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="prompt-content-box">
          <pre class="prompt-text">{{ prompt.content }}</pre>
        </div>
        
        <div class="description-section">
          <h3>💡 使用说明</h3>
          <div class="description-box">
            {{ prompt.description }}
          </div>
        </div>
      </div>
    </el-card>

    <!-- 评论区域 -->
    <div class="comments-section" v-if="prompt">
      <div class="section-header">
        <h3>
          <el-icon><ChatDotRound /></el-icon>
          评论 ({{ comments.length }})
        </h3>
      </div>

      <!-- 发表评论 -->
      <div class="comment-form">
        <el-input
          v-model="newComment"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          maxlength="500"
          show-word-limit
        />
        <div class="comment-actions">
          <el-button type="primary" @click="addComment" :disabled="!newComment.trim()">
            发表评论
          </el-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list">
        <div v-if="loadingComments" class="loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>加载评论中...</span>
        </div>
        
        <div v-else-if="comments.length === 0" class="no-comments">
          <el-empty description="暂无评论，快来发表第一条评论吧" />
        </div>
        
        <div v-else>
          <div 
            v-for="comment in comments.filter(c => !c.parentId || c.parentId === 0)" 
            :key="comment.id" 
            class="comment-item"
          >
            <div class="comment-avatar">
              <el-avatar :size="40" :src="comment.user?.avatar || '/default-avatar.png'">
                {{ comment.user?.nickname?.charAt(0) || comment.user?.username?.charAt(0) || 'U' }}
              </el-avatar>
            </div>
            
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.user?.nickname || comment.user?.username || '匿名用户' }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                <el-button 
                  v-if="comment.userId === currentUser?.id" 
                  type="text" 
                  size="small" 
                  @click="deleteComment(comment.id)"
                  class="comment-delete"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
              
              <div class="comment-text">{{ comment.content }}</div>
              
              <div class="comment-actions">
                <el-button 
                  type="text" 
                  size="small" 
                  @click="toggleLikeComment(comment)"
                  :class="{ 'liked': comment.isLiked }"
                >
                  <el-icon>
                    <svg v-if="comment.isLiked" viewBox="0 0 1024 1024" width="14" height="14">
                      <path d="M923 283.6c-13.4-31.1-32.6-58.9-56.9-82.8-24.3-23.8-52.5-42.4-84-55.5-32.5-13.5-66.9-20.3-102.4-20.3-49.3 0-97.4 13.5-139.2 39-10 6.1-19.5 12.8-28.5 19.8-9-7-18.5-13.7-28.5-19.8-41.8-25.5-89.9-39-139.2-39-35.5 0-69.9 6.8-102.4 20.3-31.5 13.1-59.7 31.7-84 55.5-24.4 23.9-43.5 51.7-56.9 82.8-13.9 32.3-21 66.3-21 101.4 0 30.4 6.1 58.7 18.1 84.4 11.9 25.4 28.6 48.7 49.6 69.6 21 21 44.3 37.6 69.6 49.6 25.7 12 54 18.1 84.4 18.1 37.3 0 73.5-10.6 104.8-30.6 31.3 20 67.5 30.6 104.8 30.6 30.4 0 58.7-6.1 84.4-18.1 25.4-11.9 48.7-28.6 69.6-49.6 21-21 37.6-44.3 49.6-69.6 12-25.7 18.1-54 18.1-84.4 0-35.1-7.1-69.1-21-101.4z" fill="currentColor"/>
                    </svg>
                    <svg v-else viewBox="0 0 1024 1024" width="14" height="14">
                      <path d="M923 283.6c-13.4-31.1-32.6-58.9-56.9-82.8-24.3-23.8-52.5-42.4-84-55.5-32.5-13.5-66.9-20.3-102.4-20.3-49.3 0-97.4 13.5-139.2 39-10 6.1-19.5 12.8-28.5 19.8-9-7-18.5-13.7-28.5-19.8-41.8-25.5-89.9-39-139.2-39-35.5 0-69.9 6.8-102.4 20.3-31.5 13.1-59.7 31.7-84 55.5-24.4 23.9-43.5 51.7-56.9 82.8-13.9 32.3-21 66.3-21 101.4 0 30.4 6.1 58.7 18.1 84.4 11.9 25.4 28.6 48.7 49.6 69.6 21 21 44.3 37.6 69.6 49.6 25.7 12 54 18.1 84.4 18.1 37.3 0 73.5-10.6 104.8-30.6 31.3 20 67.5 30.6 104.8 30.6 30.4 0 58.7-6.1 84.4-18.1 25.4-11.9 48.7-28.6 69.6-49.6 21-21 37.6-44.3 49.6-69.6 12-25.7 18.1-54 18.1-84.4 0-35.1-7.1-69.1-21-101.4z" fill="currentColor"/>
                    </svg>
                  </el-icon>
                  {{ comment.likeCount || 0 }}
                </el-button>
                
                <el-button 
                  type="text" 
                  size="small" 
                  @click="startReply(comment)"
                >
                  <el-icon><ChatDotRound /></el-icon>
                  回复
                </el-button>
                
                <el-button 
                  v-if="comment.replies && comment.replies.length > 0"
                  type="text" 
                  size="small" 
                  @click="toggleReplies(comment)"
                >
                  <el-icon>
                    <CaretBottom v-if="!comment.showReplies" />
                    <CaretTop v-else />
                  </el-icon>
                  {{ comment.showReplies ? '收起' : '展开' }}回复 ({{ comment.replies.length }})
                </el-button>
                
                <el-button 
                  v-if="comment.userId === currentUser?.id && canDeleteComment(comment)" 
                  type="text" 
                  size="small" 
                  @click="deleteComment(comment.id)"
                  class="comment-delete"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>

              <!-- 回复框 -->
              <div v-if="replyingTo === comment.id" class="reply-form">
                <el-input
                  v-model="replyContent"
                  type="textarea"
                  :rows="2"
                  placeholder="写下你的回复..."
                  maxlength="200"
                  show-word-limit
                />
                <div class="reply-actions">
                  <el-button type="primary" size="small" @click="submitReply(comment.id)" :disabled="!replyContent.trim()">
                    发表回复
                  </el-button>
                  <el-button size="small" @click="cancelReply">取消</el-button>
                </div>
              </div>

              <!-- 回复列表 -->
              <div v-if="comment.replies && comment.replies.length > 0" class="replies">
                <div 
                  v-for="reply in comment.replies" 
                  :key="reply.id" 
                  class="reply-item"
                  v-show="comment.showReplies"
                >
                  <div class="reply-avatar">
                    <el-avatar :size="32" :src="reply.user?.avatar || '/default-avatar.png'">
                      {{ reply.user?.username?.charAt(0) || 'U' }}
                    </el-avatar>
                  </div>
                  
                  <div class="reply-content">
                    <div class="reply-header">
                      <span class="reply-author">{{ reply.user?.username || '匿名用户' }}</span>
                      <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                      <el-button 
                        v-if="reply.userId === currentUser?.id && canDeleteComment(reply)" 
                        type="text" 
                        size="small" 
                        @click="deleteComment(reply.id)"
                        class="comment-delete"
                      >
                        <el-icon><Delete /></el-icon>
                        删除
                      </el-button>
                    </div>
                    
                    <div class="reply-text">{{ reply.content }}</div>
                    
                    <div class="comment-actions">
                      <el-button 
                        type="text" 
                        size="small" 
                        @click="toggleLikeComment(reply)"
                        :class="{ 'liked': reply.isLiked }"
                      >
                        <el-icon>
                          <svg v-if="reply.isLiked" viewBox="0 0 1024 1024" width="12" height="12">
                            <path d="M923 283.6c-13.4-31.1-32.6-58.9-56.9-82.8-24.3-23.8-52.5-42.4-84-55.5-32.5-13.5-66.9-20.3-102.4-20.3-49.3 0-97.4 13.5-139.2 39-10 6.1-19.5 12.8-28.5 19.8-9-7-18.5-13.7-28.5-19.8-41.8-25.5-89.9-39-139.2-39-35.5 0-69.9 6.8-102.4 20.3-31.5 13.1-59.7 31.7-84 55.5-24.4 23.9-43.5 51.7-56.9 82.8-13.9 32.3-21 66.3-21 101.4 0 30.4 6.1 58.7 18.1 84.4 11.9 25.4 28.6 48.7 49.6 69.6 21 21 44.3 37.6 69.6 49.6 25.7 12 54 18.1 84.4 18.1 37.3 0 73.5-10.6 104.8-30.6 31.3 20 67.5 30.6 104.8 30.6 30.4 0 58.7-6.1 84.4-18.1 25.4-11.9 48.7-28.6 69.6-49.6 21-21 37.6-44.3 49.6-69.6 12-25.7 18.1-54 18.1-84.4 0-35.1-7.1-69.1-21-101.4z" fill="currentColor"/>
                          </svg>
                          <svg v-else viewBox="0 0 1024 1024" width="12" height="12">
                            <path d="M923 283.6c-13.4-31.1-32.6-58.9-56.9-82.8-24.3-23.8-52.5-42.4-84-55.5-32.5-13.5-66.9-20.3-102.4-20.3-49.3 0-97.4 13.5-139.2 39-10 6.1-19.5 12.8-28.5 19.8-9-7-18.5-13.7-28.5-19.8-41.8-25.5-89.9-39-139.2-39-35.5 0-69.9 6.8-102.4 20.3-31.5 13.1-59.7 31.7-84 55.5-24.4 23.9-43.5 51.7-56.9 82.8-13.9 32.3-21 66.3-21 101.4 0 30.4 6.1 58.7 18.1 84.4 11.9 25.4 28.6 48.7 49.6 69.6 21 21 44.3 37.6 69.6 49.6 25.7 12 54 18.1 84.4 18.1 37.3 0 73.5-10.6 104.8-30.6 31.3 20 67.5 30.6 104.8 30.6 30.4 0 58.7-6.1 84.4-18.1 25.4-11.9 48.7-28.6 69.6-49.6 21-21 37.6-44.3 49.6-69.6 12-25.7 18.1-54 18.1-84.4 0-35.1-7.1-69.1-21-101.4z" fill="currentColor"/>
                          </svg>
                        </el-icon>
                        {{ reply.likeCount || 0 }}
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="loading">
      <el-skeleton :rows="5" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { DocumentCopy, Folder, User, View, CollectionTag, ChatDotRound, Delete, Loading, CaretBottom, CaretTop } from '@element-plus/icons-vue'
import { promptsApi } from '../api/prompts'
import { categoriesApi } from '../api/categories'
import { usersApi } from '../api/users'
import { commentsApi } from '../api/comments'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const prompt = ref(null)
const isLiked = ref(false)
const isFavorited = ref(false)
const comments = ref([])
const newComment = ref('')
const replyingTo = ref(null)
const replyContent = ref('')
const loadingComments = ref(false)

const currentUser = computed(() => userStore.user)

const loadPromptDetail = async () => {
  try {
    const res = await promptsApi.getPromptDetail(route.params.id)
    const promptData = res.data
    
    // 获取分类信息
    if (promptData.categoryId) {
      try {
        const categoryRes = await categoriesApi.getCategory(promptData.categoryId)
        promptData.categoryName = categoryRes.data.name
      } catch (error) {
        promptData.categoryName = '未分类'
      }
    } else {
      promptData.categoryName = '未分类'
    }
    
    // 获取作者信息
    if (promptData.userId) {
      try {
        const userRes = await usersApi.getUserById(promptData.userId)
        promptData.authorName = userRes.data.nickname || userRes.data.username || '匿名用户'
      } catch (error) {
        promptData.authorName = '匿名用户'
      }
    } else {
      promptData.authorName = '匿名用户'
    }
    
    prompt.value = promptData
    
    const [likeRes, favoriteRes] = await Promise.all([
      promptsApi.checkLikeStatus(prompt.value.id),
      promptsApi.checkFavoriteStatus(prompt.value.id)
    ])
    isLiked.value = likeRes.data
    isFavorited.value = favoriteRes.data
    
    // 加载评论
    await loadComments()
  } catch (error) {
    ElMessage.error('加载提示词失败')
    router.push('/prompts')
  }
}

const handleLike = async () => {
  try {
    if (isLiked.value) {
      await promptsApi.unlikePrompt(prompt.value.id)
      isLiked.value = false
      prompt.value.likeCount--
    } else {
      await promptsApi.likePrompt(prompt.value.id)
      isLiked.value = true
      prompt.value.likeCount++
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await promptsApi.unfavoritePrompt(prompt.value.id)
      isFavorited.value = false
      prompt.value.favoriteCount--
    } else {
      await promptsApi.favoritePrompt(prompt.value.id)
      isFavorited.value = true
      prompt.value.favoriteCount++
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const copyToClipboard = async () => {
  try {
    await navigator.clipboard.writeText(prompt.value.content)
    ElMessage.success('已复制到剪贴板')
    
    // 增加使用次数（后端会自动处理）
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

const loadComments = async () => {
  if (!prompt.value?.id) {
    console.log('[DEBUG] loadComments: prompt.value.id is null or undefined')
    return
  }
  
  console.log(`[DEBUG] loadComments: Starting to load comments for promptId: ${prompt.value.id}`)
  
  loadingComments.value = true
  try {
    console.log(`[DEBUG] loadComments: Calling commentsApi.getCommentsByPromptId(${prompt.value.id})`)
    const response = await commentsApi.getCommentsByPromptId(prompt.value.id)
    console.log('[DEBUG] loadComments: API response received', response)
    console.log('[DEBUG] loadComments: Response data', response.data)
    
    // 初始化评论数据
    const allComments = response.data || []
    
    // 分组评论：顶级评论和回复
    const topLevelComments = allComments.filter(comment => !comment.parentId || comment.parentId === 0)
    const replies = allComments.filter(comment => comment.parentId && comment.parentId !== 0)
    
    // 为每个评论添加额外属性
    comments.value = topLevelComments.map(comment => ({
      ...comment,
      showReplies: false,
      isLiked: false,
      replies: replies.filter(reply => reply.parentId === comment.id)
    }))
    
    console.log(`[DEBUG] loadComments: Set comments array with ${comments.value.length} items`)
    
    // 加载用户点赞状态
    await loadUserLikes()
    
  } catch (error) {
    console.error('[DEBUG] loadComments: Error occurred', error)
    console.error('[DEBUG] loadComments: Error response', error.response)
    console.error('[DEBUG] loadComments: Error status', error.response?.status)
    console.error('[DEBUG] loadComments: Error message', error.message)
    ElMessage.error('加载评论失败')
  } finally {
    loadingComments.value = false
    console.log('[DEBUG] loadComments: Loading finished')
  }
}

const addComment = async () => {
    if (!newComment.value.trim()) {
      ElMessage.warning('请输入评论内容')
      return
    }

    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录后再发表评论')
      return
    }

    try {
      await commentsApi.addComment({
        promptId: prompt.value.id,
        content: newComment.value.trim(),
        parentId: 0
      })
      
      newComment.value = ''
      ElMessage.success('评论发表成功')
      await loadComments()
    } catch (error) {
      ElMessage.error('发表评论失败')
    }
  }

const deleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await commentsApi.deleteComment(commentId)
    ElMessage.success('删除成功')
    await loadComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const startReply = (comment) => {
  replyingTo.value = comment.id
  replyContent.value = ''
}

const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

const submitReply = async (parentId) => {
    if (!replyContent.value.trim()) {
      ElMessage.warning('请输入回复内容')
      return
    }

    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录后再回复')
      return
    }

    try {
      await commentsApi.addComment({
        promptId: prompt.value.id,
        content: replyContent.value.trim(),
        parentId: parentId
      })
      
      replyContent.value = ''
      replyingTo.value = null
      ElMessage.success('回复成功')
      await loadComments()
    } catch (error) {
      ElMessage.error('回复失败')
    }
  }



const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  // 转换为分钟
  const minutes = Math.floor(diff / 60000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (minutes < 1440) return `${Math.floor(minutes / 60)}小时前`
  if (minutes < 10080) return `${Math.floor(minutes / 1440)}天前`
  
  return date.toLocaleDateString('zh-CN')
}

const canDeleteComment = (comment) => {
  if (!comment || !comment.createTime) return false
  
  const createTime = new Date(comment.createTime)
  const now = new Date()
  const diffHours = (now - createTime) / (1000 * 60 * 60)
  
  return diffHours <= 24
}

const toggleLikeComment = async (comment) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再点赞')
    return
  }

  try {
    if (comment.isLiked) {
      await commentsApi.unlikeComment(comment.id)
      comment.likeCount--
      comment.isLiked = false
    } else {
      await commentsApi.likeComment(comment.id)
      comment.likeCount++
      comment.isLiked = true
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const toggleReplies = (comment) => {
  console.log('Toggle replies for comment:', comment.id, 'Current state:', comment.showReplies)
  comment.showReplies = !comment.showReplies
  console.log('New state:', comment.showReplies)
}

const loadUserLikes = async () => {
  if (!userStore.isLoggedIn || !comments.value.length) return
  
  try {
    // 收集所有评论和回复的ID
    const allCommentIds = []
    comments.value.forEach(comment => {
      allCommentIds.push(comment.id)
      if (comment.replies && comment.replies.length > 0) {
        comment.replies.forEach(reply => {
          allCommentIds.push(reply.id)
        })
      }
    })
    
    const likedIds = await commentsApi.getUserLikedCommentIds(allCommentIds)
    
    // 设置评论和回复的点赞状态
    comments.value.forEach(comment => {
      comment.isLiked = likedIds.includes(comment.id)
      if (comment.replies && comment.replies.length > 0) {
        comment.replies.forEach(reply => {
          reply.isLiked = likedIds.includes(reply.id)
        })
      }
    })
  } catch (error) {
    console.error('加载点赞状态失败', error)
  }
}

onMounted(() => {
  loadPromptDetail()
  loadComments()
})
</script>

<style scoped>
.prompt-detail {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
  padding: 20px 0;
  border-bottom: 1px solid #e9ecef;
}

.detail-header h1 {
  margin: 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.prompt-content {
  margin-top: 0;
}

.prompt-meta-info {
  margin-bottom: 40px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.info-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.info-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.info-icon {
  font-size: 24px;
  color: #409eff;
  margin-right: 12px;
}

.info-content {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.tags-section {
  margin-top: 24px;
}

.tags-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 12px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}



.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e9ecef;
}

.content-title {
  margin: 0;
  color: #2c3e50;
  font-size: 26px;
  font-weight: 700;
}

.prompt-content-box {
  background: #fff;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 30px;
}

.prompt-text {
  margin: 0;
  padding: 24px;
  line-height: 1.8;
  font-size: 16px;
  color: #495057;
  background: #f8f9fa;
  font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, 'Courier New', monospace;
  white-space: pre-wrap;
  word-wrap: break-word;
  max-height: none;
  overflow: visible;
  min-height: 200px;
}

.description-section {
  margin-top: 30px;
}

.description-section h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.description-box {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  line-height: 1.8;
  color: #495057;
  font-size: 16px;
  border-left: 4px solid #28a745;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.copy-btn {
  margin: 0;
}

.loading {
  text-align: center;
  padding: 60px 0;
}

/* 评论区域样式 */
.comments-section {
  margin-top: 40px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.comment-form {
  margin-bottom: 32px;
}

.comment-form .el-textarea {
  margin-bottom: 12px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.comments-list {
  margin-top: 24px;
}

.comment-item {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #e9ecef;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-delete {
  margin-left: auto;
  color: #f56c6c;
}

.comment-text {
  color: #303133;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-actions {
  margin-top: 8px;
}

.reply-form {
  margin-top: 16px;
  padding: 16px;
  background: #f8f9fa;
}
 .comment-actions {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.comment-actions .el-button {
  padding: 4px 8px;
  height: auto;
  line-height: 1.4;
  font-size: 12px;
  color: #666;
  transition: all 0.3s ease;
}

.comment-actions .el-button:hover {
  color: #409EFF;
  transform: translateY(-1px);
}

.comment-actions .el-button.liked {
  color: #ff6b6b;
  font-weight: 500;
}

.comment-actions .el-button.liked:hover {
  color: #ff5252;
}

.comment-actions .comment-delete {
  color: #f56c6c;
}

.comment-actions .comment-delete:hover {
  color: #ff4d4f;
  transform: translateY(-1px);
}

.reply-actions {
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-end;
}

.reply-actions .el-button {
  padding: 2px 6px;
  height: auto;
  line-height: 1.2;
  font-size: 11px;
  color: #666;
  transition: all 0.3s ease;
}

.reply-actions .el-button:hover {
  color: #409EFF;
  transform: translateY(-1px);
}

.reply-actions .el-button.liked {
  color: #ff6b6b;
  font-weight: 500;
}

.reply-actions .el-button.liked:hover {
  color: #ff5252;
}

.reply-item {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.replies {
  margin-top: 16px;
  padding-left: 56px;
}

.reply-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-avatar {
  flex-shrink: 0;
}

.reply-content {
  flex: 1;
  min-width: 0;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.reply-author {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}

.reply-text {
  color: #303133;
  line-height: 1.5;
  font-size: 14px;
}

.no-comments {
  text-align: center;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .detail-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .info-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .comment-item {
    flex-direction: column;
    gap: 8px;
  }
  
  .replies {
    padding-left: 0;
    margin-left: 0;
  }
}
  
  .content-title {
    font-size: 22px;
  }
  
  .prompt-text {
    padding: 16px;
    font-size: 14px;
  }
  
  .description-box {
    padding: 16px;
  }


:deep(.el-card) {
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

:deep(.el-tag--info.is-plain) {
  color: #6c757d;
  background-color: rgba(108, 117, 125, 0.1);
  border-color: rgba(108, 117, 125, 0.3);
}
</style>