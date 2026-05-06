<template>
  <ElCard class="art-card-xs" shadow="never">
    <div class="add-form">
      <ElInput
        v-model="formData.permission"
        placeholder="权限标识，如：system.user.add"
        clearable
        class="add-input"
        @keyup.enter="handleAdd"
      >
        <template #prefix>
          <ArtSvgIcon icon="ri:key-2-line" />
        </template>
      </ElInput>
      <ElInput
        v-model="formData.description"
        placeholder="权限描述（可选）"
        clearable
        class="add-input-desc"
        @keyup.enter="handleAdd"
      >
        <template #prefix>
          <ArtSvgIcon icon="ri:file-text-line" />
        </template>
      </ElInput>
      <ElButton type="primary" @click="handleAdd" v-ripple>
        <ArtSvgIcon icon="ri:add-line" class="mr-1" />
        添加权限
      </ElButton>
    </div>
  </ElCard>
</template>

<script setup lang="ts">
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'

  interface Emits {
    (e: 'add', data: { permission: string; description: string }): void
  }

  const emit = defineEmits<Emits>()

  const formData = ref({
    permission: '',
    description: ''
  })

  /**
   * 处理添加
   */
  const handleAdd = () => {
    if (!formData.value.permission.trim()) {
      return
    }
    emit('add', { ...formData.value })
    // 清空表单
    formData.value = {
      permission: '',
      description: ''
    }
  }
</script>

<style lang="scss" scoped>
  .add-form {
    display: flex;
    align-items: center;
    gap: 12px;

    .add-input {
      flex: 2;
      min-width: 200px;
    }

    .add-input-desc {
      flex: 2;
      min-width: 200px;
    }
  }

  @media (max-width: 768px) {
    .add-form {
      flex-direction: column;

      .add-input,
      .add-input-desc {
        width: 100%;
      }
    }
  }
</style>
