<template>
  <div class="product-card fruit-card" @click="goDetail">
    <div class="img-box">
      <img
        v-if="product.image"
        :src="product.image"
        :alt="product.name"
        loading="lazy"
        @error="productFallbackImage"
      />
      <div v-else class="img-fallback">🍉</div>
      <span v-if="product.hot === 1" class="hot-tag">🔥 热销</span>
      <span v-if="product.originalPrice" class="discount-tag">
        {{ discountText }}
      </span>
    </div>
    <div class="info">
      <div class="name">{{ product.name }}</div>
      <div class="sales">已售 {{ product.sales || 0 }} 件</div>
      <div class="bottom">
        <div class="price-box">
          <span class="price">¥{{ formatPrice(product.price) }}</span>
          <span v-if="product.originalPrice" class="origin-price">¥{{ formatPrice(product.originalPrice) }}</span>
        </div>
        <div class="cart-btn" @click.stop="addCart">
          <el-icon><ShoppingCart /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { addToCart } from '@/api/cart'
import { formatPrice, productFallbackImage } from '@/utils/format'

const props = defineProps({
  product: { type: Object, required: true }
})

const router = useRouter()
const userStore = useUserStore()

const discountText = computed(() => {
  const { price, originalPrice } = props.product
  if (!price || !originalPrice || Number(originalPrice) <= 0) return ''
  const off = Math.round((1 - Number(price) / Number(originalPrice)) * 10)
  return off > 0 ? `${off}折` : ''
})

function goDetail() {
  router.push(`/product/${props.product.id}`)
}

async function addCart() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录再选购水果哦')
    router.push('/login')
    return
  }
  try {
    await addToCart({ productId: props.product.id, quantity: 1 })
    ElMessage.success('已加入购物车 🧺')
  } catch {
    /* 错误已由拦截器提示 */
  }
}
</script>

<style lang="scss" scoped>
.product-card {
  cursor: pointer;
  overflow: hidden;
  height: 100%;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 32px rgba(251, 146, 60, 0.22);

    .img-box img {
      transform: scale(1.06);
    }
  }
}

.img-box {
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
  background: linear-gradient(135deg, #fff7ed, #ecfdf5);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s ease;
  }

  .img-fallback {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 64px;
    background: linear-gradient(135deg, #fff7ed, #ecfdf5);
  }
}

.hot-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background: linear-gradient(135deg, #f97316, #fb923c);
  color: #fff;
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(249, 115, 22, 0.4);
}

.discount-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(34, 197, 94, 0.9);
  color: #fff;
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 12px;
}

.info {
  padding: 12px 14px 14px;

  .name {
    font-size: 15px;
    font-weight: 700;
    color: $text-main;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .sales {
    font-size: 12px;
    color: $text-sub;
    margin: 6px 0 8px;
  }

  .bottom {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .price-box {
      display: flex;
      align-items: baseline;
      gap: 6px;

      .price {
        color: $primary;
        font-size: 20px;
        font-weight: 800;
      }

      .origin-price {
        color: #b9a999;
        font-size: 12px;
        text-decoration: line-through;
      }
    }

    .cart-btn {
      width: 34px;
      height: 34px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #fb923c, #f97316);
      color: #fff;
      font-size: 16px;
      transition: transform 0.25s;

      &:hover {
        transform: scale(1.15) rotate(-10deg);
      }
    }
  }
}
</style>
