<template>
  <div class="admin-layout">
    <!-- 侧边栏（浅色系） -->
    <aside class="sidebar">
      <div class="side-logo">
        <span class="logo-icon">🍊</span>
        <span class="logo-text">果漾鲜生</span>
        <span class="logo-sub">管理后台</span>
      </div>

      <el-menu
        :default-active="route.path"
        router
        class="side-menu"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item index="/admin/products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><FolderOpened /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/banners">
          <el-icon><Picture /></el-icon>
          <span>轮播图管理</span>
        </el-menu-item>
      </el-menu>

      <div class="side-foot">
        <el-button text class="back-btn" @click="router.push('/')">
          <el-icon><Back /></el-icon> 返回商城
        </el-button>
      </div>
    </aside>

    <!-- 主区域 -->
    <div class="main-area">
      <header class="topbar">
        <div class="topbar-title">{{ route.meta.title || '管理后台' }}</div>
        <div class="topbar-right">
          <el-tag type="success" effect="light" round>🍎 管理员：{{ userStore.userInfo?.nickname }}</el-tag>
          <el-dropdown trigger="click">
            <el-avatar :size="32" class="top-avatar">
              {{ userStore.userInfo?.nickname?.charAt(0) || '管' }}
            </el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/')">返回商城</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #fdf6ee;
}

/* ---------- 侧边栏：浅色渐变 ---------- */
.sidebar {
  width: 220px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #fff7ed 0%, #fef3e2 100%);
  border-right: 1px solid rgba(251, 146, 60, 0.15);

  .side-logo {
    padding: 26px 20px 20px;
    text-align: center;

    .logo-icon {
      font-size: 30px;
      display: block;
      animation: bounce 3s ease-in-out infinite;
    }

    .logo-text {
      display: block;
      margin-top: 6px;
      font-size: 20px;
      font-weight: 900;
      background: linear-gradient(135deg, #fb923c, #f97316);
      -webkit-background-clip: text;
      background-clip: text;
      -webkit-text-fill-color: transparent;
    }

    .logo-sub {
      display: inline-block;
      margin-top: 4px;
      font-size: 11px;
      color: #b08a5e;
      background: rgba(251, 146, 60, 0.12);
      padding: 2px 12px;
      border-radius: 10px;
    }
  }

  @keyframes bounce {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-4px); }
  }
}

.side-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 0 12px;

  --el-menu-item-height: 46px;
  --el-menu-base-level-padding: 14px;

  .el-menu-item {
    border-radius: 12px;
    margin-bottom: 4px;
    color: #7a6248;

    &:hover {
      background: rgba(251, 146, 60, 0.1);
      color: $primary;
    }

    &.is-active {
      background: linear-gradient(135deg, #fb923c, #f97316);
      color: #fff;
      font-weight: 700;
      box-shadow: 0 6px 16px rgba(251, 146, 60, 0.35);
    }
  }
}

.side-foot {
  padding: 16px;
  border-top: 1px dashed rgba(251, 146, 60, 0.25);

  .back-btn {
    width: 100%;
    color: $primary;
    font-weight: 600;
  }
}

/* ---------- 主区域 ---------- */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.topbar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  background: #fff;
  border-bottom: 1px solid rgba(251, 146, 60, 0.12);
  box-shadow: 0 2px 10px rgba(251, 146, 60, 0.06);

  .topbar-title {
    font-size: 17px;
    font-weight: 800;
    color: $text-main;
  }

  .topbar-right {
    display: flex;
    align-items: center;
    gap: 14px;
  }

  .top-avatar {
    cursor: pointer;
    background: linear-gradient(135deg, #fb923c, #f97316);
    color: #fff;
    font-weight: 700;
  }
}

.content {
  flex: 1;
  padding: 24px 28px;
  overflow: auto;
}
</style>
