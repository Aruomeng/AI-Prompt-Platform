<template>
  <div class="prompt-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>提示词管理</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索提示词标题或描述"
              style="width: 300px; margin-right: 10px"
              @keyup.enter="searchPrompts"
            >
              <template #append>
                <el-button @click="searchPrompts">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
            <el-button type="primary" @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="promptList"
        v-loading="loading"
        style="width: 100%"
        border
      >
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="description" label="描述" min-width="250" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="user.nickname" label="作者" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="80" />
        <el-table-column prop="likeCount" label="点赞数" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetail(row)">
              详情
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="success" 
              size="small" 
              @click="approvePrompt(row)">
              通过
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="warning" 
              size="small" 
              @click="rejectPrompt(row)">
              拒绝
            </el-button>
            <el-button type="danger" size="small" @click="deletePrompt(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="提示词详情"
      width="60%"
    >
      <div v-if="currentPrompt">
        <h3>{{ currentPrompt.title }}</h3>
        <p><strong>描述：</strong>{{ currentPrompt.description }}</p>
        <p><strong>分类：</strong>{{ currentPrompt.categoryName }}</p>
        <p><strong>作者：</strong>{{ currentPrompt.user.nickname }}</p>
        <p><strong>状态：</strong>{{ getStatusText(currentPrompt.status) }}</p>
        <p><strong>浏览量：</strong>{{ currentPrompt.viewCount }}</p>
        <p><strong>点赞数：</strong>{{ currentPrompt.likeCount }}</p>
        <p><strong>创建时间：</strong>{{ formatDate(currentPrompt.createTime) }}</p>
        <div class="prompt-content">
          <h4>提示词内容：</h4>
          <pre>{{ currentPrompt.content }}</pre>
        </div>
      </div>
    </el-dialog>

    <!-- 拒绝理由对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="选择拒绝理由"
      width="500px"
    >
      <div>
        <p style="margin-bottom: 15px; color: #666;">
          请选择拒绝该提示词的理由：
        </p>
        <el-select 
          v-model="rejectReason" 
          placeholder="请选择拒绝理由" 
          style="width: 100%; margin-bottom: 15px;"
          filterable
          allow-create
        >
          <el-option
            v-for="reason in rejectReasons"
            :key="reason"
            :label="reason"
            :value="reason"
          />
        </el-select>
        <el-input
          v-if="rejectReason && !rejectReasons.includes(rejectReason)"
          v-model="rejectReason"
          type="textarea"
          :rows="3"
          placeholder="请输入具体的拒绝理由..."
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="handleReject" :disabled="!rejectReason">
            确认拒绝
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import request from '../../utils/request'

const promptList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const dialogVisible = ref(false)
const currentPrompt = ref(null)
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const rejectReasons = [
  '内容不符合规范',
  '重复提交',
  '涉嫌侵权',
  '垃圾信息',
  '其他原因'
]
const currentRejectPrompt = ref(null)

const loadPrompts = async () => {
  loading.value = true
  try {
    const response = await request.get('/admin/prompts', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value
      }
    })
    promptList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    pending: '待审核',
    approved: '已审核',
    rejected: '已拒绝'
  }
  return texts[status] || status
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadPrompts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadPrompts()
}

const searchPrompts = () => {
  currentPage.value = 1
  loadPrompts()
}

const viewDetail = (row) => {
  currentPrompt.value = row
  dialogVisible.value = true
}

const deletePrompt = async (prompt) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该提示词吗？此操作不可恢复！',
      '确认删除',
      { type: 'warning' }
    )
    
    await request.delete(`/admin/prompts/${prompt.id}`)
    
    ElMessage.success('删除成功')
    loadPrompts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleReject = async () => {
  if (!rejectReason.value || !currentRejectPrompt.value) return
  
  try {
    await request.put(`/admin/prompts/${currentRejectPrompt.value.id}/reject`, {
      reason: rejectReason.value
    })
    
    ElMessage.success('已拒绝')
    rejectDialogVisible.value = false
    rejectReason.value = ''
    currentRejectPrompt.value = null
    loadPrompts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const rejectPrompt = (prompt) => {
  currentRejectPrompt.value = prompt
  rejectDialogVisible.value = true
  rejectReason.value = ''
}

const approvePrompt = async (prompt) => {
  try {
    await ElMessageBox.confirm(
      '确定要通过该提示词吗？',
      '确认通过',
      { type: 'warning' }
    )
    
    await request.put(`/admin/prompts/${prompt.id}/approve`)
    
    ElMessage.success('已通过')
    loadPrompts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const refreshData = () => {
  loadPrompts()
}

onMounted(() => {
  loadPrompts()
})
</script>

<style scoped>
.prompt-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.prompt-content pre {
  background-color: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>