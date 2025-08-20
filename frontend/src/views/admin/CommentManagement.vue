<template>
  <div class="comment-management">
    <el-card class="comment-card">
      <template #header>
        <div class="card-header">
          <span>评论管理</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索评论内容"
              style="width: 300px; margin-right: 10px"
              @keyup.enter="searchComments"
            >
              <template #append>
                <el-button @click="searchComments">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
            <el-button type="danger" @click="batchDelete" :disabled="selectedComments.length === 0">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button type="primary" @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="treeData"
        style="width: 100%; flex: 1"
        row-key="id"
        v-loading="loading"
        :expand-row-keys="expandedRows"
        @expand-change="handleExpandChange"
        @selection-change="handleSelectionChange"
        class="comment-table"
      >
        <el-table-column type="expand" width="50">
          <template #default="{ row }">
            <div v-if="row.hasChildren && row.children && row.children.length > 0" class="children-comments">
              <el-table
                :data="row.children"
                style="width: 100%"
                :show-header="false"
              >
                <el-table-column width="55" />
                <el-table-column width="50" />
                <el-table-column label="内容" width="350" show-overflow-tooltip>
                  <template #default="{ row: child }">
                    <div class="comment-text" style="padding-left: 50px;">↳ {{ child.content }}</div>
                  </template>
                </el-table-column>
                <el-table-column prop="userName" label="用户" width="90" />
                <el-table-column prop="promptTitle" label="关联提示词" width="130" show-overflow-tooltip />
                <el-table-column prop="categoryName" label="分类" width="70" />
                <el-table-column prop="likes" label="点赞" width="60" align="center" />
                <el-table-column prop="createdAt" label="时间" width="200">
                  <template #default="{ row: child }">
                    {{ formatDate(child.createdAt) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="{ row: child }">
                    <el-button type="danger" size="small" @click="deleteComment(child)">
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-else class="no-children">
              暂无回复
            </div>
          </template>
        </el-table-column>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="content" label="评论内容" width="350" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="comment-text">{{ row.content || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="user" label="作者" width="90">
          <template #default="{ row }">
            <span>{{ row.user?.nickname || row.user?.username || '匿名' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="promptTitle" label="所属提示词" width="130" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="prompt-cell" :title="row.promptTitle">
              {{ row.promptTitle || '-' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="70">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.categoryName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评论时间" width="140">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
              {{ row.status === 'active' ? '正常' : '已删除' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="deleteComment(row)">
              <el-icon><Delete /></el-icon>
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search, Delete, ChatLineRound } from '@element-plus/icons-vue'
import request from '../../utils/request'

const commentList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const selectedComments = ref([])



// 树形数据
const treeData = ref([])
const expandedRows = ref([])

const loadComments = async () => {
  loading.value = true
  try {
    const response = await request.get('/admin/comments', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value
      }
    })
    
    console.log('API Response:', response)
    
    // 获取所有评论
    const allComments = response.data?.records || response.data || []
    
    console.log('All comments:', allComments)
    
    // 构建树形数据
    treeData.value = buildTreeData(allComments)
    total.value = response.data?.total || allComments.length
    
    console.log('Tree data:', treeData.value)
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('加载失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 构建树形数据结构
const buildTreeData = (comments) => {
  const commentMap = new Map()
  const tree = []
  
  // 首先将所有评论放入Map中
  comments.forEach(comment => {
    commentMap.set(comment.id, { 
      ...comment, 
      children: [],
      level: 0,
      hasChildren: false,
      expanded: false
    })
  })
  
  // 构建树形结构
  comments.forEach(comment => {
    const node = commentMap.get(comment.id)
    if (comment.parentId && comment.parentId !== 0) {
      // 这是一个回复
      const parent = commentMap.get(comment.parentId)
      if (parent) {
        parent.children = parent.children || []
        parent.children.push(node)
        parent.hasChildren = true
        node.level = (parent.level || 0) + 1
      }
    } else {
      // 这是一个主评论
      node.level = 0
      tree.push(node)
    }
  })
  
  // 只返回主评论（顶级节点），回复会在展开行中显示
  return tree.filter(node => !node.parentId || node.parentId === 0)
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const handleSelectionChange = (selection) => {
  selectedComments.value = selection
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadComments()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadComments()
}

const searchComments = () => {
  currentPage.value = 1
  loadComments()
}

const deleteComment = async (comment) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除该评论吗？${comment.children && comment.children.length > 0 ? '该评论下的所有回复也将被删除！' : ''}`,
      '确认删除',
      { type: 'warning' }
    )
    
    await request.delete(`/admin/comments/${comment.id}`)
    
    ElMessage.success('删除成功')
    loadComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const batchDelete = async () => {
  if (selectedComments.value.length === 0) {
    ElMessage.warning('请选择要删除的评论')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedComments.value.length} 条评论吗？此操作不可恢复！`,
      '确认批量删除',
      { type: 'warning' }
    )
    
    const commentIds = selectedComments.value.map(comment => comment.id)
    await request.post('/admin/comments/batch-delete', { commentIds })
    
    ElMessage.success('批量删除成功')
    loadComments()
    selectedComments.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const refreshData = () => {
  loadComments()
}

const handleExpandChange = (row, expandedRowsList) => {
  expandedRows.value = expandedRowsList.map(r => r.id)
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-management {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.comment-card {
  flex: 1;
  display: flex;
  flex-direction: column;
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

.content-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px 0;
}

.comment-text {
  font-size: 14px;
  line-height: 1.2;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.prompt-cell {
  font-size: 14px;
  color: #303133;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-cell {
  display: flex;
  align-items: center;
}

.time-cell {
  font-size: 14px;
  color: #606266;
}

.status-cell {
  display: flex;
  align-items: center;
}

.actions-cell {
  display: flex;
  align-items: center;
}

.expand-icon {
  margin-right: 4px;
  cursor: pointer;
  color: #409eff;
}

.expand-icon:hover {
  color: #66b1ff;
}

.children-comments {
  padding: 0;
  background-color: #f9f9f9;
  border-left: 3px solid #409eff;
  margin-left: 20px;
}

.children-comments .el-table {
  background-color: transparent;
}

.children-comments .el-table__row {
  background-color: transparent !important;
}

.children-comments .el-table__row:hover {
  background-color: #f5f7fa !important;
}

.no-children {
  padding: 20px;
  text-align: center;
  color: #909399;
  font-size: 14px;
}

.comment-management :deep(.el-table__row) {
  height: 40px;
}

.comment-management :deep(.el-table__cell) {
  padding: 8px 0;
}

.tree-table-container :deep(.el-table-v2__row:hover) {
  background-color: #f5f7fa;
}

.tree-table-container :deep(.el-table-v2__row--level-1) {
  background-color: #fafafa;
}

.tree-table-container :deep(.el-table-v2__row--level-2) {
  background-color: #fdfdfd;
}

.tree-indent {
  display: inline-block;
  width: 16px;
  height: 1px;
}

.expand-icon {
  cursor: pointer;
  color: #409eff;
  margin-right: 4px;
  transition: transform 0.2s;
}

.expand-icon.expanded {
  transform: rotate(90deg);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
}

.status-tag {
  font-size: 12px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.comment-text {
  margin: 4px 0;
  color: #303133;
}

.comment-meta {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>