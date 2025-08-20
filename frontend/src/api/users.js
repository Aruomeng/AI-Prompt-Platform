import request from '../utils/request'

export const usersApi = {
  // 根据用户ID获取用户信息
  getUserById(userId) {
    return request.get(`/users/${userId}`)
  },
  
  // 根据用户名获取用户信息
  getUserByUsername(username) {
    return request.get(`/users/username/${username}`)
  }
}