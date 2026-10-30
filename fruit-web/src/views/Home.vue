<template>
  <div class="home">
    <Navbar />

    <main class="page-container">
      <!-- 轮播图 -->
      <el-carousel v-if="banners.length" height="360px" class="banner-carousel" :interval="4500" arrow="hover">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div class="banner-item" @click="goBanner(banner)">
            <img :src="banner.image" :alt="banner.title" @error="productFallbackImage" />
            <div class="banner-mask"></div>
            <div class="banner-text">
              <h2>{{ banner.title }}</h2>
              <span>立即抢购 →</span>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>

      <!-- 分类导航 -->
      <section class="section category-section">
        <div class="section-head">
          <h3 class="section-title"><span class="title-icon">🧺</span> 逛分类</h3>
        </div>
        <div class="category-grid">
          <div
            v-for="category in categories"
            :key="category.id"
            class="category-item fruit-card"
            @click="goCategory(category)"
          >
            <span class="category-icon">{{ category.icon }}</span>
            <span class="category-name">{{ category.name }}</span>
          </div>
        </div>
      </section>

      <!-- 热销推荐 -->
      <section class="section">
        <div class="section-head">
          <h3 class="section-title"><span class="title-icon">🔥</span> 热销爆款</h3>
          <router-link to="/products" class="more-link">查看更多 →</router-link>
        </div>
        <div v-if="hotProducts.length" class="product-grid">
          <ProductCard v-for="product in hotProducts" :key="product.id" :product="product" />
        </div>
        <el-empty v-else description="暂无热销商品" />
      </section>

      <!-- 新品推荐 -->
      <section class="section">
        <div class="section-head">
          <h3 class="section-title"><span class="title-icon">✨</span> 全部水果</h3>
          <router-link to="/products" class="more-link">查看全部 →</router-link>
        </div>
        <div v-if="newProducts.length" class="product-grid">
          <ProductCard v-for="product in newProducts" :key="product.id" :product="product" />
        </div>
        <el-empty v-else description="暂无商品" />
      </section>
    </main>

    <footer class="footer">
      <p>🍊 果漾鲜生 FruitFresh · 新鲜水果在线商城</p>
      <p>产地直采 · 冷链配送 · 坏果包赔 · 天天特价</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'
import ProductCard from '@/components/ProductCard.vue'
import { getBannerList, getCategoryList, getHotProducts, getProductPage } from '@/api/product'
import { productFallbackImage } from '@/utils/format'

const router = useRouter()
const banners = ref([])
const categories = ref([])
const hotProducts = ref([])
const newProducts = ref([])

async function loadData() {
  try {
    const [bannerRes, categoryRes, hotRes, pageRes] = await Promise.all([
      getBannerList(),
      getCategoryList(),
      getHotProducts(),
      getProductPage({ pageNum: 1, pageSize: 8 })
    ])
    banners.value = bannerRes.data
    categories.value = categoryRes.data
    hotProducts.value = hotRes.data
    newProducts.value = pageRes.data.records
  } catch {
    /* 错误已提示 */
  }
}

function goCategory(category) {
  router.push({ path: '/products', query: { categoryId: category.id } })
}

function goBanner(banner) {
  if (banner.linkUrl) {
    router.push(banner.linkUrl)
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.banner-carousel {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 12px 36px rgba(251, 146, 60, 0.2);
}

.banner-item {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .banner-mask {
    position: absolute;
    inset: 0;
    background: linear-gradient(90deg, rgba(61, 44, 28, 0.55) 0%, transparent 60%);
  }

  .banner-text {
    position: absolute;
    left: 60px;
    top: 50%;
    transform: translateY(-50%);
    color: #fff;

    h2 {
      font-size: 34px;
      font-weight: 800;
      text-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
    }

    span {
      display: inline-block;
      margin-top: 14px;
      padding: 8px 22px;
      border-radius: 20px;
      background: linear-gradient(135deg, #fb923c, #f97316);
      font-size: 14px;
      font-weight: 700;
      box-shadow: 0 6px 18px rgba(249, 115, 22, 0.5);
    }
  }
}

.section {
  margin-top: 36px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;

  .section-title {
    font-size: 22px;
    font-weight: 800;
    color: $text-main;

    .title-icon {
      margin-right: 6px;
    }
  }

  .more-link {
    color: $primary;
    font-size: 14px;
    font-weight: 600;

    &:hover {
      text-decoration: underline;
    }
  }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 22px 10px;
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 28px rgba(251, 146, 60, 0.2);

    .category-icon {
      transform: scale(1.2) rotate(-8deg);
    }
  }

  .category-icon {
    font-size: 38px;
    transition: transform 0.3s;
  }

  .category-name {
    font-size: 14px;
    font-weight: 700;
    color: $text-main;
  }
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.footer {
  margin-top: 60px;
  padding: 30px 0;
  text-align: center;
  background: linear-gradient(135deg, #fff7ed, #f0fdf4);
  border-top: 1px solid rgba(251, 146, 60, 0.15);
  color: $text-sub;
  font-size: 13px;
  line-height: 1.8;
}

@media (max-width: 900px) {
  .category-grid { grid-template-columns: repeat(3, 1fr); }
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
