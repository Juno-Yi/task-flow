package com.junoyi.notification.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.notification.domain.dto.NotificationPublishLogQueryDTO;
import com.junoyi.notification.domain.vo.NotificationPublishLogVO;
import com.junoyi.notification.service.INotificationPublishLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知发布日志控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/notification/publish-log")
@RequiredArgsConstructor
public class NotificationPublishLogController extends BaseController {

    private final INotificationPublishLogService notificationPublishLogService;

    /**
     * 获取通知发布日志列表（分页）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "notification.ui.publish-log.view"
    )
    public R<PageResult<NotificationPublishLogVO>> getList(NotificationPublishLogQueryDTO queryDTO){
        return R.ok();
    }



}