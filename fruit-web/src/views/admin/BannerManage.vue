<template>
  <div class="manage-page">
    <div class="toolbar fruit-card">
      <span class="tip">管理首页轮播图（最多展示启用中的图片，按排序值排列）</span>
      <div class="spacer"></div>
      <el-button class="btn-fruit" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增轮播图
      </el-button>
    </div>

    <div class="table-card fruit-card">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="预览" width="180">
          <template #default="{ row }">
            <img :src="row.image" alt="" class="banner-img" @error="productFallbackImage" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="linkUrl" label="跳转链接" min-width="180">
          <template #default="{ row }">{{ row.linkUrl || '-' }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="90" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light">
              {{ row.status === 1 ? '启用中' : '已停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" plain @click="toggleStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
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
          layout="total, prev, pager, next"
          background
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑轮播图' : '新增轮播图'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="图片地址" prop="image">
          <el-input v-model="form.image" placeholder="粘贴轮播图URL" />
        </el-form-item>
        <el-form-item label="预览">
          <div class="preview-box">
            <img v-if="form.image" :src="form.image" alt="预览" @error="productFallbackImage" />
            <span v-else class="preview-empty">暂无预览</span>
          </div>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="轮播图标题（选填）" maxlength="30" />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.linkUrl" placeholder="如 /products 或 /products?categoryId=3（选填）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :step="1" />
          <span class="tip">（数字越小越靠前）</span>
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
  adminBannerPage,
  adminAddBanner,
  adminUpdateBanner,
  adminDeleteBanner,
  adminToggleBannerStatus
} from '@/api/admin'
import { productFallbackImage } from '@/utils/format'

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({
  pageNum: 1,
  pageSize: 10
})

const emptyForm = () => ({
  id: null,
  image: '',
  title: '',
  linkUrl: '',
  sort: 0
})

const form = reactive(emptyForm())

const rules = {
  image: [{ required: true, message: '请输入图片地址', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await adminBannerPage({ pageNum: query.pageNum, pageSize: query.pageSize })
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  Object.assign(form, emptyForm())
  if (row) {
    Object.assign(form, {
      id: row.id,
      image: row.image,
      title: row.title || '',
      linkUrl: row.linkUrl || '',
      sort: row.sort
    })
  }
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate().catch(() => Promise.reject())
  saving.value = true
  try {
    if (form.id) {
      await adminUpdateBanner(form)
      ElMessage.success('轮播图已更新')
    } else {
      await adminAddBanner(form)
      ElMessage.success('轮播图已添加')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await adminToggleBannerStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已启用' : '已停用')
  loadData()
}

async function remove(row) {
  await ElMessageBox.confirm('确定删除该轮播图吗？', '删除轮播图', { type: 'warning' }).catch(() => Promise.reject())
  await adminDeleteBanner(row.id)
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  margin-bottom: 18px;

  .tip {
    color: $text-sub;
    font-size: 13px;
  }

  .spacer {
    flex: 1;
  }
}

.table-card {
  padding: 20px;

  .banner-img {
    width: 140px;
    height: 60px;
    border-radius: 10px;
    object-fit: cover;
    background: linear-gradient(135deg, #fff7ed, #ecfdf5);
  }
}

.pagination-box {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.preview-box {
  width: 100%;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #faf3e8;
  border: 1px dashed #e5d9c8;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .preview-empty {
    color: #b9a999;
    font-size: 13px;
  }
}

.tip {
  margin-left: 10px;
  color: $text-sub;
  font-size: 12px;
}
</style>
