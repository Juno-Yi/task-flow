package com.junoyi.notification.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.notification.domain.dto.NotificationDTO;
import com.junoyi.notification.domain.vo.NotificationListVO;
import com.junoyi.notification.service.INotificationManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知管理控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationManageController extends BaseController {

    private final INotificationManageService notificationManagerService;

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

    /**
     * 添加通知（立即发布或者存储草稿）
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "notification.ui.manage.publish.button"
    )
    public R<Void> addNotification(@RequestBody NotificationDTO dto){
        notificationManagerService.addNotification(dto);
        return R.ok();
    }
}