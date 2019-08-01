/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.common;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP工具类
 *
 * @author
 */
public final class IpUtils {

    private static final String REGEX = "((25[0-5]|2[0-4]\\d|1\\d{2}|0?[1-9]\\d|0?0?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|0?[1-9]\\d|0?0?\\d)";

    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private static final int ONE = 1;

    private static final int TWO = 2;

    private static final int THREE = 3;

    private static final int FOUR = 4;

    private static final int ZERO = 0;

    private static final int ONE_BYTE_LENGTH = 8;

    private static final int TWO_BYTE_LENGTH = 16;

    private static final int THREE_BYTE_LENGTH = 24;
    private static final Long MAX_ADDRESS_NUM = 4294967295L;
    private static final int OX00FFFFFF = 0x00ffffff;
    private static final int OX0000FFFF = 0x0000ffff;
    private static final int OX000000FF = 0x000000ff;

    private static final Long A_LOCAL_START = 167772160L;
    private static final Long A_LOCAL_END = 184549375L;
    private static final Long B_LOCAL_START = 2886729728L;
    private static final Long B_LOCAL_END = 2887778303L;
    private static final Long C_LOCAL_START = 3232235520L;
    private static final Long C_LOCAL_END = 3232301055L;

    private IpUtils() {
    }

    /**
     * 获得网页请求IP
     *
     * @param request
     * @return 返回客户端浏览器IP，如果无法获取争取IP则返回NULL
     */
    public static String getClientIp(HttpServletRequest request) {
        //浏览器IP判断
        String ipArray = request.getHeader("x-forwarded-for");
        if (ipArray == null || ipArray.length() == 0 || "unknown".equalsIgnoreCase(ipArray)) {
            ipArray = request.getHeader("Proxy-Client-IP");
        }
        if (ipArray == null || ipArray.length() == 0 || "unknown".equalsIgnoreCase(ipArray)) {
            ipArray = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipArray == null || ipArray.length() == 0 || "unknown".equalsIgnoreCase(ipArray)) {
            ipArray = request.getRemoteAddr();
        }
        if (ipArray != null) {
            String browserIp = ipArray.split(",")[0];
            //容错，如果IP错误，不走IP判断
            if (IpUtils.checkIpStr(browserIp)) {
                return browserIp;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String getClientIpChain(HttpServletRequest request) {
        //浏览器IP判断
        String ipArray = request.getHeader("x-forwarded-for");
        if (ipArray == null || ipArray.length() == 0 || "unknown".equalsIgnoreCase(ipArray)) {
            ipArray = request.getHeader("Proxy-Client-IP");
        }
        if (ipArray == null || ipArray.length() == 0 || "unknown".equalsIgnoreCase(ipArray)) {
            ipArray = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipArray == null || ipArray.length() == 0 || "unknown".equalsIgnoreCase(ipArray)) {
            ipArray = request.getRemoteAddr();
        }
        return ipArray;
    }

    /**
     * 获得网页请求IP.
     * 严格的使用方式,将不会使用request.getRemoteAddr()方法作为最后选择.
     *
     * @param request
     * @return
     */
    public static String getStrictClientIp(HttpServletRequest request) {
        //浏览器IP判断
        String ipArray = request.getHeader("x-forwarded-for");
        if (ipArray == null || ipArray.length() == 0 || "unknown".equalsIgnoreCase(ipArray)) {
            ipArray = request.getHeader("Proxy-Client-IP");
        }
        if (ipArray == null || ipArray.length() == 0 || "unknown".equalsIgnoreCase(ipArray)) {
            ipArray = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipArray != null) {
            String browserIp = ipArray.split(",")[0];
            //容错，如果IP错误，不走IP判断
            if (IpUtils.checkIpStr(browserIp)) {
                return browserIp;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 根据点分式IP地址返回十进制IP数值
     *
     * @param ip 点分式IP地址
     * @return 点分式IP地址对应的32位二进制数的十进制格式
     */
    public static long ipToLongNum(String ip) {
        if (checkIpStr(ip)) {
            String[] ipStrArray = ip.split("\\.");
            Long[] ipLongArray = new Long[FOUR];
            for (int i = 0; i < ipStrArray.length; i++) {
                ipLongArray[i] = Long.parseLong(ipStrArray[i]);
            }
            return (ipLongArray[ZERO] << THREE_BYTE_LENGTH) + (ipLongArray[ONE] << TWO_BYTE_LENGTH) + (ipLongArray[TWO] << ONE_BYTE_LENGTH) + ipLongArray[THREE];
        } else {
            throw new IllegalArgumentException("点分式IP地址格式错误！");
        }
    }

    /**
     * 根据十进制IP数值返回点分式IP地址
     *
     * @param addrNum 十进制IP数值
     * @return 点分式IP地址
     */
    public static String longNumToIp(Long addrNum) {
        if (addrNum >= 0L && addrNum <= MAX_ADDRESS_NUM) {
            Long[] ipLongArray = new Long[FOUR];
            ipLongArray[ZERO] = addrNum >> THREE_BYTE_LENGTH;
            ipLongArray[ONE] = (addrNum & OX00FFFFFF) >> TWO_BYTE_LENGTH;
            ipLongArray[TWO] = (addrNum & OX0000FFFF) >> ONE_BYTE_LENGTH;
            ipLongArray[THREE] = addrNum & OX000000FF;
            return org.apache.commons.lang.StringUtils.join(ipLongArray, '.');
        } else {
            throw new IllegalArgumentException("十进制IP数值超出IP范围！");
        }
    }

    /**
     * 获得本机局域网IP，兼容linux,未取到返回NULL.<br/>
     * <note>本方法资源消耗较重,在大量反复调用时,会比较耗时.</note>
     *
     * @return
     */
    public static String getLocalIp() {
        return LocalUtils.getLocalIp();
    }

    /**
     * 判断点分式IPV4格式是否正确
     *
     * @param ipAddress
     * @return
     */
    public static boolean checkIpStr(String ipAddress) {
        Matcher m = PATTERN.matcher(ipAddress);
        return m.matches();
    }

    /**
     * 判断点分式IP地址是否是内网IP
     *
     * @param ipAddress
     * @return
     */
    public static boolean isLocalIp(String ipAddress) {
        if (!checkIpStr(ipAddress)) {
            return false;
        } else {
            Long ip = ipToLongNum(ipAddress);
            boolean isLocalA = (ip >= A_LOCAL_START && ip <= A_LOCAL_END);
            boolean isLocalB = (ip >= B_LOCAL_START && ip <= B_LOCAL_END);
            boolean isLocalC = (ip >= C_LOCAL_START && ip <= C_LOCAL_END);
            if (isLocalA || isLocalB || isLocalC) {
                return true;
            }
            return false;
        }
    }
}
