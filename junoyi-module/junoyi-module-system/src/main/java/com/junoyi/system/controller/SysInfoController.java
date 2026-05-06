package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.system.domain.vo.SystemInfoVO;
import com.junoyi.system.domain.vo.SystemMonitorVO;
import com.junoyi.system.service.ISysConfigService;
import com.junoyi.system.service.ISysMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;

/**
 * 系统信息控制类
 *
 * @author Fan
 */
@Tag(name = "系统信息管理")
@RestController
@RequestMapping("/system/info")
@RequiredArgsConstructor
public class SysInfoController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysInfoController.class);
    private final ISysConfigService configService;
    private final ISysMonitorService monitorService;

    /**
     * 获取系统信息（从配置中读取）
     */
    @Operation(summary = "获取系统信息")
    @GetMapping
    public R<SystemInfoVO> getSystemInfo() {
        SystemInfoVO systemInfoVo = SystemInfoVO.builder()
                .name(configService.getConfigByKey("sys.system.name"))
                .version(configService.getConfigByKey("sys.system.version"))
                .copyrightYear(configService.getConfigByKey("sys.system.copyrightYear"))
                .copyright(configService.getConfigByKey("sys.system.copyright"))
                .registration(configService.getConfigByKey("sys.system.registration"))
                .logo(configService.getConfigByKey("sys.system.logo"))
                .build();
        return R.ok(systemInfoVo);
    }

    /**
     * 获取系统监控信息
     */
    @Operation(summary = "获取系统监控信息")
    @GetMapping("/monitor")
    public R<SystemMonitorVO> getSystemMonitor() {
        SystemMonitorVO monitorInfo = monitorService.getSystemMonitorInfo();
        return R.ok(monitorInfo);
    }

    /**
     * 获取LOGO图片
     * 直接将图片响应
     */
    @Operation(summary = "获取系统Logo")
    @GetMapping("/logo")
    public ResponseEntity<byte[]> getLogo() {
        try {
            // 从 classpath 的 public 目录读取 logo
            ClassPathResource logoResource = new ClassPathResource("public/LOGO.png");
            if (!logoResource.exists()) {
                log.error("Logo file does not exist: public/LOGO.png");
                return ResponseEntity.notFound().build();
            }

            byte[] bytes = StreamUtils.copyToByteArray(logoResource.getInputStream());

            String contentType = "image/png";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(bytes);

        } catch (IOException e) {
            log.error("Failed to read Logo file", e);
            return ResponseEntity.status(500).build();
        }
    }
}