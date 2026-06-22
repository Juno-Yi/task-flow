package com.junoyi.notification.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 我的通知VO
 *
 * @author Fan
 */
@Data
public class MyNotificationVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知概况
     */
    private String summary;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型
     */
    private Integer type;

    /**
     *  通知类型标签（字典翻译）
     */
    private String typeLabel;

    /**
     * 通知类型的tip标签类型（字典翻译）
     */
    private String typeType;

    /**
     * 是否阅读
     */
    private Boolean read;

    /**
     * 阅读时间
     */
    private Date readTime;

    /**
     * 谁发布的（发布者昵称或"系统"）
     */
    private String publishedBy;

    /**
     * 发布时间
     */
    private Date publishedAt;

}