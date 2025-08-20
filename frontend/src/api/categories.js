import request from '../utils/request'

export const categoriesApi = {
  // 获取所有分类
  getAllCategories() {
    return request.get('/categories/list')
  },
  
  // 获取分类详情
  getCategory(id) {
    return request.get(`/categories/${id}`)
  }
}