package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

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
            System.err.println("Email yuborishda xatolik: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(true);
    }
}
