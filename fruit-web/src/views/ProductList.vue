<template>
  <div class="product-list">
    <Navbar />

    <main class="page-container">
      <div class="filter-bar fruit-card">
        <div class="filter-left">
          <span class="filter-label">分类：</span>
          <div
            class="filter-item"
            :class="{ active: !query.categoryId }"
            @click="selectCategory(null)"
          >全部</div>
          <div
            v-for="category in categories"
            :key="category.id"
            class="filter-item"
            :class="{ active: query.categoryId === category.id }"
            @click="selectCategory(category.id)"
          >{{ category.icon }} {{ category.name }}</div>
        </div>
        <div class="filter-right">
          <el-input
            v-model="query.keyword"
            placeholder="搜索水果…"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button class="btn-fruit" @click="handleSearch">搜索</el-button>
        </div>
      </div>

      <div class="result-info">
        共找到 <b>{{ total }}</b> 种新鲜水果 🍎
      </div>

      <div v-loading="loading">
        <div v-if="products.length" class="product-grid">
          <ProductCard v-for="product in products" :key="product.id" :product="product" />
        </div>
        <el-empty v-else description="没有找到相关水果，换个关键词试试吧" />
      </div>

      <div class="pagination-box">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[8, 12, 16]"
          layout="total, sizes, prev, pager, next"
          background
          @size-change="loadProducts"
          @current-change="loadProducts"
        />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '@/components/Navbar.vue'
import ProductCard from '@/components/ProductCard.vue'
import { getProductPage, getCategoryList } from '@/api/product'

const route = useRoute()
const loading = ref(false)
const products = ref([])
const categories = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 8,
  categoryId: null,
  keyword: ''
})

async function loadProducts() {
  loading.value = true
  try {
    const res = await getProductPage({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      categoryId: query.categoryId || undefined,
      keyword: query.keyword || undefined
    })
    products.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const res = await getCategoryList()
  categories.value = res.data
}

function selectCategory(id) {
  query.categoryId = id
  query.pageNum = 1
  loadProducts()
}

function handleSearch() {
  query.pageNum = 1
  loadProducts()
}

onMounted(() => {
  // 从首页/导航跳转带来的参数
  if (route.query.categoryId) {
    query.categoryId = Number(route.query.categoryId)
  }
  if (route.query.keyword) {
    query.keyword = route.query.keyword
  }
  loadCategories()
  loadProducts()
})

// 已在列表页时，顶部导航再次搜索会只改 URL query、组件不重新挂载，
// 因此监听路由参数变化，同步本地条件并重新请求
watch(
  () => route.query.keyword,
  (val) => {
    query.keyword = val || ''
    query.pageNum = 1
    loadProducts()
  }
)
watch(
  () => route.query.categoryId,
  (val) => {
    query.categoryId = val ? Number(val) : null
    query.pageNum = 1
    loadProducts()
  }
)
</script>

<style lang="scss" scoped>
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 16px 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;

  .filter-label {
    color: $text-sub;
    font-size: 14px;
    font-weight: 600;
  }

  .filter-item {
    padding: 6px 14px;
    border-radius: 16px;
    font-size: 14px;
    color: $text-main;
    cursor: pointer;
    transition: all 0.25s;

    &:hover {
      background: rgba(251, 146, 60, 0.1);
      color: $primary;
    }

    &.active {
      background: linear-gradient(135deg, #fb923c, #f97316);
      color: #fff;
      font-weight: 700;
      box-shadow: 0 4px 12px rgba(251, 146, 60, 0.35);
    }
  }
}

.filter-right {
  display: flex;
  gap: 10px;
}

.result-info {
  margin: 16px 0;
  color: $text-sub;
  font-size: 14px;

  b {
    color: $primary;
  }
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  min-height: 200px;
}

.pagination-box {
  display: flex;
  justify-content: center;
  margin: 32px 0;
}

@media (max-width: 900px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
