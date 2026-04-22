import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: '/products',
        name: 'Products',
        component: () => import('@/views/Products.vue')
      },
      {
        path: '/product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/ProductDetail.vue')
      },
      {
        path: '/cart',
        name: 'Cart',
        component: () => import('@/views/Cart.vue')
      },
      {
        path: '/order',
        name: 'Order',
        component: () => import('@/views/Order.vue')
      },
      {
        path: '/order/:id',
        name: 'OrderDetail',
        component: () => import('@/views/OrderDetail.vue')
      },
      {
        path: '/user',
        name: 'User',
        component: () => import('@/views/User.vue')
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  // 管理员后台
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: '/admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: '/admin/products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/Products.vue')
      },
      {
        path: '/admin/categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/Categories.vue')
      },
      {
        path: '/admin/orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const whiteList = ['/login', '/register', '/', '/home', '/products', '/product']

// 解析 JWT payload 获取 role
function getRoleFromToken(token) {
  try {
    const parts = token.split('.')
    if (parts.length !== 3) return null
    const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')))
    return payload.role
  } catch {
    return null
  }
}

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 管理员路由需要登录 + role=0
  if (to.path.startsWith('/admin')) {
    if (!token) {
      next('/login')
      return
    }
    const role = getRoleFromToken(token)
    if (role !== 0) {
      next('/home')
      return
    }
    next()
    return
  }

  // 普通路由白名单
  if (!token && !whiteList.some(path => to.path.startsWith(path))) {
    next('/login')
  } else {
    next()
  }
})

export default router
