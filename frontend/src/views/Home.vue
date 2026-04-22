<template>
  <div class="home">
    <!-- ============================
         Hero Banner
         ============================ -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">品质生活<br>从克迪开始</h1>
          <p class="hero-subtitle">精选日用品，呵护每一天</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" round @click="$router.push('/products')">
              <el-icon><ShoppingBag /></el-icon>
              立即选购
            </el-button>
            <el-button size="large" round @click="$router.push('/register')">
              新人专享
            </el-button>
          </div>
        </div>
        <div class="hero-visual">
          <div class="hero-card hero-card-1">
            <img src="https://loremflickr.com/280/180/kitchen,cooking?lock=101" alt="厨房用品" />
          </div>
          <div class="hero-card hero-card-2">
            <img src="https://loremflickr.com/200/180/cleaning,detergent?lock=102" alt="清洁用品" />
          </div>
          <div class="hero-card hero-card-3">
            <img src="https://loremflickr.com/260/160/shampoo,bottle?lock=103" alt="个人护理" />
          </div>
        </div>
      </div>
      <!-- Decorative circles -->
      <div class="hero-deco hero-deco-1"></div>
      <div class="hero-deco hero-deco-2"></div>
    </section>

    <!-- ============================
         Features Strip
         ============================ -->
    <div class="features-strip">
      <div class="features-content">
        <div class="feature-item">
          <el-icon><Van /></el-icon>
          <span>全场满99免运费</span>
        </div>
        <div class="feature-divider"></div>
        <div class="feature-item">
          <el-icon><CircleCheck /></el-icon>
          <span>精选优质商品</span>
        </div>
        <div class="feature-divider"></div>
        <div class="feature-item">
          <el-icon><Service /></el-icon>
          <span>7天无理由退换</span>
        </div>
        <div class="feature-divider"></div>
        <div class="feature-item">
          <el-icon><Clock /></el-icon>
          <span>24小时发货</span>
        </div>
      </div>
    </div>

    <!-- ============================
         Categories
         ============================ -->
    <section class="categories-section card-container">
      <div class="section-header">
        <h3>商品分类</h3>
        <el-button text @click="$router.push('/products')">查看全部</el-button>
      </div>
      <div class="categories-grid">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="category-chip"
          @click="goToProducts(cat.id)"
        >
          <div class="category-icon">
            <el-icon size="24"><component :is="getCategoryIcon(cat.name)" /></el-icon>
          </div>
          <span class="category-name">{{ cat.name }}</span>
          <el-icon class="category-arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </section>

    <!-- ============================
         Hot Products
         ============================ -->
    <section class="hot-section card-container">
      <div class="section-header">
        <h3>
          热门推荐
          <span class="section-badge">HOT</span>
        </h3>
        <el-button text @click="$router.push('/products')">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="products-grid">
        <div
          v-for="item in recommendProducts"
          :key="item.id"
          class="product-card"
          @click="$router.push(`/product/${item.id}`)"
        >
          <div class="product-image-wrap">
            <img :src="item.image" class="product-image" :alt="item.name" />
            <div class="product-tag">精选</div>
          </div>
          <div class="product-body">
            <div class="product-name">{{ item.name }}</div>
            <div class="product-bottom">
              <span class="product-price">¥{{ item.price }}</span>
              <el-button size="small" round @click.stop="quickAdd(item)">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ============================
         Promo Banner
         ============================ -->
    <section class="promo-banner-section">
      <div class="promo-card promo-card-1" @click="$router.push('/products')">
        <div class="promo-content">
          <div class="promo-tag">限时特惠</div>
          <h3>厨房用品专区</h3>
          <p>精选锅具厨具，烹饪更轻松</p>
          <el-button round>进入选购</el-button>
        </div>
        <img src="https://loremflickr.com/320/200/kitchen,pot?lock=201" alt="厨房用品" />
      </div>
      <div class="promo-card promo-card-2" @click="$router.push('/products')">
        <div class="promo-content">
          <div class="promo-tag">新品上架</div>
          <h3>清洁用品专区</h3>
          <p>洁净生活，从家开始</p>
          <el-button round>进入选购</el-button>
        </div>
        <img src="https://loremflickr.com/320/200/cleaning,spray?lock=202" alt="清洁用品" />
      </div>
      <div class="promo-card promo-card-3" @click="$router.push('/products')">
        <div class="promo-content">
          <div class="promo-tag">热卖爆款</div>
          <h3>个人护理专区</h3>
          <p>呵护自己，从每一天开始</p>
          <el-button round>进入选购</el-button>
        </div>
        <img src="https://loremflickr.com/320/200/shampoo,bottle?lock=203" alt="个人护理" />
      </div>
    </section>

    <!-- ============================
         More Categories Products
         ============================ -->
    <section
      v-for="cat in categoriesWithProducts"
      :key="cat.id"
      class="category-products-section card-container"
    >
      <div class="section-header">
        <h3>{{ cat.name }}</h3>
        <el-button text @click="$router.push({ path: '/products', query: { categoryId: cat.id } })">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="products-grid">
        <div
          v-for="item in cat.products"
          :key="item.id"
          class="product-card"
          @click="$router.push(`/product/${item.id}`)"
        >
          <div class="product-image-wrap">
            <img :src="item.image" class="product-image" :alt="item.name" />
          </div>
          <div class="product-body">
            <div class="product-name">{{ item.name }}</div>
            <div class="product-bottom">
              <span class="product-price">¥{{ item.price }}</span>
              <el-button size="small" round @click.stop="quickAdd(item)">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowRight, Van, CircleCheck, Service, Clock, ShoppingBag, Plus,
  KnifeFork, Tools, User, HomeFilled, Document, Delete
} from '@element-plus/icons-vue'
import { categoryApi } from '@/api/category'
import { productApi } from '@/api/product'
import { cartApi } from '@/api/cart'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()
const categories = ref([])
const recommendProducts = ref([])
const categoriesWithProducts = ref([])

