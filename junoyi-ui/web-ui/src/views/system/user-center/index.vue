<!-- 个人中心页面 -->
<template>
  <div class="w-full h-full p-0 bg-transparent border-none shadow-none">
    <div class="relative flex-b mt-2.5 max-md:block max-md:mt-1">
      <!-- 左侧个人信息卡片 -->
      <div class="w-112 mr-5 max-md:w-full max-md:mr-0">
        <div class="art-card-sm relative p-9 pb-6 overflow-hidden text-center">
          <img class="absolute top-0 left-0 w-full h-50 object-cover" src="@imgs/user/bg.webp" />

          <!-- 头像区域（可点击更换） -->
          <div class="relative z-10 mt-30 mx-auto w-20 h-20 group cursor-pointer" @click="triggerAvatarUpload">
            <img
                class="w-full h-full object-cover border-2 border-white rounded-full"
                :src="avatarUrl"
            />
            <div class="absolute inset-0 bg-black bg-opacity-50 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
              <ArtSvgIcon icon="ri:camera-line" class="text-white text-xl" />
            </div>
            <input
                ref="avatarInputRef"
                type="file"
                accept="image/*"
                class="hidden"
                @change="handleAvatarChange"
            />
          </div>

          <h2 class="mt-5 text-xl font-normal">{{ profile?.nickName || profile?.userName || '-' }}</h2>
          <p class="mt-2 text-sm text-gray-500">@{{ profile?.userName || '-' }}</p>

          <div class="w-75 mx-auto mt-7.5 text-left">
            <div class="mt-2.5 flex items-center">
              <ArtSvgIcon icon="ri:mail-line" class="text-g-700" />
              <span class="ml-2 text-sm">{{ profile?.email || '未设置' }}</span>
            </div>
            <div class="mt-2.5 flex items-center">
              <ArtSvgIcon icon="ri:phone-line" class="text-g-700" />
              <span class="ml-2 text-sm">{{ profile?.phonenumber || '未设置' }}</span>
            </div>
            <div class="mt-2.5 flex items-center">
              <ArtSvgIcon icon="ri:user-3-line" class="text-g-700" />
              <span class="ml-2 text-sm">{{ getSexText(profile?.sex) }}</span>
            </div>
            <div class="mt-2.5 flex items-center">
              <ArtSvgIcon icon="ri:time-line" class="text-g-700" />
              <span class="ml-2 text-sm">注册于 {{ formatDate(profile?.createTime) }}</span>
            </div>
          </div>

          <div class="mt-8">
            <ElButton type="primary" size="small" @click="triggerAvatarUpload" :loading="avatarUploading">
              <ArtSvgIcon icon="ri:image-edit-line" class="mr-1" />
              更换头像
            </ElButton>
          </div>
        </div>
      </div>

      <!-- 右侧设置区域 -->
      <div class="flex-1 overflow-hidden max-md:w-full max-md:mt-3.5">
        <!-- 基本信息设置 -->
        <div class="art-card-sm">
          <h1 class="p-4 text-xl font-normal border-b border-g-300">基本设置</h1>

          <ElForm
              :model="profileForm"
              class="box-border p-5 [&>.el-row_.el-form-item]:w-[calc(50%-10px)] [&>.el-row_.el-input]:w-full [&>.el-row_.el-select]:w-full"
              ref="profileFormRef"
              :rules="profileRules"
              label-width="86px"
              label-position="top"
          >
            <ElRow>
              <ElFormItem label="昵称" prop="nickName">
                <ElInput v-model="profileForm.nickName" placeholder="请输入昵称" :disabled="!isEditProfile" />
              </ElFormItem>
              <ElFormItem label="性别" prop="sex" class="ml-5">
                <ElSelect v-model="profileForm.sex" placeholder="请选择性别" :disabled="!isEditProfile">
                  <ElOption
                      v-for="item in sexOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                  />
                </ElSelect>
              </ElFormItem>
            </ElRow>

            <ElRow>
              <ElFormItem label="邮箱" prop="email">
                <ElInput v-model="profileForm.email" placeholder="请输入邮箱" :disabled="!isEditProfile" />
              </ElFormItem>
              <ElFormItem label="手机号" prop="phonenumber" class="ml-5">
                <ElInput v-model="profileForm.phonenumber" placeholder="请输入手机号" :disabled="!isEditProfile" />
              </ElFormItem>
            </ElRow>

            <div class="flex-c justify-end [&_.el-button]:!w-27.5 gap-3">
              <ElButton v-if="isEditProfile" @click="cancelEditProfile">取消</ElButton>
              <ElButton type="primary" :loading="profileSaving" @click="handleProfileAction">
                {{ isEditProfile ? '保存' : '编辑' }}
              </ElButton>
            </div>
          </ElForm>
        </div>

        <!-- 修改密码 -->
        <div class="art-card-sm my-5">
          <h1 class="p-4 text-xl font-normal border-b border-g-300">修改密码</h1>

          <ElForm
              :model="pwdForm"
              class="box-border p-5"
              ref="pwdFormRef"
              :rules="pwdRules"
              label-width="86px"
              label-position="top"
          >
            <ElFormItem label="当前密码" prop="oldPassword">
              <ElInput
                  v-model="pwdForm.oldPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  :disabled="!isEditPwd"
                  show-password
              />
            </ElFormItem>

            <ElFormItem label="新密码" prop="newPassword">
              <ElInput
                  v-model="pwdForm.newPassword"
                  type="password"
                  placeholder="请输入新密码（6-20位）"
                  :disabled="!isEditPwd"
                  show-password
              />
            </ElFormItem>

            <ElFormItem label="确认新密码" prop="confirmPassword">
              <ElInput
                  v-model="pwdForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  :disabled="!isEditPwd"
                  show-password
              />
            </ElFormItem>

            <div class="flex-c justify-end [&_.el-button]:!w-27.5 gap-3">
              <ElButton v-if="isEditPwd" @click="cancelEditPwd">取消</ElButton>
              <ElButton type="primary" :loading="pwdSaving" @click="handlePwdAction">
                {{ isEditPwd ? '保存' : '编辑' }}
              </ElButton>
            </div>
          </ElForm>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/store/modules/user'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import {
  fetchGetProfile,
  fetchUpdateProfile,
  fetchUpdateAvatar,
  fetchChangePassword,
  fetchUploadAvatar
} from '@/api/system/user-center'
import { getAvatarUrl } from '@/utils/avatar'

