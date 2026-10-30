<template>
  <div class="manage-page">
    <!-- 工具栏 -->
    <div class="toolbar fruit-card">
      <div class="toolbar-left">
        <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width: 160px" @change="loadData">
          <el-option v-for="c in categories" :key="c.id" :label="`${c.icon} ${c.name}`" :value="c.id" />
        </el-select>
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 130px" @change="loadData">
          <el-option label="上架中" :value="1" />
          <el-option label="已下架" :value="0" />
        </el-select>
        <el-input v-model="query.keyword" placeholder="搜索商品名称" clearable style="width: 220px" @keyup.enter="loadData" @clear="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button class="btn-fruit" @click="loadData">查询</el-button>
      </div>
      <el-button class="btn-fruit" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增商品
      </el-button>
    </div>

    <!-- 表格 -->
    <div class="table-card fruit-card">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="商品" min-width="240">
          <template #default="{ row }">
            <div class="goods-cell">
              <img :src="row.image" alt="" class="goods-img" @error="productFallbackImage" />
              <div>
                <div class="goods-name">{{ row.name }}</div>
                <div class="goods-cat">{{ categoryName(row.categoryId) }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="售价" width="110">
          <template #default="{ row }">
            <span class="price">¥{{ formatPrice(row.price) }}</span>
            <span v-if="row.originalPrice" class="origin">¥{{ formatPrice(row.originalPrice) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="90" align="center" />
        <el-table-column prop="sales" label="销量" width="90" align="center" />
        <el-table-column label="热销" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.hot === 1"
              @change="(val) => toggleHot(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light">
              {{ row.status === 1 ? '上架中' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" plain @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button size="small" type="danger" plain @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-box">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          background
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商品' : '新增商品'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="商品名称" />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="`${c.icon} ${c.name}`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
          <el-input v-model="form.image" placeholder="粘贴图片URL（留空显示水果兜底图）" />
        </el-form-item>
        <el-form-item label="售价 / 原价">
          <div class="price-inputs">
            <el-input-number v-model="form.price" :min="0.01" :precision="2" :step="1" placeholder="售价" />
            <span class="gap-text">原价</span>
            <el-input-number v-model="form.originalPrice" :min="0" :precision="2" :step="1" placeholder="原价" />
          </div>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="10" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="介绍水果的产地、口感、规格等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button class="btn-fruit" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  adminProductPage,
  adminAddProduct,
  adminUpdateProduct,
  adminDeleteProduct,
  adminToggleProductStatus,
  adminToggleProductHot
} from '@/api/admin'
import { getCategoryList } from '@/api/product'
import { formatPrice, productFallbackImage } from '@/utils/format'

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const total = ref(0)
const categories = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  categoryId: null,
  status: null,
  keyword: ''
})

const emptyForm = () => ({
  id: null,
  name: '',
  categoryId: null,
  image: '',
  description: '',
  price: 9.9,
  originalPrice: null,
  stock: 100
})

const form = reactive(emptyForm())

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

function categoryName(id) {
  return categories.value.find((c) => c.id === id)?.name || '-'
}

async function loadData() {
  loading.value = true
  try {
    const res = await adminProductPage({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      categoryId: query.categoryId || undefined,
      status: query.status ?? undefined,
      keyword: query.keyword || undefined
    })
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const res = await getCategoryList()
  categories.value = res.data
}

function openDialog(row) {
  Object.assign(form, emptyForm())
  if (row) {
    Object.assign(form, {
      id: row.id,
      name: row.name,
      categoryId: row.categoryId,
      image: row.image || '',
      description: row.description || '',
      price: Number(row.price),
      originalPrice: row.originalPrice ? Number(row.originalPrice) : null,
      stock: row.stock
    })
  }
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate().catch(() => Promise.reject())
  saving.value = true
  try {
    if (form.id) {
      await adminUpdateProduct(form)
      ElMessage.success('商品已更新')
    } else {
      await adminAddProduct(form)
      ElMessage.success('商品已添加')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await adminToggleProductStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
  loadData()
}

async function toggleHot(row, val) {
  await adminToggleProductHot(row.id, val ? 1 : 0)
  ElMessage.success(val ? '已设为热销 🔥' : '已取消热销')
  loadData()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除「${row.name}」吗？删除后不可恢复`, '删除商品', { type: 'warning' }).catch(() => Promise.reject())
  await adminDeleteProduct(row.id)
  ElMessage.success('已删除')
  loadData()
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style lang="scss" scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 20px;
  margin-bottom: 18px;
  flex-wrap: wrap;

  .toolbar-left {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }
}

.table-card {
  padding: 20px;

  .goods-cell {
    display: flex;
    align-items: center;
    gap: 12px;

    .goods-img {
      width: 52px;
      height: 52px;
      border-radius: 10px;
      object-fit: cover;
      background: linear-gradient(135deg, #fff7ed, #ecfdf5);
    }

    .goods-name {
      font-weight: 700;
      color: $text-main;
    }

    .goods-cat {
      margin-top: 4px;
      font-size: 12px;
      color: $text-sub;
    }
  }

  .price {
    color: $primary;
    font-weight: 800;
  }

  .origin {
    margin-left: 6px;
    color: #b9a999;
    font-size: 12px;
    text-decoration: line-through;
  }
}

.pagination-box {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.price-inputs {
  display: flex;
  align-items: center;
  gap: 10px;

  .gap-text {
    color: $text-sub;
    font-size: 13px;
  }
}
</style>
