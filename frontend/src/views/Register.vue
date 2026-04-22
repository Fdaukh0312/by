<template>
  <div class="register-page">
    <!-- Decorative background -->
    <div class="bg-deco bg-deco-1"></div>
    <div class="bg-deco bg-deco-2"></div>

    <div class="register-card">
      <!-- Brand -->
      <div class="brand">
        <div class="brand-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
            <path d="M7 18C5.9 18 5 18.9 5 20C5 21.1 5.9 22 7 22C8.1 22 9 21.1 9 20C9 18.9 8.1 18 7 18ZM1 2V4H3L6.6 11.59L5.25 14.04C5.09 14.32 5 14.65 5 15C5 16.1 5.9 17 7 17H19V15H7.42C7.28 15 7.17 14.89 7.1 14.76L7.2 14.63L8.1 13H15.55C16.3 13 16.96 12.59 17.3 11.97L20.88 5.48C20.96 5.34 21 5.17 21 5C21 4.45 20.55 4 20 4H5.21L4.27 2H1ZM17 18C15.9 18 15 18.9 15 20C15 21.1 15.9 22 17 22C18.1 22 19 21.1 19 20C19 18.9 18.1 18 17 18Z" fill="currentColor"/>
          </svg>
        </div>
        <h1 class="brand-name">克迪商城</h1>
        <p class="brand-sub">创建账号，开始购物之旅</p>
      </div>

      <!-- Form -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <div class="form-row">
          <el-form-item prop="username" class="form-item-half">
            <div class="input-field">
              <label class="input-label">用户名 <span class="required">*</span></label>
              <el-input v-model="form.username" placeholder="3-20个字符" size="large" :prefix-icon="User" />
            </div>
          </el-form-item>
          <el-form-item prop="nickname" class="form-item-half">
            <div class="input-field">
              <label class="input-label">昵称</label>
              <el-input v-model="form.nickname" placeholder="选填" size="large" :prefix-icon="UserFilled" />
            </div>
          </el-form-item>
        </div>

        <el-form-item prop="password">
          <div class="input-field">
            <label class="input-label">密码 <span class="required">*</span></label>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="6-20个字符"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </div>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <div class="input-field">
            <label class="input-label">确认密码 <span class="required">*</span></label>
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="再次输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </div>
        </el-form-item>

        <div class="form-row">
          <el-form-item prop="phone" class="form-item-half">
            <div class="input-field">
              <label class="input-label">手机号</label>
              <el-input v-model="form.phone" placeholder="选填" size="large" :prefix-icon="Phone" />
            </div>
          </el-form-item>
          <el-form-item prop="email" class="form-item-half">
            <div class="input-field">
              <label class="input-label">邮箱</label>
              <el-input v-model="form.email" placeholder="选填" size="large" :prefix-icon="Message" />
            </div>
          </el-form-item>
        </div>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="submit-btn"
            native-type="submit"
          >
            {{ loading ? '注册中...' : '注 册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Phone, Message } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: '',
  email: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userApi.register(form)
    ElMessage.success('注册成功，即将跳转到登录页...')
    window.location.href = '/login'
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--md-sys-color-surface);
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
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
    left: -100px;
  }

  &.bg-deco-2 {
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, #E3F2FD 0%, transparent 70%);
    bottom: -100px;
    right: -100px;
  }
}

.register-card {
  width: 100%;
  max-width: 520px;
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

.register-form {
  .input-field {
    width: 100%;

    .input-label {
      display: block;
      font-size: 14px;
      font-weight: 500;
      color: var(--md-sys-color-on-surface);
      margin-bottom: 8px;

      .required {
        color: var(--md-sys-color-error);
      }
    }
  }

  .form-row {
    display: flex;
    gap: 16px;

    .form-item-half {
      flex: 1;
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 16px;
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

.register-footer {
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
</style>
