package icu.planeter.muauction.common.utils;

import org.springframework.mail.SimpleMailMessage;

import java.util.Random;

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

    public static SimpleMailMessage generateEmail(String sender, String receiver, String subject,String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(receiver);
        message.setSubject(subject);
        message.setText(content);
        return message;
    }
}
