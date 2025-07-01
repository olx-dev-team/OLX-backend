package uz.pdp.backend.olxapp.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.backend.olxapp.entity.PasswordResetToken;
import uz.pdp.backend.olxapp.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);

    @Transactional
    @Modifying
    void deleteAllByExpiryDateBefore(LocalDateTime dateTime);

    @Transactional
    @Modifying
    @Query(value = "delete from password_reset_token where id in :ids",nativeQuery = true)
    void deleteAllByUser(List<Long> ids);

    List<PasswordResetToken> findByExpiryDateBefore(LocalDateTime dateTime);

}