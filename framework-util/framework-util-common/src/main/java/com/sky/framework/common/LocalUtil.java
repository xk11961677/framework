package com.sky.framework.common;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


/**
 * 本地资源工具类
 *
 * @author
 */
public final class LocalUtil {

    public static final String LOCAL_IP = getLocalIp();

    private LocalUtil() {
    }

    /**
     * 获取本机ip地址
     * 此方法为重量级的方法，不要频繁调用
     *
     * @return
     */
    public static String getLocalIp() {
        try {
            //根据网卡取本机配置的IP
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            String ip = null;
            a:
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ipObj = ips.nextElement();
                    if (ipObj.isSiteLocalAddress()) {
                        ip = ipObj.getHostAddress();
                        break a;
                    }
                }
            }
            return ip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
