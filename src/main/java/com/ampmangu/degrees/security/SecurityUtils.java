package com.ampmangu.degrees.security;

import static com.ampmangu.degrees.config.Constants.JWT_MASTER_TOKEN;

public class SecurityUtils {
    public static boolean checkToken(String token) throws SecurityException {
        if (JWT_MASTER_TOKEN.equalsIgnoreCase(token)) {
            return true;
        } else {
            throw new SecurityException("Wrong authorization token");
        }
    }
}
