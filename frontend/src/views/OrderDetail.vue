<template>
  <div class="order-detail-page">
    <div class="detail-header">
      <el-button text @click="$router.push('/order')" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回订单列表
      </el-button>
      <h1 class="page-title">订单详情</h1>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="skeleton-block"></div>
    </div>

    <div v-else-if="!order.id" class="empty-state">
      <p>订单不存在</p>
      <el-button type="primary" round @click="$router.push('/order')">返回</el-button>
    </div>

    <div v-else class="detail-content">
      <!-- Status Banner -->
      <div class="status-banner" :class="`status-banner--${order.status}`">
        <div class="status-icon">
          <el-icon size="36"><component :is="getStatusIcon(order.status)" /></el-icon>
        </div>
        <div class="status-info">
          <h2>{{ getStatusTitle(order.status) }}</h2>
          <p>{{ getStatusDesc(order.status) }}</p>
        </div>
      </div>

      <!-- Address -->
      <div class="detail-section card-container">
        <div class="section-header">
          <el-icon><Location /></el-icon>
          <h3>收货信息</h3>
        </div>
        <div class="address-info">
          <div class="address-row">
            <span class="address-name">{{ order.receiverName }}</span>
            <span class="address-phone">{{ order.receiverPhone }}</span>
          </div>
          <div class="address-text">{{ order.receiverAddress }}</div>
          <div v-if="order.remark" class="address-remark">备注：{{ order.remark }}</div>
        </div>
      </div>

      <!-- Items -->
      <div class="detail-section card-container">
        <div class="section-header">
          <el-icon><Goods /></el-icon>
          <h3>商品清单</h3>
        </div>
        <div class="items-list">
          <div v-for="item in orderItems" :key="item.id" class="item-row">
            <img :src="item.productImage" class="item-image" :alt="item.productName" />
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
            </div>
            <div class="item-meta">
              <span class="item-price">¥{{ item.price }}</span>
              <span class="item-qty">x{{ item.quantity }}</span>
              <span class="item-subtotal">¥{{ item.subtotal }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Summary -->
      <div class="summary-section card-container">
        <div class="summary-row">
          <span>订单编号</span>
          <span class="mono">{{ order.orderNo }}</span>
        </div>
        <div class="summary-row">
          <span>下单时间</span>
          <span>{{ formatTime(order.createTime) }}</span>
        </div>
        <div class="summary-divider"></div>
        <div class="summary-row total">
          <span>实付款</span>
          <span class="total-amount">¥{{ order.totalAmount }}</span>
        </div>
        <div class="action-row">
          <el-button v-if="order.status === 1" type="danger" round @click="handleCancel">取消订单</el-button>
          <el-button round @click="$router.push('/order')">返回</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Location, Goods, Clock, Box, Van, CircleCheck, Close, InfoFilled } from '@element-plus/icons-vue'
import { orderApi } from '@/api/order'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref({})
const orderItems = ref([])

const getStatusIcon = (s) => ({ 1: Clock, 2: Box, 3: Van, 4: CircleCheck, 5: Close })[s] || InfoFilled
const getStatusTitle = (s) => ({ 1: '待支付', 2: '待发货', 3: '配送中', 4: '已完成', 5: '已取消' })[s] || '未知'
const getStatusDesc = (s) => ({
  1: '请尽快完成支付，订单将保留 24 小时',
  2: '商家正在准备商品，请耐心等待',
  3: '商品配送中，请注意查收',
  4: '交易已完成，感谢您的购买',
  5: '订单已取消'
})[s] || ''

const formatTime = (t) => t ? new Date(t).toLocaleString('zh-CN') : ''

const loadOrderDetail = async () => {
  loading.value = true
  try {
    const [orderRes, itemsRes] = await Promise.all([
      orderApi.getList({ pageNum: 1, pageSize: 100 }),
      orderApi.getOrderItems(route.params.id)
    ])
    const orders = orderRes.data.records || []
    order.value = orders.find(o => o.id == route.params.id) || {}
    orderItems.value = itemsRes.data || []
  } catch (error) { console.error('加载订单详情失败:', error) }
  finally { loading.value = false }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await orderApi.cancel(route.params.id)
    ElMessage.success('订单已取消')
    loadOrderDetail()
  } catch (error) { if (error !== 'cancel') console.error('取消订单失败:', error) }
}

