package icu.planeter.muauction.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/5/15 0:23
 * @status dev
 */
@Slf4j
public class JWTUtils {

    public static Date getIssuedAt(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getIssuedAt();
        } catch (JWTDecodeException e) {
            log.warn("Token Decode Error:{}", token);
            return null;
        }
    }

    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            log.warn("Token Decode Error:{}", token);
            return null;
        }
    }

    /**
     * @param username email
     * @param time period of validity
     * @return JWT String
     */
    public static String sign(String username, String salt, long time) {
        try {
            Date date = new Date(System.currentTimeMillis() + time * 1000);
            Algorithm algorithm = Algorithm.HMAC256(salt);
            return JWT.create()
                    .withClaim("username", username)
                    .withExpiresAt(date)// Expire time
                    .withIssuedAt(new Date())// Sign time
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            log.warn("Token Sign FAILED");
            return null;
        }
    }

    public static boolean isTokenExpired(String token) {
        Date now = Calendar.getInstance().getTime();
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt().before(now);
        } catch (JWTDecodeException e) {
            log.warn("Token Decode Error:{}", token);
        }
        return true;
    }

    public static String generateSalt() {
        return new SecureRandomNumberGenerator().nextBytes(16).toHex();
    }

}
