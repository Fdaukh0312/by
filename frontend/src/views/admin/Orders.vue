<template>
  <div class="orders-page">
    <!-- Page Header -->
    <div class="admin-page-header">
      <div class="page-title-group">
        <h2 class="page-title">订单管理</h2>
        <span class="page-sub">共 {{ total }} 个订单</span>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="admin-card filter-card">
      <div class="filter-row">
        <el-input
          v-model="filters.keyword"
          placeholder="搜索订单号 / 收货人 / 手机"
          clearable
          style="width: 280px"
          @clear="loadData"
          @keyup.enter="loadData"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 140px" @change="loadData">
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-button @click="resetFilters">重置</el-button>
      </div>
    </div>

    <!-- Table -->
    <div class="admin-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column label="订单信息" min-width="220">
          <template #default="{ row }">
            <div class="order-info">
              <div class="order-no">{{ row.orderNo }}</div>
              <div class="order-time">{{ formatTime(row.createTime) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="收货人" width="140">
          <template #default="{ row }">
            <div class="receiver-info">
              <div class="receiver-name">{{ row.receiverName }}</div>
              <div class="receiver-phone">{{ row.receiverPhone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="收货地址" min-width="200">
          <template #default="{ row }">
            <span class="address-cell">{{ row.receiverAddress }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="120" align="center">
          <template #default="{ row }">
            <span class="amount-cell">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="openDetail(row)">详情</el-button>
            <el-button
              v-if="row.status !== 5 && row.status !== 4"
              size="small"
              text
              type="warning"
              @click="openStatusDialog(row)"
            >
              改状态
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- Detail Dialog -->
    <el-dialog v-model="detailVisible" title="订单详情" width="640px">
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-section">
          <div class="detail-title">基本信息</div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">订单号</span>
              <span class="detail-value">{{ currentOrder.orderNo }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">订单状态</span>
              <el-tag :type="getStatusType(currentOrder.status)" size="small">
                {{ getStatusLabel(currentOrder.status) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <span class="detail-label">下单时间</span>
              <span class="detail-value">{{ formatTime(currentOrder.createTime) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">订单金额</span>
              <span class="detail-value amount-cell">¥{{ currentOrder.totalAmount }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <div class="detail-title">收货信息</div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">收货人</span>
              <span class="detail-value">{{ currentOrder.receiverName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">联系电话</span>
              <span class="detail-value">{{ currentOrder.receiverPhone }}</span>
            </div>
            <div class="detail-item detail-item--full">
              <span class="detail-label">收货地址</span>
              <span class="detail-value">{{ currentOrder.receiverAddress }}</span>
            </div>
            <div v-if="currentOrder.remark" class="detail-item detail-item--full">
              <span class="detail-label">买家备注</span>
              <span class="detail-value remark-cell">{{ currentOrder.remark }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <div class="detail-title">商品明细</div>
          <el-table :data="orderItems" size="small" border>
            <el-table-column label="商品" min-width="200">
              <template #default="{ item }">
                <div class="item-cell">
                  <el-image :src="item.productImage" fit="cover" class="item-thumb">
                    <template #error><div class="thumb-placeholder"><el-icon><Picture /></el-icon></div></template>
                  </el-image>
                  <span>{{ item.productName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="100" align="center">
              <template #default="{ item }">
                <span class="price-cell">¥{{ item.price }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column label="小计" width="110" align="center">
              <template #default="{ item }">
                <span class="subtotal-cell">¥{{ item.subtotal }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="currentOrder && currentOrder.status !== 5 && currentOrder.status !== 4"
          type="primary"
          @click="openStatusDialog(currentOrder)"
        >
          修改状态
        </el-button>
      </template>
    </el-dialog>

    <!-- Status Dialog -->
    <el-dialog v-model="statusDialogVisible" title="修改订单状态" width="400px">
      <div class="status-steps">
        <el-steps :space="80" direction="horizontal" :active="currentOrder ? currentOrder.status - 1 : 0" finish-status="success">
          <el-step title="待支付" />
          <el-step title="处理中" />
          <el-step title="已发货" />
          <el-step title="已完成" />
          <el-step title="已取消" />
        </el-steps>
      </div>
      <el-select v-model="newStatus" placeholder="选择新状态" style="width: 100%; margin-top: 20px">
        <el-option
          v-for="s in nextStatusOptions"
          :key="s.value"
          :label="s.label"
          :value="s.value"
        />
      </el-select>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="confirmStatusChange">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/admin'
import { Search, Picture } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const orderItems = ref([])
const detailVisible = ref(false)
const statusDialogVisible = ref(false)
const currentOrder = ref(null)
const newStatus = ref(null)
const submitting = ref(false)

const pagination = reactive({ pageNum: 1, pageSize: 10 })
const filters = reactive({ keyword: '', status: '' })

const statusOptions = [
  { value: 1, label: '待支付' },
  { value: 2, label: '处理中' },
  { value: 3, label: '已发货' },
  { value: 4, label: '已完成' },
  { value: 5, label: '已取消' }
]

const statusMap = { 1: '待支付', 2: '处理中', 3: '已发货', 4: '已完成', 5: '已取消' }

const nextStatusOptions = computed(() => {
  if (!currentOrder.value) return []
  return statusOptions.filter(s => s.value !== currentOrder.value.status)
})

const getStatusLabel = (v) => statusMap[v] || '未知'
const getStatusType = (v) => {
  const map = { 1: 'warning', 2: 'primary', 3: 'info', 4: 'success', 5: 'danger' }
  return map[v] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await adminApi.getOrders({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...filters
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {} finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.keyword = ''
  filters.status = ''
  pagination.pageNum = 1
  loadData()
}

const openDetail = async (row) => {
  currentOrder.value = row
  detailVisible.value = true
  try {
    const res = await adminApi.getOrderItems(row.id)
    orderItems.value = res.data || []
  } catch {}
}

const openStatusDialog = (row) => {
  currentOrder.value = row
  newStatus.value = null
  statusDialogVisible.value = true
}

const confirmStatusChange = async () => {
  if (!newStatus.value) {
    ElMessage.warning('请选择新状态')
    return
  }
  submitting.value = true
  try {
    await adminApi.setOrderStatus(currentOrder.value.id, newStatus.value)
    currentOrder.value.status = newStatus.value
    ElMessage.success('状态修改成功')
    statusDialogVisible.value = false
    loadData()
  } catch {} finally {
    submitting.value = false
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.orders-page {}

.page-title-group {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #1F1F1F;
}

.page-sub {
  font-size: 13px;
  color: #999;
}

.filter-card { margin-bottom: 16px; }

.filter-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.order-info {
  .order-no {
    font-size: 13px;
    font-weight: 600;
    color: #333;
    font-family: 'Roboto Mono', monospace;
  }
  .order-time {
    font-size: 12px;
    color: #aaa;
    margin-top: 3px;
  }
}

.receiver-info {
  .receiver-name { font-size: 14px; font-weight: 500; color: #333; }
  .receiver-phone { font-size: 12px; color: #aaa; margin-top: 3px; }
}

.address-cell {
  font-size: 13px;
  color: #666;
}

.amount-cell {
  font-weight: 700;
  color: #E53935;
  font-size: 14px;
}

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.order-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section {
  .detail-title {
    font-size: 14px;
    font-weight: 600;
    color: #333;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 1px solid #f0f0f0;
  }
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;

  &--full {
    grid-column: 1 / -1;
  }

  .detail-label {
    font-size: 12px;
    color: #aaa;
    flex-shrink: 0;
    width: 64px;
  }

  .detail-value {
    font-size: 13px;
    color: #555;
    word-break: break-all;
  }

  .remark-cell {
    color: #E65100;
    background: #FFF8E1;
    padding: 4px 8px;
    border-radius: 4px;
  }
}

.item-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-thumb {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  flex-shrink: 0;
  overflow: hidden;
  border: 1px solid #f0f0f0;
}

.thumb-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #ccc;
}

.price-cell { color: #555; font-size: 13px; }
.subtotal-cell { font-weight: 600; color: #E53935; font-size: 13px; }

.status-steps {
  padding: 10px 0;
}
</style>
