package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;


    @Async // Email jo'natish sekin bo'lishi mumkin, shuning uchun asinxron qilamiz!
    public CompletableFuture<Boolean> sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // Log yoziladi, lekin foydalanuvchiga exception chiqarmaymiz
            log.info("Email sending failed");
            System.err.println("Email yuborishda xatolik: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
        log.info("Email sending completed");
        return CompletableFuture.completedFuture(true);
    }
}
