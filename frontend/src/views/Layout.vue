<template>
  <div class="layout-container">
    <!-- Top App Bar -->
    <header class="app-bar">
      <div class="app-bar-content">
        <!-- Logo -->
        <div class="logo" @click="$router.push('/home')">
          <div class="logo-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
              <path d="M7 18C5.9 18 5 18.9 5 20C5 21.1 5.9 22 7 22C8.1 22 9 21.1 9 20C9 18.9 8.1 18 7 18ZM1 2V4H3L6.6 11.59L5.25 14.04C5.09 14.32 5 14.65 5 15C5 16.1 5.9 17 7 17H19V15H7.42C7.28 15 7.17 14.89 7.1 14.76L7.2 14.63L8.1 13H15.55C16.3 13 16.96 12.59 17.3 11.97L20.88 5.48C20.96 5.34 21 5.17 21 5C21 4.45 20.55 4 20 4H5.21L4.27 2H1ZM17 18C15.9 18 15 18.9 15 20C15 21.1 15.9 22 17 22C18.1 22 19 21.1 19 20C19 18.9 18.1 18 17 18Z" fill="currentColor"/>
            </svg>
          </div>
          <span class="logo-text">克迪商城</span>
        </div>

        <!-- Center: Search -->
        <div class="search-container">
          <div class="search-bar">
            <el-icon class="search-icon"><Search /></el-icon>
            <input
              v-model="keyword"
              placeholder="搜索商品"
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">
              <el-icon><Search /></el-icon>
            </button>
          </div>
        </div>

        <!-- Right: Actions -->
        <div class="header-actions">
          <!-- Cart -->
          <div class="action-item" @click="$router.push('/cart')">
            <div class="icon-badge">
              <el-icon size="22"><ShoppingCart /></el-icon>
              <span v-if="cartStore.cartCount > 0" class="badge">{{ cartStore.cartCount > 99 ? '99+' : cartStore.cartCount }}</span>
            </div>
            <span class="action-label">购物车</span>
          </div>

          <!-- User -->
          <template v-if="userStore.token">
            <div class="action-item" @click="userMenuVisible = !userMenuVisible">
              <div class="user-avatar">
                <el-icon size="20"><User /></el-icon>
              </div>
              <span class="action-label">{{ userStore.userInfo?.nickname || '用户' }}</span>
            </div>
            <!-- Dropdown -->
            <div v-if="userMenuVisible" class="user-dropdown" @mouseleave="userMenuVisible = false">
              <div class="dropdown-item" @click="$router.push('/user'); userMenuVisible = false">
                <el-icon><User /></el-icon>
                个人中心
              </div>
              <div class="dropdown-item" @click="$router.push('/order'); userMenuVisible = false">
                <el-icon><List /></el-icon>
                我的订单
              </div>
              <div v-if="isAdmin" class="dropdown-item dropdown-item--admin" @click="$router.push('/admin/dashboard'); userMenuVisible = false">
                <el-icon><Setting /></el-icon>
                管理后台
              </div>
              <div class="dropdown-divider"></div>
              <div class="dropdown-item dropdown-item--danger" @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </div>
            </div>
          </template>
          <template v-else>
            <el-button class="login-btn" @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>

      <!-- Category Nav Bar -->
      <nav class="category-nav-bar">
        <div class="category-nav-content">
          <router-link to="/home" class="nav-link" :class="{ active: $route.path === '/home' }">首页</router-link>
          <router-link to="/products" class="nav-link" :class="{ active: $route.path === '/products' }">全部商品</router-link>
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="nav-link"
            :class="{ active: $route.query.categoryId == cat.id }"
            @click="goToProducts(cat.id)"
          >
            {{ cat.name }}
          </div>
        </div>
      </nav>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-brand">
          <div class="footer-logo">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M7 18C5.9 18 5 18.9 5 20C5 21.1 5.9 22 7 22C8.1 22 9 21.1 9 20C9 18.9 8.1 18 7 18ZM1 2V4H3L6.6 11.59L5.25 14.04C5.09 14.32 5 14.65 5 15C5 16.1 5.9 17 7 17H19V15H7.42C7.28 15 7.17 14.89 7.1 14.76L7.2 14.63L8.1 13H15.55C16.3 13 16.96 12.59 17.3 11.97L20.88 5.48C20.96 5.34 21 5.17 21 5C21 4.45 20.55 4 20 4H5.21L4.27 2H1ZM17 18C15.9 18 15 18.9 15 20C15 21.1 15.9 22 17 22C18.1 22 19 21.1 19 20C19 18.9 18.1 18 17 18Z" fill="currentColor"/>
            </svg>
            <span>克迪商城</span>
          </div>
          <p class="footer-desc">品质生活，从克迪开始</p>
        </div>
        <div class="footer-links">
          <div class="footer-col">
            <h4>客户服务</h4>
            <a href="#">帮助中心</a>
            <a href="#">退换货政策</a>
            <a href="#">联系客服</a>
          </div>
          <div class="footer-col">
            <h4>关于我们</h4>
            <a href="#">公司介绍</a>
            <a href="#">人才招聘</a>
            <a href="#">联系我们</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 克迪商城 版权所有</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ShoppingCart, User, List, SwitchButton, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { categoryApi } from '@/api/category'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const keyword = ref('')
