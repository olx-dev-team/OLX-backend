package uz.pdp.backend.olxapp.repository;

import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(@Email(message = "Invalid email address") String email);

    default User findByIdOrThrow(Long userId) {
        return findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found", HttpStatus.NOT_FOUND));
    }

    Optional<User> findByEmail(String email);

    Page<User> findByActiveFalse(boolean active, Pageable pageable);
}