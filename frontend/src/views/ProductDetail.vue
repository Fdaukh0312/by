<template>
  <div class="product-detail">
    <!-- Loading -->
    <div v-if="loading" class="detail-loading">
      <div class="loading-image"></div>
      <div class="loading-info">
        <div class="skeleton-line skeleton-line--title"></div>
        <div class="skeleton-line skeleton-line--short"></div>
        <div class="skeleton-line skeleton-line--price"></div>
        <div class="skeleton-line skeleton-line--btn"></div>
      </div>
    </div>

    <!-- Not Found -->
    <div v-else-if="!product.id" class="empty-state">
      <div class="empty-icon">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none">
          <path d="M19 6.41L17.59 5L12 10.59L6.41 5L5 6.41L10.59 12L5 17.59L6.41 19L12 13.41L17.59 19L19 17.59L13.41 12L19 6.41Z" fill="currentColor"/>
        </svg>
      </div>
      <p>商品不存在</p>
      <el-button type="primary" round @click="$router.push('/products')">去逛逛</el-button>
    </div>

    <!-- Content -->
    <div v-else class="detail-content">
      <!-- Image + Info -->
      <div class="detail-main card-container">
        <!-- Image -->
        <div class="detail-image-section">
          <div class="main-image-wrap">
            <img :src="product.image" class="main-image" :alt="product.name" />
          </div>
        </div>

        <!-- Info -->
        <div class="detail-info-section">
          <h1 class="product-name">{{ product.name }}</h1>
          <p class="product-desc">{{ product.description }}</p>

          <div class="price-card">
            <div class="price-row">
              <span class="price-label">价格</span>
              <span class="current-price">¥{{ product.price }}</span>
            </div>
            <div class="price-row">
              <span class="price-label">库存</span>
              <span class="stock-value">{{ product.stock }} 件</span>
            </div>
          </div>

          <div class="quantity-section">
            <span class="quantity-label">数量</span>
            <div class="quantity-control">
              <button class="qty-btn" @click="quantity > 1 && quantity--">−</button>
              <input
                v-model.number="quantity"
                type="number"
                min="1"
                :max="product.stock"
                class="qty-input"
                @change="quantity = Math.max(1, Math.min(quantity, product.stock))"
              />
              <button class="qty-btn" @click="quantity < product.stock && quantity++">+</button>
            </div>
          </div>

          <div class="action-buttons">
            <el-button size="large" round @click="handleBuyNow">立即购买</el-button>
            <el-button type="primary" size="large" round @click="handleAddToCart">
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
          </div>

          <div class="service-chips">
            <div class="service-chip">
              <el-icon><CircleCheck /></el-icon>
              正品保证
            </div>
            <div class="service-chip">
              <el-icon><Van /></el-icon>
              急速发货
            </div>
            <div class="service-chip">
              <el-icon><RefreshLeft /></el-icon>
              7天退换
            </div>
          </div>
        </div>
      </div>

      <!-- Recommend -->
      <div class="recommend-section card-container">
        <div class="section-header">
          <h3>猜你喜欢</h3>
          <el-button text @click="$router.push('/products')">查看更多</el-button>
        </div>
        <div class="recommend-grid">
          <div
            v-for="item in recommendProducts"
            :key="item.id"
            class="product-card"
            @click="$router.push(`/product/${item.id}`)"
          >
            <div class="product-image-wrap">
              <img :src="item.image" class="product-image" :alt="item.name" />
            </div>
            <div class="product-body">
              <div class="product-name">{{ item.name }}</div>
              <div class="product-price">¥{{ item.price }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, CircleCheck, Van, RefreshLeft } from '@element-plus/icons-vue'
import { productApi } from '@/api/product'
import { cartApi } from '@/api/cart'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const loading = ref(false)
const product = ref({})
const recommendProducts = ref([])
const quantity = ref(1)

const loadProduct = async () => {
  loading.value = true
  try {
    const res = await productApi.getDetail(route.params.id)
    product.value = res.data || {}
  } catch (error) {
    console.error('加载商品详情失败:', error)
  } finally {
    loading.value = false
  }
}

const loadRecommend = async () => {
  try {
    const res = await productApi.getRecommend(4)
    recommendProducts.value = res.data || []
  } catch (error) {
    console.error('加载推荐商品失败:', error)
  }
}

const handleAddToCart = async () => {
  if (!userStore.token) {
    router.push('/login')
    return
  }
  try {
    await cartApi.addToCart({ productId: product.value.id, quantity: quantity.value })
    await cartStore.updateCartCount()
    ElMessage.success('已加入购物车')
  } catch (error) {
    ElMessage.error('加入失败')
  }
}

const handleBuyNow = async () => {
  if (!userStore.token) {
    router.push('/login')
    return
  }
  try {
    await cartApi.addToCart({ productId: product.value.id, quantity: quantity.value })
    await cartStore.updateCartCount()
    router.push('/cart')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadProduct()
  loadRecommend()
})
</script>

