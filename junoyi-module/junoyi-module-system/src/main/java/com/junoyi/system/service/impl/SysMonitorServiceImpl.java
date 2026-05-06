package com.junoyi.system.service.impl;

import com.junoyi.framework.core.properties.JunoYiProperties;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.system.domain.vo.*;
import com.junoyi.system.service.ISysConfigService;
import com.junoyi.system.service.ISysMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringBootVersion;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 系统监控服务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysMonitorServiceImpl implements ISysMonitorService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(this.getClass());
    private final JunoYiProperties junoYiProperties;
    private final ISysConfigService configService;

    @Override
    public SystemMonitorVO getSystemMonitorInfo() {
        return SystemMonitorVO.builder()
                .systemInfo(getSystemBasicInfo())
                .serverInfo(getServerInfo())
                .javaInfo(getJavaInfo())
                .memoryInfo(getMemoryInfo())
                .diskInfo(getDiskInfo())
                .build();
    }

    /**
     * 获取系统基本信息
     */
    private SystemBasicInfoVO getSystemBasicInfo() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long startTime = runtimeMXBean.getStartTime();
        long uptime = runtimeMXBean.getUptime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTimeStr = sdf.format(new Date(startTime));
        String uptimeStr = formatUptime(uptime);

        // 获取Spring Boot版本
        String springBootVersion = SpringBootVersion.getVersion();
        
        // 从配置中获取系统名称
        String systemName = configService.getConfigByKey("sys.system.name");
        if (systemName == null || systemName.isEmpty()) {
            systemName = "JunoYi 企业级开发框架";
        }
        
        // 获取系统版本
        String systemVersion = junoYiProperties.getVersion();
        if (systemVersion == null || systemVersion.isEmpty()) {
            systemVersion = "1.0.0";
        }

        return SystemBasicInfoVO.builder()
                .name(systemName)
                .version(systemVersion)
                .frameworkVersion("Spring Boot " + (springBootVersion != null ? springBootVersion : "Unknown"))
                .environment(getEnvironment())
                .startTime(startTimeStr)
                .uptime(uptimeStr)
                .build();
    }

    /**
     * 获取服务器信息
     */
    private ServerInfoVO getServerInfo() {
        String hostName = "Unknown";
        String hostAddress = "Unknown";
        
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            hostName = localHost.getHostName();
            hostAddress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            log.error("Failed to get host information", e);
        }

        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");
        int cpuCores = Runtime.getRuntime().availableProcessors();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        return ServerInfoVO.builder()
                .name(hostName)
                .os(osName + " " + osVersion)
                .arch(osArch)
                .cpuCores(cpuCores)
                .ip(hostAddress)
                .time(currentTime)
                .build();
    }

    /**
     * 获取Java信息
     */
    private JavaInfoVO getJavaInfo() {
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        String javaHome = System.getProperty("java.home");
        String jvmName = System.getProperty("java.vm.name");
        String jvmVersion = System.getProperty("java.vm.version");

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> inputArguments = runtimeMXBean.getInputArguments();
        String args = String.join(" ", inputArguments);

        return JavaInfoVO.builder()
                .version(javaVersion)
                .vendor(javaVendor)
                .home(javaHome)
                .jvmName(jvmName)
                .jvmVersion(jvmVersion)
                .args(args)
                .build();
    }

    /**
     * 获取内存信息
     */
    private MemoryInfoVO getMemoryInfo() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

        long jvmMax = heapMemoryUsage.getMax();
        long jvmUsed = heapMemoryUsage.getUsed();
        long jvmFree = jvmMax - jvmUsed;
        int jvmUsedPercent = (int) ((double) jvmUsed / jvmMax * 100);

        // 系统内存信息（通过Runtime获取）
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        int usedPercent = (int) ((double) usedMemory / totalMemory * 100);

        return MemoryInfoVO.builder()
                .total(formatBytes(totalMemory))
                .used(formatBytes(usedMemory))
                .free(formatBytes(freeMemory))
                .usedPercent(usedPercent)
                .jvmTotal(formatBytes(jvmMax))
                .jvmUsed(formatBytes(jvmUsed))
                .jvmFree(formatBytes(jvmFree))
                .jvmUsedPercent(jvmUsedPercent)
                .build();
    }

    /**
     * 获取磁盘信息
     */
    private List<DiskInfoVO> getDiskInfo() {
        List<DiskInfoVO> diskInfoList = new ArrayList<>();
        
        File[] roots = File.listRoots();
        for (File root : roots) {
            long total = root.getTotalSpace();
            long free = root.getFreeSpace();
            long used = total - free;
            int usedPercent = total > 0 ? (int) ((double) used / total * 100) : 0;

            DiskInfoVO diskInfo = DiskInfoVO.builder()
                    .path(root.getAbsolutePath())
                    .type("Unknown") // Java无法直接获取文件系统类型
                    .total(formatBytes(total))
                    .used(formatBytes(used))
                    .free(formatBytes(free))
                    .usedPercent(usedPercent)
                    .build();
            
            diskInfoList.add(diskInfo);
        }
        
        return diskInfoList;
    }

    /**
     * 格式化字节大小
     */
    private String formatBytes(long bytes) {
        if (bytes < 0) {
            return "Unknown";
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        double kb = 1024.0;
        double mb = kb * 1024;
        double gb = mb * 1024;
        double tb = gb * 1024;

        if (bytes >= tb) {
            return df.format(bytes / tb) + " TB";
        } else if (bytes >= gb) {
            return df.format(bytes / gb) + " GB";
        } else if (bytes >= mb) {
            return df.format(bytes / mb) + " MB";
        } else if (bytes >= kb) {
            return df.format(bytes / kb) + " KB";
        } else {
            return bytes + " B";
        }
    }

    /**
     * 格式化运行时长
     */
    private String formatUptime(long uptimeMillis) {
        Duration duration = Duration.ofMillis(uptimeMillis);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("天 ");
        }
        if (hours > 0 || days > 0) {
            sb.append(hours).append("小时 ");
        }
        sb.append(minutes).append("分钟");

        return sb.toString();
    }

    /**
     * 获取运行环境
     */
    private String getEnvironment() {
        String env = System.getProperty("spring.profiles.active");
        if (env == null || env.isEmpty()) {
            env = "default";
        }
        
        // 转换为友好的显示名称
        switch (env.toLowerCase()) {
            case "prod":
            case "production":
                return "Production";
            case "dev":
            case "development":
                return "Development";
            case "test":
                return "Test";
            case "local":
                return "Local";
            default:
                return env;
        }
    }
}
