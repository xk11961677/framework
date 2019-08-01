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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检测是否为移动端设备访问
 *
 * @author
 */
public class CheckMobile {


    /**
     * 电话格式验证
     **/
    private static final String PHONE_CALL_PATTERN = "^(\\(\\d{3,4}\\)|\\d{3,4}-)?\\d{7,8}(-\\d{1,4})?$";

    /**
     * 中国电信号码格式验证 手机段： 133,153,180,181,189,177,1700
     **/
    private static final String CHINA_TELECOM_PATTERN = "(^1(33|53|77|8[019]|99)\\d{8}$)|(^1700\\d{7}$)";

    /**
     * 中国联通号码格式验证 手机段：130,131,132,155,156,185,186,145,176,1709
     **/
    private static final String CHINA_UNICOM_PATTERN = "(^1(3[0-2]|4[5]|5[56]|66|7[6]|8[56])\\d{8}$)|(^1709\\d{7}$)";

    /**
     * 中国移动号码格式验证
     * 手机段：134,135,136,137,138,139,150,151,152,157,158,159,182,183,184
     * ,187,188,147,178,1705
     **/
    private static final String CHINA_MOBILE_PATTERN = "(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478]|98)\\d{8}$)|(^1705\\d{7}$)";


    // \b 是单词边界(连着的两个(字母字符 与 非字母字符) 之间的逻辑上的间隔),
    // 字符串在编译时会被转码一次,所以是 "\\b"
    // \B 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔)
    static String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i" + "|windows (phone|ce)|blackberry"
            + "|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp" + "|laystation portable)|nokia|fennec|htc[-_]"
            + "|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
    static String tableReg = "\\b(ipad|tablet|(Nexus 7)|up.browser" + "|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";

    // 移动设备正则匹配：手机端、平板
    static Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);
    static Pattern tablePat = Pattern.compile(tableReg, Pattern.CASE_INSENSITIVE);

    /**
     * 检测是否是移动设备访问
     *
     * @param userAgent 浏览器标识
     * @return true:移动设备接入，false:pc端接入
     */
    public static boolean check(String userAgent) {
        if (null == userAgent) {
            userAgent = "";
        }
        // 匹配
        Matcher matcherPhone = phonePat.matcher(userAgent);
        Matcher matcherTable = tablePat.matcher(userAgent);
        if (matcherPhone.find() || matcherTable.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查访问方式是否为移动端
     *
     * @param request
     * @throws IOException
     */
    public static boolean check(HttpServletRequest request, HttpServletResponse response) {
        boolean isFromMobile = false;

        HttpSession session = request.getSession();
        // 检查是否已经记录访问方式（移动端或pc端）
        if (null == session.getAttribute("ua")) {
            try {
                // 获取ua，用来判断是否为移动端访问
                String userAgent = request.getHeader("USER-AGENT").toLowerCase();
                if (null == userAgent) {
                    userAgent = "";
                }
                isFromMobile = CheckMobile.check(userAgent);
                // 判断是否为移动端访问
                if (isFromMobile) {
                    session.setAttribute("ua", "mobile");
                } else {
                    session.setAttribute("ua", "pc");
                }
            } catch (Exception e) {
            }
        } else {
            isFromMobile = session.getAttribute("ua").equals("mobile");
        }

        return isFromMobile;
    }


    /**
     * 验证【电信】手机号码的格式
     *
     * @param str 校验手机字符串
     * @return 返回true, 否则为false
     */
    public static boolean isChinaTelecomPhoneNum(String str) {

        return str == null || str.trim().equals("") ? false : match(
                CHINA_TELECOM_PATTERN, str);
    }

    /**
     * 验证【联通】手机号码的格式
     *
     * @param str 校验手机字符串
     * @return 返回true, 否则为false
     */
    public static boolean isChinaUnicomPhoneNum(String str) {

        return str == null || str.trim().equals("") ? false : match(
                CHINA_UNICOM_PATTERN, str);
    }

    /**
     * 验证【移动】手机号码的格式
     *
     * @param str 校验手机字符串
     * @return 返回true, 否则为false
     */
    public static boolean isChinaMobilePhoneNum(String str) {

        return str == null || str.trim().equals("") ? false : match(
                CHINA_MOBILE_PATTERN, str);
    }

    /**
     * 验证电话号码的格式
     *
     * @param str 校验电话字符串
     * @return 返回true, 否则为false
     */
    public static boolean isPhoneCallNum(String str) {

        return str == null || str.trim().equals("") ? false : match(
                PHONE_CALL_PATTERN, str);
    }

    /**
     * 验证手机和电话号码的格式
     *
     * @param str 校验手机字符串
     * @return 返回true, 否则为false
     */
    public static boolean isPhoneNum(String str) {
        // 如果字符串为空，直接返回false
        if (str == null || str.trim().equals("")) {
            return false;
        } else {
            int comma = str.indexOf(",");// 是否含有逗号
            int caesuraSign = str.indexOf("、");// 是否含有顿号
            int space = str.trim().indexOf(" ");// 是否含有空格
            if (comma == -1 && caesuraSign == -1 && space == -1) {
                // 如果号码不含分隔符,直接验证
                str = str.trim();
                return (isPhoneCallNum(str) || isChinaTelecomPhoneNum(str)
                        || isChinaUnicomPhoneNum(str) || isChinaMobilePhoneNum(str)) ? true
                        : false;
            } else {
                // 号码含分隔符,先把分隔符统一处理为英文状态下的逗号
                if (caesuraSign != -1) {
                    str = str.replaceAll("、", ",");
                }
                if (space != -1) {
                    str = str.replaceAll(" ", ",");
                }

                String[] phoneNumArr = str.split(",");
                //遍历验证
                for (String temp : phoneNumArr) {
                    temp = temp.trim();
                    if (isPhoneCallNum(temp) || isChinaTelecomPhoneNum(temp)
                            || isChinaUnicomPhoneNum(temp)
                            || isChinaMobilePhoneNum(temp)) {
                        continue;
                    } else {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    /**
     * 执行正则表达式
     *
     * @param pat 表达式
     * @param str 待验证字符串
     * @return 返回true, 否则为false
     */
    private static boolean match(String pat, String str) {
        Pattern pattern = Pattern.compile(pat);
        Matcher match = pattern.matcher(str);
        return match.find();
    }

    public static void main(String[] args) {
        String phoneNum = "19916006011";
        if (CheckMobile.isPhoneNum(phoneNum)) {
            if (CheckMobile.isChinaMobilePhoneNum(phoneNum)) {
                //移动
                System.out.println("cmcc");
            } else if (CheckMobile.isChinaUnicomPhoneNum(phoneNum)) {
                //联通
                System.out.println("cucc");
            } else if (CheckMobile.isChinaTelecomPhoneNum(phoneNum)) {
                //电信
                System.out.println("ctc");
            }
        }
    }

}
