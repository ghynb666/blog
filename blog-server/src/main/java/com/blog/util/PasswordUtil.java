package com.blog.util;

import cn.dev33.satoken.secure.SaSecureUtil;

public class PasswordUtil {

    public static String hash(String password) {
        return SaSecureUtil.bCrypt(password);
    }

    public static boolean check(String password, String hash) {
        return SaSecureUtil.checkBCrypt(password, hash);
    }

    public static boolean isBCrypt(String hash) {
        return hash != null && hash.startsWith("$2a$");
    }
}
