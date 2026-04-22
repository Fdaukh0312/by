<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="admin-sidebar">
      <!-- Header -->
      <div class="sidebar-header">
        <div class="logo">
          <div class="logo-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
              <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M9 22V12H15V22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="logo-text">
            <span class="logo-title">克迪商城</span>
            <span class="logo-sub">管理后台</span>
          </div>
        </div>
      </div>

      <!-- Nav -->
      <nav class="sidebar-nav">
        <div class="nav-section-label">主菜单</div>
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
        >
          <el-icon class="nav-icon">
            <component :is="item.icon" />
          </el-icon>
          <span>{{ item.label }}</span>
          <div v-if="item.badge" class="nav-badge">{{ item.badge }}</div>
        </router-link>
      </nav>

      <!-- Footer -->
      <div class="sidebar-footer">
        <div class="footer-links">
          <router-link to="/home" class="footer-link">
            <el-icon><Shop /></el-icon>
            <span>返回商城</span>
          </router-link>
          <button class="footer-link" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </button>
        </div>
        <div class="admin-user">
          <div class="user-avatar">{{ adminInitial }}</div>
          <div class="user-info">
            <span class="user-name">{{ adminName }}</span>
            <span class="user-role">超级管理员</span>
          </div>
        </div>
      </div>
    </aside>

    <!-- Main -->
    <div class="admin-main">
      <header class="admin-topbar">
        <div class="topbar-left">
          <h2 class="page-title">{{ currentTitle }}</h2>
        </div>
        <div class="topbar-right">
          <div class="admin-badge">
            <el-icon><UserFilled /></el-icon>
            <span>管理员</span>
          </div>
        </div>
      </header>

      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  DataAnalysis,
  Goods,
  Grid,
  Document,
  Shop,
  SwitchButton,
  UserFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const navItems = [
  { path: '/admin/dashboard', label: '数据概览', icon: DataAnalysis },
  { path: '/admin/products', label: '商品管理', icon: Goods },
  { path: '/admin/categories', label: '分类管理', icon: Grid },
  { path: '/admin/orders', label: '订单管理', icon: Document }
]

const titleMap = {
  '/admin/dashboard': '数据概览',
  '/admin/products': '商品管理',
  '/admin/categories': '分类管理',
  '/admin/orders': '订单管理'
}

const currentTitle = computed(() => titleMap[route.path] || '管理后台')
const adminName = computed(() => userStore.userInfo?.nickname || userStore.userInfo?.username || 'Admin')
const adminInitial = computed(() => adminName.value.charAt(0).toUpperCase())

const isActive = (path) => route.path === path

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style lang="scss" scoped>
// Inline critical admin layout styles
// Uses Material You palette from main.scss (--md-sys-color-primary: #3069ca)

.admin-layout {
  display: flex;
  min-height: 100vh;
}

.admin-sidebar {
  width: var(--admin-sidebar-width);
  background: var(--md-sys-color-inverse-surface);
  color: var(--md-sys-color-inverse-on-surface);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    top: 0; right: 0;
    width: 1px;
    height: 100%;
    background: linear-gradient(180deg, transparent 0%, rgba(48,105,202,0.25) 50%, transparent 100%);
  }
}

.sidebar-header {
  padding: 24px 20px 20px;
  flex-shrink: 0;

  .logo { display: flex; align-items: center; gap: 12px; }

  .logo-icon {
    width: 42px;
    height: 42px;
    background: var(--md-sys-color-primary);
    border-radius: var(--md-sys-shape-corner-medium);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--md-sys-color-on-primary);
    flex-shrink: 0;
    box-shadow: 0 4px 14px rgba(48,105,202,0.35);
    transition: transform 0.25s ease;

    &:hover { transform: rotate(6deg) scale(1.06); }
  }

  .logo-title {
    font-size: 17px;
    font-weight: 700;
    color: var(--md-sys-color-inverse-on-surface);
  }

  .logo-sub {
    font-size: 10px;
    color: var(--md-sys-color-inverse-on-surface);
    opacity: 0.5;
    margin-top: 3px;
    letter-spacing: 2px;
    text-transform: uppercase;
  }
}

