<template>
  <div class="products-page">
    <!-- Header -->
    <div class="products-header">
      <div class="header-top">
        <h1 class="page-title">全部商品</h1>
        <span class="result-count" v-if="total > 0">共 {{ total }} 件商品</span>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <div class="filter-row">
        <span class="filter-label">分类</span>
        <div class="filter-chips">
          <button
            class="chip"
            :class="{ active: categoryId === null }"
            @click="setCategory(null)"
          >全部</button>
          <button
            v-for="cat in categories"
            :key="cat.id"
            class="chip"
            :class="{ active: categoryId === cat.id }"
            @click="setCategory(cat.id)"
          >{{ cat.name }}</button>
        </div>
      </div>
      <div class="filter-row filter-row--right">
        <span class="filter-label">排序</span>
        <div class="filter-chips">
          <button class="chip" :class="{ active: sortType === 1 }" @click="setSort(1)">综合</button>
          <button class="chip" :class="{ active: sortType === 2 }" @click="setSort(2)">
            价格 ↑
          </button>
          <button class="chip" :class="{ active: sortType === 3 }" @click="setSort(3)">
            价格 ↓
          </button>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="products-grid">
      <div v-for="i in 12" :key="i" class="product-card product-card--skeleton">
        <div class="skeleton-image"></div>
        <div class="skeleton-body">
          <div class="skeleton-line skeleton-line--long"></div>
          <div class="skeleton-line skeleton-line--short"></div>
        </div>
      </div>
    </div>

    <!-- Empty -->
    <div v-else-if="products.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none">
          <path d="M19 6.41L17.59 5L12 10.59L6.41 5L5 6.41L10.59 12L5 17.59L6.41 19L12 13.41L17.59 19L19 17.59L13.41 12L19 6.41Z" fill="currentColor"/>
        </svg>
      </div>
      <p class="empty-title">未找到相关商品</p>
      <p class="empty-text">换个关键词试试吧</p>
      <el-button type="primary" round @click="$router.push('/products')">查看全部商品</el-button>
    </div>

    <!-- Grid -->
    <div v-else class="products-grid">
      <div
        v-for="item in products"
        :key="item.id"
        class="product-card"
        @click="$router.push(`/product/${item.id}`)"
      >
        <div class="product-image-wrap">
          <img :src="item.image" class="product-image" :alt="item.name" />
          <div class="product-overlay">
            <el-button round size="small">查看详情</el-button>
          </div>
        </div>
        <div class="product-body">
          <div class="product-name">{{ item.name }}</div>
          <div class="product-desc">{{ item.description }}</div>
          <div class="product-bottom">
            <span class="product-price">¥{{ item.price }}</span>
            <span class="product-stock">{{ item.stock }}件</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination-container" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="prev, pager, next, sizes"
        @current-change="loadProducts"
        @size-change="loadProducts"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { categoryApi } from '@/api/category'
import { productApi } from '@/api/product'

const route = useRoute()
const loading = ref(false)
const products = ref([])
const categories = ref([])
const categoryId = ref(null)
const sortType = ref(1)
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)

const setCategory = (id) => {
  categoryId.value = id
  pageNum.value = 1
  loadProducts()
}

const setSort = (type) => {
  sortType.value = type
  pageNum.value = 1
  loadProducts()
}

const loadCategories = async () => {
  try {
    const res = await categoryApi.getList()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await productApi.getList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      categoryId: categoryId.value,
      keyword: route.query.keyword || null
    })
    let list = res.data.records || []
    if (sortType.value === 2) {
      list = [...list].sort((a, b) => a.price - b.price)
    } else if (sortType.value === 3) {
      list = [...list].sort((a, b) => b.price - a.price)
    }
    products.value = list
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

watch(() => route.query, (query) => {
  if (query.categoryId) {
    categoryId.value = Number(query.categoryId)
  } else if (query.keyword) {
    categoryId.value = null
  }
  pageNum.value = 1
  loadProducts()
}, { immediate: false })