const categories = ref([])
const userMenuVisible = ref(false)

// 解析 JWT 获取 role
const isAdmin = computed(() => {
  try {
    const token = localStorage.getItem('token')
    if (!token) return false
    const parts = token.split('.')
    if (parts.length !== 3) return false
    const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')))
    return payload.role === 0
  } catch { return false }
})

const handleSearch = () => {
  if (keyword.value.trim()) {
    router.push({ path: '/products', query: { keyword: keyword.value } })
    keyword.value = ''
  }
}

const goToProducts = (categoryId) => {
  router.push({ path: '/products', query: { categoryId } })
}

const handleLogout = () => {
  userMenuVisible.value = false
  userStore.logout()
  router.push('/login')
}

const loadCategories = async () => {
  try {
    const res = await categoryApi.getList()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

onMounted(() => {
  if (userStore.token) {
    userStore.getUserInfo()
    cartStore.updateCartCount()
  }
  loadCategories()
})
</script>

<style lang="scss" scoped>
// ==============================
// App Bar (Top Navigation)
// ==============================
.app-bar {
  background: var(--md-sys-color-surface);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--md-sys-elevation-1);
}

.app-bar-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 24px;
}

// ==============================
// Logo
// ==============================
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
  text-decoration: none;

  .logo-icon {
    width: 40px;
    height: 40px;
    background: var(--md-sys-color-primary);
    border-radius: var(--md-sys-shape-corner-medium);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }

  .logo-text {
    font-size: 20px;
    font-weight: 500;
    color: var(--md-sys-color-on-surface);
    letter-spacing: -0.5px;
  }
}

// ==============================
// Search
// ==============================
.search-container {
  flex: 1;
  max-width: 560px;
}

.search-bar {
  display: flex;
  align-items: center;
  background: var(--md-sys-color-surface-variant);
  border-radius: var(--md-sys-shape-corner-full);
  height: 48px;
  padding: 0 8px 0 16px;
  transition: box-shadow 0.2s ease;
  border: 2px solid transparent;

  &:focus-within {
    border-color: var(--md-sys-color-primary);
    background: var(--md-sys-color-surface);
    box-shadow: 0 0 0 4px var(--color-primary-container);
  }

  .search-icon {
    color: var(--md-sys-color-on-surface-variant);
    flex-shrink: 0;
    margin-right: 8px;
  }

  .search-input {
    flex: 1;
    border: none;
    background: transparent;
    outline: none;
    font-size: 15px;
    color: var(--md-sys-color-on-surface);
    font-family: inherit;

    &::placeholder {
      color: var(--md-sys-color-on-surface-variant);
    }
  }

  .search-btn {
    width: 40px;
    height: 40px;
    border-radius: var(--md-sys-shape-corner-full);
    border: none;
    background: var(--md-sys-color-primary);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: background 0.2s;
    flex-shrink: 0;

    &:hover {
      background: #095C3F;
    }
  }
}

// ==============================
// Header Actions
// ==============================
.header-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
  position: relative;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: var(--md-sys-shape-corner-full);
  cursor: pointer;
  transition: background 0.2s;
  position: relative;

  &:hover {
    background: var(--color-primary-container);
  }

  .action-label {
    font-size: 14px;
    color: var(--md-sys-color-on-surface);
  }
}