const getCategoryIcon = (name) => {
  const iconMap = {
    '厨房用品': KnifeFork,
    '清洁用品': Tools,
    '个人护理': User,
    '家居日用': HomeFilled,
    '纸品湿巾': Document,
    '衣物洗护': Delete
  }
  return iconMap[name] || Tools
}

const goToProducts = (categoryId) => {
  router.push({ path: '/products', query: { categoryId } })
}

const quickAdd = async (item) => {
  if (!userStore.token) {
    router.push('/login')
    return
  }
  try {
    await cartApi.addToCart({ productId: item.id, quantity: 1 })
    await cartStore.updateCartCount()
    ElMessage.success('已加入购物车')
  } catch (error) {
    ElMessage.error('加入失败')
  }
}

const loadData = async () => {
  try {
    const [catRes, proRes] = await Promise.all([
      categoryApi.getList(),
      productApi.getRecommend(8)
    ])
    categories.value = catRes.data || []
    recommendProducts.value = proRes.data || []

    // Load products for each category
    const catProds = await Promise.all(
      categories.value.slice(0, 3).map(async (cat) => {
        try {
          const res = await productApi.getList({ pageNum: 1, pageSize: 4, categoryId: cat.id })
          return { ...cat, products: (res.data.records || []).slice(0, 4) }
        } catch {
          return { ...cat, products: [] }
        }
      })
    )
    categoriesWithProducts.value = catProds.filter(c => c.products.length > 0)
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
// ==============================
// Hero Section
// ==============================
.hero-section {
  position: relative;
  background: linear-gradient(135deg, #E8F5E9 0%, #F1F8E9 50%, #E3F2FD 100%);
  border-radius: 0 0 28px 28px;
  overflow: hidden;
  margin-bottom: 24px;
  margin: -24px -16px 24px;
  padding: 48px 16px 0;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 48px;
  position: relative;
  z-index: 1;
  padding-bottom: 32px;
}

.hero-text {
  flex: 1;

  .hero-title {
    font-size: 48px;
    font-weight: 400;
    color: var(--md-sys-color-on-surface);
    line-height: 1.15;
    letter-spacing: -1px;
    margin-bottom: 16px;
  }

  .hero-subtitle {
    font-size: 18px;
    color: var(--md-sys-color-on-surface-variant);
    margin-bottom: 32px;
    line-height: 1.5;
  }

  .hero-actions {
    display: flex;
    gap: 12px;
  }
}

.hero-visual {
  position: relative;
  width: 400px;
  height: 300px;
  flex-shrink: 0;

  .hero-card {
    position: absolute;
    border-radius: var(--md-sys-shape-corner-large);
    overflow: hidden;
    box-shadow: var(--md-sys-elevation-3);
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-4px) scale(1.02);
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }

    &.hero-card-1 {
      width: 240px;
      height: 200px;
      top: 0;
      right: 0;
      z-index: 3;
    }

    &.hero-card-2 {
      width: 180px;
      height: 160px;
      top: 60px;
      left: 0;
      z-index: 2;
    }

    &.hero-card-3 {
      width: 200px;
      height: 150px;
      bottom: 0;
      right: 80px;
      z-index: 1;
    }
  }
}

.hero-deco {
  position: absolute;
  border-radius: 50%;
  z-index: 0;
  opacity: 0.5;

  &.hero-deco-1 {
    width: 300px;
    height: 300px;
    background: radial-gradient(circle, var(--color-primary-container) 0%, transparent 70%);
    bottom: -100px;
    right: 10%;
  }

  &.hero-deco-2 {
    width: 200px;
    height: 200px;
    background: radial-gradient(circle, #C8E6C9 0%, transparent 70%);
    bottom: -50px;
    left: 5%;
  }
}

// ==============================
// Features Strip
// ==============================
.features-strip {
  margin-bottom: 24px;
}

.features-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  background: var(--md-sys-color-surface);
  border-radius: var(--md-sys-shape-corner-large);
  padding: 16px 32px;
  box-shadow: var(--md-sys-elevation-1);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 24px;
  font-size: 14px;
  color: var(--md-sys-color-on-surface-variant);

  .el-icon {
    color: var(--md-sys-color-primary);
    font-size: 20px;
  }
}

.feature-divider {
  width: 1px;
  height: 20px;
  background: var(--color-outline-variant);
}

// ==============================
// Categories Grid
// ==============================
.categories-section {
  margin-bottom: 24px;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}

.category-chip {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: var(--md-sys-color-surface-variant);
  border-radius: var(--md-sys-shape-corner-large);
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;

  &:hover {
    background: var(--color-primary-container);
    border-color: var(--color-on-primary-container);
    transform: translateX(4px);

    .category-icon {
      background: var(--md-sys-color-primary);
      color: #fff;
    }

    .category-arrow {
      opacity: 1;
      transform: translateX(0);
    }
  }

  .category-icon {
    width: 44px;
    height: 44px;
    background: var(--md-sys-color-surface);
    border-radius: var(--md-sys-shape-corner-medium);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--md-sys-color-primary);
    flex-shrink: 0;
    transition: all 0.2s;
    box-shadow: var(--md-sys-elevation-1);
  }

  .category-name {
    flex: 1;
    font-size: 14px;
    font-weight: 500;
    color: var(--md-sys-color-on-surface);
  }

  .category-arrow {
    color: var(--md-sys-color-on-primary-container);
    opacity: 0;
    transform: translateX(-4px);
    transition: all 0.2s;
  }
}

