// 国际化配置文件 - 中文
export default {
  // 通用消息
  success: '操作成功',
  failed: '操作失败',
  error: '错误',
  warning: '警告',
  info: '提示',
  
  // 用户相关
  user: {
    login: '登录',
    logout: '登出',
    register: '注册',
    username: '用户名',
    password: '密码',
    email: '邮箱',
    nickname: '昵称',
    loginSuccess: '登录成功',
    logoutSuccess: '登出成功',
    registerSuccess: '注册成功',
    loginFailed: '登录失败',
    registerFailed: '注册失败',
    userNotFound: '用户不存在',
    passwordIncorrect: '密码错误',
    usernameExists: '用户名已存在',
    emailExists: '邮箱已存在',
  },
  
  // 提示词相关
  prompt: {
    title: '标题',
    content: '内容',
    description: '描述',
    category: '分类',
    tags: '标签',
    create: '创建',
    edit: '编辑',
    delete: '删除',
    search: '搜索',
    like: '点赞',
    unlike: '取消点赞',
    favorite: '收藏',
    unfavorite: '取消收藏',
    usage: '使用次数',
    createSuccess: '创建成功',
    createFailed: '创建失败',
    updateSuccess: '更新成功',
    updateFailed: '更新失败',
    deleteSuccess: '删除成功',
    deleteFailed: '删除失败',
    likeSuccess: '点赞成功',
    unlikeSuccess: '取消点赞成功',
    favoriteSuccess: '收藏成功',
    unfavoriteSuccess: '取消收藏成功',
    notFound: '提示词不存在',
    approved: '已批准',
    pending: '待审核',
  },
  
  // 分类相关
  category: {
    name: '分类名称',
    all: '全部分类',
    writing: '写作助手',
    programming: '编程助手',
    learning: '学习助手',
    life: '生活助手',
    work: '工作助手',
    creativity: '创意灵感',
  },
  
  // 按钮和操作
  button: {
    submit: '提交',
    cancel: '取消',
    confirm: '确认',
    delete: '删除',
    edit: '编辑',
    save: '保存',
    clear: '清空',
    search: '搜索',
    reset: '重置',
    back: '返回',
    next: '下一步',
    previous: '上一步',
    close: '关闭',
    open: '打开',
  },
  
  // 分页
  pagination: {
    previous: '上一页',
    next: '下一页',
    total: '共',
    items: '条',
    page: '第',
    of: '页，共',
    pages: '页',
  },
  
  // 验证消息
  validation: {
    required: '此字段为必填项',
    email: '请输入有效的邮箱地址',
    minLength: '长度不能少于 {min} 个字符',
    maxLength: '长度不能超过 {max} 个字符',
    passwordMinLength: '密码至少需要 8 个字符',
    passwordRequirements: '密码必须包含大小写字母、数字和特殊字符',
  },
  
  // 确认对话框
  confirm: {
    deletePrompt: '确定要删除此提示词吗？',
    unfavorite: '确定要取消收藏吗？',
    logout: '确定要登出吗？',
  },
  
  // 空状态
  empty: {
    noData: '暂无数据',
    noResults: '未找到相关数据',
    noFavorites: '暂无收藏',
    noPrompts: '暂无提示词',
  },
  
  // 错误消息
  errorMessage: {
    networkError: '网络错误，请检查网络连接',
    serverError: '服务器错误，请稍后重试',
    unauthorized: '未授权，请登录',
    forbidden: '禁止访问',
    notFound: '资源不存在',
    validationError: '数据验证失败',
    unknown: '发生未知错误，请稍后重试',
  },
  
  // 导航
  nav: {
    home: '首页',
    promptList: '提示词列表',
    myPrompts: '我的提示词',
    myFavorites: '我的收藏',
    createPrompt: '创建提示词',
    admin: '管理后台',
    profile: '个人资料',
    settings: '设置',
    logout: '登出',
  },
}
