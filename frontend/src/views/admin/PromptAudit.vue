<template>
  <div class="prompt-audit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>提示词审核</span>
          <el-button type="primary" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
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
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetail(row)">
              详情
            </el-button>
            <el-button type="success" size="small" @click="auditPrompt(row, 'approved')">
              通过
            </el-button>
            <el-button type="danger" size="small" @click="openRejectDialog(row)">
              拒绝
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
import { Refresh } from '@element-plus/icons-vue'
import request from '../../utils/request'

const promptList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const currentPrompt = ref(null)
const rejectDialogVisible = ref(false)
const currentRejectPrompt = ref(null)
const rejectReason = ref('')
const rejectReasons = [
  '内容不符合平台规范',
  '包含敏感或不当内容',
  '存在版权问题',
  '重复或低质量内容',
  '描述不清晰或不完整',
  '分类选择不当',
  '其他原因'
]

const loadPendingPrompts = async () => {
  loading.value = true
  try {
    const response = await request.get('/admin/prompts/pending', {
        params: {
          page: currentPage.value,
          size: pageSize.value
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

const handleSizeChange = (val) => {
  pageSize.value = val
  loadPendingPrompts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadPendingPrompts()
}

const viewDetail = (row) => {
  currentPrompt.value = row
  dialogVisible.value = true
}

const openRejectDialog = (prompt) => {
  currentRejectPrompt.value = prompt
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const handleReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请选择或输入拒绝理由')
    return
  }
  
  try {
    await request.post(`/admin/prompts/${currentRejectPrompt.value.id}/audit?status=rejected&reviewComment=${encodeURIComponent(rejectReason.value)}`)
    
    ElMessage.success('拒绝成功')
    rejectDialogVisible.value = false
    loadPendingPrompts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const auditPrompt = async (prompt, status) => {
  if (status === 'rejected') {
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要通过该提示词吗？`,
      '确认通过',
      { type: 'warning' }
    )
    
    await request.post(`/admin/prompts/${prompt.id}/audit?status=approved&reviewComment=${encodeURIComponent('审核通过')}`)
    
    ElMessage.success('审核通过')
    loadPendingPrompts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const refreshData = () => {
  loadPendingPrompts()
}

onMounted(() => {
  loadPendingPrompts()
})
</script>

<style scoped>
.prompt-audit {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
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

.reject-reason-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reject-reason-item {
  display: block;
  margin-right: 0;
  padding: 8px 0;
}

.reject-reason-item :deep(.el-radio__label) {
  font-size: 14px;
  color: #333;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 10px 20px 20px;
}
</style>