package uz.pdp.backend.olxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.backend.olxapp.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}