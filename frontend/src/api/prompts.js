import request from '../utils/request'

export const promptsApi = {
  // 获取提示词列表
  getPrompts(params) {
    return request.get('/prompts/public/list', { params })
  },
  
  // 获取提示词详情
  getPromptDetail(id) {
    return request.get(`/prompts/public/${id}`)
  },
  
  // 创建提示词
  createPrompt(data) {
    return request.post('/prompts/create', data)
  },
  
  // 获取我的提示词
  getMyPrompts(params) {
    return request.get('/prompts/my', { params })
  },
  
  // 删除提示词
  deletePrompt(id) {
    return request.delete(`/prompts/${id}`)
  },
  
  // 点赞提示词
  likePrompt(id) {
    return request.post(`/prompts/${id}/like`)
  },
  
  // 取消点赞提示词
  unlikePrompt(id) {
    return request.delete(`/prompts/${id}/like`)
  },
  
  // 收藏提示词
  favoritePrompt(id) {
    return request.post(`/prompts/${id}/favorite`)
  },
  
  // 取消收藏提示词
  unfavoritePrompt(id) {
    return request.delete(`/prompts/${id}/favorite`)
  },
  
  // 检查点赞状态
  checkLikeStatus(id) {
    return request.get(`/prompts/${id}/like-status`)
  },
  
  // 检查收藏状态
  checkFavoriteStatus(id) {
    return request.get(`/prompts/${id}/favorite-status`)
  },
  
  // 获取我的收藏列表
  getFavorites(params) {
    return request.get('/prompts/my-favorites', { params })
  },

  // 更新提示词
  updatePrompt(id, data) {
    return request.put(`/prompts/${id}`, data)
  }
}