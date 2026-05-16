package com.junoyi.project.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 项目删除DTO（需要密码验证）
 *
 * @author Fan
 */
@Data
public class ProjectDeleteDTO {

    /**
     * 项目ID列表
     */
    private List<Long> ids;

    /**
     * 密码
     */
    private String password;
}
