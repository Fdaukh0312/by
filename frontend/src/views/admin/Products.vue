<template>
  <div class="products-page">
    <!-- Page Header -->
    <div class="admin-page-header">
      <div class="page-title-group">
        <h2 class="page-title">商品管理</h2>
        <span class="page-sub">共 {{ total }} 个商品</span>
      </div>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>
        添加商品
      </el-button>
    </div>

    <!-- Filter Bar -->
    <div class="admin-card filter-card">
      <div class="filter-row">
        <el-input
          v-model="filters.keyword"
          placeholder="搜索商品名称"
          clearable
          style="width: 240px"
          @clear="loadData"
          @keyup.enter="loadData"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filters.categoryId" placeholder="全部分类" clearable style="width: 160px" @change="loadData">
          <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 140px" @change="loadData">
          <el-option label="上架中" :value="1" />
          <el-option label="已下架" :value="0" />
        </el-select>
        <el-button @click="resetFilters">重置</el-button>
      </div>
    </div>

    <!-- Table -->
    <div class="admin-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column label="商品" min-width="280">
          <template #default="{ row }">
            <div class="product-cell">
              <el-image :src="row.image" fit="cover" class="product-thumb">
                <template #error><div class="thumb-placeholder"><el-icon><Picture /></el-icon></div></template>
              </el-image>
              <div class="product-info">
                <div class="product-name">{{ row.name }}</div>
                <div class="product-desc">{{ row.description }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="120" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.categoryName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="价格" width="110" align="center">
          <template #default="{ row }">
            <span class="price-cell">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="90" align="center">
          <template #default="{ row }">
            <span :class="row.stock <= 10 ? 'stock-low' : ''">{{ row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="toggleStatus(row)"
              active-text="上架"
              inactive-text="下架"
              inline-prompt
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" text type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品' : '添加商品'"
      width="580px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
          <el-input v-model="form.image" placeholder="请输入图片URL" />
          <div v-if="form.image" class="image-preview">
            <el-image :src="form.image" fit="cover" style="width: 80px; height: 80px; border-radius: 8px;" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'
import { Plus, Search, Picture } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categoryList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const pagination = reactive({ pageNum: 1, pageSize: 10 })
const filters = reactive({ keyword: '', categoryId: '', status: '' })

const defaultForm = () => ({
  id: null, name: '', description: '', categoryId: null,
  price: 0, stock: 0, image: '', status: 1
})
const form = reactive(defaultForm())

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入商品价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入商品库存', trigger: 'blur' }]
}

const loadCategories = async () => {
  try {
    const res = await adminApi.getCategories()
    categoryList.value = res.data || []
  } catch {}
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await adminApi.getProducts({
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
  filters.categoryId = ''
  filters.status = ''
  pagination.pageNum = 1
  loadData()
}

const openAddDialog = () => {
  Object.assign(form, defaultForm())
  isEdit.value = false
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  Object.assign(form, {
    id: row.id, name: row.name, description: row.description,
    categoryId: row.categoryId, price: row.price, stock: row.stock,
    image: row.image, status: row.status
  })
  isEdit.value = true
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await adminApi.updateProduct(form)
      ElMessage.success('修改成功')
    } else {
      await adminApi.addProduct(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {} finally {
    submitting.value = false
  }
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await adminApi.setProductStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(newStatus === 1 ? '上架成功' : '下架成功')
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除商品「${row.name}」？`, '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await adminApi.deleteProduct(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch {}
  }).catch(() => {})
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style lang="scss" scoped>
.products-page {}

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

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-thumb {
  width: 56px;
  height: 56px;
  border-radius: 10px;
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

.product-info {
  min-width: 0;
  .product-name {
    font-size: 14px;
    font-weight: 500;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .product-desc {
    font-size: 12px;
    color: #aaa;
    margin-top: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.price-cell {
  font-weight: 600;
  color: #0B6E4F;
  font-size: 14px;
}

.stock-low {
  color: #E53935;
  font-weight: 600;
}

.image-preview {
  margin-top: 8px;
}

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
