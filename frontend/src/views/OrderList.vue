<template>
  <div class="order-list-page">
    <div class="page-header">
      <h1 class="page-title">我的订单</h1>
    </div>

    <div v-if="loading" class="loading-list">
      <div v-for="i in 3" :key="i" class="skeleton-card"></div>
    </div>

    <div v-else-if="orders.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none">
          <path d="M19 3H5C3.9 3 3 3.9 3 5V19C3 20.1 3.9 21 5 21H19C20.1 21 21 20.1 21 19V5C21 3.9 20.1 3 19 3ZM9 17H7V10H9V17ZM13 17H11V7H13V17ZM17 17H15V13H17V17Z" fill="currentColor"/>
        </svg>
      </div>
      <p class="empty-title">暂无订单</p>
      <p class="empty-text">快去挑选心仪的商品吧</p>
      <el-button type="primary" round @click="$router.push('/products')">去购物</el-button>
    </div>

    <div v-else class="order-list">
      <div
        v-for="order in orders"
        :key="order.id"
        class="order-card card-container"
      >
        <!-- Order Header -->
        <div class="order-header">
          <div class="order-meta">
            <span class="order-no">订单号 {{ order.orderNo }}</span>
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
          </div>
          <div class="order-status">
            <span class="status-chip" :class="`status-chip--${order.status}`">
              {{ getStatusText(order.status) }}
            </span>
          </div>
        </div>

        <!-- Items summary -->
        <div class="order-items" @click="$router.push(`/order/${order.id}`)">
          <div class="items-info">
            <span class="item-count">
              <el-icon><Goods /></el-icon>
              共 {{ getOrderItemCount(order.id) }} 件商品
            </span>
            <span class="view-detail">
              查看详情 <el-icon><ArrowRight /></el-icon>
            </span>
          </div>
        </div>

        <!-- Footer -->
        <div class="order-footer">
          <div class="order-amount">
            <span class="amount-label">实付款</span>
            <span class="amount-value">¥{{ order.totalAmount }}</span>
          </div>
          <div class="order-actions">
            <el-button
              v-if="order.status === 1"
              round
              size="small"
              @click.stop="handleCancel(order.id)"
            >取消订单</el-button>
            <el-button type="primary" round size="small" @click.stop="$router.push(`/order/${order.id}`)">
              查看详情
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-container" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadOrders"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Goods, ArrowRight } from '@element-plus/icons-vue'
import { orderApi } from '@/api/order'

const loading = ref(false)
const orders = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const orderItemCounts = ref({})

const getStatusText = (status) => {
  const texts = { 1: '待支付', 2: '待发货', 3: '待收货', 4: '已完成', 5: '已取消' }
  return texts[status] || '未知'
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const getOrderItemCount = (orderId) => orderItemCounts.value[orderId] || 0

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.getList({ pageNum: pageNum.value, pageSize: pageSize.value })
    orders.value = res.data.records || []
    total.value = res.data.total || 0
    for (const order of orders.value) {
      try {
        const itemsRes = await orderApi.getOrderItems(order.id)
        orderItemCounts.value[order.id] = itemsRes.data?.length || 0
      } catch { orderItemCounts.value[order.id] = 0 }
    }
  } catch (error) { console.error('加载订单失败:', error) }
  finally { loading.value = false }
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await orderApi.cancel(id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) { if (error !== 'cancel') console.error('取消订单失败:', error) }
}

onMounted(() => { loadOrders() })
</script>

<style lang="scss" scoped>
.order-list-page { max-width: 900px; margin: 0 auto; }

.page-header { margin-bottom: 24px; }

.loading-list {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .skeleton-card {
    height: 140px;
    border-radius: var(--md-sys-shape-corner-large);
    background: linear-gradient(90deg, var(--md-sys-color-surface-variant) 25%, var(--color-outline-variant) 50%, var(--md-sys-color-surface-variant) 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
  }
}

.order-list { display: flex; flex-direction: column; gap: 16px; }

.order-card {
  padding: 0;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: var(--md-sys-color-surface-variant);
  border-bottom: 1px solid var(--color-outline-variant);

  .order-meta {
    display: flex;
    gap: 16px;
    align-items: center;

    .order-no {
      font-size: 13px;
      font-weight: 500;
      color: var(--md-sys-color-on-surface);
      font-family: 'Roboto', monospace;
    }

    .order-time {
      font-size: 12px;
      color: var(--md-sys-color-on-surface-variant);
    }
  }
}

.status-chip {
  padding: 4px 14px;
  border-radius: var(--md-sys-shape-corner-full);
  font-size: 12px;
  font-weight: 500;

  &--1 { background: #FFF3CD; color: #664D03; }
  &--2 { background: #E3F2FD; color: #0D47A1; }
  &--3 { background: #E8F5E9; color: #1B5E20; }
  &--4 { background: #F3E5F5; color: #4A148C; }
  &--5 { background: var(--md-sys-color-surface-variant); color: var(--md-sys-color-on-surface-variant); }
}

.order-items {
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.15s;

  &:hover { background: rgba(11, 110, 79, 0.03); }

  .items-info {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .item-count {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 14px;
      color: var(--md-sys-color-on-surface-variant);

      .el-icon { font-size: 16px; }
    }

    .view-detail {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: var(--md-sys-color-primary);
    }
  }
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid var(--color-outline-variant);

  .order-amount {
    display: flex;
    align-items: baseline;
    gap: 8px;

    .amount-label {
      font-size: 13px;
      color: var(--md-sys-color-on-surface-variant);
    }

    .amount-value {
      font-size: 20px;
      font-weight: 600;
      color: var(--md-sys-color-primary);
    }
  }

  .order-actions {
    display: flex;
    gap: 10px;
  }
}

.empty-state {
  text-align: center;
  padding: 80px 20px;

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
</style>
