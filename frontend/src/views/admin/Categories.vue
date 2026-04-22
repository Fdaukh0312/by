<template>
  <div class="categories-page">
    <!-- Page Header -->
    <div class="admin-page-header">
      <div class="page-title-group">
        <h2 class="page-title">分类管理</h2>
        <span class="page-sub">共 {{ tableData.length }} 个分类</span>
      </div>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>
        添加分类
      </el-button>
    </div>

    <!-- Table -->
    <div class="admin-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column label="分类图标" width="80" align="center">
          <template #default="{ row }">
            <div class="category-icon" :style="{ background: row.iconBg || '#f0f0f0' }">
              <span>{{ row.name.charAt(0) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="分类名称" prop="name" min-width="200">
          <template #default="{ row }">
            <span class="category-name">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sort" width="100" align="center">
          <template #default="{ row }">
            <el-input-number
              v-model="row.sort"
              :min="0"
              :max="9999"
              size="small"
              controls-position="right"
              @change="updateSort(row)"
              style="width: 90px"
            />
          </template>
        </el-table-column>
        <el-table-column label="商品数" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.productCount || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" align="center">
          <template #default="{ row }">
            <span class="time-cell">{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" text type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类' : '添加分类'"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" maxlength="30" show-word-limit placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序权重" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999" style="width: 100%" />
          <div class="form-tip">数值越小越靠前，建议使用 10、20、30...</div>
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
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({ id: null, name: '', sort: 0 })
const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await adminApi.getCategories()
    tableData.value = res.data || []
  } catch {} finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  form.id = null
  form.name = ''
  form.sort = (tableData.value.length + 1) * 10
  isEdit.value = false
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  form.id = row.id
  form.name = row.name
  form.sort = row.sort
  isEdit.value = true
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await adminApi.updateCategory({ id: form.id, name: form.name, sort: form.sort })
      ElMessage.success('修改成功')
    } else {
      await adminApi.addCategory({ name: form.name, sort: form.sort })
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {} finally {
    submitting.value = false
  }
}

const updateSort = async (row) => {
  try {
    await adminApi.updateCategory({ id: row.id, name: row.name, sort: row.sort })
    ElMessage.success('排序已更新')
  } catch {
    loadData()
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除分类「${row.name}」？`, '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await adminApi.deleteCategory(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch {}
  }).catch(() => {})
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.categories-page {}

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

.category-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  color: #fff;
}

.category-name {
  font-weight: 500;
  color: #333;
}

.time-cell {
  font-size: 12px;
  color: #999;
}

.form-tip {
  font-size: 12px;
  color: #aaa;
  margin-top: 6px;
}
</style>
