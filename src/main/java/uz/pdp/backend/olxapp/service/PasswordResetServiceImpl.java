package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.entity.PasswordResetToken;
import uz.pdp.backend.olxapp.repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository repository;

    @Override
//    @Transactional
    public void deleteAllExpiredSince() {
        log.info("Starting to delete everything:)");
//        List<PasswordResetToken> tokens = repository.findByExpiryDateBefore(LocalDateTime.now());

//        repository.deleteAllByUser(tokens.stream().map(PasswordResetToken::getId).collect(Collectors.toList()));

//        tokens.forEach(repository::delete);

//        repository.deleteAll(tokens);


    }
}
