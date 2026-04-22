<template>
  <div class="login-page">
    <!-- Decorative background -->
    <div class="bg-deco bg-deco-1"></div>
    <div class="bg-deco bg-deco-2"></div>

    <div class="login-card">
      <!-- Brand -->
      <div class="brand">
        <div class="brand-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
            <path d="M7 18C5.9 18 5 18.9 5 20C5 21.1 5.9 22 7 22C8.1 22 9 21.1 9 20C9 18.9 8.1 18 7 18ZM1 2V4H3L6.6 11.59L5.25 14.04C5.09 14.32 5 14.65 5 15C5 16.1 5.9 17 7 17H19V15H7.42C7.28 15 7.17 14.89 7.1 14.76L7.2 14.63L8.1 13H15.55C16.3 13 16.96 12.59 17.3 11.97L20.88 5.48C20.96 5.34 21 5.17 21 5C21 4.45 20.55 4 20 4H5.21L4.27 2H1ZM17 18C15.9 18 15 18.9 15 20C15 21.1 15.9 22 17 22C18.1 22 19 21.1 19 20C19 18.9 18.1 18 17 18Z" fill="currentColor"/>
          </svg>
        </div>
        <h1 class="brand-name">克迪商城</h1>
        <p class="brand-sub">欢迎回来，请登录您的账号</p>
      </div>

      <!-- Form -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <div class="input-field">
            <label class="input-label">用户名</label>
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <div class="input-field">
            <label class="input-label">密码</label>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="submit-btn"
            native-type="submit"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- Footer -->
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>

      <!-- Demo hint -->
      <div class="demo-hint">
        <el-icon><InfoFilled /></el-icon>
        演示账号：admin / admin123
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, InfoFilled } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await userApi.login(form)
    userStore.setToken(res.data.token)
    ElMessage.success('登录成功')
    window.location.href = '/home'
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--md-sys-color-surface);
  position: relative;
  overflow: hidden;
  padding: 24px;
}

.bg-deco {
  position: absolute;
  border-radius: 50%;
  z-index: 0;
  opacity: 0.6;

  &.bg-deco-1 {
    width: 500px;
    height: 500px;
    background: radial-gradient(circle, var(--color-primary-container) 0%, transparent 70%);
    top: -150px;
    right: -100px;
  }

  &.bg-deco-2 {
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, #E3F2FD 0%, transparent 70%);
    bottom: -100px;
    left: -100px;
  }
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--md-sys-color-surface);
  border-radius: var(--md-sys-shape-corner-extra-large);
  padding: 48px 40px 40px;
  box-shadow: var(--md-sys-elevation-3);
  border: 1px solid var(--color-outline-variant);
  position: relative;
  z-index: 1;
}

.brand {
  text-align: center;
  margin-bottom: 36px;

  .brand-icon {
    width: 64px;
    height: 64px;
    background: var(--md-sys-color-primary);
    border-radius: var(--md-sys-shape-corner-large);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    margin: 0 auto 16px;
    box-shadow: var(--md-sys-elevation-2);
  }

  .brand-name {
    font-size: 26px;
    font-weight: 400;
    color: var(--md-sys-color-on-surface);
    margin-bottom: 8px;
  }

  .brand-sub {
    font-size: 14px;
    color: var(--md-sys-color-on-surface-variant);
  }
}

.login-form {
  .input-field {
    width: 100%;

    .input-label {
      display: block;
      font-size: 14px;
      font-weight: 500;
      color: var(--md-sys-color-on-surface);
      margin-bottom: 8px;
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  :deep(.el-form-item__error) {
    font-size: 12px;
  }

  .submit-btn {
    width: 100%;
    height: 52px;
    font-size: 16px !important;
    letter-spacing: 2px;
    border-radius: var(--md-sys-shape-corner-full) !important;
  }
}

.login-footer {
  text-align: center;
  margin-top: 8px;
  font-size: 14px;
  color: var(--md-sys-color-on-surface-variant);

  a {
    color: var(--md-sys-color-primary);
    text-decoration: none;
    font-weight: 500;
    margin-left: 4px;

    &:hover {
      text-decoration: underline;
    }
  }
}

.demo-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 20px;
  padding: 12px 16px;
  background: var(--color-primary-container);
  border-radius: var(--md-sys-shape-corner-medium);
  font-size: 13px;
  color: var(--md-sys-color-on-primary-container);

  .el-icon {
    flex-shrink: 0;
  }
}
</style>
