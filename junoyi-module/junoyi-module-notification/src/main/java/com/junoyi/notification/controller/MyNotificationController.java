package com.junoyi.notification.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.notification.domain.vo.MyNotificationDetailVO;
import com.junoyi.notification.domain.vo.MyNotificationVO;
import com.junoyi.notification.service.IMyNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  我的通知控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/notification/my-notification")
@RequiredArgsConstructor
public class MyNotificationController extends BaseController {

    private final IMyNotificationService myNotificationService;

    /**
     * 获取我的通知消息列表
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<MyNotificationVO>> getMyNotificationList(){
        Long currentUserId = getUserId();
        if (currentUserId == null || currentUserId == 0)
            return R.fail("非法请求");
        return R.ok(myNotificationService.getMyNotificationList(currentUserId, buildPage()));
    }

    /**
     * 获取我的通知详情（同时标记为已读）
     */
    @GetMapping("/detail/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<MyNotificationDetailVO> getMyNotificationDetail(@PathVariable("id") Long notificationId){
        Long currentUserId = getUserId();
        if (currentUserId == null || currentUserId == 0)
            return R.fail("非法请求");
        return R.ok(myNotificationService.getMyNotificationDetail(currentUserId, notificationId));
    }

    /**
     * 全部标记为已读
     */
    @PostMapping("/mark-all-read")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Integer> markAllAsRead(){
        Long currentUserId = getUserId();
        if (currentUserId == null || currentUserId == 0)
            return R.fail("非法请求");
        int count = myNotificationService.markAllAsRead(currentUserId);
        return R.ok(count);
    }
}