.sidebar-nav {
  flex: 1;
  padding: 8px 14px;
  overflow-y: auto;
  overflow-x: hidden;

  &::-webkit-scrollbar { width: 3px; }
  &::-webkit-scrollbar-track { background: transparent; }
  &::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 3px; }
}

.nav-section-label {
  font-size: 10px;
  font-weight: 600;
  color: var(--md-sys-color-inverse-on-surface);
  opacity: 0.35;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  padding: 16px 14px 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 13px;
  padding: 12px 16px;
  border-radius: var(--md-sys-shape-corner-small);
  color: var(--md-sys-color-inverse-on-surface);
  opacity: 0.65;
  text-decoration: none;
  font-size: 14px;
  margin-bottom: 3px;
  transition: background 0.2s ease, opacity 0.2s ease;
  cursor: pointer;
  white-space: nowrap;

  .nav-icon {
    font-size: 19px;
    width: 22px;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: transform 0.2s ease;
  }

  span { flex: 1; }

  .nav-badge {
    background: var(--md-sys-color-primary);
    color: var(--md-sys-color-on-primary);
    font-size: 11px;
    font-weight: 700;
    padding: 2px 7px;
    border-radius: 20px;
  }

  &:hover {
    background: rgba(48,105,202,0.12);
    opacity: 1;
    .nav-icon { transform: scale(1.1) translateX(1px); }
  }

  &.active {
    background: rgba(48,105,202,0.18);
    color: var(--md-sys-color-inverse-on-surface);
    opacity: 1;
    font-weight: 600;
    box-shadow: inset 3px 0 0 var(--md-sys-color-primary);
    .nav-icon { transform: scale(1.12); }
  }
}

.sidebar-footer {
  padding: 12px 14px 20px;
  border-top: 1px solid rgba(255,255,255,0.08);
  flex-shrink: 0;

  .footer-links {
    display: flex;
    gap: 4px;
    margin-bottom: 10px;

    .footer-link {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 5px;
      padding: 8px 6px;
      border-radius: var(--md-sys-shape-corner-small);
      font-size: 12px;
      color: var(--md-sys-color-inverse-on-surface);
      opacity: 0.55;
      text-decoration: none;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        background: rgba(48,105,202,0.12);
        opacity: 1;
        color: var(--md-sys-color-inverse-on-surface);
      }
    }
  }

  .admin-user {
    display: flex;
    align-items: center;
    gap: 11px;
    padding: 10px 12px;
    border-radius: var(--md-sys-shape-corner-small);
    background: rgba(255,255,255,0.05);
    border: 1px solid rgba(255,255,255,0.07);
    transition: background 0.2s ease;

    &:hover { background: rgba(255,255,255,0.09); }

    .user-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: var(--md-sys-color-primary);
      color: var(--md-sys-color-on-primary);
      font-size: 15px;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .user-info {
      display: flex;
      flex-direction: column;
      min-width: 0;

      .user-name { font-size: 13px; font-weight: 600; color: var(--md-sys-color-inverse-on-surface); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
      .user-role { font-size: 11px; color: var(--md-sys-color-primary); margin-top: 2px; font-weight: 500; opacity: 0.85; }
    }
  }
}

.admin-main {
  margin-left: var(--admin-sidebar-width);
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--md-sys-color-surface);
}

.admin-topbar {
  height: var(--admin-topbar-height);
  background: var(--md-sys-color-surface);
  border-bottom: 1px solid var(--color-outline-variant);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  position: sticky;
  top: 0;
  z-index: 50;
  box-shadow: var(--md-sys-elevation-1);

  .page-title { font-size: 20px; font-weight: 600; color: var(--md-sys-color-on-surface); }

  .admin-badge {
    display: flex;
    align-items: center;
    gap: 7px;
    padding: 6px 16px;
    background: var(--color-primary-container);
    border-radius: 24px;
    font-size: 13px;
    color: var(--color-on-primary-container);
    font-weight: 600;
    border: 1px solid transparent;
  }
}

.admin-content {
  flex: 1;
  padding: 28px 32px;
}
</style>
