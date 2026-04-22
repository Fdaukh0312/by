<template>
  <div class="order-page">
    <div class="order-header">
      <h1 class="page-title">确认订单</h1>
    </div>

    <div class="order-layout">
      <!-- Left: Form + Items -->
      <div class="order-left">
        <!-- Address -->
        <div class="section-card card-container">
          <div class="section-header">
            <el-icon><Location /></el-icon>
            <h3>收货信息</h3>
          </div>
          <el-form ref="addressFormRef" :model="addressForm" :rules="addressRules" label-position="top">
            <div class="form-grid">
              <el-form-item label="收货人" prop="receiverName" class="form-item-half">
                <el-input v-model="addressForm.receiverName" placeholder="收货人姓名" />
              </el-form-item>
              <el-form-item label="联系电话" prop="receiverPhone" class="form-item-half">
                <el-input v-model="addressForm.receiverPhone" placeholder="手机号码" />
              </el-form-item>
            </div>
            <el-form-item label="收货地址" prop="receiverAddress">
              <el-input v-model="addressForm.receiverAddress" type="textarea" :rows="2" placeholder="详细收货地址" />
            </el-form-item>
            <el-form-item label="备注（选填）">
              <el-input v-model="addressForm.remark" type="textarea" :rows="2" placeholder="可备注特殊需求" />
            </el-form-item>
          </el-form>
        </div>

        <!-- Items -->
        <div class="section-card card-container">
          <div class="section-header">
            <el-icon><Goods /></el-icon>
            <h3>商品清单</h3>
          </div>
          <div class="items-list">
            <div v-for="item in cartItems" :key="item.id" class="item-row">
              <img :src="item.product?.image" class="item-image" :alt="item.product?.name" />
              <div class="item-info">
                <div class="item-name">{{ item.product?.name }}</div>
                <div class="item-price">¥{{ item.product?.price }} x {{ item.quantity }}</div>
              </div>
              <div class="item-subtotal">¥{{ ((item.product?.price || 0) * item.quantity).toFixed(2) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Summary -->
      <div class="order-right">
        <div class="summary-card card-container">
          <h3 class="summary-title">订单摘要</h3>
          <div class="summary-row">
            <span>商品总数</span>
            <span>{{ totalCount }} 件</span>
          </div>
          <div class="summary-row">
            <span>商品金额</span>
            <span>¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>运费</span>
            <span :class="{ free: freight === 0 }">{{ freight > 0 ? '¥' + freight : '免运费' }}</span>
          </div>
          <div class="summary-divider"></div>
          <div class="summary-row total">
            <span>应付总额</span>
            <span class="total-amount">¥{{ finalPrice.toFixed(2) }}</span>
          </div>
          <div class="submit-btn-wrap">
            <el-button type="primary" size="large" round :loading="submitting" class="submit-btn" @click="handleSubmitOrder">
              提交订单
            </el-button>
            <el-button text @click="$router.push('/cart')" class="back-link">返回购物车</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Goods } from '@element-plus/icons-vue'
import { cartApi } from '@/api/cart'
import { orderApi } from '@/api/order'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()
const addressFormRef = ref()
const submitting = ref(false)
const cartItems = ref([])

const addressForm = ref({ receiverName: '', receiverPhone: '', receiverAddress: '', remark: '' })

const addressRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  receiverAddress: [{ required: true, message: '请输入收货地址', trigger: 'blur' }]
}

const totalCount = computed(() => cartItems.value.reduce((sum, item) => sum + item.quantity, 0))
const totalPrice = computed(() => cartItems.value.reduce((sum, item) => sum + (item.product?.price || 0) * item.quantity, 0))
const freight = computed(() => totalPrice.value >= 99 ? 0 : 10)
const finalPrice = computed(() => totalPrice.value + freight.value)

const loadCart = async () => {
  try {
    const res = await cartApi.getList()
    const items = res.data || []
    if (items.length === 0) {
      ElMessage.warning('购物车是空的，请先添加商品')
      router.push('/products')
      return
    }
    cartItems.value = items
  } catch (error) {
    console.error('加载购物车失败:', error)
    router.push('/products')
  }
}

const handleSubmitOrder = async () => {
  const valid = await addressFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await orderApi.create(addressForm.value)
    ElMessage.success('订单提交成功')
    await cartStore.updateCartCount()
    router.push('/order')
  } catch (error) { console.error('提交订单失败:', error) }
  finally { submitting.value = false }
}

onMounted(() => { loadCart() })
</script>

<style lang="scss" scoped>
.order-page { max-width: 1000px; margin: 0 auto; }
.order-header { margin-bottom: 24px; }

.order-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.order-left { flex: 1; display: flex; flex-direction: column; gap: 16px; }
.order-right { width: 300px; flex-shrink: 0; position: sticky; top: 88px; }

.section-card {
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

.form-grid {
  display: flex;
  gap: 16px;

  .form-item-half { flex: 1; }
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

    .item-name {
      font-size: 14px;
      color: var(--md-sys-color-on-surface);
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .item-price {
      font-size: 13px;
      color: var(--md-sys-color-on-surface-variant);
    }
  }

  .item-subtotal {
    font-size: 15px;
    font-weight: 600;
    color: var(--md-sys-color-primary);
  }
}

.summary-card {
  .summary-title {
    font-size: 16px;
    font-weight: 500;
    color: var(--md-sys-color-on-surface);
    margin-bottom: 16px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--color-outline-variant);
  }

  .summary-row {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    font-size: 14px;
    color: var(--md-sys-color-on-surface-variant);

    .free { color: var(--md-sys-color-primary); }
  }

  .summary-divider { height: 1px; background: var(--color-outline-variant); margin: 8px 0; }

  .summary-row.total {
    font-size: 16px;
    color: var(--md-sys-color-on-surface);
    font-weight: 500;
    padding: 12px 0;

    .total-amount {
      font-size: 24px;
      font-weight: 600;
      color: var(--md-sys-color-primary);
    }
  }

  .submit-btn-wrap {
    margin-top: 20px;

    .submit-btn {
      width: 100%;
      height: 52px;
      font-size: 16px !important;
    }

    .back-link {
      width: 100%;
      margin-top: 8px;
      color: var(--md-sys-color-on-surface-variant);
      font-size: 13px;
    }
  }
}

@media (max-width: 768px) {
  .order-layout { flex-direction: column; }
  .order-right { width: 100%; position: static; }
  .form-grid { flex-direction: column; }
}
</style>
