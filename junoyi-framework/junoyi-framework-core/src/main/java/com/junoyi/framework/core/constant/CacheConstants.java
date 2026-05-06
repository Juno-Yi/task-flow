package com.junoyi.framework.core.constant;

/**
 * 缓存键常量
 *
 * @author Fan
 */
public interface CacheConstants {

    /**
     * 缓存键前缀
     */
    String PREFIX = "junoyi:";

    // ==================== 登录相关 ====================

    /**
     * 平台登录失败次数
     * 完整 Key: junoyi:login:fail:{platformType}:{username}
     * Value: 失败次数
     * TTL: 冷却时间
     */
    String LOGIN_FAIL = PREFIX + "login:fail:";

    /**
     * 平台登录错误IP限制模式
     * 完整 Key: junoyi:login:fail:ip:{ip}
     * Value: 失败次数
     * TTL: ip冷却时间
     */
    String LOGIN_FAIL_IP = PREFIX + "login:fail:ip:";

    // ==================== 会话相关 ====================

    /**
     * RefreshToken 有效性标记
     * 完整 Key: junoyi:refresh:{tokenId}
     * Value: 用户ID，用于验证 RefreshToken 是否有效、是否被撤销
     */
    String REFRESH_TOKEN = PREFIX + "refresh:";

    /**
     * 用户会话信息
     * 完整 Key: junoyi:session:{tokenId}
     * Value: UserSession 对象，包含完整用户信息、权限、角色、过期时间等
     */
    String SESSION = PREFIX + "session:";

    /**
     * 用户的所有会话列表
     * 完整 Key: junoyi:user:sessions:{userId}
     * Value: Set<String> 集合，存储该用户所有的 tokenId，用于"踢人下线"、"查看在线设备"
     */
    String USER_SESSIONS = PREFIX + "user:sessions:";

    // ==================== 字典相关 ====================

    /**
     * 字典数据缓存
     * 完整 Key: junoyi:dict:{dictType}
     * Value: List<SysDictDataVO> 字典数据列表
     * TTL: 永久(手动刷新)
     */
    String DICT_DATA = PREFIX + "dict:";

    /**
     * 字典标签缓存
     * 完整 Key: junoyi:dict:label:{dictType}:{dictValue}
     * Value: String 字典标签
     * TTL: 永久(手动刷新)
     */
    String DICT_LABEL = PREFIX + "dict:label:";

    /**
     * 字典值缓存
     * 完整 Key: junoyi:dict:value:{dictType}:{dictLabel}
     * Value: String 字典值
     * TTL: 永久(手动刷新)
     */
    String DICT_VALUE = PREFIX + "dict:value:";
}
