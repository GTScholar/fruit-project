<template>
  <div class="product-detail">
    <Navbar />

    <main class="page-container">
      <div v-if="product" class="detail-layout">
        <!-- 左侧图片 -->
        <div class="img-panel fruit-card">
          <img
            v-if="product.image"
            :src="product.image"
            :alt="product.name"
            class="main-img"
            @error="productFallbackImage"
          />
          <div v-else class="img-fallback">🍉</div>
        </div>

        <!-- 右侧信息 -->
        <div class="info-panel fruit-card">
          <h1 class="product-name">{{ product.name }}</h1>
          <div class="sales-line">
            已售 <b>{{ product.sales || 0 }}</b> 件 · 库存 <b>{{ product.stock }}</b> 件
            <el-tag v-if="product.hot === 1" type="warning" effect="light" round>🔥 热销</el-tag>
            <el-tag v-if="product.status === 1" type="success" effect="light" round>新鲜在售</el-tag>
          </div>

          <div class="price-box">
            <span class="current">¥{{ formatPrice(product.price) }}</span>
            <span v-if="product.originalPrice" class="original">¥{{ formatPrice(product.originalPrice) }}</span>
            <span v-if="discount" class="discount">{{ discount }}</span>
          </div>

          <div class="desc-box">
            <span class="desc-label">商品描述</span>
            <p class="desc-text">{{ product.description || '新鲜水果，产地直采，冷链配送～' }}</p>
          </div>

          <div class="quantity-box">
            <span class="qty-label">购买数量</span>
            <el-input-number v-model="quantity" :min="1" :max="Math.max(product.stock, 1)" />
            <span class="stock-tip">（库存 {{ product.stock }} 件）</span>
          </div>

          <div class="action-box">
            <el-button class="btn-fruit" size="large" :disabled="product.stock <= 0" @click="buyNow">
              <el-icon><ShoppingCart /></el-icon> 立即购买
            </el-button>
            <el-button size="large" :disabled="product.stock <= 0" @click="addCart">
              <el-icon><Plus /></el-icon> 加入购物车
            </el-button>
          </div>

          <div class="tips">
            <span>🚚 冷链直达</span>
            <span>🍃 产地直采</span>
            <span>🔄 坏果包赔</span>
          </div>
        </div>
      </div>
      <el-skeleton v-else :rows="6" animated />
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Navbar from '@/components/Navbar.vue'
import { getProductDetail } from '@/api/product'
import { addToCart } from '@/api/cart'
import { useUserStore } from '@/stores/user'
import { formatPrice, productFallbackImage } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const product = ref(null)
const quantity = ref(1)

const discount = computed(() => {
  const { price, originalPrice } = product.value || {}
  if (!price || !originalPrice || Number(originalPrice) <= 0) return ''
  const off = Math.round((1 - Number(price) / Number(originalPrice)) * 10)
  return off > 0 ? `限时${off}折` : ''
})

function checkLogin() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录再选购水果哦')
    router.push('/login')
    return false
  }
  return true
}

async function addCart() {
  if (!checkLogin()) return
  await addToCart({ productId: product.value.id, quantity: quantity.value })
  ElMessage.success('已加入购物车 🧺')
}

async function buyNow() {
  if (!checkLogin()) return
  await ElMessageBox.confirm(`确认将 ${quantity.value} 件「${product.value.name}」加入购物车并结算吗？`, '立即购买', {
    confirmButtonText: '去结算',
    cancelButtonText: '再想想',
    type: 'info'
  }).catch(() => Promise.reject())
  await addToCart({ productId: product.value.id, quantity: quantity.value })
  router.push('/cart')
}

onMounted(async () => {
  const res = await getProductDetail(route.params.id)
  product.value = res.data
})
</script>

<style lang="scss" scoped>
.detail-layout {
  display: grid;
  grid-template-columns: 1fr 1.1fr;
  gap: 28px;
  align-items: start;
}

.img-panel {
  position: relative;
  overflow: hidden;

  .main-img {
    width: 100%;
    aspect-ratio: 1;
    object-fit: cover;
  }

  .img-fallback {
    aspect-ratio: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 110px;
    background: linear-gradient(135deg, #fff7ed, #ecfdf5);
  }
}

.info-panel {
  padding: 32px;

  .product-name {
    font-size: 24px;
    font-weight: 800;
    color: $text-main;
    line-height: 1.4;
  }

  .sales-line {
    margin-top: 12px;
    color: $text-sub;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 8px;

    b {
      color: $primary;
    }
  }

  .price-box {
    margin-top: 18px;
    padding: 16px 20px;
    border-radius: 14px;
    background: linear-gradient(135deg, #fff7ed, #ffedd5);
    display: flex;
    align-items: baseline;
    gap: 12px;

    .current {
      color: $primary;
      font-size: 32px;
      font-weight: 900;
    }

    .original {
      color: #b9a999;
      font-size: 15px;
      text-decoration: line-through;
    }

    .discount {
      padding: 3px 10px;
      border-radius: 10px;
      background: linear-gradient(135deg, #f97316, #fb923c);
      color: #fff;
      font-size: 12px;
      font-weight: 700;
    }
  }

  .desc-box {
    margin-top: 20px;

    .desc-label {
      font-size: 14px;
      font-weight: 700;
      color: $text-main;
    }

    .desc-text {
      margin-top: 8px;
      color: $text-sub;
      font-size: 14px;
      line-height: 1.7;
    }
  }

  .quantity-box {
    margin-top: 22px;
    display: flex;
    align-items: center;
    gap: 14px;

    .qty-label {
      font-size: 14px;
      font-weight: 700;
      color: $text-main;
    }

    .stock-tip {
      color: $text-sub;
      font-size: 13px;
    }
  }

  .action-box {
    margin-top: 28px;
    display: flex;
    gap: 14px;

    .el-button {
      border-radius: 24px;
      padding: 0 30px;
      height: 46px;
      font-weight: 700;
    }
  }

  .tips {
    margin-top: 26px;
    padding-top: 18px;
    border-top: 1px dashed rgba(251, 146, 60, 0.3);
    display: flex;
    gap: 24px;
    color: $text-sub;
    font-size: 13px;
  }
}

@media (max-width: 900px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
}
</style>
