<template>
  <div class="user-page">
    <div class="user-header">
      <h1 class="page-title">个人中心</h1>
    </div>

    <div class="user-layout">
      <!-- Sidebar -->
      <aside class="user-sidebar">
        <div class="user-profile">
          <div class="avatar-wrap">
            <div class="avatar">
              {{ userInfo?.nickname?.charAt(0) || userInfo?.username?.charAt(0) || 'U' }}
            </div>
          </div>
          <div class="user-name">{{ userInfo?.nickname || userInfo?.username || '用户' }}</div>
          <div class="user-tag">会员</div>
        </div>

        <nav class="user-nav">
          <div
            v-for="item in menuItems"
            :key="item.key"
            class="nav-item"
            :class="{ active: activeMenu === item.key }"
            @click="activeMenu = item.key"
          >
            <el-icon size="20"><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
            <el-icon class="nav-arrow"><ArrowRight /></el-icon>
          </div>
        </nav>
      </aside>

      <!-- Content -->
      <main class="user-content">
        <!-- Info -->
        <div v-if="activeMenu === 'info'" class="content-card card-container">
          <div class="card-header">
            <h3>基本信息</h3>
          </div>
          <el-form :model="infoForm" label-width="80px" class="info-form">
            <el-form-item label="用户名">
              <el-input v-model="infoForm.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="infoForm.nickname" placeholder="设置您的昵称" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="infoForm.email" placeholder="选填" />
            </el-form-item>
            <el-form-item label="手机">
              <el-input v-model="infoForm.phone" placeholder="选填" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" round @click="handleUpdateInfo">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- Password -->
        <div v-if="activeMenu === 'password'" class="content-card card-container">
          <div class="card-header">
            <h3>修改密码</h3>
          </div>
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="80px" class="password-form">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" round @click="handleUpdatePassword">确认修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- Orders -->
        <div v-if="activeMenu === 'order'" class="content-card" style="padding: 0;">
          <router-view />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, List, ArrowRight } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'

const userStore = useUserStore()
const activeMenu = ref('info')
const passwordFormRef = ref()

const menuItems = [
  { key: 'info', label: '基本信息', icon: User },
  { key: 'password', label: '修改密码', icon: Lock },
  { key: 'order', label: '我的订单', icon: List }
]

const infoForm = reactive({ username: '', nickname: '', email: '', phone: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const userInfo = computed(() => userStore.userInfo)

const validateConfirm = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) callback(new Error('两次输入的密码不一致'))
  else callback()
}
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

const loadUserInfo = () => {
  Object.assign(infoForm, {
    username: userInfo.value?.username || '',
    nickname: userInfo.value?.nickname || '',
    email: userInfo.value?.email || '',
    phone: userInfo.value?.phone || ''
  })
}

const handleUpdateInfo = async () => {
  try {
    await userApi.updateUserInfo({ nickname: infoForm.nickname, email: infoForm.email, phone: infoForm.phone })
    await userStore.getUserInfo()
    ElMessage.success('修改成功')
  } catch (error) { console.error('修改失败:', error) }
}

const handleUpdatePassword = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await userApi.updatePassword(passwordForm.oldPassword, passwordForm.newPassword)
    ElMessage.success('密码修改成功')
    passwordFormRef.value.resetFields()
  } catch (error) { console.error('修改密码失败:', error) }
}

onMounted(() => { loadUserInfo() })
</script>

<style lang="scss" scoped>
.user-page {
  max-width: 1200px;
  margin: 0 auto;
}

.user-header { margin-bottom: 24px; }

.user-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.user-sidebar {
  width: 240px;
  flex-shrink: 0;
  background: var(--md-sys-color-surface);
  border-radius: var(--md-sys-shape-corner-large);
  box-shadow: var(--md-sys-elevation-1);
  border: 1px solid var(--color-outline-variant);
  overflow: hidden;
}

.user-profile {
  padding: 32px 20px 24px;
  text-align: center;
  background: linear-gradient(135deg, #E8F5E9, #F1F8E9);
  border-bottom: 1px solid var(--color-outline-variant);

  .avatar-wrap {
    margin-bottom: 12px;
  }

  .avatar {
    width: 72px;
    height: 72px;
    border-radius: 50%;
    background: var(--md-sys-color-primary);
    color: #fff;
    font-size: 28px;
    font-weight: 400;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto;
    box-shadow: var(--md-sys-elevation-2);
  }

  .user-name {
    font-size: 16px;
    font-weight: 500;
    color: var(--md-sys-color-on-surface);
    margin-bottom: 4px;
  }

  .user-tag {
    display: inline-block;
    padding: 2px 12px;
    background: var(--color-primary-container);
    color: var(--md-sys-color-on-primary-container);
    border-radius: var(--md-sys-shape-corner-full);
    font-size: 11px;
    font-weight: 500;
  }
}

.user-nav {
  padding: 8px;

  .nav-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    border-radius: var(--md-sys-shape-corner-large);
    cursor: pointer;
    color: var(--md-sys-color-on-surface-variant);
    font-size: 14px;
    transition: all 0.2s;

    &:hover {
      background: var(--color-primary-container);
      color: var(--md-sys-color-on-primary-container);
    }

    &.active {
      background: var(--color-primary-container);
      color: var(--md-sys-color-primary);
      font-weight: 500;

      .nav-arrow { opacity: 1; }
    }

    .nav-arrow {
      margin-left: auto;
      font-size: 16px;
      opacity: 0;
      transition: opacity 0.2s;
    }
  }
}

.user-content {
  flex: 1;
  min-width: 0;
}

.content-card {
  .card-header {
    padding: 20px 24px;
    border-bottom: 1px solid var(--color-outline-variant);

    h3 {
      font-size: 18px;
      font-weight: 500;
      color: var(--md-sys-color-on-surface);
    }
  }

  .el-form {
    padding: 24px;
    max-width: 480px;
  }
}

@media (max-width: 768px) {
  .user-layout { flex-direction: column; }
  .user-sidebar { width: 100%; }
}
</style>
