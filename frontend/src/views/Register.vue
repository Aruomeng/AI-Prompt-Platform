<template>
  <div class="register-container">
    <div class="register-wrapper">
      <div class="register-logo">
        <div class="logo-icon">
          <div class="ai-logo">
            <div class="ai-icon">AI</div>
          </div>
        </div>
        <div class="logo-text">
          <span class="ai-badge">Prompt</span>
        </div>
      </div>
      
      <el-card class="register-card">
        <template #header>
          <div class="register-header">
            <h2>欢迎加入！</h2>
            <p class="subtitle">创建您的账号，开启AI创作的无限可能</p>
          </div>
        </template>
        
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleRegister"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请为您的账号取个名字"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="请输入您的邮箱地址"
              prefix-icon="Message"
              size="large"
            />
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请设置一个安全的密码"
              prefix-icon="Lock"
              show-password
              size="large"
            />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次确认您的密码"
              prefix-icon="Lock"
              show-password
              size="large"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              native-type="submit"
              :loading="loading"
              size="large"
              style="width: 100%"
              class="register-btn"
            >
              立即注册
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="register-footer">
          <p class="footer-text">
            已有账号？
            <el-link type="primary" @click="$router.push('/login')" class="link-text">
              直接登录，开始创作
            </el-link>
          </p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref()
const loading = ref(false)

const registerForm = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.value.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.register(
          registerForm.value.username,
          registerForm.value.password,
          registerForm.value.email
        )
        router.push('/')
      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: fixed;
  top: 0;
  left: 0;
  margin: 0;
  padding: 0;
}

.register-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 40px;
  width: 100%;
  height: 100%;
  justify-content: center;
}

.register-logo {
  position: absolute;
  top: 30px;
  left: 30px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.register-logo:hover {
  transform: translateY(-2px);
}

.register-logo:hover .ai-icon {
  transform: rotate(-15deg);
}

.register-logo .logo-icon {
  position: relative;
  width: 40px;
  height: 40px;
}

.register-logo .ai-logo {
  position: relative;
  width: 100%;
  height: 100%;
}

.register-logo .ai-icon {
  width: 40px;
  height: 40px;
  background: white;
  color: #667eea;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 800;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.register-logo .logo-text {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 24px;
  font-weight: 800;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.register-logo .ai-badge {
  color: white;
  padding: 6px 8px;
  border-radius: 6px;
  font-size: 25px;
  font-weight: 800;
}

.register-card {
  width: 380px;
  padding: 0;
  border-radius: 12px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
  border: none;
  overflow: hidden;
}

.register-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f2ff 100%);
  border-bottom: 1px solid #e8ecff;
  padding: 25px 30px 15px;
}

.register-header h2 {
  margin: 0 0 6px 0;
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  text-align: center;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #8b92a9;
  text-align: center;
  line-height: 1.4;
}

.register-card :deep(.el-card__body) {
  padding: 30px;
}

.register-card :deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 6px;
}

.register-card :deep(.el-input__wrapper) {
  padding: 8px 12px;
  border-radius: 6px;
  box-shadow: 0 0 0 1px #dcdfe6;
  transition: all 0.3s ease;
  min-height: 36px;
}

.register-card :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea;
}

.register-card :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #667eea;
}

.register-btn {
  height: 40px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 3px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.register-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
}

.register-footer {
  text-align: center;
  margin-top: 20px;
}

.footer-text {
  margin: 0;
  font-size: 14px;
  color: #8b92a9;
  line-height: 1.5;
}

.link-text {
  font-weight: 600;
  font-size: 14px;
}</style>