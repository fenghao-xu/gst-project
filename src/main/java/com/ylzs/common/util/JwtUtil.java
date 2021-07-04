package com.ylzs.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    public static String buildToken(String key, String val) {
        Map<String, Object> header = new HashMap<String, Object>(0);
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.HOUR,24*365);//过期24小时
        // Date expireAt = new Date(System.currentTimeMillis() + 3600*5*1000L);
        String token = JWT.create().withHeader(header)//头信息
                .withIssuer("xuwei")//发行者
                .withClaim(key, val) //一般是用户信息
                .withExpiresAt(nowTime.getTime())
                .sign(Algorithm.HMAC256("iFashionCloud"));//加密的私有秘钥
        return token;
    }

    public static String getTokenData(String token, String key) {
        return JWT.decode(token).getClaim(key).asString();
    }

    public static boolean checkToken(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("iFashionCloud")).build();
            DecodedJWT jwt = jwtVerifier.verify(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


}