// ==============================
// Products Grid
// ==============================
.hot-section,
.category-products-section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h3 {
    font-size: 22px;
    font-weight: 400;
    color: var(--md-sys-color-on-surface);
    display: flex;
    align-items: center;
    gap: 10px;

    .section-badge {
      font-size: 11px;
      font-weight: 700;
      background: linear-gradient(135deg, #FF6B6B, #FF8E53);
      color: #fff;
      padding: 2px 8px;
      border-radius: var(--md-sys-shape-corner-full);
      letter-spacing: 1px;
    }
  }
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.product-card {
  background: var(--md-sys-color-surface);
  border-radius: var(--md-sys-shape-corner-large);
  overflow: hidden;
  cursor: pointer;
  box-shadow: var(--md-sys-elevation-1);
  border: 1px solid var(--color-outline-variant);
  transition: all 0.25s ease;

  &:hover {
    box-shadow: var(--md-sys-elevation-3);
    transform: translateY(-3px);

    .product-image {
      transform: scale(1.05);
    }

    .el-button {
      background: var(--md-sys-color-primary);
      color: #fff;
    }
  }

  .product-image-wrap {
    position: relative;
    width: 100%;
    height: 180px;
    overflow: hidden;
    background: var(--md-sys-color-surface-variant);

    .product-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
      display: block;
    }

    .product-tag {
      position: absolute;
      top: 10px;
      left: 10px;
      background: var(--md-sys-color-primary);
      color: #fff;
      font-size: 11px;
      font-weight: 600;
      padding: 3px 10px;
      border-radius: var(--md-sys-shape-corner-full);
      letter-spacing: 0.5px;
    }
  }

  .product-body {
    padding: 14px;

    .product-name {
      font-size: 14px;
      color: var(--md-sys-color-on-surface);
      margin-bottom: 12px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      line-height: 1.4;
    }

    .product-bottom {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .product-price {
        font-size: 18px;
        font-weight: 500;
        color: var(--md-sys-color-primary);
        letter-spacing: -0.5px;
      }

      .el-button {
        width: 32px;
        height: 32px;
        min-width: 32px;
        padding: 0;
        border-radius: var(--md-sys-shape-corner-full) !important;
        background: var(--md-sys-color-surface-variant);
        color: var(--md-sys-color-primary);
        border: 1px solid var(--color-outline-variant) !important;
        transition: all 0.2s;

        &:hover {
          background: var(--md-sys-color-primary) !important;
          color: #fff !important;
        }
      }
    }
  }
}

