<template>
  <div class="manage-page">
    <div class="toolbar fruit-card">
      <el-input v-model="query.keyword" placeholder="搜索用户名 / 昵称" clearable style="width: 240px" @keyup.enter="loadData" @clear="loadData">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button class="btn-fruit" @click="loadData">查询</el-button>
      <span class="tip">共 {{ total }} 位注册用户</span>
    </div>

    <div class="table-card fruit-card">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="用户" min-width="200">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="38" :src="row.avatar && row.avatar.startsWith('http') ? row.avatar : ''" class="user-avatar">
                {{ row.nickname?.charAt(0) || row.username?.charAt(0) }}
              </el-avatar>
              <div>
                <div class="user-name">{{ row.nickname || '-' }}</div>
                <div class="user-username">@{{ row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130">
          <template #default="{ row }">{{ row.phone || '-' }}</template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180">
          <template #default="{ row }">{{ row.email || '-' }}</template>
        </el-table-column>
        <el-table-column label="会员类型" width="110" align="center">
          <template #default="{ row }">
            <el-tag type="success" effect="light">普通会员</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="light">
              {{ row.status === 1 ? '正常' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="130" align="center">
          <template #default="{ row }">
            <el-button
              size="small"
              :type="row.status === 1 ? 'danger' : 'success'"
              plain
              @click="toggleStatus(row)"
            >{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminUserPage, adminToggleUserStatus } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

async function loadData() {
  loading.value = true
  try {
    const res = await adminUserPage({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      keyword: query.keyword || undefined
    })
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

async function toggleStatus(row) {
  const action = row.status === 1 ? '禁用' : '启用'
  await ElMessageBox.confirm(`确定${action}用户「${row.username}」吗？`, '用户状态', { type: 'warning' }).catch(() => Promise.reject())
  await adminToggleUserStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success(`已${action}`)
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
}

.table-card {
  padding: 20px;

  .user-cell {
    display: flex;
    align-items: center;
    gap: 12px;

    .user-avatar {
      background: linear-gradient(135deg, #fb923c, #f97316);
      color: #fff;
      font-weight: 700;
      flex-shrink: 0;
    }

    .user-name {
      font-weight: 700;
      color: $text-main;
    }

    .user-username {
      margin-top: 3px;
      font-size: 12px;
      color: $text-sub;
    }
  }
}

.pagination-box {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}
</style>
