<template>
  <div class="manage-page">
    <div class="toolbar fruit-card">
      <el-input v-model="query.keyword" placeholder="搜索分类名称" clearable style="width: 220px" @keyup.enter="loadData" @clear="loadData">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button class="btn-fruit" @click="loadData">查询</el-button>
      <div class="spacer"></div>
      <el-button class="btn-fruit" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增分类
      </el-button>
    </div>

    <div class="table-card fruit-card">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="图标" width="90" align="center">
          <template #default="{ row }">
            <span class="cat-icon">{{ row.icon || '🍎' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="分类名称" min-width="160" />
        <el-table-column prop="sort" label="排序" width="90" align="center" />
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light">
              {{ row.status === 1 ? '启用中' : '已停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
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
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="460px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="如：时令鲜果" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="emoji 图标，如 🍉" maxlength="4" />
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
  adminCategoryPage,
  adminAddCategory,
  adminUpdateCategory,
  adminDeleteCategory,
  adminToggleCategoryStatus
} from '@/api/admin'

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const emptyForm = () => ({ id: null, name: '', icon: '🍎', sort: 0 })

const form = reactive(emptyForm())

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await adminCategoryPage({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      name: query.keyword || undefined
    })
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  Object.assign(form, emptyForm())
  if (row) {
    Object.assign(form, { id: row.id, name: row.name, icon: row.icon, sort: row.sort })
  }
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate().catch(() => Promise.reject())
  saving.value = true
  try {
    if (form.id) {
      await adminUpdateCategory(form)
      ElMessage.success('分类已更新')
    } else {
      await adminAddCategory(form)
      ElMessage.success('分类已添加')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await adminToggleCategoryStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已启用' : '已停用')
  loadData()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '删除分类', { type: 'warning' }).catch(() => Promise.reject())
  await adminDeleteCategory(row.id)
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

  .spacer {
    flex: 1;
  }
}

.table-card {
  padding: 20px;

  .cat-icon {
    font-size: 30px;
  }

  .tip {
    margin-left: 10px;
    color: $text-sub;
    font-size: 12px;
  }
}

.pagination-box {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}
</style>
