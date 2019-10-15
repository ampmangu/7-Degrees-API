package com.ampmangu.degrees.security;

import java.security.InvalidKeyException;

import static com.ampmangu.degrees.config.Constants.JWT_MASTER_TOKEN;

public class SecurityUtils {
    public static void checkToken(String token) throws InvalidKeyException {
        //TODO Shouldn't this come from Application (as its the master token?)
        if (!JWT_MASTER_TOKEN.equalsIgnoreCase(token)) {
            throw new InvalidKeyException("Wrong authorization token");
        }
    }
}