defineOptions({ name: 'UserCenter' })

const userStore = useUserStore()

// 用户信息
const profile = ref<Api.System.SysUserVO | null>(null)
const loading = ref(false)

// 头像相关
const avatarInputRef = ref<HTMLInputElement>()
const avatarUploading = ref(false)
const avatarUrl = computed(() => getAvatarUrl(profile.value?.avatar))

// 基本信息编辑
const isEditProfile = ref(false)
const profileSaving = ref(false)
const profileFormRef = ref<FormInstance>()
const profileForm = reactive({
  nickName: '',
  email: '',
  phonenumber: '',
  sex: ''
})

// 密码修改
const isEditPwd = ref(false)
const pwdSaving = ref(false)
const pwdFormRef = ref<FormInstance>()
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 性别选项
const sexOptions = [
  { value: '0', label: '未知' },
  { value: '1', label: '男' },
  { value: '2', label: '女' }
]

// 表单验证规则
const profileRules = reactive<FormRules>({
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phonenumber: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
})

const pwdRules = reactive<FormRules>({
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

/**
 * 获取性别文本
 */
const getSexText = (sex?: string) => {
  if (sex === '0') return '未知'
  if (sex === '1') return '男'
  if (sex === '2') return '女'
  return '未设置'
}

/**
 * 格式化日期
 */
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

/**
 * 加载用户信息
 */
const loadProfile = async () => {
  loading.value = true
  try {
    profile.value = await fetchGetProfile()
    // 同步到表单
    profileForm.nickName = profile.value?.nickName || ''
    profileForm.email = profile.value?.email || ''
    profileForm.phonenumber = profile.value?.phonenumber || ''
    profileForm.sex = profile.value?.sex || ''
  } catch (error) {
    console.error('加载用户信息失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 触发头像上传
 */
const triggerAvatarUpload = () => {
  avatarInputRef.value?.click()
}

/**
 * 处理头像文件选择
 */
const handleAvatarChange = async (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  // 验证文件大小（最大2MB）
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }

  avatarUploading.value = true
  try {
    // 上传头像文件
    const fileInfo = await fetchUploadAvatar(file)
    
    // 直接使用后端返回的 fileUrl（已经是相对路径）
    // 例如：/files/avatar/2026/02/07/xxx.jpg
    await fetchUpdateAvatar(fileInfo.fileUrl)
    
    // 刷新用户信息
    await loadProfile()
    // 更新store中的用户信息
    if (profile.value?.avatar) {
      userStore.setUserInfo({
        ...userStore.getUserInfo,
        avatar: profile.value.avatar
      } as Api.Auth.UserInfo)
    }
    ElMessage.success('头像更新成功')
  } catch (error) {
    console.error('头像上传失败:', error)
  } finally {
    avatarUploading.value = false
    // 清空input
    input.value = ''
  }
}

/**
 * 处理基本信息操作（编辑/保存）
 */
const handleProfileAction = async () => {
  if (!isEditProfile.value) {
    isEditProfile.value = true
    return
  }

  // 验证表单
  const valid = await profileFormRef.value?.validate().catch(() => false)
  if (!valid) return

  profileSaving.value = true
  try {
    await fetchUpdateProfile({
      nickName: profileForm.nickName,
      email: profileForm.email,
      phonenumber: profileForm.phonenumber,
      sex: profileForm.sex
    })
    // 刷新用户信息
    await loadProfile()
    // 更新store
    userStore.setUserInfo({
      ...userStore.getUserInfo,
      nickName: profileForm.nickName,
      email: profileForm.email
    } as Api.Auth.UserInfo)
    isEditProfile.value = false
  } catch (error) {
    console.error('更新个人信息失败:', error)
  } finally {
    profileSaving.value = false
  }
}

/**
 * 取消编辑基本信息
 */
const cancelEditProfile = () => {
  isEditProfile.value = false
  // 恢复原始数据
  profileForm.nickName = profile.value?.nickName || ''
  profileForm.email = profile.value?.email || ''
  profileForm.phonenumber = profile.value?.phonenumber || ''
  profileForm.sex = profile.value?.sex || ''
  profileFormRef.value?.clearValidate()
}

/**
 * 处理密码操作（编辑/保存）
 */
const handlePwdAction = async () => {
  if (!isEditPwd.value) {
    isEditPwd.value = true
    return
  }

  // 验证表单
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return

  pwdSaving.value = true
  try {
    await fetchChangePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword,
      confirmPassword: pwdForm.confirmPassword
    })
    isEditPwd.value = false
    // 清空密码表单
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
    pwdFormRef.value?.clearValidate()
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    pwdSaving.value = false
  }
}

/**
 * 取消修改密码
 */
const cancelEditPwd = () => {
  isEditPwd.value = false
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  pwdFormRef.value?.clearValidate()
}

onMounted(() => {
  loadProfile()
})
</script>
