package com.lucq.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    public static String MD5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPass){
        //通过拼接字符串,即使有人通过某种手段反查,得到的也是12+密码+c3
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return MD5(str);
    }

    /**
     * 为了安全起见,将转换过一次的密码再转换一次才存入DB
     * @param formPass MD5机密过一次的密码
     * @param salt 随机salt
     * @return
     */
    public static String formPassToDBPass(String formPass, String salt){
        //通过拼接字符串,即使有人通过某种手段反查,得到的也是12+密码+c3
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return MD5(str);
    }

    public static String inputPassToDBPass(String inputPass, String saltDB) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }

//    public static void main(String[] args) {
//        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
//    }
}