.icon-badge {
  position: relative;
  display: flex;
  align-items: center;
  color: var(--md-sys-color-on-surface-variant);

  .badge {
    position: absolute;
    top: -8px;
    right: -10px;
    min-width: 18px;
    height: 18px;
    padding: 0 4px;
    background: var(--md-sys-color-error);
    color: #fff;
    border-radius: var(--md-sys-shape-corner-full);
    font-size: 10px;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    font-family: 'Roboto', sans-serif;
  }
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: var(--md-sys-shape-corner-full);
  background: var(--md-sys-color-primary-container);
  color: var(--md-sys-color-on-primary-container);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 180px;
  background: var(--md-sys-color-surface);
  border-radius: var(--md-sys-shape-corner-medium);
  box-shadow: var(--md-sys-elevation-3);
  border: 1px solid var(--color-outline-variant);
  overflow: hidden;
  z-index: 200;
  padding: 4px;

  .dropdown-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    border-radius: var(--md-sys-shape-corner-full);
    font-size: 14px;
    color: var(--md-sys-color-on-surface);
    cursor: pointer;
    transition: background 0.15s;

    &:hover {
      background: var(--color-primary-container);
    }

    &--danger {
      color: var(--md-sys-color-error);
      &:hover {
        background: var(--color-error-container);
      }
    }

    &--admin {
      color: var(--md-sys-color-primary);
      font-weight: 500;
      &:hover {
        background: var(--color-primary-container);
      }
    }
  }

  .dropdown-divider {
    height: 1px;
    background: var(--color-outline-variant);
    margin: 4px 12px;
  }
}

.login-btn {
  margin-left: 8px;
  font-size: 14px !important;
}

// ==============================
// Category Nav Bar
// ==============================
.category-nav-bar {
  background: var(--md-sys-color-surface);
  border-top: 1px solid var(--color-outline-variant);
}

.category-nav-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  align-items: center;
  gap: 4px;
  height: 48px;
  overflow-x: auto;
  scrollbar-width: none;

  &::-webkit-scrollbar {
    display: none;
  }
}

.nav-link {
  padding: 8px 16px;
  border-radius: var(--md-sys-shape-corner-full);
  font-size: 14px;
  color: var(--md-sys-color-on-surface-variant);
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
  text-decoration: none;
  font-weight: 400;

  &:hover {
    background: var(--color-primary-container);
    color: var(--md-sys-color-on-primary-container);
  }

  &.active {
    background: var(--color-primary-container);
    color: var(--md-sys-color-on-primary-container);
    font-weight: 500;
  }
}

// ==============================
// Footer
// ==============================
.footer {
  background: var(--md-sys-color-surface-variant);
  border-top: 1px solid var(--color-outline-variant);
  margin-top: 48px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 16px 24px;
}

.footer-brand {
  margin-bottom: 32px;

  .footer-logo {
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--md-sys-color-primary);
    font-size: 18px;
    font-weight: 500;
    margin-bottom: 8px;
  }

  .footer-desc {
    color: var(--md-sys-color-on-surface-variant);
    font-size: 14px;
  }
}

.footer-links {
  display: flex;
  gap: 48px;
  margin-bottom: 32px;
}

.footer-col {
  h4 {
    font-size: 14px;
    font-weight: 500;
    color: var(--md-sys-color-on-surface);
    margin-bottom: 12px;
  }

  a {
    display: block;
    font-size: 14px;
    color: var(--md-sys-color-on-surface-variant);
    text-decoration: none;
    margin-bottom: 8px;
    transition: color 0.2s;

    &:hover {
      color: var(--md-sys-color-primary);
    }
  }
}

.footer-bottom {
  padding-top: 24px;
  border-top: 1px solid var(--color-outline-variant);

  p {
    font-size: 13px;
    color: var(--md-sys-color-on-surface-variant);
  }
}

// ==============================
// Responsive
// ==============================
@media (max-width: 768px) {
  .app-bar-content {
    gap: 12px;
    height: 56px;
    padding: 0 12px;
  }

  .logo-text {
    display: none;
  }

  .search-container {
    max-width: none;
  }

  .action-label {
    display: none;
  }

  .footer-links {
    gap: 24px;
    flex-wrap: wrap;
  }
}
</style>
