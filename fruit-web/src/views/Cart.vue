<template>
  <div class="cart-page">
    <Navbar />

    <main class="page-container">
      <h2 class="page-title"><span>🧺</span> 我的购物车</h2>

      <div v-loading="loading">
        <div v-if="cartList.length" class="cart-list fruit-card">
          <!-- 表头 -->
          <div class="cart-row cart-header">
            <div class="col-check">
              <el-checkbox v-model="allChecked" @change="checkAll" />
            </div>
            <div class="col-info">商品</div>
            <div class="col-price">单价</div>
            <div class="col-qty">数量</div>
            <div class="col-total">小计</div>
            <div class="col-op">操作</div>
          </div>

          <!-- 商品行 -->
          <div v-for="item in cartList" :key="item.id" class="cart-row">
            <div class="col-check">
              <el-checkbox
                :model-value="item.checked === 1"
                @change="(val) => toggleChecked(item, val)"
              />
            </div>
            <div class="col-info">
              <div class="goods" @click="router.push(`/product/${item.productId}`)">
                <img :src="item.image" alt="" @error="productFallbackImage" />
                <div class="goods-text">
                  <p class="goods-name">{{ item.name }}</p>
                  <p v-if="item.status !== 1" class="off-shelf">已下架</p>
                  <p v-else-if="item.stock <= 0" class="off-shelf">已售罄</p>
                </div>
              </div>
            </div>
            <div class="col-price">¥{{ formatPrice(item.price) }}</div>
            <div class="col-qty">
              <el-input-number
                :model-value="item.quantity"
                :min="1"
                :max="Math.max(item.stock, 1)"
                size="small"
                :disabled="item.status !== 1 || item.stock <= 0"
                @change="(val) => changeQuantity(item, val)"
              />
            </div>
            <div class="col-total total-price">¥{{ formatPrice(item.price * item.quantity) }}</div>
            <div class="col-op">
              <el-button text type="danger" @click="removeItem(item)">删除</el-button>
            </div>
          </div>

          <!-- 结算栏 -->
          <div class="settle-bar">
            <div class="settle-left">
              <el-button text type="danger" @click="clearInvalid">清除失效商品</el-button>
            </div>
            <div class="settle-right">
              <span class="summary">已选 <b>{{ checkedCount }}</b> 件，合计：</span>
              <span class="total-money">¥{{ formatPrice(checkedAmount) }}</span>
              <el-button class="btn-fruit settle-btn" :disabled="checkedCount === 0" @click="goSettle">
                去结算
              </el-button>
            </div>
          </div>
        </div>

        <el-empty v-else-if="!loading" description="购物车空空的，快去挑选新鲜水果吧">
          <el-button class="btn-fruit" @click="router.push('/products')">去逛一逛</el-button>
        </el-empty>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Navbar from '@/components/Navbar.vue'
import { useCartStore } from '@/stores/cart'
import { getCartList, updateCartQuantity, updateCartChecked, deleteCart } from '@/api/cart'
import { formatPrice, productFallbackImage } from '@/utils/format'

const router = useRouter()
const cartStore = useCartStore()
const loading = ref(false)
const cartList = ref([])

const allChecked = computed({
  get: () => cartList.value.length > 0 && cartList.value.every((i) => i.checked === 1),
  set: () => {}
})

const checkedList = computed(() => cartList.value.filter((i) => i.checked === 1))
const checkedCount = computed(() => checkedList.value.reduce((sum, i) => sum + i.quantity, 0))
const checkedAmount = computed(() =>
  checkedList.value.reduce((sum, i) => sum + Number(i.price) * i.quantity, 0)
)

async function loadCart() {
  loading.value = true
  try {
    const res = await getCartList()
    cartList.value = res.data
    // 同步导航栏角标（删除/修改数量后即时更新，无需刷新页面）
    cartStore.setCount(cartList.value.reduce((sum, i) => sum + (i.checked === 1 ? i.quantity : 0), 0))
  } finally {
    loading.value = false
  }
}

async function checkAll(val) {
  await Promise.all(cartList.value.map((item) => updateCartChecked(item.id, val ? 1 : 0)))
  loadCart()
}

async function toggleChecked(item, val) {
  await updateCartChecked(item.id, val ? 1 : 0)
  item.checked = val ? 1 : 0
}

async function changeQuantity(item, val) {
  if (!val || val === item.quantity) return
  await updateCartQuantity(item.id, val)
  item.quantity = val
}

async function removeItem(item) {
  await ElMessageBox.confirm(`确定删除「${item.name}」吗？`, '提示', { type: 'warning' }).catch(() => Promise.reject())
  await deleteCart(item.id)
  ElMessage.success('已删除')
  loadCart()
}

async function clearInvalid() {
  const invalid = cartList.value.filter((i) => i.status !== 1 || i.stock <= 0)
  if (!invalid.length) {
    ElMessage.info('没有失效商品')
    return
  }
  await Promise.all(invalid.map((i) => deleteCart(i.id)))
  loadCart()
}

function goSettle() {
  router.push('/order/confirm')
}

onMounted(loadCart)
</script>

<style lang="scss" scoped>
.page-title {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 20px;
  color: $text-main;
}

.cart-list {
  padding: 8px 24px;
}

.cart-row {
  display: grid;
  grid-template-columns: 60px 1fr 130px 140px 130px 80px;
  align-items: center;
  padding: 18px 0;
  border-bottom: 1px solid #f5eee4;
  gap: 8px;

  &:last-child {
    border-bottom: none;
  }
}

.cart-header {
  color: $text-sub;
  font-size: 13px;
  font-weight: 600;
  border-bottom: 2px solid #f5eee4;
}

.col-info .goods {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;

  img {
    width: 72px;
    height: 72px;
    border-radius: 14px;
    object-fit: cover;
    background: linear-gradient(135deg, #fff7ed, #ecfdf5);
  }

  .goods-name {
    font-size: 15px;
    font-weight: 700;
    color: $text-main;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .off-shelf {
    margin-top: 6px;
    font-size: 12px;
    color: #f97316;
  }
}

.col-price, .col-total {
  color: $text-main;
  font-size: 14px;
}

.total-price {
  color: $primary;
  font-weight: 800;
}

.settle-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 0 16px;
  border-top: 2px solid #f5eee4;

  .settle-right {
    display: flex;
    align-items: center;
    gap: 12px;

    .summary {
      color: $text-sub;
      font-size: 14px;

      b {
        color: $primary;
      }
    }

    .total-money {
      color: $primary;
      font-size: 26px;
      font-weight: 900;
    }

    .settle-btn {
      height: 44px;
      padding: 0 36px;
      border-radius: 22px;
      font-weight: 700;
      font-size: 15px;
    }
  }
}

@media (max-width: 900px) {
  .cart-row {
    grid-template-columns: 40px 1fr 90px 100px;
  }
  .col-price, .col-total, .col-op {
    display: none;
  }
}
</style>
