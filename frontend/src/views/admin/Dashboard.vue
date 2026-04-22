<template>
  <div class="dashboard">
    <!-- Stat Cards -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-icon stat-icon--green">
          <el-icon><Goods /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.productCount || 0 }}</div>
          <div class="stat-label">商品总数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon--blue">
          <el-icon><Goods /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.activeProductCount || 0 }}</div>
          <div class="stat-label">在售商品</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon--orange">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.userCount || 0 }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon--purple">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.orderCount || 0 }}</div>
          <div class="stat-label">订单总数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon--red">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pendingOrderCount || 0 }}</div>
          <div class="stat-label">待处理订单</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon--teal">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value stat-value--money">¥{{ stats.totalSales || '0.00' }}</div>
          <div class="stat-label">累计销售额</div>
        </div>
      </div>

      <div class="stat-card stat-card--highlight">
        <div class="stat-icon stat-icon--gold">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value stat-value--money">¥{{ stats.todaySales || '0.00' }}</div>
          <div class="stat-label">今日销售额</div>
        </div>
      </div>
    </div>

    <!-- Quick Action -->
    <div class="admin-card">
      <div class="card-header">
        <h3>快捷操作</h3>
      </div>
      <div class="quick-actions">
        <router-link to="/admin/products" class="quick-action-btn">
          <el-icon><Plus /></el-icon>
          <span>添加商品</span>
        </router-link>
        <router-link to="/admin/categories" class="quick-action-btn">
          <el-icon><FolderAdd /></el-icon>
          <span>添加分类</span>
        </router-link>
        <router-link to="/admin/orders" class="quick-action-btn">
          <el-icon><List /></el-icon>
          <span>处理订单</span>
        </router-link>
      </div>
    </div>

    <!-- Recent Activity -->
    <div class="admin-card">
      <div class="card-header">
        <h3>待处理事项</h3>
      </div>
      <div class="activity-list">
        <div class="activity-item" v-if="(stats.pendingOrderCount || 0) > 0">
          <div class="activity-dot activity-dot--warning"></div>
          <div class="activity-content">
            <div class="activity-title">待处理订单</div>
            <div class="activity-desc">当前有 {{ stats.pendingOrderCount }} 个订单等待处理</div>
          </div>
          <router-link to="/admin/orders" class="activity-action">
            立即处理 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="activity-item" v-else>
          <div class="activity-dot activity-dot--success"></div>
          <div class="activity-content">
            <div class="activity-title">所有订单已处理完毕</div>
            <div class="activity-desc">太棒了，当前没有待处理的订单</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import {
  Goods, User, Document, Clock, Money,
  TrendCharts, Plus, FolderAdd, List, ArrowRight
} from '@element-plus/icons-vue'

const stats = ref({})

const loadStats = async () => {
  try {
    const res = await adminApi.getStats()
    stats.value = res.data || {}
  } catch (e) {
    // silently fail
  }
}

onMounted(loadStats)
</script>

<style lang="scss" scoped>
.dashboard {}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 18px;
  border: 1px solid #E8E8E8;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  transition: box-shadow 0.2s ease, transform 0.2s ease;

  &:hover {
    box-shadow: 0 4px 16px rgba(0,0,0,0.08);
    transform: translateY(-2px);
  }

  &--highlight {
    background: linear-gradient(135deg, #0B6E4F 0%, #2ecc71 100%);
    border: none;

    .stat-icon { background: rgba(255,255,255,0.2); color: #fff; }
    .stat-value { color: #fff; }
    .stat-label { color: rgba(255,255,255,0.75); }
  }
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;

  &--green { background: rgba(11,110,79,0.1); color: #0B6E4F; }
  &--blue { background: rgba(25,118,210,0.1); color: #1976D2; }
  &--orange { background: rgba(230,81,0,0.1); color: #E65100; }
  &--purple { background: rgba(103,58,183,0.1); color: #7B1FA2; }
  &--red { background: rgba(211,47,47,0.1); color: #C62828; }
  &--teal { background: rgba(0,150,136,0.1); color: #00897B; }
  &--gold { background: rgba(255,255,255,0.2); color: #fff; }
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1F1F1F;
  line-height: 1.1;
  font-family: 'Roboto', 'PingFang SC', sans-serif;

  &--money {
    font-size: 22px;
    letter-spacing: -0.5px;
  }
}

.stat-label {
  font-size: 13px;
  color: #888;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #1F1F1F;
  }
}

.quick-actions {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.quick-action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 10px;
  background: #F5F7FA;
  color: #555;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s ease;
  border: 1px solid transparent;

  &:hover {
    background: #0B6E4F;
    color: #fff;
    border-color: #0B6E4F;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(11,110,79,0.25);
  }
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 0;
  border-bottom: 1px solid #F0F0F0;

  &:last-child { border-bottom: none; }
}

.activity-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;

  &--warning { background: #FF9800; box-shadow: 0 0 0 3px rgba(255,152,0,0.15); }
  &--success { background: #4CAF50; box-shadow: 0 0 0 3px rgba(76,175,80,0.15); }
}

.activity-content {
  flex: 1;

  .activity-title {
    font-size: 14px;
    font-weight: 500;
    color: #1F1F1F;
  }

  .activity-desc {
    font-size: 12px;
    color: #999;
    margin-top: 3px;
  }
}

.activity-action {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #0B6E4F;
  text-decoration: none;
  font-weight: 500;

  &:hover { text-decoration: underline; }
}

@media (max-width: 1280px) {
  .stat-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 1024px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
