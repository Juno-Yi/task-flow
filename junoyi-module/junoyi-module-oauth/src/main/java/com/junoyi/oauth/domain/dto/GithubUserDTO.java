package com.junoyi.oauth.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * GitHub 用户信息 DTO
 * @author Echo
 */
@Data
public class GithubUserDTO {

    /**
     * GitHub 用户 ID
     */
    private Long id;

    /**
     * GitHub 用户名
     */
    private String login;

    /**
     * 用户昵称
     */
    private String name;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户头像
     */
    @JsonProperty("avatar_url")
    private String avatarUrl;

    /**
     * 用户简介
     */
    private String bio;

    /**
     * 用户主页
     */
    @JsonProperty("html_url")
    private String htmlUrl;

    /**
     * 公司
     */
    private String company;

    /**
     * 位置
     */
    private String location;
}

