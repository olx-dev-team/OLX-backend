package uz.pdp.backend.olxapp.schooled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.backend.olxapp.entity.PasswordResetToken;
import uz.pdp.backend.olxapp.repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordTokenCleanerService {

    private final PasswordResetTokenRepository tokenRepository;

    @Scheduled(fixedRate = 5 * 60 * 1000) // ⏰ har 5 daqiqada ishga tushadi (millis)
    @Transactional
    public void deleteExpiredTokens() {

//        tokenRepository.deleteAllExpiredSince(LocalDateTime.now());// ⚠️ bu yerda hamma tokenlarni tekshirib olib, muddati o‘tganlarini tozalaydi (har bir token uchun qo‘llaniladi))
        log.info("🧹 muddati o‘tgan token tozalandi time: {}, deleted: {} ", LocalDateTime.now(), 1);


//        List<PasswordResetToken> tokens = tokenRepository.findAll();
//        for (PasswordResetToken token : tokens) {
//            if (!token.getExpiryDate().isBefore(LocalDateTime.now())) continue;
//            tokenRepository.deleteById(token.getId());
//        }
//        log.info("🧹 muddati o‘tgan token tozalandi time: {}", LocalDateTime.now());

    }

}
