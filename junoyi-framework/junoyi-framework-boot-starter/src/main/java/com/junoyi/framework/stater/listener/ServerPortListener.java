package com.junoyi.framework.stater.listener;

import com.junoyi.framework.log.core.JunoYiLogStatic;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
* 服务器端口监听器
* 在服务器启动时候，打印服务器端口、访问地址等信息
*
* @author Fan
*/
@Component
public class ServerPortListener implements ApplicationListener<ApplicationEvent> {

   private volatile Integer port;
   private volatile String protocol;
   private volatile boolean printed;

    /**
     * 处理应用程序事件回调方法
     * 当Web服务器初始化完成或应用程序启动完成时执行相应逻辑
     *
     * @param event 应用程序事件对象，不能为空
     */
   @Override
   public void onApplicationEvent(@NonNull ApplicationEvent event) {
       if (event instanceof WebServerInitializedEvent webServerInitializedEvent) {
           this.port = webServerInitializedEvent.getWebServer().getPort();
           this.protocol = webServerInitializedEvent.getApplicationContext().getEnvironment()
                   .getProperty("server.ssl.enabled", "false").equals("true") ? "https" : "http";
           return;
       }

       if (event instanceof ApplicationStartedEvent) {
           if (printed || port == null || protocol == null) {
               return;
           }
           printed = true;

           try {
               String hostName = InetAddress.getLocalHost().getHostName();
               String hostAddress = getLocalHostAddress();

               JunoYiLogStatic.info("Local:      {}://localhost:{}", protocol, port);
               JunoYiLogStatic.info("External:   {}://{}:{}", protocol, hostAddress, port);
               JunoYiLogStatic.info("Host Name:  " + hostName);
           } catch (Exception e) {
               // 无法获取主机地址错误处理
               JunoYiLogStatic.warn("Unable to determine host address: {}", e.getMessage());
               JunoYiLogStatic.info("Application is running...");
               JunoYiLogStatic.info("Access URLs:");
               JunoYiLogStatic.info("Local:      {}://localhost:{}", protocol, port);
           }
       }
   }

   /**
    * 获取本机真实IP地址(非127.0.0.1)
    * 优先返回IPv4地址,排除回环地址和虚拟网卡
    */
   private String getLocalHostAddress() throws SocketException, UnknownHostException {
       // 遍历所有网络接口
       Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
       while (networkInterfaces.hasMoreElements()) {
           NetworkInterface networkInterface = networkInterfaces.nextElement();
           
           // 跳过回环接口、虚拟接口和未启用的接口
           if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
               continue;
           }
           
           Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
           while (addresses.hasMoreElements()) {
               InetAddress address = addresses.nextElement();
               
               // 优先返回IPv4地址,排除回环地址和链路本地地址
               if (!address.isLoopbackAddress() && !address.isLinkLocalAddress() && address.isSiteLocalAddress()) {
                   return address.getHostAddress();
               }
           }
       }
       
       // 如果没有找到合适的地址,返回默认地址
       return InetAddress.getLocalHost().getHostAddress();
   }
}
