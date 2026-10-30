<template>
  <div class="profile-page">
    <Navbar />

    <main class="page-container">
      <h2 class="page-title"><span>👤</span> 个人中心</h2>

      <div class="profile-layout">
        <!-- 左侧信息卡 -->
        <div class="profile-card fruit-card">
          <el-avatar :size="86" :src="avatarUrl" class="big-avatar">
            {{ userStore.userInfo?.nickname?.charAt(0) || '果' }}
          </el-avatar>
          <div class="nickname">{{ userStore.userInfo?.nickname }}</div>
          <div class="username">@{{ userStore.userInfo?.username }}</div>
          <div class="badges">
            <el-tag v-if="userStore.isAdmin" type="danger" effect="light">管理员</el-tag>
            <el-tag v-else type="success" effect="light">普通会员</el-tag>
            <el-tag type="warning" effect="light" round>🍎 鲜果会员</el-tag>
          </div>
          <el-button v-if="userStore.isAdmin" class="btn-fruit admin-btn" @click="router.push('/admin')">
            <el-icon><Setting /></el-icon> 进入管理后台
          </el-button>
        </div>

        <!-- 右侧编辑区 -->
        <div class="edit-card fruit-card">
          <el-tabs v-model="activeTab">
            <!-- 基本信息 -->
            <el-tab-pane label="基本信息" name="info">
              <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="90px">
                <el-form-item label="头像地址">
                  <div class="avatar-edit">
                    <el-input v-model="infoForm.avatar" placeholder="粘贴图片URL，留空使用默认头像" />
                    <el-avatar :size="46" :src="avatarUrl" class="mini-avatar">果</el-avatar>
                  </div>
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="infoForm.nickname" placeholder="给自己起个可爱昵称" maxlength="20" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="infoForm.phone" placeholder="手机号" maxlength="11" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="infoForm.email" placeholder="邮箱" />
                </el-form-item>
                <el-form-item>
                  <el-button class="btn-fruit" :loading="savingInfo" @click="saveInfo">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 修改密码 -->
            <el-tab-pane label="修改密码" name="password">
              <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="6-20位新密码" />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
                </el-form-item>
                <el-form-item>
                  <el-button class="btn-fruit" :loading="savingPwd" @click="savePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Navbar from '@/components/Navbar.vue'
import { getInfo, updateInfo, updatePassword } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('info')
const savingInfo = ref(false)
const savingPwd = ref(false)
const infoFormRef = ref()
const pwdFormRef = ref()

const avatarUrl = computed(() => {
  const avatar = infoForm.avatar || userStore.userInfo?.avatar
  return avatar && avatar.startsWith('http') ? avatar : ''
})

const infoForm = reactive({
  nickname: '',
  avatar: '',
  phone: '',
  email: ''
})

const infoRules = {
  nickname: [{ max: 20, message: '昵称最长20个字符', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需在6~20位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

async function loadInfo() {
  const res = await getInfo()
  const user = res.data
  Object.assign(infoForm, {
    nickname: user.nickname || '',
    avatar: user.avatar || '',
    phone: user.phone || '',
    email: user.email || ''
  })
  userStore.setUserInfo(user)
}

async function saveInfo() {
  await infoFormRef.value.validate().catch(() => Promise.reject())
  savingInfo.value = true
  try {
    await updateInfo(infoForm)
    await loadInfo()
    ElMessage.success('个人信息已更新 🍊')
  } finally {
    savingInfo.value = false
  }
}

async function savePassword() {
  await pwdFormRef.value.validate().catch(() => Promise.reject())
  savingPwd.value = true
  try {
    await updatePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    router.push('/login')
  } finally {
    savingPwd.value = false
  }
}

onMounted(loadInfo)
</script>

<style lang="scss" scoped>
.page-title {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 20px;
  color: $text-main;
}

.profile-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
  align-items: start;
}

.profile-card {
  padding: 36px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;

  .big-avatar {
    font-size: 36px;
    background: linear-gradient(135deg, #fb923c, #f97316);
    color: #fff;
    box-shadow: 0 8px 24px rgba(251, 146, 60, 0.35);
  }

  .nickname {
    margin-top: 16px;
    font-size: 20px;
    font-weight: 800;
    color: $text-main;
  }

  .username {
    margin-top: 6px;
    color: $text-sub;
    font-size: 13px;
  }

  .badges {
    margin-top: 16px;
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    justify-content: center;
  }

  .admin-btn {
    margin-top: 24px;
    width: 100%;
    border-radius: 22px;
  }
}

.edit-card {
  padding: 28px 32px;

  .avatar-edit {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 100%;

    .mini-avatar {
      flex-shrink: 0;
      background: linear-gradient(135deg, #fb923c, #f97316);
      color: #fff;
    }
  }
}

@media (max-width: 900px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }
}
</style>
