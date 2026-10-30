<template>
  <div class="order-confirm">
    <Navbar />

    <main class="page-container">
      <h2 class="page-title"><span>📦</span> 确认订单</h2>

      <div v-loading="loading">
        <!-- 收货地址 -->
        <div class="panel fruit-card">
          <div class="panel-title">收货地址</div>
          <div v-if="addresses.length" class="address-list">
            <div
              v-for="address in addresses"
              :key="address.id"
              class="address-item"
              :class="{ active: selectedAddressId === address.id }"
              @click="selectedAddressId = address.id"
            >
              <div class="addr-top">
                <span class="addr-name">{{ address.receiverName }}</span>
                <span class="addr-phone">{{ address.receiverPhone }}</span>
                <el-tag v-if="address.isDefault === 1" type="warning" size="small" effect="light">默认</el-tag>
              </div>
              <div class="addr-detail">
                {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
              </div>
            </div>
            <el-button class="addr-add" text @click="router.push('/address')">
              <el-icon><Plus /></el-icon> 管理地址
            </el-button>
          </div>
          <el-empty v-else description="还没有收货地址">
            <el-button class="btn-fruit" @click="router.push('/address')">去添加地址</el-button>
          </el-empty>
        </div>

        <!-- 商品清单 -->
        <div class="panel fruit-card">
          <div class="panel-title">商品清单（{{ checkedList.length }} 种）</div>
          <div v-for="item in checkedList" :key="item.id" class="goods-row">
            <img :src="item.image" alt="" @error="productFallbackImage" />
            <div class="goods-name">{{ item.name }}</div>
            <div class="goods-price">¥{{ formatPrice(item.price) }} × {{ item.quantity }}</div>
            <div class="goods-total">¥{{ formatPrice(item.price * item.quantity) }}</div>
          </div>
        </div>

        <!-- 备注与结算 -->
        <div class="panel fruit-card settle-panel">
          <div class="remark-row">
            <span class="remark-label">订单备注</span>
            <el-input
              v-model="remark"
              placeholder="选填，给商家留言（如：送货前电话联系）"
              maxlength="100"
              style="width: 420px"
            />
          </div>
          <div class="settle-row">
            <div class="settle-amount">
              共 <b>{{ checkedCount }}</b> 件商品，应付：
              <span class="total-money">¥{{ formatPrice(checkedAmount) }}</span>
            </div>
            <el-button
              class="btn-fruit submit-btn"
              :loading="submitting"
              :disabled="!selectedAddressId || checkedCount === 0"
              @click="submitOrder"
            >提交订单</el-button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Navbar from '@/components/Navbar.vue'
import { getCartList } from '@/api/cart'
import { getAddressList, createOrder } from '@/api/order'
import { formatPrice, productFallbackImage } from '@/utils/format'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const addresses = ref([])
const cartList = ref([])
const selectedAddressId = ref(null)
const remark = ref('')

const checkedList = computed(() => cartList.value.filter((i) => i.checked === 1))
const checkedCount = computed(() => checkedList.value.reduce((sum, i) => sum + i.quantity, 0))
const checkedAmount = computed(() =>
  checkedList.value.reduce((sum, i) => sum + Number(i.price) * i.quantity, 0)
)

onMounted(async () => {
  loading.value = true
  try {
    const [addrRes, cartRes] = await Promise.all([getAddressList(), getCartList()])
    addresses.value = addrRes.data
    cartList.value = cartRes.data
    const def = addresses.value.find((a) => a.isDefault === 1)
    selectedAddressId.value = def ? def.id : addresses.value[0]?.id || null
    if (checkedList.value.length === 0) {
      ElMessage.warning('购物车没有勾选的商品，请先选择')
      router.replace('/cart')
    }
  } finally {
    loading.value = false
  }
})

async function submitOrder() {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  await ElMessageBox.confirm(`确认支付 ¥${formatPrice(checkedAmount.value)} 提交订单吗？`, '提交订单', {
    confirmButtonText: '确认提交',
    cancelButtonText: '再想想',
    type: 'info'
  }).catch(() => Promise.reject())

  submitting.value = true
  try {
    const res = await createOrder({ addressId: selectedAddressId.value, remark: remark.value })
    ElMessage.success('订单创建成功！请在30分钟内完成支付')
    router.push(`/orders?highlight=${res.data.id}`)
  } catch {
    /* 错误已提示 */
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.page-title {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 20px;
  color: $text-main;
}

.panel {
  padding: 24px;
  margin-bottom: 20px;

  .panel-title {
    font-size: 16px;
    font-weight: 800;
    color: $text-main;
    margin-bottom: 16px;
  }
}

.address-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 14px;
}

.address-item {
  padding: 16px;
  border-radius: 14px;
  border: 2px solid #f0e7da;
  cursor: pointer;
  transition: all 0.25s;

  &:hover {
    border-color: $primary-light;
  }

  &.active {
    border-color: $primary;
    background: #fff7ed;
    box-shadow: 0 6px 18px rgba(251, 146, 60, 0.15);
  }

  .addr-top {
    display: flex;
    align-items: center;
    gap: 10px;

    .addr-name {
      font-weight: 800;
      color: $text-main;
    }

    .addr-phone {
      color: $text-sub;
      font-size: 13px;
    }
  }

  .addr-detail {
    margin-top: 8px;
    color: $text-sub;
    font-size: 13px;
    line-height: 1.6;
  }
}

.addr-add {
  align-self: center;
}

.goods-row {
  display: grid;
  grid-template-columns: 64px 1fr 160px 120px;
  align-items: center;
  gap: 14px;
  padding: 12px 0;
  border-bottom: 1px dashed #f0e7da;

  &:last-child {
    border-bottom: none;
  }

  img {
    width: 64px;
    height: 64px;
    border-radius: 12px;
    object-fit: cover;
    background: linear-gradient(135deg, #fff7ed, #ecfdf5);
  }

  .goods-name {
    font-weight: 600;
    color: $text-main;
  }

  .goods-price {
    color: $text-sub;
    font-size: 14px;
    text-align: right;
  }

  .goods-total {
    color: $primary;
    font-weight: 800;
    text-align: right;
  }
}

.settle-panel {
  .remark-row {
    display: flex;
    align-items: center;
    gap: 14px;
    margin-bottom: 20px;

    .remark-label {
      font-weight: 700;
      color: $text-main;
      white-space: nowrap;
    }
  }

  .settle-row {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 20px;

    .settle-amount {
      color: $text-sub;

      b {
        color: $primary;
      }

      .total-money {
        color: $primary;
        font-size: 26px;
        font-weight: 900;
      }
    }

    .submit-btn {
      height: 46px;
      padding: 0 40px;
      border-radius: 23px;
      font-size: 16px;
      font-weight: 700;
    }
  }
}
</style>