<style lang="scss" scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.detail-loading {
  display: flex;
  gap: 40px;
  background: var(--md-sys-color-surface);
  border-radius: var(--md-sys-shape-corner-large);
  padding: 24px;
  box-shadow: var(--md-sys-elevation-1);
  margin-bottom: 24px;

  .loading-image {
    width: 400px;
    height: 400px;
    border-radius: var(--md-sys-shape-corner-large);
    background: linear-gradient(90deg, var(--md-sys-color-surface-variant) 25%, var(--color-outline-variant) 50%, var(--md-sys-color-surface-variant) 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
    flex-shrink: 0;
  }

  .loading-info {
    flex: 1;
    padding: 20px 0;
  }

  .skeleton-line {
    height: 20px;
    border-radius: 4px;
    background: linear-gradient(90deg, var(--md-sys-color-surface-variant) 25%, var(--color-outline-variant) 50%, var(--md-sys-color-surface-variant) 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
    margin-bottom: 16px;

    &--title { height: 32px; width: 70%; }
    &--short { width: 40%; }
    &--price { height: 40px; width: 30%; }
    &--btn { height: 48px; width: 200px; border-radius: var(--md-sys-shape-corner-full); }
  }
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-main {
  display: flex;
  gap: 40px;
}

.detail-image-section {
  flex-shrink: 0;
  width: 420px;

  .main-image-wrap {
    border-radius: var(--md-sys-shape-corner-large);
    overflow: hidden;
    background: var(--md-sys-color-surface-variant);

    .main-image {
      width: 100%;
      height: 420px;
      object-fit: cover;
      display: block;
    }
  }
}

.detail-info-section {
  flex: 1;

  .product-name {
    font-size: 28px;
    font-weight: 400;
    color: var(--md-sys-color-on-surface);
    margin-bottom: 12px;
    line-height: 1.3;
    letter-spacing: -0.5px;
  }

  .product-desc {
    font-size: 14px;
    color: var(--md-sys-color-on-surface-variant);
    line-height: 1.6;
    margin-bottom: 24px;
    padding: 16px;
    background: var(--md-sys-color-surface-variant);
    border-radius: var(--md-sys-shape-corner-medium);
  }
}

.price-card {
  background: linear-gradient(135deg, #E8F5E9 0%, #F1F8E9 100%);
  border-radius: var(--md-sys-shape-corner-large);
  padding: 20px;
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;

  .price-row {
    display: flex;
    align-items: center;
    gap: 16px;

    .price-label {
      font-size: 14px;
      color: var(--md-sys-color-on-surface-variant);
      min-width: 48px;
    }

    .current-price {
      font-size: 36px;
      font-weight: 500;
      color: var(--md-sys-color-primary);
      letter-spacing: -1px;
    }

    .stock-value {
      font-size: 14px;
      color: var(--md-sys-color-on-surface-variant);
    }
  }
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 28px;

  .quantity-label {
    font-size: 14px;
    font-weight: 500;
    color: var(--md-sys-color-on-surface-variant);
  }

  .quantity-control {
    display: flex;
    align-items: center;
    background: var(--md-sys-color-surface-variant);
    border-radius: var(--md-sys-shape-corner-full);
    overflow: hidden;

    .qty-btn {
      width: 40px;
      height: 40px;
      border: none;
      background: transparent;
      font-size: 20px;
      color: var(--md-sys-color-on-surface-variant);
      cursor: pointer;
      transition: all 0.2s;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        background: var(--color-primary-container);
        color: var(--md-sys-color-primary);
      }
    }

    .qty-input {
      width: 60px;
      height: 40px;
      border: none;
      background: transparent;
      text-align: center;
      font-size: 16px;
      font-weight: 500;
      color: var(--md-sys-color-on-surface);
      font-family: inherit;
      outline: none;
      -moz-appearance: textfield;

      &::-webkit-inner-spin-button,
      &::-webkit-outer-spin-button {
        -webkit-appearance: none;
      }
    }
  }
}

.action-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;

  .el-button {
    flex: 1;
    height: 52px;
    font-size: 16px !important;
  }
}

.service-chips {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;

  .service-chip {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    background: var(--md-sys-color-surface-variant);
    border-radius: var(--md-sys-shape-corner-full);
    font-size: 13px;
    color: var(--md-sys-color-on-surface-variant);

    .el-icon {
      color: var(--md-sys-color-primary);
      font-size: 16px;
    }
  }
}

.recommend-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h3 {
      font-size: 20px;
      font-weight: 400;
      color: var(--md-sys-color-on-surface);
    }
  }
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
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
  }

  .product-image-wrap {
    width: 100%;
    height: 160px;
    overflow: hidden;
    background: var(--md-sys-color-surface-variant);

    .product-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
      display: block;
    }
  }

  .product-body {
    padding: 12px;

    .product-name {
      font-size: 14px;
      color: var(--md-sys-color-on-surface);
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .product-price {
      font-size: 16px;
      font-weight: 500;
      color: var(--md-sys-color-primary);
    }
  }
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: var(--md-sys-color-surface);
  border-radius: var(--md-sys-shape-corner-large);
  box-shadow: var(--md-sys-elevation-1);

  .empty-icon {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: var(--color-outline-variant);
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;
    color: var(--md-sys-color-on-surface-variant);
  }

  p {
    font-size: 16px;
    color: var(--md-sys-color-on-surface-variant);
    margin-bottom: 20px;
  }
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

@media (max-width: 768px) {
  .detail-main {
    flex-direction: column;
  }

  .detail-image-section {
    width: 100%;
    .main-image { height: 300px; }
  }

  .recommend-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .product-name {
    font-size: 22px !important;
  }

  .current-price {
    font-size: 28px !important;
  }
}
</style>