onMounted(() => {
  loadCategories()
  if (route.query.categoryId) {
    categoryId.value = Number(route.query.categoryId)
  }
  if (route.query.keyword) {
    categoryId.value = null
  }
  loadProducts()
})
</script>

<style lang="scss" scoped>
.products-page {
  max-width: 1200px;
  margin: 0 auto;
}

.products-header {
  margin-bottom: 24px;

  .header-top {
    display: flex;
    align-items: baseline;
    gap: 16px;

    .page-title {
      font-size: 28px;
      font-weight: 400;
      color: var(--md-sys-color-on-surface);
    }

    .result-count {
      font-size: 14px;
      color: var(--md-sys-color-on-surface-variant);
    }
  }
}

.filter-bar {
  background: var(--md-sys-color-surface);
  border-radius: var(--md-sys-shape-corner-large);
  padding: 20px 24px;
  margin-bottom: 24px;
  box-shadow: var(--md-sys-elevation-1);
  border: 1px solid var(--color-outline-variant);
  display: flex;
  flex-wrap: wrap;
  gap: 20px;

  .filter-row {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;

    &--right {
      margin-left: auto;
    }

    .filter-label {
      font-size: 14px;
      font-weight: 500;
      color: var(--md-sys-color-on-surface-variant);
      white-space: nowrap;
    }
  }
}

.filter-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  padding: 7px 18px;
  border-radius: var(--md-sys-shape-corner-full);
  border: 1px solid var(--color-outline-variant);
  background: var(--md-sys-color-surface);
  color: var(--md-sys-color-on-surface-variant);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: 'Roboto', sans-serif;

  &:hover {
    border-color: var(--md-sys-color-primary);
    color: var(--md-sys-color-primary);
    background: var(--color-primary-container);
  }

  &.active {
    background: var(--md-sys-color-primary);
    border-color: var(--md-sys-color-primary);
    color: var(--md-sys-color-on-primary);
    font-weight: 500;
  }
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  margin-bottom: 32px;
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

    .product-overlay {
      opacity: 1;
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

    .product-overlay {
      position: absolute;
      inset: 0;
      background: rgba(0,0,0,0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.2s;

      .el-button {
        background: #fff !important;
        color: #333 !important;
      }
    }
  }

  .product-body {
    padding: 14px;

    .product-name {
      font-size: 14px;
      color: var(--md-sys-color-on-surface);
      margin-bottom: 6px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .product-desc {
      font-size: 12px;
      color: var(--md-sys-color-on-surface-variant);
      margin-bottom: 10px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .product-bottom {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .product-price {
        font-size: 18px;
        font-weight: 500;
        color: var(--md-sys-color-primary);
      }

      .product-stock {
        font-size: 12px;
        color: var(--md-sys-color-on-surface-variant);
      }
    }
  }

  &--skeleton {
    pointer-events: none;

    .skeleton-image {
      width: 100%;
      height: 180px;
      background: linear-gradient(90deg, var(--md-sys-color-surface-variant) 25%, var(--color-outline-variant) 50%, var(--md-sys-color-surface-variant) 75%);
      background-size: 200% 100%;
      animation: shimmer 1.5s infinite;
    }

    .skeleton-body {
      padding: 14px;
    }

    .skeleton-line {
      height: 14px;
      border-radius: 4px;
      background: linear-gradient(90deg, var(--md-sys-color-surface-variant) 25%, var(--color-outline-variant) 50%, var(--md-sys-color-surface-variant) 75%);
      background-size: 200% 100%;
      animation: shimmer 1.5s infinite;
      margin-bottom: 8px;

      &--long { width: 80%; }
      &--short { width: 40%; height: 12px; }
    }
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

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .filter-row--right {
    margin-left: 0 !important;
  }

  .page-title {
    font-size: 22px !important;
  }
}
</style>
