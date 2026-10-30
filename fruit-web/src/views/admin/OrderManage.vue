<template>
  <div class="manage-page">
    <div class="toolbar fruit-card">
      <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px" @change="loadData">
        <el-option v-for="(item, key) in ORDER_STATUS" :key="key" :label="item.text" :value="Number(key)" />
      </el-select>
      <el-input v-model="query.orderNo" placeholder="搜索订单号" clearable style="width: 240px" @keyup.enter="loadData" @clear="loadData">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button class="btn-fruit" @click="loadData">查询</el-button>
    </div>

    <div class="table-card fruit-card">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="收货人" min-width="130">
          <template #default="{ row }">
            <div class="receiver">
              <div>{{ row.receiverName }}</div>
              <div class="phone">{{ row.receiverPhone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="110">
          <template #default="{ row }">
            <span class="price">¥{{ formatPrice(row.totalAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="orderStatusType(row.status)" effect="light">
              {{ orderStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === 1"
              size="small"
              class="btn-fruit"
              @click="deliver(row)"
            >发货</el-button>
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

    <!-- 订单详情弹窗（居中） -->
    <el-dialog v-model="detailVisible" title="订单详情" width="600px" align-center>
      <div v-if="currentOrder" class="dialog-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="orderStatusType(currentOrder.status)" effect="light">
              {{ orderStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ currentOrder.deliveryTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ currentOrder.receiverName }} {{ currentOrder.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ currentOrder.receiverAddress }}</el-descriptions-item>
          <el-descriptions-item v-if="currentOrder.remark" label="备注">{{ currentOrder.remark }}</el-descriptions-item>
        </el-descriptions>

        <div class="dialog-goods">
          <div v-for="item in currentOrder.items" :key="item.id" class="dialog-goods-row">
            <img :src="item.productImage" alt="" @error="productFallbackImage" />
            <span class="d-name">{{ item.productName }}</span>
            <span class="d-price">¥{{ formatPrice(item.price) }} × {{ item.quantity }}</span>
          </div>
        </div>

        <div class="dialog-total">
          订单金额：<span class="total-money">¥{{ formatPrice(currentOrder.totalAmount) }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminOrderPage, adminOrderDetail, adminDeliverOrder } from '@/api/admin'
import { formatPrice, orderStatusText, orderStatusType, ORDER_STATUS, productFallbackImage } from '@/utils/format'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref(null)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  status: null
})

async function loadData() {
  loading.value = true
  try {
    const res = await adminOrderPage({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      orderNo: query.orderNo || undefined,
      status: query.status ?? undefined
    })
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

async function viewDetail(row) {
  const res = await adminOrderDetail(row.id)
  // 后端详情返回 { order, items } 两层结构，展开合并为单层对象
  const data = res.data
  currentOrder.value = { ...data.order, items: data.items || [] }
  detailVisible.value = true
}

async function deliver(row) {
  await ElMessageBox.confirm(`确认为订单 ${row.orderNo} 发货吗？`, '订单发货', { type: 'info' }).catch(() => Promise.reject())
  await adminDeliverOrder(row.id)
  ElMessage.success('发货成功 🚚')
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
}

.table-card {
  padding: 20px;

  .receiver .phone {
    margin-top: 3px;
    font-size: 12px;
    color: $text-sub;
  }

  .price {
    color: $primary;
    font-weight: 800;
  }
}

.pagination-box {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.dialog-body {
  .dialog-goods {
    margin-top: 20px;

    .dialog-goods-row {
      display: grid;
      grid-template-columns: 52px 1fr auto;
      align-items: center;
      gap: 10px;
      padding: 10px 0;
      border-bottom: 1px dashed #f0e7da;

      img {
        width: 52px;
        height: 52px;
        border-radius: 10px;
        object-fit: cover;
        background: linear-gradient(135deg, #fff7ed, #ecfdf5);
      }

      .d-name {
        font-size: 14px;
        color: $text-main;
      }

      .d-price {
        color: $text-sub;
        font-size: 13px;
        white-space: nowrap;
      }
    }
  }

  .dialog-total {
    margin-top: 18px;
    text-align: right;
    color: $text-sub;

    .total-money {
      color: $primary;
      font-size: 22px;
      font-weight: 900;
    }
  }
}
</style>
