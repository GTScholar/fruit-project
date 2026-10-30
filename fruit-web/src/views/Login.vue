<template>
  <div class="login-page">
    <!-- 左侧宣传区：渐变 + 浮动水果 -->
    <div class="left-panel">
      <div class="fruit fruit-1">🍊</div>
      <div class="fruit fruit-2">🍓</div>
      <div class="fruit fruit-3">🍍</div>
      <div class="fruit fruit-4">🍇</div>
      <div class="fruit fruit-5">🍉</div>
      <div class="fruit fruit-6">🥝</div>

      <div class="slogan">
        <h1 class="title">果漾鲜生</h1>
        <p class="subtitle">从枝头到舌尖 · 只留新鲜</p>
        <p class="desc">产地直采 · 冷链配送 · 坏果包赔</p>
      </div>
      <div class="leaf leaf-1"></div>
      <div class="leaf leaf-2"></div>
    </div>

    <!-- 右侧登录卡片 -->
    <div class="right-panel">
      <div class="form-card">
        <div class="card-head">
          <h2>欢迎回来 👋</h2>
          <p>登录果漾鲜生，开启新鲜之旅</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" clearable>
              <template #prefix><el-icon><User /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" show-password>
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button class="btn-fruit login-btn" :loading="loading" @click="handleLogin">
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="card-foot">
          <span>还没有账号？</span>
          <router-link to="/register" class="link">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate().catch(() => Promise.reject())
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功，欢迎回来 🍎')
    // 管理员进后台，普通用户按来源跳转
    if (userStore.isAdmin) {
      router.push('/admin/dashboard')
    } else {
      router.push(route.query.redirect || '/')
    }
  } catch {
    /* 错误已由拦截器提示 */
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  background: #fffaf2;
}

/* ---------- 左侧 ---------- */
.left-panel {
  position: relative;
  flex: 1.1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(150deg, #fb923c 0%, #f59e0b 40%, #22c55e 100%);
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: radial-gradient(circle at 20% 20%, rgba(255, 255, 255, 0.25), transparent 40%),
      radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.2), transparent 40%);
  }
}

.slogan {
  position: relative;
  z-index: 2;
  color: #fff;
  text-align: center;
  animation: fadeUp 0.8s ease;

  .title {
    font-size: 52px;
    font-weight: 900;
    letter-spacing: 6px;
    text-shadow: 0 6px 24px rgba(0, 0, 0, 0.15);
  }

  .subtitle {
    margin-top: 16px;
    font-size: 20px;
    font-weight: 600;
    letter-spacing: 3px;
  }

  .desc {
    margin-top: 10px;
    font-size: 14px;
    opacity: 0.9;
    letter-spacing: 2px;
  }
}

/* 浮动水果 */
.fruit {
  position: absolute;
  font-size: 48px;
  z-index: 1;
  filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.18));
  animation: float 5s ease-in-out infinite;
}

.fruit-1 { top: 12%; left: 12%; animation-delay: 0s; font-size: 60px; }
.fruit-2 { top: 18%; right: 16%; animation-delay: 0.8s; font-size: 46px; }
.fruit-3 { bottom: 22%; left: 18%; animation-delay: 1.5s; font-size: 52px; }
.fruit-4 { bottom: 12%; right: 12%; animation-delay: 2.2s; font-size: 42px; }
.fruit-5 { top: 52%; left: 6%; animation-delay: 1s; font-size: 36px; }
.fruit-6 { top: 48%; right: 8%; animation-delay: 2.8s; font-size: 40px; }

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-18px) rotate(8deg); }
}

/* 叶子装饰 */
.leaf {
  position: absolute;
  width: 90px;
  height: 46px;
  border-radius: 0 90% 0 90%;
  background: rgba(255, 255, 255, 0.16);
  transform: rotate(-20deg);
}

.leaf-1 { top: 6%; right: 24%; }
.leaf-2 { bottom: 6%; left: 26%; transform: rotate(160deg); }

/* ---------- 右侧 ---------- */
.right-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.form-card {
  width: 400px;
  background: #fff;
  border-radius: 24px;
  padding: 44px 40px 32px;
  box-shadow: 0 20px 60px rgba(251, 146, 60, 0.18);
  animation: fadeUp 0.7s ease;

  .card-head {
    text-align: center;
    margin-bottom: 32px;

    h2 {
      font-size: 26px;
      font-weight: 800;
      color: $text-main;
    }

    p {
      margin-top: 8px;
      color: $text-sub;
      font-size: 14px;
    }
  }

  .login-btn {
    width: 100%;
    height: 46px;
    font-size: 16px;
    font-weight: 700;
    letter-spacing: 4px;
    border-radius: 23px;
  }

  .card-foot {
    margin-top: 20px;
    text-align: center;
    color: $text-sub;
    font-size: 14px;

    .link {
      color: $primary;
      font-weight: 700;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 输入框聚焦微动效 */
:deep(.el-input__wrapper) {
  border-radius: 12px;
  transition: box-shadow 0.3s;

  &.is-focus {
    box-shadow: 0 0 0 1.5px #fb923c inset, 0 6px 16px rgba(251, 146, 60, 0.18);
  }
}
</style>
