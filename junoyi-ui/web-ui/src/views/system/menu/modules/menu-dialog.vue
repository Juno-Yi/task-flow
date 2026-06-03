<template>
  <ElDialog
    :title="dialogTitle"
    :model-value="visible"
    @update:model-value="handleCancel"
    width="860px"
    align-center
    class="menu-dialog"
    @closed="handleClosed"
  >
    <ArtForm
      ref="formRef"
      v-model="form"
      :items="formItems"
      :rules="rules"
      :span="width > 640 ? 12 : 24"
      :gutter="20"
      label-width="100px"
      :show-reset="false"
      :show-submit="false"
    >
      <template #menuType>
        <ElRadioGroup v-model="form.menuType" :disabled="isEdit">
          <ElRadioButton :value="1">菜单</ElRadioButton>
          <ElRadioButton :value="0">目录</ElRadioButton>
        </ElRadioGroup>
      </template>
      <template #parentId>
        <ElTreeSelect
          v-model="form.parentId"
          :data="parentMenuOptions"
          node-key="id"
          :props="{ label: 'title', children: 'children' }"
          :render-after-expand="false"
          check-strictly
          clearable
          default-expand-all
          placeholder="选择父级目录（不选则为一级）"
          style="width: 100%"
        />
      </template>
      <template #isExternalLink>
        <ElSwitch v-model="isExternalLink" />
      </template>
      <template #icon>
        <ArtIconPicker v-model="form.icon" placeholder="请输入或选择图标" />
      </template>
    </ArtForm>

    <template #footer>
      <span class="dialog-footer">
        <ElButton @click="handleCancel">取消</ElButton>
        <ElButton type="primary" :loading="submitting" @click="handleSubmit">确定</ElButton>
      </span>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormRules } from 'element-plus'
  import { ElIcon, ElTooltip, ElSwitch, ElTreeSelect } from 'element-plus'
  import { QuestionFilled } from '@element-plus/icons-vue'
  import type { FormItem } from '@/components/core/forms/art-form/index.vue'
  import ArtForm from '@/components/core/forms/art-form/index.vue'
  import ArtIconPicker from '@/components/core/forms/art-icon-picker/index.vue'
  import { useWindowSize } from '@vueuse/core'
  import { fetchAddMenu, fetchUpdateMenu, fetchGetMenuTree } from '@/api/system/menu'
  import { formatMenuTitle } from '@/utils/router'

  const { width } = useWindowSize()

  /**
   * 创建带 tooltip 的表单标签
   */
  const createLabelTooltip = (label: string, tooltip: string) => {
    return () =>
      h('span', { class: 'flex items-center' }, [
        h('span', label),
        h(
          ElTooltip,
          { content: tooltip, placement: 'top' },
          () => h(ElIcon, { class: 'ml-0.5 cursor-help' }, () => h(QuestionFilled))
        )
      ])
  }

  interface Props {
    visible: boolean
    editData?: Api.System.MenuVO | null
    defaultParentId?: number
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'success'): void
  }

  const props = withDefaults(defineProps<Props>(), {
    visible: false,
    editData: null,
    defaultParentId: 0
  })

  const emit = defineEmits<Emits>()

  const formRef = ref()
  const isEdit = ref(false)
  const submitting = ref(false)
  const isExternalLink = ref(false)
  const menuTreeData = ref<Api.System.MenuVO[]>([])

  // 表单数据
  const form = reactive<Api.System.MenuDTO>({
    menuType: 1,
    parentId: 0,
    name: '',
    path: '',
    component: '',
    title: '',
    icon: '',
    sort: 1,
    isHide: 0,
    isHideTab: 0,
    keepAlive: 1,
    isIframe: 0,
    link: '',
    isFullPage: 0,
    fixedTab: 0,
    activePath: '',
    showBadge: 0,
    showTextBadge: '',
    permission: '',
    status: 1,
    remark: ''
  })

  // 是否是一级菜单（没有父级）
  const isTopLevel = computed(() => !form.parentId || form.parentId === 0)

  // 是否是目录类型
  const isDirectory = computed(() => form.menuType === 0)

  // 是否开启内嵌模式
  const isIframeMode = computed(() => form.isIframe === 1)

  // 类型文本
  const typeLabel = computed(() => isDirectory.value ? '目录' : '菜单')

  // 动态校验规则
  const rules = computed<FormRules>(() => {
    // 内嵌模式：路由地址和外链地址都必填
    const pathRequired = isIframeMode.value || !isExternalLink.value
    const linkRequired = isExternalLink.value || isIframeMode.value
    
    return {
      title: [
        { required: true, message: `请输入${typeLabel.value}名称`, trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
      ],
      path: pathRequired ? [{ required: true, message: '请输入路由地址', trigger: 'blur' }] : [],
      link: linkRequired ? [{ required: true, message: '请输入外链地址', trigger: 'blur' }] : [],
      name: [{ required: true, message: '请输入路由名称', trigger: 'blur' }],
      // 目录类型、外链模式或内嵌模式下，组件路径不是必填
      component: (isDirectory.value || isExternalLink.value || isIframeMode.value) ? [] : [{ required: true, message: '请输入组件路径', trigger: 'blur' }]
    }
  })

  // Switch 组件的 span
  const switchSpan = computed(() => (width.value < 640 ? 12 : 6))

  /**
   * 处理菜单树数据，格式化标题并过滤只保留目录
   */
  const buildParentOptions = (list: Api.System.MenuVO[], excludeId?: number): any[] => {
    const result: any[] = []
    
    for (const item of list) {
      // 排除当前编辑的菜单
      if (item.id === excludeId) continue
      
      // 只保留目录类型 (menuType === 0)
      if (item.menuType === 0) {
        const node: any = {
          id: item.id,
          title: formatMenuTitle(item.title),
          children: item.children?.length ? buildParentOptions(item.children, excludeId) : undefined
        }
        // 如果没有子节点，删除 children 属性
        if (!node.children?.length) {
          delete node.children
        }
        result.push(node)
      }
    }
    
    return result
  }

  // 父级菜单选项（只显示目录，树状结构）
  const parentMenuOptions = computed(() => {
    const editId = isEdit.value ? props.editData?.id : undefined
    return buildParentOptions(menuTreeData.value, editId)
  })

  /**
   * 加载菜单树数据
   */
  const loadMenuTree = async () => {
    try {
      const list = await fetchGetMenuTree()
      menuTreeData.value = list || []
    } catch (error) {
      console.error('获取菜单树失败:', error)
    }
  }

  /**
   * 表单项配置
   */
  const formItems = computed<FormItem[]>(() => {
    // 目录类型：简化表单
    if (isDirectory.value) {
      return [
        { label: '类型', key: 'menuType', span: 24 },
        { label: '父级目录', key: 'parentId', span: 24 },
        { label: '目录名称', key: 'title', type: 'input', props: { placeholder: '请输入目录名称' } },
        {
          label: createLabelTooltip(
            '路由地址',
            '一级目录：以 / 开头的绝对路径（如 /system）\n二级及以下：相对路径（如 user）'
          ),
          key: 'path',
          type: 'input',
          props: { placeholder: '如：/system 或 user' }
        },
        {
          label: createLabelTooltip('路由名称', '路由的唯一标识'),
          key: 'name',
          type: 'input',
          props: { placeholder: '如：Demo' }
        },
        { label: '图标', key: 'icon' },
        {
          label: '排序',
          key: 'sort',
          type: 'number',
          props: { min: 0, controlsPosition: 'right', style: { width: '100%' } }
        },
        { label: '状态', key: 'status', type: 'switch', span: switchSpan.value, props: { activeValue: 1, inactiveValue: 0 } },
        { label: '隐藏菜单', key: 'isHide', type: 'switch', span: switchSpan.value, props: { activeValue: 1, inactiveValue: 0 } }
      ]
    }

    // 菜单类型：完整表单
    const items: FormItem[] = [
      { label: '类型', key: 'menuType', span: 24 },
      { label: '父级目录', key: 'parentId', span: 24 },
      { label: '菜单名称', key: 'title', type: 'input', props: { placeholder: '请输入菜单名称' } }
    ]

    // 外链模式
    if (isExternalLink.value) {
      items.push(
        {
          label: '外链地址',
          key: 'link',
          type: 'input',
          props: { placeholder: '如：https://www.example.com' }
        },
        {
          label: createLabelTooltip('路由地址', '外链模式下可选填，用于生成唯一路由'),
          key: 'path',
          type: 'input',
          props: { placeholder: '可选，如：external-link' }
        }
      )
    } else {
      items.push(
        {
          label: createLabelTooltip(
            '路由地址',
            '一级菜单：以 / 开头的绝对路径（如 /dashboard）\n二级及以下：相对路径（如 console、user）'
          ),
          key: 'path',
          type: 'input',
          props: { placeholder: '如：/dashboard 或 console' }
        }
      )
    }

    items.push(
      {
        label: createLabelTooltip('路由名称', '路由的唯一标识，用于 keep-alive 缓存'),
        key: 'name',
        type: 'input',
        props: { placeholder: '如：User' }
      },
      {
        label: createLabelTooltip(
          '组件路径',
          isTopLevel.value
            ? '一级菜单必须填写'
            : '具体页面：填写组件路径（如 /system/user）'
        ),
        key: 'component',
        type: 'input',
        props: {
          placeholder: isTopLevel.value ? '一级菜单必须填写' : '如：/system/user'
        }
      },
      { label: '图标', key: 'icon' },
      {
        label: '权限标识',
        key: 'permission',
        type: 'input',
        props: { placeholder: '如：system.menu.add' }
      },
      {
        label: '排序',
        key: 'sort',
        type: 'number',
        props: { min: 0, controlsPosition: 'right', style: { width: '100%' } }
      },
      {
        label: '文本徽章',
        key: 'showTextBadge',
        type: 'input',
        props: { placeholder: '如：New、Hot' }
      },
      {
        label: createLabelTooltip(
          '激活路径',
          '用于详情页等隐藏菜单，指定高亮显示的父级菜单路径'
        ),
        key: 'activePath',
        type: 'input',
        props: { placeholder: '如：/system/user' }
      },
      { label: '状态', key: 'status', type: 'switch', span: switchSpan.value, props: { activeValue: 1, inactiveValue: 0 } },
      { label: '页面缓存', key: 'keepAlive', type: 'switch', span: switchSpan.value, props: { activeValue: 1, inactiveValue: 0 } },
      { label: '外链模式', key: 'isExternalLink', span: switchSpan.value },
      { label: '是否内嵌', key: 'isIframe', type: 'switch', span: switchSpan.value, props: { activeValue: 1, inactiveValue: 0 } },
      { label: '隐藏菜单', key: 'isHide', type: 'switch', span: switchSpan.value, props: { activeValue: 1, inactiveValue: 0 } },
      { label: '显示徽章', key: 'showBadge', type: 'switch', span: switchSpan.value, props: { activeValue: 1, inactiveValue: 0 } },
      { label: '固定标签', key: 'fixedTab', type: 'switch', span: switchSpan.value, props: { activeValue: 1, inactiveValue: 0 } },
      { label: '标签隐藏', key: 'isHideTab', type: 'switch', span: switchSpan.value, props: { activeValue: 1, inactiveValue: 0 } },
      { label: '全屏页面', key: 'isFullPage', type: 'switch', span: switchSpan.value, props: { activeValue: 1, inactiveValue: 0 } }
    )

    return items
  })

  const dialogTitle = computed(() => {
    const type = form.menuType === 0 ? '目录' : '菜单'
    return isEdit.value ? `编辑${type}` : `新建${type}`
  })

  /**
   * 重置表单数据
   */
  const resetForm = (): void => {
    formRef.value?.reset()
    isExternalLink.value = false
    Object.assign(form, {
      id: undefined,
      menuType: 1,
      parentId: 0,
      name: '',
      path: '',
      component: '',
      title: '',
      icon: '',
      sort: 1,
      isHide: 0,
      isHideTab: 0,
      keepAlive: 1,
      isIframe: 0,
      link: '',
      isFullPage: 0,
      fixedTab: 0,
      activePath: '',
      showBadge: 0,
      showTextBadge: '',
      permission: '',
      status: 1,
      remark: ''
    })
  }

  /**
   * 加载表单数据（编辑模式）
   */
  const loadFormData = (): void => {
    if (!props.editData) return

    isEdit.value = true
    const row = props.editData

    // 判断是否是外链模式
    isExternalLink.value = !!row.link

    Object.assign(form, {
      id: row.id,
      menuType: row.menuType ?? 1,
      parentId: row.parentId ?? 0,
      name: row.name || '',
      path: row.path || '',
      component: row.component || '',
      title: row.title || '',
      icon: row.icon || '',
      sort: row.sort ?? 1,
      isHide: row.isHide ?? 0,
      isHideTab: row.isHideTab ?? 0,
      keepAlive: row.keepAlive ?? 1,
      isIframe: row.isIframe ?? 0,
      link: row.link || '',
      isFullPage: row.isFullPage ?? 0,
      fixedTab: row.fixedTab ?? 0,
      activePath: row.activePath || '',
      showBadge: row.showBadge ?? 0,
      showTextBadge: row.showTextBadge || '',
      permission: row.permission || '',
      status: row.status ?? 1,
      remark: row.remark || ''
    })
  }

  /**
   * 提交前验证
   */
  const validateBeforeSubmit = (): string | null => {
    const path = form.path?.trim() || ''
    const component = form.component?.trim() || ''
    
    if (isTopLevel.value) {
      // 一级菜单/目录验证
      if (path && !path.startsWith('/')) {
        return '一级菜单的路由地址必须以 / 开头'
      }
      // 一级菜单必须有 component（目录会自动填充）
      if (!isDirectory.value && !isExternalLink.value) {
        if (!component) {
          return '一级菜单必须填写组件路径'
        }
      }
    } else {
      // 二级及以下验证
      if (path && path.startsWith('/')) {
        return '二级及以下菜单的路由地址不能以 / 开头，请使用相对路径'
      }
      if (component === '/index/index') {
        return '二级及以下菜单的组件路径不能是 /index/index'
      }
    }
    
    return null
  }

  /**
   * 提交表单
   */
  const handleSubmit = async (): Promise<void> => {
    if (!formRef.value || submitting.value) return

    try {
      await formRef.value.validate()

      // 额外验证
      const error = validateBeforeSubmit()
      if (error) {
        ElMessage.warning(error)
        return
      }

      submitting.value = true

      // 处理提交数据
      const submitData = { ...form }
      
      // parentId 为空时设为 0
      if (!submitData.parentId) {
        submitData.parentId = 0
      }
      
      // 一级目录自动设置 component 为 /index/index
      // 二级及以下目录 component 留空
      if (isDirectory.value) {
        submitData.component = isTopLevel.value ? '/index/index' : ''
      }

      if (isEdit.value) {
        await fetchUpdateMenu(submitData)
      } else {
        await fetchAddMenu(submitData)
      }

      emit('success')
      handleCancel()
    } catch {
      // 表单校验失败或接口报错
    } finally {
      submitting.value = false
    }
  }

  /**
   * 取消操作
   */
  const handleCancel = (): void => {
    emit('update:visible', false)
  }

  /**
   * 对话框关闭后的回调
   */
  const handleClosed = (): void => {
    resetForm()
    isEdit.value = false
  }

  /**
   * 监听对话框显示状态
   */
  watch(
    () => props.visible,
    (newVal) => {
      if (newVal) {
        loadMenuTree()
        if (props.editData) {
          nextTick(() => loadFormData())
        } else if (props.defaultParentId) {
          // 新增时设置默认父级目录
          form.parentId = props.defaultParentId
        }
      }
    }
  )
</script>
