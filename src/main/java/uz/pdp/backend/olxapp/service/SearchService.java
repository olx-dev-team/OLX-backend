package uz.pdp.backend.olxapp.service;

import uz.pdp.backend.olxapp.payload.FilterDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.ProductDTO;

public interface SearchService {
    PageDTO<?> search(FilterDTO filterDTO, Integer page, Integer size);

}
