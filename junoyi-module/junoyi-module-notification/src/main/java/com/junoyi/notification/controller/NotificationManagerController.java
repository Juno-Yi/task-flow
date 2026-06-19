package com.junoyi.notification.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.notification.domain.vo.NotificationListVO;
import com.junoyi.notification.service.INotificationManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知管理控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationManagerController extends BaseController {

    private final INotificationManagerService notificationManagerService;

    /**
     * 获取通知列表
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "notification.ui.manage.view"
    )
    public R<PageResult<NotificationListVO>> getList(){
        return R.ok(notificationManagerService.getNotificationList(buildPage()));
    }
}