// ==============================
// Promo Banner
// ==============================
.promo-banner-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.promo-card {
  position: relative;
  border-radius: var(--md-sys-shape-corner-large);
  overflow: hidden;
  background: var(--md-sys-color-surface);
  box-shadow: var(--md-sys-elevation-1);
  border: 1px solid var(--color-outline-variant);
  min-height: 180px;
  cursor: pointer;
  transition: all 0.25s ease;
  display: flex;

  &:hover {
    box-shadow: var(--md-sys-elevation-3);
    transform: translateY(-2px);

    img {
      transform: scale(1.05);
    }

    .el-button {
      background: var(--md-sys-color-primary) !important;
      color: #fff !important;
    }
  }

  .promo-content {
    flex: 1;
    padding: 24px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 8px;

    .promo-tag {
      font-size: 11px;
      font-weight: 600;
      color: var(--md-sys-color-primary);
      text-transform: uppercase;
      letter-spacing: 1px;
    }

    h3 {
      font-size: 18px;
      font-weight: 500;
      color: var(--md-sys-color-on-surface);
      line-height: 1.3;
    }

    p {
      font-size: 13px;
      color: var(--md-sys-color-on-surface-variant);
      line-height: 1.4;
    }

    .el-button {
      align-self: flex-start;
      margin-top: 4px;
      font-size: 13px !important;
      background: var(--color-primary-container) !important;
      color: var(--md-sys-color-on-primary-container) !important;
      border: none !important;
    }
  }

  img {
    width: 160px;
    object-fit: cover;
    transition: transform 0.3s ease;
    display: block;
  }
}

.promo-card-1 { background: linear-gradient(135deg, #E8F5E9, #F1F8E9); }
.promo-card-2 { background: linear-gradient(135deg, #E3F2FD, #E8EAF6); }
.promo-card-3 { background: linear-gradient(135deg, #FFF3E0, #FCE4EC); }

// ==============================
// Responsive
// ==============================
@media (max-width: 1024px) {
  .hero-visual {
    display: none;
  }

  .hero-text {
    text-align: center;

    .hero-actions {
      justify-content: center;
    }
  }

  .categories-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .promo-banner-section {
    grid-template-columns: 1fr;
  }

  .promo-card img {
    width: 120px;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 32px !important;
  }

  .features-content {
    padding: 12px 16px;
    flex-wrap: wrap;
    gap: 8px;
  }

  .feature-item {
    padding: 0 12px;
    font-size: 13px;
  }

  .feature-divider {
    display: none;
  }

  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .product-image-wrap {
    height: 140px !important;
  }

  .promo-card img {
    width: 100px;
  }
}
</style>
