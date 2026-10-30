<template>
  <div class="address-page">
    <Navbar />

    <main class="page-container">
      <div class="page-head">
        <h2 class="page-title"><span>📍</span> 收货地址</h2>
        <el-button class="btn-fruit" @click="openDialog()">
          <el-icon><Plus /></el-icon> 新增地址
        </el-button>
      </div>

      <div v-if="addresses.length" class="address-grid">
        <div v-for="address in addresses" :key="address.id" class="address-card fruit-card">
          <div class="card-top">
            <span class="name">{{ address.receiverName }}</span>
            <span class="phone">{{ address.receiverPhone }}</span>
            <el-tag v-if="address.isDefault === 1" type="warning" size="small" effect="light">默认</el-tag>
          </div>
          <div class="address-text">
            {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
          </div>
          <div class="card-actions">
            <el-button v-if="address.isDefault !== 1" text size="small" type="primary" @click="setDefault(address)">
              设为默认
            </el-button>
            <el-button text size="small" @click="openDialog(address)">编辑</el-button>
            <el-button text size="small" type="danger" @click="removeAddress(address)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="还没有收货地址，快添加一个吧">
        <el-button class="btn-fruit" @click="openDialog()">新增地址</el-button>
      </el-empty>
    </main>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑地址' : '新增地址'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="收货人手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="省市区" prop="region">
          <el-cascader
            v-model="form.region"
            :options="regionOptions"
            placeholder="请选择省 / 市 / 区"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" placeholder="街道、小区、门牌号等" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button class="btn-fruit" :loading="saving" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Navbar from '@/components/Navbar.vue'
import {
  getAddressList,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
} from '@/api/order'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const addresses = ref([])
const formRef = ref()

const emptyForm = () => ({
  id: null,
  receiverName: '',
  receiverPhone: '',
  region: [],
  detail: '',
  isDefault: 0
})

const form = reactive(emptyForm())

const rules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  region: [{ required: true, type: 'array', message: '请选择省市区', trigger: 'change' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 常用省市区示例（可自行扩展）
const regionOptions = [
  {
    value: '湖南省',
    label: '湖南省',
    children: [
      {
        value: '湘潭市',
        label: '湘潭市',
        children: [
          { value: '雨湖区', label: '雨湖区' },
          { value: '岳塘区', label: '岳塘区' },
          { value: '湘潭县', label: '湘潭县' }
        ]
      },
      {
        value: '长沙市',
        label: '长沙市',
        children: [
          { value: '岳麓区', label: '岳麓区' },
          { value: '雨花区', label: '雨花区' },
          { value: '天心区', label: '天心区' }
        ]
      }
    ]
  },
  {
    value: '广东省',
    label: '广东省',
    children: [
      {
        value: '广州市',
        label: '广州市',
        children: [
          { value: '天河区', label: '天河区' },
          { value: '越秀区', label: '越秀区' }
        ]
      }
    ]
  }
]

async function loadAddresses() {
  loading.value = true
  try {
    const res = await getAddressList()
    addresses.value = res.data
  } finally {
    loading.value = false
  }
}

function openDialog(address) {
  Object.assign(form, emptyForm())
  if (address) {
    Object.assign(form, {
      id: address.id,
      receiverName: address.receiverName,
      receiverPhone: address.receiverPhone,
      region: [address.province, address.city, address.district],
      detail: address.detail,
      isDefault: address.isDefault
    })
  }
  dialogVisible.value = true
}

async function saveAddress() {
  await formRef.value.validate().catch(() => Promise.reject())
  saving.value = true
  try {
    const data = {
      ...form,
      province: form.region[0],
      city: form.region[1],
      district: form.region[2]
    }
    if (form.id) {
      await updateAddress(data)
      ElMessage.success('地址已更新')
    } else {
      await addAddress(data)
      ElMessage.success('地址已添加')
    }
    dialogVisible.value = false
    loadAddresses()
  } finally {
    saving.value = false
  }
}

async function setDefault(address) {
  await setDefaultAddress(address.id)
  ElMessage.success('已设为默认地址')
  loadAddresses()
}

async function removeAddress(address) {
  await ElMessageBox.confirm(`确定删除「${address.receiverName}」的地址吗？`, '提示', { type: 'warning' }).catch(() => Promise.reject())
  await deleteAddress(address.id)
  ElMessage.success('已删除')
  loadAddresses()
}

onMounted(loadAddresses)
</script>

<style lang="scss" scoped>
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 800;
  color: $text-main;
}

.address-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 18px;
}

.address-card {
  padding: 20px;
  transition: transform 0.25s, box-shadow 0.25s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 28px rgba(251, 146, 60, 0.18);
  }

  .card-top {
    display: flex;
    align-items: center;
    gap: 10px;

    .name {
      font-size: 16px;
      font-weight: 800;
      color: $text-main;
    }

    .phone {
      color: $text-sub;
      font-size: 14px;
    }
  }

  .address-text {
    margin: 12px 0 16px;
    color: $text-sub;
    font-size: 14px;
    line-height: 1.7;
  }

  .card-actions {
    display: flex;
    justify-content: flex-end;
    border-top: 1px dashed #f0e7da;
    padding-top: 10px;
  }
}
</style>
