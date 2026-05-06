package com.junoyi.system.listener;

import com.junoyi.framework.event.annotation.EventHandler;
import com.junoyi.framework.event.annotation.EventListener;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.system.event.ConfigChangedEvent;
import lombok.RequiredArgsConstructor;

/**
 * 系统参数配置变更事件监听器
 * <p>
 * 监听配置变更事件，可以在此处理配置变更后的业务逻辑，例如：
 * - 记录配置变更历史
 * - 通知其他服务配置已变更
 * - 触发相关业务逻辑
 * </p>
 *
 * @author Fan
 */
@EventListener
@RequiredArgsConstructor
public class ConfigChangedListener {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(ConfigChangedListener.class);

    /**
     * 监听配置变更事件
     * <p>
     * 使用 @EventHandler(async = true) 异步处理，避免阻塞主流程
     * </p>
     *
     * @param event 配置变更事件
     */
    @EventHandler(async = true)
    public void onConfigChanged(ConfigChangedEvent event) {
        try {
            log.info("ConfigChanged",
                    "配置变更: key={}, type={}, operator={}, oldValue={}, newValue={}",
                    event.getConfigKey(),
                    event.getOperationType(),
                    event.getOperator(),
                    event.getOldValue(),
                    event.getNewValue()
            );

            // TODO: 在此处添加配置变更后的业务逻辑
            // 例如：
            // 1. 记录配置变更历史到数据库
            // 2. 发送通知给管理员
            // 3. 触发相关业务逻辑（如配置变更后需要重新加载某些资源）
            // 4. 通过消息队列通知其他微服务

        } catch (Exception e) {
            log.error("ConfigChanged", "处理配置变更事件失败: " + e.getMessage(), e);
        }
    }
}
