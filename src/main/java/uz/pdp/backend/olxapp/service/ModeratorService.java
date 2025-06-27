package uz.pdp.backend.olxapp.service;

import org.springframework.data.domain.Pageable;
import uz.pdp.backend.olxapp.payload.ModeratedProductDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;

import java.util.List;

/**
 * Created by Avazbek on 26/06/25 13:51
 */
public interface ModeratorService {
    public PageDTO<ModeratedProductDTO> getAll(Pageable pageable);

}
