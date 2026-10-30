<template>
  <div class="order-list">
    <Navbar />

    <main class="page-container">
      <h2 class="page-title"><span>🧾</span> 我的订单</h2>

      <!-- 状态筛选 -->
      <el-tabs v-model="activeStatus" class="status-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="待付款" name="0" />
        <el-tab-pane label="待发货" name="1" />
        <el-tab-pane label="待收货" name="2" />
        <el-tab-pane label="已完成" name="3" />
        <el-tab-pane label="已取消" name="4" />
      </el-tabs>

      <div v-loading="loading">
        <div v-if="orders.length" class="order-card-list">
          <div v-for="order in orders" :key="order.id" class="order-card fruit-card">
            <!-- 订单头 -->
            <div class="order-head">
              <div class="order-no">订单号：{{ order.orderNo }}</div>
              <div class="order-time">{{ order.createTime }}</div>
              <el-tag :type="orderStatusType(order.status)" effect="light" round>
                {{ orderStatusText(order.status) }}
              </el-tag>
            </div>

            <!-- 商品 -->
            <div class="order-goods" @click="viewDetail(order)">
              <div class="goods-text">
                <p class="goods-name">{{ order.items?.[0]?.productName || '查看详情' }}</p>
                <p class="goods-extra">
                  共 {{ order.items?.length || 0 }} 种商品
                </p>
              </div>
              <div class="order-amount">
                <span class="amount">¥{{ formatPrice(order.totalAmount) }}</span>
              </div>
            </div>

            <!-- 操作 -->
            <div class="order-actions">
              <el-button text type="info" @click="viewDetail(order)">查看详情</el-button>
              <template v-if="order.status === 0">
                <el-button class="btn-fruit" size="small" @click="handlePay(order)">去支付</el-button>
                <el-button size="small" @click="handleCancel(order)">取消订单</el-button>
              </template>
              <template v-else-if="order.status === 2">
                <el-button class="btn-fruit" size="small" @click="handleConfirm(order)">确认收货</el-button>
              </template>
            </div>
          </div>
        </div>
        <el-empty v-else-if="!loading" description="暂无相关订单" />
      </div>

      <div class="pagination-box">
        <el-pagination
          v-model:current-page="query.pageNum"
          :page-size="query.pageSize"
          :total="total"
          layout="prev, pager, next"
          background
          @current-change="loadOrders"
        />
      </div>
    </main>

    <!-- 订单详情弹窗（居中） -->
    <el-dialog v-model="detailVisible" title="订单详情" width="560px" align-center>
      <div v-if="currentOrder" class="dialog-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="orderStatusType(currentOrder.status)" effect="light">
              {{ orderStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
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
          实付金额：<span class="total-money">¥{{ formatPrice(currentOrder.totalAmount) }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Navbar from '@/components/Navbar.vue'
import { getOrderList, getOrderDetail, payOrder, cancelOrder, confirmOrder } from '@/api/order'
import { formatPrice, orderStatusText, orderStatusType, productFallbackImage } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const orders = ref([])
const total = ref(0)
const activeStatus = ref(route.query.status !== undefined ? String(route.query.status) : '')
const detailVisible = ref(false)
const currentOrder = ref(null)

const query = reactive({
  pageNum: 1,
  pageSize: 5,
  status: undefined
})

async function loadOrders() {
  loading.value = true
  try {
    const res = await getOrderList({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      status: query.status
    })
    orders.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  query.status = activeStatus.value === '' ? undefined : Number(activeStatus.value)
  query.pageNum = 1
  loadOrders()
}

async function viewDetail(order) {
  const res = await getOrderDetail(order.id)
  // 后端详情返回 { order, items } 两层结构，展开合并为单层对象
  const data = res.data
  currentOrder.value = { ...data.order, items: data.items || [] }
  detailVisible.value = true
}

async function handlePay(order) {
  await ElMessageBox.confirm(
    `确认支付 ¥${formatPrice(order.totalAmount)} 吗？（模拟支付，不会真实扣款）`,
    '模拟支付',
    { type: 'info' }
  ).catch(() => Promise.reject())
  await payOrder(order.id)
  ElMessage.success('支付成功，商家将尽快发货 🚚')
  loadOrders()
}

async function handleCancel(order) {
  await ElMessageBox.confirm('确定取消该订单吗？', '取消订单', { type: 'warning' }).catch(() => Promise.reject())
  await cancelOrder(order.id)
  ElMessage.success('订单已取消')
  loadOrders()
}

async function handleConfirm(order) {
  await ElMessageBox.confirm('确认已收到货吗？', '确认收货', { type: 'info' }).catch(() => Promise.reject())
  await confirmOrder(order.id)
  ElMessage.success('确认收货成功，期待再次光临 🍉')
  loadOrders()
}

onMounted(() => {
  if (route.query.highlight) {
    activeStatus.value = ''
  }
  loadOrders()
})
</script>

<style lang="scss" scoped>
.page-title {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 16px;
  color: $text-main;
}

.status-tabs {
  margin-bottom: 8px;
}

.order-card {
  padding: 20px 24px;
  margin-bottom: 18px;
  transition: transform 0.25s, box-shadow 0.25s;

  &:hover {
    transform: translateY(-3px);
  }
}

.order-head {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 14px;
  border-bottom: 1px dashed #f0e7da;
  color: $text-sub;
  font-size: 13px;

  .order-no {
    color: $text-main;
    font-weight: 600;
  }
}

.order-goods {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  cursor: pointer;

  .goods-name {
    font-size: 16px;
    font-weight: 700;
    color: $text-main;
  }

  .goods-extra {
    margin-top: 6px;
    color: $text-sub;
    font-size: 13px;
  }

  .amount {
    color: $primary;
    font-size: 20px;
    font-weight: 800;
  }
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 12px;
  border-top: 1px dashed #f0e7da;
}

.pagination-box {
  display: flex;
  justify-content: center;
  margin: 28px 0;
}

.dialog-body {
  .dialog-goods {
    margin-top: 20px;

    .dialog-goods-row {
      display: grid;
      grid-template-columns: 56px 1fr auto;
      align-items: center;
      gap: 12px;
      padding: 10px 0;
      border-bottom: 1px dashed #f0e7da;

      img {
        width: 56px;
        height: 56px;
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
