<template>
  <div class="register-page">
    <!-- 左侧宣传区 -->
    <div class="left-panel">
      <div class="fruit fruit-1">🍎</div>
      <div class="fruit fruit-2">🍋</div>
      <div class="fruit fruit-3">🍑</div>
      <div class="fruit fruit-4">🫐</div>

      <div class="slogan">
        <h1 class="title">加入果漾鲜生</h1>
        <p class="subtitle">新用户注册 · 立享新人礼包</p>
        <p class="desc">首单立减10元 · 天天特价 · 坏果包赔</p>
      </div>
    </div>

    <!-- 右侧注册卡片 -->
    <div class="right-panel">
      <div class="form-card">
        <div class="card-head">
          <h2>创建账号 ✨</h2>
          <p>只需几步，开启你的鲜果之旅</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleRegister">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名（3-20位）" clearable>
              <template #prefix><el-icon><User /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="nickname">
            <el-input v-model="form.nickname" placeholder="昵称（选填）" clearable>
              <template #prefix><el-icon><Smiling /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码（6-20位）" show-password>
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" show-password>
              <template #prefix><el-icon><Key /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button class="btn-fruit reg-btn" :loading="loading" @click="handleRegister">
              注 册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="card-foot">
          <span>已有账号？</span>
          <router-link to="/login" class="link">直接登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度需在3~20位之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需在6~20位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

async function handleRegister() {
  await formRef.value.validate().catch(() => Promise.reject())
  loading.value = true
  try {
    await register({
      username: form.username,
      password: form.password,
      nickname: form.nickname
    })
    ElMessage.success('注册成功，快去登录吧 🍊')
    router.push('/login')
  } catch {
    /* 错误已由拦截器提示 */
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  display: flex;
  min-height: 100vh;
  background: #fffaf2;
}

.left-panel {
  position: relative;
  flex: 1.1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(150deg, #34d399 0%, #22c55e 50%, #fb923c 100%);
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
    font-size: 46px;
    font-weight: 900;
    letter-spacing: 4px;
    text-shadow: 0 6px 24px rgba(0, 0, 0, 0.15);
  }

  .subtitle {
    margin-top: 16px;
    font-size: 19px;
    font-weight: 600;
    letter-spacing: 2px;
  }

  .desc {
    margin-top: 10px;
    font-size: 14px;
    opacity: 0.92;
    letter-spacing: 1px;
  }
}

.fruit {
  position: absolute;
  z-index: 1;
  filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.18));
  animation: float 5s ease-in-out infinite;
}

.fruit-1 { top: 14%; left: 14%; font-size: 58px; animation-delay: 0s; }
.fruit-2 { top: 20%; right: 18%; font-size: 46px; animation-delay: 1s; }
.fruit-3 { bottom: 20%; left: 20%; font-size: 50px; animation-delay: 1.8s; }
.fruit-4 { bottom: 14%; right: 14%; font-size: 40px; animation-delay: 2.6s; }

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-18px) rotate(8deg); }
}

.right-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.form-card {
  width: 420px;
  background: #fff;
  border-radius: 24px;
  padding: 40px 40px 30px;
  box-shadow: 0 20px 60px rgba(34, 197, 94, 0.16);
  animation: fadeUp 0.7s ease;

  .card-head {
    text-align: center;
    margin-bottom: 28px;

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

  .reg-btn {
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

:deep(.el-input__wrapper) {
  border-radius: 12px;
  transition: box-shadow 0.3s;

  &.is-focus {
    box-shadow: 0 0 0 1.5px #fb923c inset, 0 6px 16px rgba(251, 146, 60, 0.18);
  }
}
</style>
