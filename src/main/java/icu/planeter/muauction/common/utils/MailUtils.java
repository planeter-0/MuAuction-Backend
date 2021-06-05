package icu.planeter.muauction.common.utils;

import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Planeter
 * @description: MailUtils
 * @date 2021/6/5 21:28
 * @status dev
 */
public class MailUtils {

    public static String generateCode(int n){
        Random r = new Random();
        StringBuffer sb =new StringBuffer();
        for(int i = 0;i < n;i ++){
            int randNumber = r.nextInt(10);
            sb.append(randNumber);
        }
        return sb.toString();
    }
}
