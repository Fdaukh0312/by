<template>
  <div class="cart-page">
    <div class="cart-header">
      <h1 class="page-title">我的购物车</h1>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="cart-loading">
      <div v-for="i in 3" :key="i" class="skeleton-card"></div>
    </div>

    <!-- Empty -->
    <div v-else-if="cartItems.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none">
          <path d="M7 18C5.9 18 5 18.9 5 20C5 21.1 5.9 22 7 22C8.1 22 9 21.1 9 20C9 18.9 8.1 18 7 18ZM1 2V4H3L6.6 11.59L5.25 14.04C5.09 14.32 5 14.65 5 15C5 16.1 5.9 17 7 17H19V15H7.42C7.28 15 7.17 14.89 7.1 14.76L7.2 14.63L8.1 13H15.55C16.3 13 16.96 12.59 17.3 11.97L20.88 5.48C20.96 5.34 21 5.17 21 5C21 4.45 20.55 4 20 4H5.21L4.27 2H1ZM17 18C15.9 18 15 18.9 15 20C15 21.1 15.9 22 17 22C18.1 22 19 21.1 19 20C19 18.9 18.1 18 17 18Z" fill="currentColor"/>
        </svg>
      </div>
      <p class="empty-title">购物车是空的</p>
      <p class="empty-text">快去挑选心仪的商品吧</p>
      <el-button type="primary" round @click="$router.push('/products')">去逛逛</el-button>
    </div>

    <!-- Content -->
    <div v-else class="cart-content">
      <!-- Item list -->
      <div class="cart-list">
        <div class="list-header">
          <el-checkbox
            v-model="allSelected"
            @change="toggleAll"
            class="header-checkbox"
          >
            全选
          </el-checkbox>
          <div class="header-info">商品信息</div>
          <div class="header-price">单价</div>
          <div class="header-qty">数量</div>
          <div class="header-subtotal">小计</div>
          <div class="header-action">操作</div>
        </div>

        <div
          v-for="item in cartItems"
          :key="item.id"
          class="cart-item"
          :class="{ 'cart-item--selected': item.selected }"
        >
          <el-checkbox v-model="item.selected" @change="updateSelected" class="item-checkbox" />

          <div class="item-info" @click="$router.push(`/product/${item.productId}`)">
            <img :src="item.product?.image" class="item-image" :alt="item.product?.name" />
            <div class="item-details">
              <div class="item-name">{{ item.product?.name }}</div>
              <div class="item-desc">{{ item.product?.description }}</div>
            </div>
          </div>

          <div class="item-price">¥{{ item.product?.price }}</div>

          <div class="item-qty">
            <div class="qty-control">
              <button class="qty-btn" @click="changeQty(item, -1)">−</button>
              <span class="qty-num">{{ item.quantity }}</span>
              <button class="qty-btn" @click="changeQty(item, 1)">+</button>
            </div>
          </div>

          <div class="item-subtotal">¥{{ ((item.product?.price || 0) * item.quantity).toFixed(2) }}</div>

          <div class="item-action">
            <el-button text type="danger" @click="handleRemove(item.id)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <!-- Bottom bar -->
      <div class="cart-bottom-bar">
        <div class="bar-left">
          <el-checkbox v-model="allSelected" @change="toggleAll">全选</el-checkbox>
          <el-button text type="danger" @click="handleClearCart" class="clear-btn">清空购物车</el-button>
        </div>
        <div class="bar-right">
          <div class="summary-info">
            <span class="selected-count">已选 {{ selectedCount }} 件</span>
            <div class="total-price">
              <span class="label">合计：</span>
              <span class="amount">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
          </div>
          <el-button type="primary" size="large" round class="checkout-btn" @click="handleCheckout">
            去结算
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { cartApi } from '@/api/cart'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()
const loading = ref(false)
const cartItems = ref([])

const allSelected = computed({
  get: () => cartItems.value.length > 0 && cartItems.value.every(item => item.selected),
  set: (val) => cartItems.value.forEach(item => item.selected = val)
})

const selectedCount = computed(() => cartItems.value.filter(item => item.selected).length)
const totalPrice = computed(() =>
  cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + (item.product?.price || 0) * item.quantity, 0)
)

const loadCart = async () => {
  loading.value = true
  try {
    const res = await cartApi.getList()
    cartItems.value = (res.data || []).map(item => ({ ...item, selected: true }))
  } catch (error) {
    console.error('加载购物车失败:', error)
  } finally {
    loading.value = false
  }
}

const toggleAll = () => {
  cartItems.value = [...cartItems.value]
}

const updateSelected = () => {
  cartItems.value = [...cartItems.value]
}

const changeQty = async (item, delta) => {
  const newQty = item.quantity + delta
  if (newQty < 1 || newQty > (item.product?.stock || 999)) return
  item.quantity = newQty
  try {
    await cartApi.updateQuantity(item.id, newQty)
  } catch {
    loadCart()
  }
}

const handleRemove = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这件商品吗？', '提示', { type: 'warning' })
    await cartApi.remove(id)
    ElMessage.success('已删除')
    loadCart()
    cartStore.updateCartCount()
  } catch (error) {
    if (error !== 'cancel') console.error('删除失败:', error)
  }
}

const handleClearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', { type: 'warning' })
    await cartApi.clear()
    ElMessage.success('已清空')
    loadCart()
    cartStore.updateCartCount()
  } catch (error) {
    if (error !== 'cancel') console.error('清空失败:', error)
  }
}

const handleCheckout = () => {
  if (selectedCount.value === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  router.push('/order')
}

onMounted(() => { loadCart() })
</script>

<style lang="scss" scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
}

.cart-header {
  margin-bottom: 24px;

  .page-title {
    font-size: 28px;
    font-weight: 400;
    color: var(--md-sys-color-on-surface);
  }
}

.cart-loading {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .skeleton-card {
    height: 100px;
    border-radius: var(--md-sys-shape-corner-large);
    background: linear-gradient(90deg, var(--md-sys-color-surface-variant) 25%, var(--color-outline-variant) 50%, var(--md-sys-color-surface-variant) 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
  }
}

.cart-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cart-list {
  background: var(--md-sys-color-surface);
  border-radius: var(--md-sys-shape-corner-large);
  box-shadow: var(--md-sys-elevation-1);
  border: 1px solid var(--color-outline-variant);
  overflow: hidden;
}

.list-header {
  display: grid;
  grid-template-columns: 48px 1fr 120px 140px 120px 60px;
  align-items: center;
  padding: 14px 20px;
  background: var(--md-sys-color-surface-variant);
  font-size: 13px;
  font-weight: 500;
  color: var(--md-sys-color-on-surface-variant);
  gap: 12px;
}

.cart-item {
  display: grid;
  grid-template-columns: 48px 1fr 120px 140px 120px 60px;
  align-items: center;
  padding: 16px 20px;
  gap: 12px;
  border-top: 1px solid var(--color-outline-variant);
  transition: background 0.15s;

  &:hover {
    background: rgba(11, 110, 79, 0.03);
  }

  &--selected {
    background: rgba(11, 110, 79, 0.04);
  }
}

.item-info {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;

  .item-image {
    width: 80px;
    height: 80px;
    border-radius: var(--md-sys-shape-corner-medium);
    object-fit: cover;
    background: var(--md-sys-color-surface-variant);
    flex-shrink: 0;
  }

  .item-name {
    font-size: 14px;
    color: var(--md-sys-color-on-surface);
    margin-bottom: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .item-desc {
    font-size: 12px;
    color: var(--md-sys-color-on-surface-variant);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 300px;
  }
}

.item-price {
  font-size: 15px;
  font-weight: 500;
  color: var(--md-sys-color-on-surface);
}

.qty-control {
  display: flex;
  align-items: center;
  background: var(--md-sys-color-surface-variant);
  border-radius: var(--md-sys-shape-corner-full);
  overflow: hidden;
  width: fit-content;

  .qty-btn {
    width: 32px;
    height: 32px;
    border: none;
    background: transparent;
    font-size: 18px;
    color: var(--md-sys-color-on-surface-variant);
    cursor: pointer;
    transition: all 0.15s;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      background: var(--color-primary-container);
      color: var(--md-sys-color-primary);
    }
  }

  .qty-num {
    min-width: 36px;
    text-align: center;
    font-size: 14px;
    font-weight: 500;
    color: var(--md-sys-color-on-surface);
  }
}

.item-subtotal {
  font-size: 16px;
  font-weight: 600;
  color: var(--md-sys-color-primary);
}

.cart-bottom-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--md-sys-color-surface);
  border-radius: var(--md-sys-shape-corner-large);
  box-shadow: var(--md-sys-elevation-2);
  border: 1px solid var(--color-outline-variant);
  padding: 16px 24px;
  position: sticky;
  bottom: 16px;

  .bar-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .bar-right {
    display: flex;
    align-items: center;
    gap: 24px;
  }

  .selected-count {
    font-size: 14px;
    color: var(--md-sys-color-on-surface-variant);
  }

  .summary-info {
    text-align: right;
  }

  .total-price {
    display: flex;
    align-items: baseline;
    gap: 8px;

    .label {
      font-size: 14px;
      color: var(--md-sys-color-on-surface-variant);
    }

    .amount {
      font-size: 24px;
      font-weight: 500;
      color: var(--md-sys-color-primary);
    }
  }

  .checkout-btn {
    height: 48px;
    padding: 0 32px;
    font-size: 16px !important;
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

  .empty-title {
    font-size: 18px;
    font-weight: 500;
    color: var(--md-sys-color-on-surface);
    margin-bottom: 8px;
  }

  .empty-text {
    font-size: 14px;
    color: var(--md-sys-color-on-surface-variant);
    margin-bottom: 24px;
  }
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

@media (max-width: 768px) {
  .list-header { display: none; }

  .cart-item {
    grid-template-columns: 40px 1fr;
    grid-template-rows: auto auto;

    .item-checkbox { grid-row: 1 / 3; }
    .item-info { grid-column: 2; }
    .item-price { display: none; }
    .item-qty { grid-column: 2; }
    .item-subtotal { display: none; }
    .item-action { position: absolute; right: 20px; top: 16px; }

    position: relative;
  }

  .item-qty {
    margin-top: 8px;
  }
}
</style>
