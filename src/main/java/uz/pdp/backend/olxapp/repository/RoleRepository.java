package uz.pdp.backend.olxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.backend.olxapp.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}