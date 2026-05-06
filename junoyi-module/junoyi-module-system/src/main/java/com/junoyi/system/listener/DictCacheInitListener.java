package com.junoyi.system.listener;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.system.api.SysDictApiImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 字典缓存初始化监听器
 * 在应用启动完成后，预加载所有字典数据到Redis缓存
 *
 * @author Fan
 */
@Component
@RequiredArgsConstructor
public class DictCacheInitListener implements ApplicationListener<ApplicationReadyEvent> {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(DictCacheInitListener.class);
    private final SysDictApiImpl sysDictApi;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("DictCache", "Application started, preloading dictionary cache...");
        
        try {
            sysDictApi.refreshAllDictCache();
            log.info("DictCache", "Dictionary cache preload completed");
        } catch (Exception e) {
            log.error("DictCache", "Dictionary cache preload failed: {}", e.getMessage(), e);
        }
    }
}
