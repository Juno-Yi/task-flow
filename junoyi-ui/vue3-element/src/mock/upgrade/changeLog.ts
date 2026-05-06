interface UpgradeLogDetail {
  added?: string[] // 新增
  changed?: string[] // 优化
  fixed?: string[] // 修复
  demo?: string[] // 测试/示例
}

interface UpgradeLog {
  version: string // 版本号
  title: string // 更新标题
  date: string // 更新日期
  detail?: UpgradeLogDetail // 更新内容（分类）
  breakingChange?: boolean // 是否为破坏性更新
  remark?: string // 备注
}

export const upgradeLogList = ref<UpgradeLog[]>([
    {
        version: 'v0.5.4',
        title: '修复无法关闭验证码bug',
        date: '2026-3-3',
        detail: {
            added: [
                '添加SysCaptchaController获取验证码配置信息的接口  ',
                '新增NoCaptchaConfiguration提供验证码禁用时的空实现配置',
                '添加NoCaptchaHelper作为验证码禁用时的默认实现类  ',
                '添加验证码功能已关闭异常处理',
            ],
            changed: [
                '在AutoConfiguration中注册NoCaptchaConfiguration配置类',
                '修改SysAuthController实现验证码功能可选注入和条件验证',
                '实现验证码功能动态开关，支持运行时启用或禁用验证码验证'
            ]
        }
    },
    {
        version: 'v0.5.3',
        title: '修复与优化',
        date: '2026-3-1',
        detail: {
            changed: [
                '优化菜单全屏模式展现效果'
            ],
            fixed: [
                '修复权限机制 RefreshToken 未更新bug',
                '修复会话的并发问题',
                '修复异步权限更新事件处理没有异常捕获'
            ]
        }
    },
    {
        version: 'v0.5.2',
        title: '系统信息',
        date: '2026-2-11',
        detail: {
            added: [
                '系统信息页面增加详细监控信息',
                '实现系统基本信息、服务器信息、Java信息、内存信息和磁盘信息的监控功能',
                '添加系统监控类型定义和API接口',
                '初始化系统信息权限数据'
            ],
            changed: [
                '更新系统信息页面',
                '实现动态监控数据显示',

            ],
            fixed: [

            ]
        }

    },
    {
        version: 'v0.5.1-beta',
        title: '优化字典管理',
        date: '2026-2-11',
        detail: {
            added: [

            ],
            changed: [
                '优化用户管理状态字段显示',
                '优化角色管理状态字段显示',
                '优化部门管理状态字段显示',
                '移除菜单服务中的字典翻译功能',
                '优化部分细节',
                '更新国际化文件',
                '移出demo模块'
            ],
            fixed: [
                '修复一小部分bug',

            ],

        }
    },
    {
        version: 'v0.5.0-beta',
        title: '字典管理',
        date: '2026-2-11',
        detail: {
            added: [
                '添加字典类型数据表',
                '添加字典数据数据表',
                '添加前端字典管理页面',
                '新增字典数据和字典类型的完整CRUD操作接口',
                '实现分页查询、按类型查询和批量删除功能',
                '前端对接字典数据和字典类型的完整CRUD操作接口',
                '新增系统字典API接口及实现',
                '添加字典缓存功能',
                '添加字典缓存刷新功能',
                '添加字典数据和字典类型的操作日志事件记录功能',
                '初始化字典管理权限数据'
            ],
            changed: [
                '优化前端字典数据表单样式',
                '重构字典类型卡片的样式结构，提升视觉效果',
                '优化字典类型名称和代码的显示样式',
                '优化字典类型删除逻辑',
                '优化字典数据操作',
                '优化字典数据表格操作列显示'
            ],
            fixed: [
                '修复用户性别字段映射逻辑错误',
                '修复SQL注入过滤器对字典管理接口的误拦截问题',
                '修复SQL注入过滤器对权限管理接口的误拦截',
            ],
            demo: [

            ]
        }
    },
    {
        version: 'v0.4.6-beta',
        title: '系统用户信息',
        date: '2026-2-7',
        detail: {
            added: [
                '添加用户中心相关API接口和类型定义',
                '添加用户个人中心功能模块',
                '添加用户个人中心API接口',
                '前端对接用户个人中心接口',
                '实现头像上传',
                '顶部支持动态用户头像',
                '屏幕锁定组件支持动态用户头像',
            ],
            changed: [
                '简化本地文件存储的URL生成逻辑',
                '优化头像处理逻辑',
            ],
            fixed: [
                '修复头像URL处理和ResizeObserver循环错误'
            ]
        }
    },
    {
        version: 'v0.4.5-alpha',
        title: '系统参数',
        date: '2026-2-6',
        detail: {
            added: [
                '添加 sys_config 数据表',
                '添加前端系统参数模块基础文件',
                '添加系统配置参数类型分组排序状态字段支持',
                '添加系统参数配置验证',
                '添加系统参数配置事件发布功能',
                '添加配置管理专用异常类',
                '添加全局水印参数配置支持',
                '添加水印文本参数配置项支持',
                '添加菜单布局参数配置项支持',
                '添加菜单布局可编辑性控制参数配置项支持',

            ],
            changed: [
                '优化前端系统参数',
                '实现系统参数配置管理功能',
                '优化系统参数配置服务实现',
                '重构系统配置参数类型和界面组件',
                '调整配置管理系统的系统内置标识字段类型',
                '修改系统信息获取从硬编码改成从系统参数动态获取',
                '移除系统配置中的分组功能',
                '优化验证逻辑',
                '获取系统应用配置接口',
                '移除前端设置水印相关配置选项',
                '优化系统配置更新交互逻辑',
                '系统参数权限数据初始化'
            ],
            fixed: [
                '修复系统配置中isSystem字段的数据类型转换问题',
                '修复系统logo获取功能'
            ]
        }
    },
    {
        version: 'v0.4.4-alpha',
        title: '操作日志管理',
        date: '2026-2-5',
        detail: {
            added: [
                '添加前端操作日志页面',
                '添加获取操作日志列表接口',
                '前端对接获取操作日志列表接口',
                '添加删除操作日志接口',
                '前端对接删除操作日志接口',
                '添加清空操作日志接口',
                '前端对接清空操作日志接口',
                '添加用户操作事件',
                '添加用户操作日志事件记录功能',
                '添加角色操作日志事件记录功能',
                '添加对系统内置角色的删除保护机制',
                '添加部门操作日志事件记录功能',
                '添加权限组操作日志事件记录功能',
                '添加菜单操作日志事件记录功能',
                '添加系统权限组删除保护功能',
                '添加操作日志权限相关数据',
            ],
            changed: [
                '重构操作日志数据表',
                '重构用户操作事件监听',

            ],
            fixed: [
                '修正系统操作日志清空功能的表名错误',
                '修复旧版本重构遗留问题',

            ]
        }
    },
    {
        version: 'v0.4.3-alpha',
        title: '登录日志管理',
        date: '2026-2-3',
        detail: {
            added: [
                '添加前端登录日志页面',
                '添加后端获取登录日志列表接口',
                '前端对接获取登陆日志列表接口',
                '添加批量删除登录日志接口',
                '前端对接批量删除登录日志接口',
                '添加清除登录日志接口',
                '前端对接清除登录日志接口',
                '添加乐观锁和防全表攻击插件配置',
                '添加登录日志管理相关权限标识符数据'
            ],
            changed: [
                '重构登录日志数据表',
                '优化登录日志数据表',
                '优化登录日志清空功能',
                '重构用户登录事件',
                '添加登录日志监听器',
                '添加登录事件发布功能',
                '更新登录日志组件权限标识',


            ],
            fixed: [
                '修复登录日志页面组件类型定义bug',

            ],
        }
    },
    {
        version: 'v0.4.2-alpha',
        title: '优化与修复',
        date: '2026-1-23',
        detail: {
            added: [
                '在 SysUserServiceImpl 中添加用户名、邮箱、手机号唯一性校验功能',
                '新增 UserEmailAlreadyExistsException 异常类用于邮箱重复处理  ',
                '新增 UserNameAlreadyExistsException 异常类用于用户名重复处理 ',
                '新增 UserPhoneAlreadyExistsException 异常类用于手机号重复处理',
                '新增 DataPermissionProperties 配置类，支持注解模式和全局模式',
                '实现字段存在性缓存机制，提升性能',
                '添加表字段动态检测功能，只对存在相关字段的表应用数据范围过滤',

            ],
            changed: [
                '解决用户重复数据安全问题并添加唯一性验证',
                '重构数据权限配置和实现',
                '将数据范围配置重命名为数据权限配置，统一使用 data-permission 前缀',
                '移除系统表白名单机制，改为动态检查表字段存在性',
                '优化异常处理机制，避免影响业务执行',
                '更新 MyBatis Plus 配置以支持新的数据权限配置',
                '移除旧的数据范围配置属性类',
                '提取 LoginUser 构建逻辑到独立的 LoginUserBuilder 类',
                '优化权限加载性能，一次性查询所有权限相关数据  ',
                '统一权限上下文管理，避免重复查询数据库'
            ],
            fixed: [

            ]
        }
    },
    {
        version: 'v0.4.1-alpha',
        title: '文件上传接口',
        date: '2026-1-22',
        detail: {
            added: [
                '新增文件上传策略',
                '新增系统文件服务',
                '添加系统文件服务接口和统一文件上传策略',
                '新增文件上传策略接口和抽象类，支持文件类型、大小验证',
                '添加头像业务类型上传策略',
                '添加文档业务类型上传策略',
                '添加图片业务类型上传策略',
                '添加视频业务类型上传策略',
                '添加音频业务类型上传策略',
                '实现上传策略工厂和业务类型枚举',
                '新增文件类型检测工具类',
                '实现上传文件时自动根据文件类型检测存储目录',

            ],
            changed: [
                '更新文件存储路径构建逻辑，按业务类型和日期分目录存储',
                '在FileHelper中添加基于策略的上传方法',
                '更新本地和阿里云OSS存储的路径构建逻辑',
                '优化文件路径构建，未指定业务类型时默认使用other目录',
                '重构上传方法以支持自动类型检测逻辑',
            ],
            demo: [
                '测试策略性上传功能',
                '测试自动检查类型上传',
                '测试自定义上传',
            ]
        },
        remark: '除调用系统提供的文件上传API接口，可自行通过 FileHelper 自行实现策略性上传、自动检查类型上传、自定义上传 以达到实现自己想要的上传方式'
    },
    {
        version: 'v0.4.0-alpha',
        title: '文件存储模块',
        date: '2026-1-20',
        detail: {
            added: [
                '添加 junoyi-framework-file 模块依赖',
                '创建文件助手 FileHelper 提供简化的文件操作接口',
                '设计存储工厂 FileStorageFactory 支持动态创建存储实例',
                '添加文件信息实体类 FileInfo 包含完整的文件元数据',
                '创建文件存储配置属性类 FileStorageProperties 支持多平台配置',
                '添加详细的错误处理和友好的错误提示信息',
                '添加带分类和参数的日志记录功能',
                '添加本地文件静态资源配置'
            ],
            changed: [
                '实现文件存储统一接口 FileStorage 支持本地和OSS存储',
                '实现本地文件存储 LocalFileStorage 支持按日期分目录存储',
                '实现阿里云OSS存储 OssFileStorage 支持临时URL功能',
                '定义存储类型枚举 StorageType 支持本地、OSS、MinIO等多种方式',
                '重构文件存储模块日志实现和依赖管理',
                '实现阿里云OSS配置验证，确保必要参数不为空',
                '重构FileHelper移除日志注解，优化存储实例获取方式',
                '重写FileStorageConfiguration配置类，使用Bean注解管理组件',
                '初始化阿里云OSS客户端时添加连接测试和异常处理',
                '更新阿里云OSS文件存储实现',
                '改进OSS连接测试机制，使用getBucketInfo替代doesBucketExist',
                '实现OSS客户端关闭功能，防止资源泄露',
            ],
            demo: [
                '添加文件上传测试接口',
                '添加文件下载测试接口',
            ]
        }
    },
    {
        version: 'v0.3.4-alpha',
        title: '优化',
        date: '2026-1-18',
        detail: {
            added: [
                '添加 GPU 加速属性 to 强制硬件渲染',
                '添加页面路径显示功能',
            ],
            changed: [
                '删除快速入口相关的配置文件和类型定义',
                '从头部栏和设置面板中移除快速入口功能选项',
                '更新头部栏配置以移除快速入口相关设置项',
                '优化侧边栏菜单性能',
                '移除菜单展开/收起超时配置，避免不必要的延迟',
                '使用 shallowRef 替代深度响应式，提升计算属性性能',
                '优化菜单过滤算法，使用循环替代映射操作',
                '禁用部分过渡动画以提升渲染性能',
            ],
            fixed: [
                '修复数据范围过滤器导致的用户信息查询异常',
                '修复数据权限作用域处理逻辑',
            ]
        }
    },
    {
        version: 'v0.3.3-alpha',
        title: '优化',
        date: '2026-1-17',
        detail: {
            added: [
                '实现登录页面多布局支持',
                '实现忘记密码多布局支持',
                '实现注册页面多布局支持',
                '实现动态系统信息功能',
                '添加系统信息接口调用，在应用启动时获取系统信息',
                '实现系统名称的动态显示，优先使用接口返回数据，否则使用配置文件',
                '添加系统Logo的动态加载，支持相对路径和完整URL',

            ],
            changed: [
                '移出遗留的废弃页面以及菜单',
                '登录界面重构',
                '优化数据源配置和SQL拦截器性能',
                '调整本地环境数据库连接池配置',
                '优化数据权限处理器性能',
                '更新 application-local.yml 中的数据库连接配置，调整连接池参数',
                '在 SysRoleServiceImpl 中实现差量更新策略，替换原有的"先删后插"方式',
                '在 SysUserServiceImpl 中为用户角色、部门和权限组绑定添加差量更新优化',
                '优化事务处理和事件发布逻辑，仅在实际变更时触发事件',
                '更新登录成功通知显示用户昵称而非系统名称'
            ],
        }
    },
    {
        version: 'v0.3.2-alpha',
        title: '优化',
        date: '2026-1-14',
        detail: {
            added: [
                '新增SQL注入排除配置',
                '添加API加密过滤器排除路径配置',
                '添加会话访问时间异步更新机制，减少Redis写入频率',
            ],
            changed: [
                '优化SQL防注入逻辑，降低误报率',
                '优化权限加载和会话管理性能',
                '移除重复的用户权限、角色、部门数据库查询，直接从LoginUser会话中获取',
                '实现头像和邮箱字段的按需查询，避免不必要的数据加载',
                '优化服务器配置',
                '优化安全过滤器性能'
            ],
            fixed: [
                '修复SQL注入过滤器误报问题',
                '修复接口加密把接口文档请求也加密的bug',
            ]
        }
    },
    {
        version: 'v0.3.1-alpha',
        title: '菜单i18n国际化支持与权限配置',
        date: '2026-1-14',
        detail: {
            changed: [
                '更新国际化配置',
                '前端将权限池页面从标签云布局重构为表格布局',
                '优化用户管理权限配置',
                '优化角色管理权限配置',
                '优化部门管理权限配置',
                '优化权限组管理权限配置',
                '优化前端权限池权限控制',
                '优化权限池权限配置',
                '优化菜单管理权限配置',
                '优化会话监控权限配置',
                '优化缓存页面操作按钮组件',
                '优化缓存监控权限配置'
            ],
            fixed: [
                '修复部分菜单未支持i18n',
                '修复菜单编辑权限控制',
            ]
        }
    },
    {
        version: 'v0.3.0-alpha',
        title: '数据范围',
        date: '2026-1-13',
        detail: {
            added: [
                '数据范围（DataScope）行级权限控制 基于 MyBatis-Plus DataPermissionHandler 实现自动 SQL 过滤',
                '支持四种数据范围类型：全部数据(ALL)、本部门(DEPT)、本部门及下级(DEPT_AND_CHILD)、仅本人(SELF)',
                '支持两种使用模式：注解模式（@DataScope）和全局模式（自动过滤所有查询）',
                '新增 @IgnoreDataScope 注解，支持在 Mapper 类或方法上标记忽略数据范围过滤',
                '新增 DataScopeType 枚举，包含优先级比较方法 max()',
                '新增 DataScopeContextHolder 管理 ThreadLocal 上下文（userId/userName/deptIds/accessibleDeptIds/superAdmin）',
                '新增 DataScopeHandler 实现 MyBatis-Plus DataPermissionHandler 接口',
                '新增 DataScopeConfig 配置类，支持 enabled/globalEnabled/defaultDeptField/defaultUserField',
                'LoginUser / UserSession 新增 dataScope、accessibleDeptIds 字段',
                '新增 docs/JunoYi数据范围使用指南.md 完整技术文档'
            ],
            changed: [
                '重构数据权限实现：删除旧的 DataScopeInterceptor，改用 MyBatis-Plus 官方 DataPermissionInterceptor',
                'MyBatisPlusConfig 集成数据权限插件，确保在分页插件之前执行',
                'TokenAuthenticationTokenFilter 集成数据范围上下文设置与清理',
                'SessionHelperImpl 同步传递数据范围属性到 Redis 会话',
                'SysAuthServiceImpl 实现数据范围计算逻辑',
                '全局模式自动排除系统管理表（sys_user/sys_role/sys_dept 等），系统管理功能通过菜单权限控制',
                'SELF 模式使用 userName（用户名）进行过滤，与 create_by 字段存储格式一致',
                'junoyi-framework-security 模块新增 datasource 依赖'
            ],
            demo: [
                '新增 /demo/data-scope 测试接口，用于查看当前用户的数据范围信息'
            ]
        },
        breakingChange: true,
        remark: ''
    },
    {
        version: 'v0.2.6-alpha',
        title: '接口文档',
        date: '2026-1-12',
        detail: {
            added: [
                '新增 junoyi-framework-api-doc 模块，统一管理 API 文档配置',
                '新增 ApiDocProperties 配置类，支持 junoyi.api-doc.* 前缀配置',
                '新增 ApiDocConfiguration 自动配置类，支持自定义文档信息和 API 分组',
                '新增 API 文档使用指南文档 docs/JunoYi API文档使用指南.md',
                '集成 springdoc-openapi-starter-webmvc-ui 2.8.4（Spring Boot 3.x 官方推荐方案）',
                '集成 knife4j-openapi3-ui 4.5.0（仅 UI 界面，提供更友好的文档展示',
                '新增 API 文档相关路径到安全白名单（/doc.html、/swagger-ui/**、/v3/api-docs/**）'

            ],
            changed: [
                '移除 knife4j-openapi3-jakarta-spring-boot-starter 依赖（与 Spring Boot 3.5.0 存在兼容性问题）',
                '移除 springfox-boot-starter 依赖（已废弃，不兼容 Spring Boot 3.x）',
                '支持通过配置文件自定义文档标题、描述、版本、联系人等信息',
                '支持按路径或包名配置 API 分组',
                '支持全局 JWT Bearer Token 认证配置',
                '采用 SpringDoc + Knife4j UI 分离方案解决兼容性问题',

            ],
            fixed: [
                '修复 Knife4j 4.3.0 与 Spring Boot 3.5.0 不兼容导致的 NoSuchMethodError 异常',

            ],
            demo: [
                'Knife4j UI：http://localhost:7588/doc.html（推荐）',
                'Swagger UI：http://localhost:7588/swagger-ui.html'
            ]
        }
    },
    {
        version: 'v0.2.5-alpha',
        title: '重构异常机制',
        date: '2026-1-12',
        detail: {
            added: [
                '添加Token过期异常'
            ],
            changed: [
                '优化全局异常处理',
                '移出旧异常机制',
                '重构异常机制',
                '优化异常机制'
            ],
            fixed: [
            ],
            demo: [
            ]
        },
        breakingChange: true,
        remark: '重构后的异常机制遵循"谁抛出谁定义"原则'
    },
    {
        version: 'v0.2.4-alpha',
        title: '前后端统一分页优化',
        date: '2026-1-12',
        detail: {
            added: [

            ],
            changed: [
                '后端优化将分页参数统一从pageNum/pageSize 替换为 current/size',
                '后端统一分页请求响应格式',
                '前端优化将分页参数从pageNum/pageSize改为current/size',
                '前端统一API分页参数命名规范',
                '修复角色列表接口路径问题'
            ],
            fixed: [
                '后端修复分页参数出现混乱bug',
                '前端修复分页参数出现混乱bug',

            ]
        }
    },
    {
        version: 'v0.2.3-alpha',
        title: '缓存监控',
        date: '2026-1-12',
        detail: {
            added: [
                '新增RedisInfoVO类等相关数据体用于封装Redis服务器信息数据',
                '在RedisUtils工具类中相关操作方法',
                '添加系统缓存监控接口',
                '前端对接系统缓存监控接口',
                '添加 Redis 键信息查询功能',
                '添加缓存键列表查询接口',
                '前端对接缓存键列表查询接口',
                '前端更新缓存类型枚举',
                '添加缓存键详情查询接口',
                '前端对接缓存键详情查询',
                '添加系统缓存删除接口',
                '前端对接系统缓存删除接口',
                '添加批量删除缓存接口',
                '前端对接批量删除缓存接口',
                '添加清理所有缓存接口',
                '前端对接清理所有缓存接口'
            ],
            changed: [
                '前端将Redis信息展示部分提取到独立的RedisInfoCard组件',
                '优化Redis缓存服务实现',
                '前端将缓存详情抽屉逻辑从主页面提取到 CacheDetailDrawer 组件',

            ],
            fixed: [

            ],
            demo: [

            ]
        }
    },
    {
        version: 'v0.2.2-alpha',
        title: '优化会话机制',
        date: '2026-1-11',
        detail: {
            changed: [
                '扩展API加密过滤器支持DELETE请求解密',
                '重构会话机制',
                '优化accessToken机制',
                '优化refreshToken机制',
                '引入滑动会话机制，Session TTL 等于 AccessToken 有效期，每次刷新 Token 时自动续期',
                '修改 Redis 存储结构，会话详情使用 AccessToken 有效期作为 TTL，RefreshToken 白名单存储完整 UserSession',
                '实现 Session 过期后自动从 RefreshToken 白名单恢复功能',
                '优化令牌刷新逻辑，支持滑动续期并保持 RefreshToken 不变',
                '计算滑动续期时长，确保 Session TTL 不超过 RefreshToken 剩余有效期',
            ],
            fixed: [
                '修复会话监控bug',
                '修复登录IP列宽度显示问题',

            ],
            demo: [

            ]
        },
        remark: '会话时长不再是refreshToken时长，会话时长使用refreshToken时长会导致出现很多“僵尸会话”占用不必要的内存'
    },
    {
        version: 'v0.2.1-alpha',
        title: '会话监控',
        date: '2026-1-10',
        detail: {
            added: [
                'sessionHelper接口添加获取所有活跃会话功能',
                '添加查询会话列表分页接口',
                '添加踢出指定会话接口',
                '前端对接查询会话列表分页接口',
                '前端对接踢出指定会话接口',
                '添加批量踢出会话接口',
                '前端对接批量踢出会话接口',
                '集成ip2region实现IP地址地理位置解析功能',
                '添加IP地区信息到用户会话',
                '添加User-Agent解析工具类',
                '前端会话管理页面增加设备信息和浏览器显示功能'
            ],
            changed: [
                '重构系统会话实体和实现类',
                '扩展会话信息以支持操作系统和浏览器识别',
            ],
            fixed: [
                '修复未登录状态下触发logout请求出现无效请求bug',

            ],
            demo: [

            ]
        }
    },
    {
        version: 'v0.2.0-alpha',
        title: '权限池',
        date: '2026-1-8',
        detail: {
            added: [
                '前端添加会话监控页面',
                '前端添加缓存监控界面',
                '添加 sys_permission 数据表',
                '添加获取分页权限池列表接口',
                '添加权限池下列列表接口',
                '添加权限池添加权限接口',
                '添加权限池删除权限接口',
                '添加权限池批量删除权限接口',
                '添加权限状态更新接口',
                '前端对接获取分页权限池列表接口',
                '前端对接权限池添加权限接口',
                '前端对接权限池删除权限接口',
                '前端对接权限池批量删除权限接口',
                '前端权限状态更新接口',
                '添加权限组权限编辑移动端触摸拖拽支持'
            ],
            changed: [
                '前端优化权限池交互界面',
                '优化批量删除按钮的条件渲染逻辑',
                '前端优化权限池界面显示和状态管理',
                '重构权限池页面样式和交互逻辑',
                '优化前端用户管理、角色管理、部门管理中分配权限组操作逻辑',
                '重构用户独立权限对话框交互逻辑'
            ],
            fixed: [
                '修复权限池相关权限标识符错误',
                '修复类型问题打包编译bug'
            ]
        },
        remark: '权限池只是用于开发者在开发阶段添加所使用到的权限，这里的权限可以分配给权限组进行使用, 跟这里权限机制无直接钩挂'
    },
    {
        version: 'v0.1.9-alpha',
        title: '修复与优化',
        date: '2026-1-6',
        detail: {
            added: [
                '添加适配移动端拖拽滑动功能',

            ],
            changed: [
                '优化前端请求与加密解密先后逻辑循序',
                '移除了请求处理中的详细日志输出，改为仅在解密时记录调试信息',
                '移除部分过滤器中的调试日志',
                '重构前端所有拖拽功能逻辑',
                '优化前端所有拖拽功能逻辑',
                '优化API加密解密',
                '修改密钥长度优化性能',

            ],
            fixed: [
                '修复API加密解密与过滤器链兼容性问题',
                '修复拖拽相关的类型问题',
                '修复前端打包出现无法打包情况',
                '修复API加密公钥格式和密钥解密逻辑bug',
                '修复 404/500 异常页面"返回首页"按钮无效bug',
                '修复刷新令牌出现无法解密导致令牌无法刷新bug'
            ],
            demo: [

            ]
        }
    },
    {
        version: 'v0.1.8-alpha',
        title: '权限管理',
        date: '2026-1-5',
        detail: {
            added: [
                '添加权限管理目录，并添加两个菜单权限组管理',
                '前端添加权限管理页面',
                '添加获取权限组列表接口',
                '前端对接获取权限组列表接口',
                '添加 ip2region 依赖管理',
                '添加添加权限组接口',
                '前端对接添加权限组接口',
                '添加权限组下拉列表',
                '添加更新权限组接口',
                '前端对接更新权限组接口',
                '添加权限组删除接口',
                '前端对接权限组删除接口',
                '添加权限组批量删除接口',
                '前端对接权限组批量删除接口',
                '添加权限修改更新事件',
                '添加权限修改更新事件监听器，权限更新时候异步更新会话缓存中的用户权限',
                '前端添加用户管理分配权限组交互',
                '添加获取用户已绑定权限组接口',
                '添加更新用户绑定权限组接口',
                '前端对接获取用户已绑定权限组接口',
                '前端对接更新用户绑定权限组接口',
                '前端添加角色管理分配权限组交互',
                '添加获取角色已经绑定权限组接口',
                '添加角色绑定权限组接口',
                '前端对接获取角色已经绑定权限组接口',
                '前端对接角色绑定权限组接口',
                '前端添加部门管理分配权限组交互',
                '添加获取部门已经绑定权限组接口',
                '添加部门绑定权限组接口',
                '前端对接获取部门已经绑定权限组接口',
                '前端对接添加部门绑定权限组接口',
                '前端添加用户独立权限管理功能交互',

            ],
            changed: [
                '重构权限机制交互逻辑',
                '优化前端权限组管理交互',
                '更新组件类型定义和类型注解',
                '优化实体类注解配置',
                '优化权限组列表显示',
                '重构权限改变事件',
                '重构优化权限改变事件监听器',
                '前端优化添加用户独立权限弹窗样式',
                '暂时移出临时权限功能',
                '优化独立权限添加逻辑（全量替换模式改为增量添加模式）',
                '重构前端用户独立权限相关API和类型定义',
                '更新前端用户独立权限弹窗交互逻辑',

            ],
            fixed: [
                '修复用户管理中非超级管理员用户也能使用重置密码bug',
                '修复菜单权限注解中的拼写错误',
                '修复权限组列表查询中空指针异常',
                '修复权限组管理查询无效 bug',
                '修复前端类型报错问题',
                '修复打包因类型定义导致无法打包bug',
                '修复批量删除权限组数据格式问题',
                '修复用户绑定/取消绑定角色时，权限未同步 bug',
                '修复用户绑定/取消绑定部门时，权限未同步 bug',
                '修复后端菜单过滤逻辑中，只做了精确匹配 permissions.contains(perm)，没有使用 PermissionMatcher 进行通配符匹配bug',
                '修复前端权限机制中，v-permission指令无法进行多级通用符过滤bug',

            ],
            demo: [

            ]
        },
        remark: '暂时移出临时权限功能，保留过期时间字段，在后续版本更新添加临时权限功能'

    },
    {
        version: 'v0.1.7-alpha',
        title: '用户管理',
        date: '2026-1-3',
        detail: {
            added: [
                '添加用户管理页面左右表格，支持部门筛选',
                '添加用户列表查询接口',
                '前端对接用户列表查询接口',
                '添加用户按照部门树筛选功能',
                '添加添加用户接口',
                '前端对接添加用户接口',
                '添加更新用户接口',
                '前端对接更新用户接口',
                '添加用户删除接口',
                '添加批量删除用户接口',
                '前端对接删除用户接口',
                '前端对接批量删除用户接口',
                '前端添加用户角色分配弹窗',
                '前端添加用户部门分配弹窗',
                '添加获取用户已绑定角色接口',
                '添加用户绑定角色接口',
                '前端对接获取用户已绑定角色接口',
                '前端对接添加用户绑定角色接口',
                '添加获取用户已绑定部门接口',
                '添加用户绑定部门接口',
                '前端对接获取用户已绑定部门接口',
                '前端对接获取用户绑定部门接口',
                '前端添加部门拖拽排序功能',
                '添加部门排序接口',
                '前端对接部门排序接口',
                '添加超级管理员重置用户密码接口',
                '前端添加重置密码弹窗',
                '前端对接超级管理员重置用户密码接口'
            ],
            changed: [
                '移除用户查询中的创建时间降序排序',
                '保持按钮功能不变，仅调整视觉效果',
                '获取角色列表时排除超级管理员',
                '后端重构系统模块实体类和用户服务',
                '更新用户管理界面操作按钮和表单结构',
                '优化前端样式，统一样式',
                '前端优化用户角色分配、部门分配交互',
                '优化后端代码',
                '优化部门排序功能',
                '优化菜单排序功能',

            ],
            fixed: [
                '修复用户管理中更新时间数据始终为null bug',
                '修复部门排序功能 bug',
                '修复部门管理拖拽模式取消后展开状态未重置的问题',
                '修复菜单管理拖拽模式取消后展开状态未重置的问题',

            ],
            demo: [

            ]
        },
        remark: '最讨厌编辑来编辑去的方式，烦死，直接图形化拖拽，人性化、高效化、现代化，面向开发者设计，让操作更加优雅美观'
    },
    {
        version: 'v0.1.6-alpha',
        title: '部门管理',
        date: '2026-1-2',
        detail: {
            added: [
                '后端添加获取部门树状列表接口',
                '后端添加获取部门详情接口',
                '添加部门列表页面，支持树形结构展示',
                '添加集成搜索栏功能，支持按部门名称和状态筛选',
                '添加部门弹窗组件，支持新增、编辑和删除操作',
                '添加集成权限控制，支持不同按钮权限管理',
                '前端对接获取部门树状列表接口',
                '前端对接获取部门详情接口',
                '后端添加添加部门接口',
                '前端对接添加部门接口',
                '前端添加部门搜索功能并优化数据过滤',
                '添加部门更新接口',
                '添加部门删除接口',
                '添加部门异常以及部门异常处理',

            ],
            changed: [
                '优化前端部门管理表单结构和样式',
            ],
            fixed: [
                '修复前端角色状态选项值映射错误',
                '修复部门名称列宽度问题',
                '修复前端部门树展开情况，搜索后，点击重置，表单还处于展开状态 bug',

            ],
            demo: [
                '添加测试部门用于测试',
                '修改测试部门用于测试',
                '删除测试部门用于测试'
            ]
        }
    },
    {
        version: 'v0.1.5-alpha',
        title: '小部分优化重构',
        date: '2026-1-2',
        detail: {
            added: [
                '前端添加API加密配置',
                '前端添加API请求响应加密功能',
                '前端登录页面添加系统版权信息显示',

            ],
            changed: [
                '调整前端开发环境配置',
                '统一前端API封装函数命名规范',
                '重构前端API封装',
                '调整前端API函数调用',
                '重构数据库部分数据表',
                '优化数据库'
            ],
        },
        remark: '本版本更新内容少，核心主要整理开发思路，调整规范后续开发计划'
    },
    {
        version: 'v0.1.4-alpha',
        title: '角色管理',
        date: '2026-1-1',
        detail: {
            added: [
                '添加菜单层级变化时的路径修正功能',
                '添加内嵌模式支持并优化表单校验规则',
                '添加查询角色列表接口',
                '前端添加角色管理相关接口封装',
                '后端添加相关的枚举类',
                '前端添加角色删除功能对接',
                '前端添加角色批量删除功能对接',
                '前端添加角色管理权限控制功能',

            ],
            changed: [
                '优化分页结果返回逻辑',
                '优化后端角色数据处理',
                '重构前端角色管理模块的数据结构和API接口',
                '重构优化后端结构和类',
                '优化角色管理功能',
                '更新前端角色编辑对话框功能',
                '优化后端角色管理逻辑',

            ],
            fixed: [
                '修复菜单路由机制出现布局重复或者直接500 bug',
                '修复角色查询中删除标志的过滤逻辑',
                '角色管理后端接口功能完善',

            ],
            demo: [
                '添加三个测试角色数据用于测试',
                '单独删除测试角色测试',
                '批量删除测试角色测试'
            ]
        },
        remark: '角色权限分配功能并未完善，需要在后续权限管理功能完善后，再进行添加完善'
    },
    {
        version: 'v0.1.3-alpha',
        title: '菜单管理优化',
        date: '2025-12-31',
        detail: {
            added: [
                '后端添加菜单权限控制注解',
                '后端添加菜单排序接口',
                '前端添加菜单拖拽排序功能',
                '前端添加图标选择器组件',
                '添加 RemixIcon 图标数据',
                '添加 MaterialIconTheme 图标数据',
                '添加 RivetIcons 图标数据',
                '添加 StreamlinePlumpColor 图标数据'
            ],
            changed: [
                '移出异常页面目录以及其相关子菜单',
                '重构前端异常页面组件位置并更新类型声明',
                '前端菜单管理目录添加子菜单操作优化',
                '优化菜单拖拽排序功能',
                '优化拖拽树节点组件实现',
                '优化菜单拖拽排序功能逻辑',
                '优化前端添加/修改菜单弹窗表单',
                '优化前端菜单管理UI',
                '重构图标选择器组件结构',
            ],
            fixed: [
                '修复前端菜单管理中修改外链菜单还必须要填路由地址bug',
                '修复菜单删除功能，禁止删除包含子菜单的菜单项',
                '修复菜单拖拽排序时候定位条出现混乱bug',
                '修复菜单管理拖拽模式提示样式不支持主题色更改bug'
            ]
        }
    },
    {
        version: 'v0.1.2-alpha',
        title: '菜单管理',
        date: '2025-12-30',
        detail: {
            added: [
                '添加默认用户权限组',
                '添加默认管理权限组',
                '添加令牌刷新接口到白名单',
                '添加获取用户会话时，进行懒清理机制',
                '添加系统菜单查询接口和数据转换功能',
                '扩展BaseController功能并添加分页支持',
                '新增菜单树形结构查询接口 getMenuTree',
                '新增菜单平铺列表查询接口 getMenuList',
                '添加批量删除菜单功能',
                '添加菜单类型、状态等查询条件支持',
                '添加菜单分页查询功能',
                '添加前端权限指令',
                '添加菜单异常处理机制',

            ],
            changed: [
                '重构删除前端不必要的页面',
                '重构数据库',
                '优化JWT令牌刷新机制',
                '重构菜单API模块结构',
                '实现菜单详情查询、新增、更新、删除功能',
                '完善系统菜单管理功能',
                '优化菜单业务逻辑',
                '重构前端菜单模块类型定义和图标显示',
                '优化菜单搜索功能中的标题匹配逻辑',
                '优化前端菜单管理中时间显示格式',
                '修复后端字段跟数据库表字段不匹配问题',
                '实现前端v-permission 指令支持权限验证、通配符和黑名单功能',
                '优化后端权限组实体结构和查询逻辑',
                '移除前端旧版权限指令并更新组件使用新权限系统',
                '移出不必要的菜单',
                '调整菜单类型定义，废除 "按钮型菜单" 概念',
                '重构权限验证系统',
                '完善菜单管理API接口和类型定义',
                '优化菜单管理',
                '完善优化基础菜单管理功能',

            ],
            fixed: [
                '修复前端不自动刷新accessToken bug',
                '修复欢迎信息显示问题',
                '修复后端refreshToken刷新时候一直保留旧的，并且永远不会失效bug',
                '修复refreshToken过期，redis依旧存在用户会话对应的 tokenId bug',
                '修复删除菜单时的标题显示问题 (并且支持i18n)',
                '修复前端顶级目录中无子菜单，点击后布局出现bug',

            ],
        },
        remark: '目录本身不是功能，如果目录下没有任何「可见菜单」，那么这个目录就不该显示',
        breakingChange: true
    },
    {
        version: 'v0.1.1-alpha',
        title: '后端菜单数据，前端动态路由',
        date: '2025-12-29',
        detail: {
            added: [
                '添加 sys_menu 数据表',
                '添加 sys_menu_auth 数据表',
                '初始化系统菜单数据并更新表结构',
                '重构VO规范',
                '添加系统路由服务接口和实现',
                '添加系统角色和用户权限相关实体类',
                '添加用户各种相关的数据实体与权限组关联',
                '添加退出登录功能',
                '增加权限组查询的DEBUG日志调试',
                '添加用户独立权限功能',
                '前端添加 Token 刷新机制'
            ],
            changed: [
                '将用户部门ID改为部门列表支持多部门',
                '优化SQL日志输出',
                '实现用户部门关联功能',
                '前端对接退出登录',
                '实现权限组功能并优化数据源配置',
                '更新数据库结构以支持部门权限组关联',
                '实现用户权限查询逻辑',
                '优化用户权限查询逻辑',
                '重构系统路由服务接口和实现',
                '后端更新路由组件数据结构和映射逻辑',
                '后端完成菜单路由接口（根据权限来筛选计算获取菜单路由）',
                '移出数据表sys_menu_auth',
                '移除前端路由模式支持',
                '优化菜单处理逻辑',
                '优化前端菜单排列顺序',
                '删除前端静态路由各种模块',
                '优化前端逻辑',
            ],
            fixed: [
                '修复SQL拦截器没有根据配置属性判断是否启用bug',
                '修复拦截器未读取配置属性 ，现在会根据 sql-beautify-enabled、sql-log-enabled、slow-sql-enabled 配置来决定是否输出日志',
                '修复前端小部分类型错误bug',
            ],

        },
        breakingChange: true,
        remark: '前端部分重构路由机制，路由由后端根据权限计算动态生成'
    },
    {
        version: 'v0.1.0-alpha',
        title: '前端对接登录认证',
        date: '2025-12-27',
        detail: {
            added: [
                '添加定时任务核心封装模块(预留，待完成)',
                '添加集成 MapStruct 对象转换框架',
                '添加验证码异常处理',
                '添加登录接口（兼容全平台账号密码登录方式）',
                '添加刷新token接口',
                '添加退出登录接口',
                '添加获取当前用户登录接口',
                '前端添加验证码接口',
                '添加前端登录页面图像验证码',
                '添加开发环境自动填充登录信息功能',
                'BaseController 中添加安全工具类的导入和用户信息获取方法',
                '后端添加预留用户部门信息',
                '后端添加CORS预检请求处理',

            ],
            changed: [
                '移除 junoyi-common 模块',
                '优化验证逻辑',
                '重构登录认证对象结构',
                '前端添加验证码验证',
                '优化登录登出功能',
                '优化认证控制器',
                '重构前端环境配置',
                '移出前端 Vite 代理设置',
                '重构前端登录页面表单数据类型',
                '重构userInfo获取接口',
                '重构 BaseController 基类',
                '重构前端UserInfo解析',
                '重构前端useAuth.ts',
                '重构前端',
                '重构前端用户权限体系',
                '重构前端用户角色验证机制'
            ],
            fixed: [
                '修复账号密码和验证码正确情况下，第一次请求登录失败，第二次请求登录成功的Bug',
                '修复 maven 依赖循环问题',
                '修复获取userinfo接口',
                '修复后端跨域配置出现不生效的bug',
            ],
            demo: [
                '前端开始接口调通测试',

            ]
        },
        breakingChange: true,
        remark: '当前版本后开始正式前后端对接'
    },
  {
    version: 'v0.0.9-alpha',
    title: '权限机制设计',
    date: '2025-12-26',
    detail: {
      added: [
          '添加访问日志功能',
          '添加权限效果、权限类型、权限逻辑符枚举类',
          '在application.yml中添加permission配置项，支持启用权限控制、缓存设置、超级管理员配置和默认权限组',
          '添加@DataScope注解用于数据范围控制',
          '添加FieldPermission注解用于字段权限控制',
          '添加 sys_permission 系统权限节点数据表',
          '添加 sys_perm_group 系统权限组数据表',
          '添加 sys_group_permission 权限组-权限关联数据表',
          '新增 DataScope 注解用于控制数据行级别访问权限',
          '新增 FieldPermission 注解用于控制字段级别读写权限',
          '新增 Permission 注解用于方法权限校验',
          '新增 DataScopeType 枚举定义数据范围类型',
          '在 LoginUser 类中添加权限节点、权限组、部门ID和超级管理员字段',
          '为超级管理员添加权限验证的快捷路径',
          '添加权限测试接口并更新白名单配置',
          '添加了PermissionException基类',
          '新增NoPermissionException和NotLoginException具体异常类',
          '在全局异常处理器中添加对权限相关异常的专门处理',
          '添加 sys_user_group 表用于用户权限组关联  ',
          '添加 sys_user_perm 表用于用户独立权限管理',
          '添加字段权限序列化',
          '添加数据脱敏工具类',
          '添加字段权限功能支持',
          '新增MaskPattern枚举类定义脱敏模式',
      ],

      changed: [
          '优化访问日志拦截器中的IP获取逻辑',
          '移除旧的 PermissionScope 注解',
          '重构权限系统数据库结构',
          '重构权限上下文管理',
          '移除 PermissionContext 和 PermissionContextHolder 类',
          '修改 PermissionHelper 使用 Supplier 模式获取权限信息',
          '更新 TokenAuthenticationTokenFilter 中的用户会话构建逻辑',
          '扩展会话信息包含权限组和部门信息',
          '优化权限匹配器',
          '实现权限模块异常体系',
          '实现@Permission注解功能',
          '删除了 AccessDeniedException 的全局异常处理方法',
          '优化@Permission，去除PermissionType',
          '重构字段权限脱敏功能实现',
          'Permission模块移除DataScope注解和DataScopeType枚举相关代码',
          '重构FieldPermissionBeanSerializerModifier添加日志记录',
          '重构FieldPermissionModule构造函数并添加模块注册日志',
          '重构FieldPermissionSerializer调整权限检查顺序并添加调试日志',
          '修改JacksonConfig使用modulesToInstall方法注册模块',
          '重构MaskUtils工具类mask方法参数类型为MaskPattern',
          '使用Jackson2ObjectMapperBuilderCustomizer注册模块',
          '优化权限配置属性结构',
          '优化权限模块日志输出',
      ],

      fixed: [
          '修复FieldPermissionSerializer 中mask依旧使用String而不是MaskPattern问题',

      ],

      demo: [
          '测试接口权限功能',
          '测试字段权限功能'
      ]
    }
  },
  {
    version: 'v0.0.8-alpha',
    title: '前端重构，后端验证码机制',
    date: '2025-12-25',
    detail: {
      added: [
          '前端JunoYi-Vue-ElementPlus项目重构',
          '修复前端`upgrade.ts`中的字段引用',
          '添加junoyi-framework-captcha验证码验证核心模块',
          '添加图形化验证码',
          '添加不支持验证码类型异常类',
          '添加 AJ-Captcha 依赖',
          '添加滑块验证码背景图尺寸',
          '添加打包maven插件',

      ],
      changed: [
          '升级SpringBoot版本为3.5.0',
          '优化图形验证码生成',
          '优化图形验证码字符生成逻辑',
          '优化图形验证码数学运算模式情况生成逻辑',
          '修改验证码图片数据格式',
          '移出Aj-Captcha依赖',
          '移出其他人机验证机制',
      ],
      fixed: [
        '修复打包后日志模块出现自动配置异常bug',

      ],
      demo: [
          '测试是否正常打包',
      ]
    },
    remark: '移出的人机验证机制：滑动验证、点选验证、行为验证，在后续版本中推出，当前仅支持基础的图形验证（字符/数字运算）'
  },
  {
    version: 'v0.0.7-alpha',
    title: '后端Web模块',
    date: '2025-12-23',
    detail: {
      added: [
          '添加全局异常处理机制',
          '新增全局异常处理器',
          '新增认证异常体系以及全局异常处理',
          '新增 BaseException 支持领域标识和状态码',
          '新增 AuthException 认证异常领域类，继承 BaseException',
          '新增 LoginException 登录异常领域类，扩展认证异常  ',
          '添加登录账户相关异常处理类  ',
          '添加XSS防护过滤器和请求包装器 ',
          '添加XSS过滤器配置选项和多种过滤模式',
          '添加SQL注入增强防护功能',
          '添加跨域配置功能'
      ],
      changed: [
          '重构基础异常类',
          '移除旧的 GlobalException 类 ',
          '完善用户验证逻辑中的异常类型处理  ',
          '优化全局异常处理器功能  ',
          '完成 XSS 过滤功能',
          '优化SQL注入防护',
          '增强SQL注入防护功能',
      ],
      fixed: [
          '修复XSS过滤器配置和过滤模式',
          '修复默认XSS过滤器配置不安全的问题',
      ]

    }
  },
  {
    version: 'v0.0.6-alpha',
    title: '后端Security核心模块',
    date: '2025-12-19',
    detail: {
      added: [
        '添加junoyi-framework-security模块',
        '新增各种基类：BaseController、BaseEntity、BaseException',
        '添加 junoyi-framework-permission 权限机制封装模块',
        '添加系统用户相关实体类和枚举',
        '添加Sa-Token权限认证框架',
        '新增平台类型枚举和用户平台信息实体类',
        '添加 junoyi-framework-boot-starter 模块聚合模式',
        '新增登录用户信息类和令牌刷新功能',
        '添加JWT认证过滤器',
        '添加API加密过滤器',
        '添加令牌处理过程',
        '添加实现JWT Token服务并重构认证流程',
        '实现基于 Redis 的会话管理机制',
        '新增系统认证模块基础结构',
        '更新 SysAuthServiceImpl.java 接口，增加带 platformType 参数的 login 方法',
        '添加支持多平台登录以及Token不同有效期配置',
        '添加IP工具类和Servlet工具类',
        '引入PBKDF2密码加密工具类并优化认证逻辑',
        '添加AES和RSA加密服务及密钥管理',
        '添加实例公钥和私钥',
        '实现API请求响应加解密功能',
        '添加登录失败限制和IP限制功能',
        '添加单点登录功能支持（单点登录、多点登录配置文件切换）'
      ],
      changed: [
        '优化升级JWT库版本并调整配置',
        '优化项目结构',
        '重构Security模块',
        '重构 junoyi-framework-security 模块上下文管理机制',
        '增强日志功能并优化配置',
        '优化移出Sa-Token框架',
        '重构JWT Token服务实现',
        '完善会话管理与认证功能',
        '将MyBatis-Plus版本从3.5.5升级至3.5.7',
        '更新MyBatis-Plus starter依赖为spring-boot3-starter',
        '更新dynamic-datasource starter依赖为spring-boot3-starter',
        '屏蔽MyBatis Mapper扫描日志',
        '优化 RefreshToken 为不透明格式',
        '更新配置文件，区分 admin-web 和 front-web',
        '更新 JwtTokenService 的平台映射',
        '重构优化junoyi-framework-security 模块包结构和命名',
        '引入缓存键常量并优化会话管理',
        '优化登录失败处理逻辑（如果登录失败被锁，还继续尝试登录错误，将刷新锁定冷却时间）'
      ],
      fixed: [
        '修复Spring事件兼容问题，移除SpringApplicationStartingEvent及相关适配器',
        '修复 junoyi-framework-security 模块未加载 TokenHelper Bean 报错',
        '修复日志系统debug无法打印输出日志bug',
        '修复Mybatis-Plus不兼容问题bug',
        '修复因版本不兼容导致的配置问题'
      ]
    }
  },
  {
    version: 'v0.0.5-alpha',
    title: 'Spring事件桥接机制',
    date: '2025-12-16',
    detail: {
      added: [
        '新增Spring事件桥接机制',
        '新增Spring应用事件相关领域事件类',
        '新增 SpringApplicationEvent 类，封装Spring应用事件源和命令行参数',
        '新增 SpringApplicationReadyEvent 类，包含应用上下文和启动耗时信息',
        '新增 SpringApplicationStartingEvent 类，封装引导上下文信息',
        '添加 SpringApplicationReadyEventAdapter 适配器，转换Spring原生ApplicationReadyEvent',
        '添加 SpringApplicationStartingEventAdapter 适配器，转换Spring原生ApplicationStartingEvent',
        '新增SpringApplicationStartedEvent类，封装应用启动完成信息',
        '添加SpringApplicationStartedEventAdapter适配器，转换Spring原生ApplicationStartedEvent',
        '新增SpringContextClosedEvent类，用于表示Spring应用上下文关闭事件',
        '添加SpringContextClosedEventAdapter适配器，将Spring原生ContextClosedEvent转换为系统内部事件',
        '添加SpringApplicationContextEvent类用于封装应用上下文事件',
        '新增SpringContextRefreshedEvent适配器类',
        '新增SpringContextStartedEvent适配器类',
        '新增SpringContextStoppedEvent适配器'
      ],
      changed: [
        '优化Spring事件适配器以支持应用启动参数',
        '优化SpringContextClosedEvent继承SpringApplicationContextEvent'
      ],
      fixed: [
        '修复SpringContextStoppedEventAdapter中判断是否支持指定Spring事件方法'
      ],
      demo: [
        '在TestEventListener中增加监听处理（测试）',
        '测试Spring Context相关事件'
      ]
    },
    remark: 'SpringContextStartedEvent事件（Spring默认正常情况下不会触发该事件）'
  },
  {
    version: 'v0.0.4-alpha',
    title: 'Excel模块与事件机制',
    date: '2025-12-13',
    detail: {
      added: [
        '添加junoyi-framework-excel模块',
        '添加 @CellMerge 单元格合并注解、@ExcelDictFormat字典格式化注解、@ExcelEnumFormat枚举格式化注解',
        '添加ExcelUtils Excel表格静态工具类',
        '添加junoyi-framework-event模块',
        '添加基本事件机制',
        '添加多种事件注册机制（@EventListner注解自动扫描注册、Listener接口手动实现并手动注册）',
        '添加异步事件（异步事件线程池）',
        '添加事件总线触发方法（支持Spring Bean注入或静态方法直接调用，并且同时支持同步事件或异步事件）',
        '添加module-demo模块、module-demo-api模块用测试'
      ],
      changed: [
        '完成 excel 核心基础设置逻辑',
        '优化事件机制',
        '优化服务器端口监听器',
        '增强事件注册于监听功能'
      ],
      fixed: [
        '修复事件监听器事件优先级执行并非按照优先级的bug',
        '修复包名简化逻辑导致日志名称截取问题'
      ]
    }
  },
  {
    version: 'v0.0.3-alpha',
    title: 'JSON模块与Redis模块',
    date: '2025-12-12',
    detail: {
      added: [
        '添加junoyi-framework-json模块',
        '添加jackson配置',
        '添加json静态工具类',
        '添加junoyi-framework-redis模块',
        '添加redisson配置',
        '添加redis key键前缀处理',
        '添加 CacheUtils 缓存静态工具类',
        '添加 QueueUtils 队列静态工具类',
        '添加 RedisUtls 静态工具类，提供基于 Redisson 的 Redis 操作',
        '添加module-system模块、module-system-api模块',
        '添加获取系统信息接口'
      ],
      changed: [
        '优化junoyi-framework-redis模块'
      ]
    }
  },
  {
    version: 'v0.0.2-alpha',
    title: '核心模块与数据源模块',
    date: '2025-12-11',
    detail: {
      added: [
        '添加junoyi-framework-core核心模块',
        '添加常用核心工具类',
        '添加application.yml中junoyi相关配置读取',
        '添加HTTP协议状态常量',
        '添加junoyi-framework-starter 框架启动核心模块',
        '添加junoyi-framework-datasource模块',
        '添加datasource模块中阿里数据池配置',
        '添加多数据源处理',
        '添加SQL美化输出',
        '添加慢SQL'
      ],
      changed: [
        '优化框架启动（隐藏不必要的日志，添加junoyi banner，优化日志输出）',
        '优化datasource模块',
        '优化application.yml配置文件'
      ]
    }
  },
  {
    version: 'v0.0.1-alpha',
    title: '初始化项目框架',
    date: '2025-12-08',
    detail: {
      added: [
        '初始化项目框架',
        '添加日志系统junoyi-framework-log模块',
        '添加日志格式',
        '添加日志文件存储',
        '添加日志静态工具类、日志工厂类',
        '添加日志异步等多种功能更全方法',
        '添加日志配置'
      ],
      changed: [
        '优化日志格式'
      ],
      fixed: [
        '修复日志系统配置未读取应用bug'
      ]
    }
  }
])
