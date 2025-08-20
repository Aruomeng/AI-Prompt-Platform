import request from '../utils/request'

export const commentsApi = {
  /**
   * 获取提示词的评论列表
   */
  getCommentsByPromptId(promptId) {
    console.log(`[DEBUG] commentsApi.getCommentsByPromptId: Making request for promptId: ${promptId}`)
    console.log(`[DEBUG] commentsApi.getCommentsByPromptId: URL will be /comments/prompt/${promptId}`)
    
    return request({
      url: `/comments/prompt/${promptId}`,
      method: 'get'
    }).then(response => {
      console.log('[DEBUG] commentsApi.getCommentsByPromptId: Request successful', response)
      return response
    }).catch(error => {
      console.error('[DEBUG] commentsApi.getCommentsByPromptId: Request failed', error)
      console.error('[DEBUG] commentsApi.getCommentsByPromptId: Error config', error.config)
      console.error('[DEBUG] commentsApi.getCommentsByPromptId: Error response', error.response)
      throw error
    })
  },

  /**
   * 添加评论
   */
  addComment(data) {
    return request({
      url: '/comments',
      method: 'post',
      data
    })
  },

  /**
   * 删除评论
   */
  deleteComment(commentId) {
    return request({
      url: `/comments/${commentId}`,
      method: 'delete'
    })
  },

  /**
   * 获取评论的回复列表
   */
  getReplies(parentId) {
    return request({
      url: `/comments/${parentId}/replies`,
      method: 'get'
    })
  },

  /**
   * 点赞评论
   */
  likeComment(commentId) {
    return request({
      url: `/comments/${commentId}/like`,
      method: 'post'
    })
  },

  /**
   * 取消点赞评论
   */
  unlikeComment(commentId) {
    return request({
      url: `/comments/${commentId}/like`,
      method: 'delete'
    })
  },

  /**
   * 检查评论点赞状态
   */
  checkCommentLikeStatus(commentId) {
    return request({
      url: `/comments/${commentId}/like-status`,
      method: 'get'
    })
  },

  /**
   * 批量获取用户点赞的评论ID
   */
  getUserLikedCommentIds(commentIds) {
    return request({
      url: '/comments/liked-ids',
      method: 'post',
      data: commentIds
    })
  }
}