package uz.pdp.backend.olxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import uz.pdp.backend.olxapp.entity.Attachment;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    default Attachment findByIdOrElseTrow(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Attachment with id " + id + " not found", HttpStatus.NOT_FOUND));
    }
}