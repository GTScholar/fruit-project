<template>
  <header class="navbar">
    <div class="nav-inner">
      <router-link to="/" class="logo">
        <span class="logo-icon">🍊</span>
        <span class="logo-text gradient-text">果漾鲜生</span>
      </router-link>

      <nav class="nav-menu">
        <router-link to="/" class="nav-item" :class="{ active: route.path === '/' }">首页</router-link>
        <router-link to="/products" class="nav-item" :class="{ active: route.path.startsWith('/products') }">全部水果</router-link>
      </nav>

      <div class="nav-search">
        <el-input
          v-model="keyword"
          placeholder="搜索想吃的水果…"
          clearable
          @keyup.enter="doSearch"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>

      <div class="nav-right">
        <el-badge :value="cartStore.count" :hidden="cartStore.count === 0" :max="99">
          <el-icon class="cart-icon" @click="goCart"><ShoppingCart /></el-icon>
        </el-badge>

        <template v-if="userStore.isLogin">
          <el-dropdown trigger="click">
            <div class="user-entry">
              <el-avatar :size="34" :src="avatarUrl" class="user-avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) || '果' }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo?.nickname || '果友' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="router.push('/orders')">我的订单</el-dropdown-item>
                <el-dropdown-item @click="router.push('/address')">收货地址</el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" divided @click="router.push('/admin')">
                  <el-icon><Setting /></el-icon> 管理后台
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button text class="login-btn" @click="router.push('/login')">登录</el-button>
          <el-button class="btn-fruit" @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getCartList } from '@/api/cart'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const keyword = ref('')

const avatarUrl = computed(() => {
  const avatar = userStore.userInfo?.avatar
  return avatar && avatar.startsWith('http') ? avatar : ''
})

async function loadCartCount() {
  if (!userStore.isLogin) return
  try {
    const res = await getCartList()
    cartStore.setCount(res.data.reduce((sum, item) => sum + (item.checked === 1 ? item.quantity : 0), 0))
  } catch {
    cartStore.setCount(0)
  }
}

function doSearch() {
  router.push({ path: '/products', query: { keyword: keyword.value || undefined } })
}

function goCart() {
  if (!userStore.isLogin) {
    router.push('/login')
    return
  }
  router.push('/cart')
}

function handleLogout() {
  userStore.logout()
  cartStore.reset()
  ElMessage.success('已退出登录')
  router.push('/')
}

onMounted(loadCartCount)
watch(() => route.fullPath, loadCartCount)
</script>

<style lang="scss" scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 250, 242, 0.92);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(251, 146, 60, 0.15);
}

.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 28px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 22px;
  font-weight: 800;
  white-space: nowrap;

  .logo-icon {
    font-size: 28px;
    animation: bounce 3s ease-in-out infinite;
  }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}

.nav-menu {
  display: flex;
  gap: 8px;

  .nav-item {
    padding: 8px 16px;
    border-radius: 20px;
    color: #6b5a48;
    font-weight: 600;
    transition: all 0.25s;

    &:hover {
      color: $primary;
      background: rgba(251, 146, 60, 0.1);
    }

    &.active {
      color: #fff;
      background: linear-gradient(135deg, #fb923c, #f97316);
      box-shadow: 0 4px 12px rgba(251, 146, 60, 0.35);
    }
  }
}

.nav-search {
  flex: 1;
  max-width: 320px;

  :deep(.el-input__wrapper) {
    border-radius: 20px;
    background: #fff;
    box-shadow: 0 0 0 1px rgba(251, 146, 60, 0.2) inset;
  }
}

.nav-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 18px;

  .cart-icon {
    font-size: 24px;
    color: #6b5a48;
    cursor: pointer;
    transition: all 0.25s;

    &:hover {
      color: $primary;
      transform: scale(1.1);
    }
  }
}

.user-entry {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;

  .user-avatar {
    background: linear-gradient(135deg, #fb923c, #f97316);
    color: #fff;
    font-weight: 700;
  }

  .user-name {
    color: #6b5a48;
    font-weight: 600;
    max-width: 90px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.login-btn {
  color: $primary;
  font-weight: 600;
}
</style>
