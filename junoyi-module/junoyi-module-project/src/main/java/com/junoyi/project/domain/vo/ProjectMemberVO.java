package com.junoyi.project.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 项目成员VO
 *
 * @author Fan
 */
@Data
public class ProjectMemberVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 项目角色
     */
    private String role;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 加入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date joinTime;

    /**
     * 离开时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leaveTime;
}
