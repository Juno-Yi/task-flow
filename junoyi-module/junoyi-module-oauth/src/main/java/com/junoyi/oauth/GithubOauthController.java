package com.junoyi.oauth;

import com.junoyi.framework.web.domain.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Github Oauth 控制器
 *
 * @author
 */
@RestController
@RequestMapping("/auth/github")
@RequiredArgsConstructor
public class GithubOauthController extends BaseController {
}