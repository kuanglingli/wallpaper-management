package com.wallpaper.management.util;

import org.apache.shiro.crypto.hash.SimpleHash;

public class TestUtil {

    /**
     * 盐值
     */
    private static final String SALT = "wallpaper";

    /**
     * 加密算法
     */
    private static final String ALGORITHM_NAME = "md5";

    /**
     * 加密次数
     */
    private static final int HASH_ITERATIONS = 2;

    public static void main(String[] args) {

        String pwd = "kll521?!";
        System.out.println(TestUtil.encryptPassword(pwd));
    }

    public static String encryptPassword(String password) {
        return new SimpleHash(ALGORITHM_NAME, password, SALT, HASH_ITERATIONS).toHex();
    }
}