onMounted(() => { loadOrderDetail() })
</script>

<style lang="scss" scoped>
.order-detail-page { max-width: 800px; margin: 0 auto; }

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;

  .back-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    color: var(--md-sys-color-primary);
    font-size: 14px;
  }
}

.loading-container .skeleton-block {
  height: 300px;
  border-radius: var(--md-sys-shape-corner-large);
  background: linear-gradient(90deg, var(--md-sys-color-surface-variant) 25%, var(--color-outline-variant) 50%, var(--md-sys-color-surface-variant) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.detail-content { display: flex; flex-direction: column; gap: 16px; }

.status-banner {
  border-radius: var(--md-sys-shape-corner-large);
  padding: 32px;
  display: flex;
  align-items: center;
  gap: 20px;
  color: #fff;

  &--1 { background: linear-gradient(135deg, #F59E0B, #D97706); }
  &--2 { background: linear-gradient(135deg, #3B82F6, #2563EB); }
  &--3 { background: linear-gradient(135deg, #10B981, #059669); }
  &--4 { background: linear-gradient(135deg, #8B5CF6, #7C3AED); }
  &--5 { background: var(--md-sys-color-surface-variant); color: var(--md-sys-color-on-surface-variant); }

  .status-icon {
    width: 72px;
    height: 72px;
    background: rgba(255,255,255,0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(8px);
  }

  h2 {
    font-size: 22px;
    font-weight: 500;
    margin-bottom: 6px;
  }

  p { font-size: 14px; opacity: 0.9; }
}

.detail-section {
  .section-header {
    display: flex;
    align-items: center;
    gap: 10px;
    padding-bottom: 16px;
    margin-bottom: 16px;
    border-bottom: 1px solid var(--color-outline-variant);

    h3 { font-size: 16px; font-weight: 500; color: var(--md-sys-color-on-surface); }
    .el-icon { color: var(--md-sys-color-primary); }
  }
}

.address-info {
  .address-row {
    display: flex;
    gap: 16px;
    margin-bottom: 6px;
    .address-name { font-weight: 500; color: var(--md-sys-color-on-surface); }
    .address-phone { color: var(--md-sys-color-on-surface-variant); }
  }
  .address-text { font-size: 14px; color: var(--md-sys-color-on-surface-variant); }
  .address-remark { font-size: 13px; color: var(--md-sys-color-on-surface-variant); margin-top: 8px; padding: 8px 12px; background: var(--md-sys-color-surface-variant); border-radius: var(--md-sys-shape-corner-medium); }
}

.items-list { display: flex; flex-direction: column; gap: 12px; }

.item-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  border-radius: var(--md-sys-shape-corner-medium);
  transition: background 0.15s;

  &:hover { background: var(--md-sys-color-surface-variant); }

  .item-image {
    width: 64px;
    height: 64px;
    border-radius: var(--md-sys-shape-corner-medium);
    object-fit: cover;
    background: var(--md-sys-color-surface-variant);
    flex-shrink: 0;
  }

  .item-info {
    flex: 1;
    min-width: 0;
    .item-name { font-size: 14px; color: var(--md-sys-color-on-surface); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  }

  .item-meta {
    display: flex;
    gap: 16px;
    align-items: center;
    flex-shrink: 0;

    .item-price { font-size: 14px; color: var(--md-sys-color-on-surface-variant); }
    .item-qty { font-size: 14px; color: var(--md-sys-color-on-surface-variant); }
    .item-subtotal { font-size: 15px; font-weight: 600; color: var(--md-sys-color-primary); }
  }
}

.summary-section {
  .summary-row {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    font-size: 14px;
    color: var(--md-sys-color-on-surface-variant);

    .mono { font-family: 'Roboto', monospace; font-size: 13px; }
  }

  .summary-divider { height: 1px; background: var(--color-outline-variant); margin: 8px 0; }

  .summary-row.total {
    font-size: 16px;
    color: var(--md-sys-color-on-surface);
    padding: 12px 0;

    .total-amount {
      font-size: 28px;
      font-weight: 600;
      color: var(--md-sys-color-primary);
    }
  }

  .action-row {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 16px;
  }
}

.empty-state {
  text-align: center;
  padding: 80px 20px;

  p { font-size: 16px; color: var(--md-sys-color-on-surface-variant); margin-bottom: 20px; }
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
