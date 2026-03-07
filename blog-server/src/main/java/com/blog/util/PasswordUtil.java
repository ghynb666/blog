package com.blog.util;

import cn.dev33.satoken.secure.BCrypt;

public class PasswordUtil {

    public static String hash(String password) {
        return BCrypt.hashpw(password);
    }

    public static boolean check(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

    public static boolean isBCrypt(String hash) {
        return hash != null && hash.startsWith("$2a$");
    }
}
