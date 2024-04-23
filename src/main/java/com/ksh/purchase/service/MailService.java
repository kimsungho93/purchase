package com.ksh.purchase.service;

import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;


    @Async
    public void sendMail(String to, String subject, String code) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        String content = """
                가입을 환영합니다.
                아래 링크를 클릭하여 이메일 인증을 완료해주세요.
                http://localhost:8080/api/v1/auth/email/verify?email=%s"
                 """.formatted(code);

        try {
            helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.MAIL_SEND_ERROR);
        }
    }
}
