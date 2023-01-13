package com.yixihan.yibot.serivce;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮件发送 服务类
 *
 * @author : yixihan
 * @date : 2022-09-15-14:19
 */
@Log
@Service
public class MailSendService {

    @Resource
    private JavaMailSenderImpl mailSender;

    /**
     * 邮件发送者
     */
    @Value ("${spring.mail.username}")
    private String sendEmail;

    public void sendMail (String message, String email) {
        try {
            // 创建一个复杂的文件
            MimeMessage mailMessage = mailSender.createMimeMessage ();

            // 组装邮件
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage,true,"utf-8");

            helper.setSubject("小易自动签到");
            helper.setText(message,true);

            // 收件人
            helper.setTo(email);
            // 发件人
            helper.setFrom(sendEmail);

            // 发送
            mailSender.send(mailMessage);
        } catch (MessagingException e) {
            log.warning ("邮件发送失败!");
        }

    }
}
