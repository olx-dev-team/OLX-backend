package uz.pdp.backend.olxapp.schooled;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import uz.pdp.backend.olxapp.service.PasswordResetService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordTokenCleanerService {

    private final PasswordResetService passwordResetService;

    @Scheduled(fixedRate = 300000)
    public void deleteExpiredTokens() {
        passwordResetService.deleteAllExpiredSince();
    }
}
