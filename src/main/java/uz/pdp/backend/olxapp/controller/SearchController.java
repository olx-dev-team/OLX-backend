package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.payload.FilterDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.ProductDTO;
import uz.pdp.backend.olxapp.service.SearchService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/open/v1/search")
    public PageDTO<?> search( FilterDTO filterDTO,
                             @RequestParam(defaultValue = "0") Integer page,
                             @RequestParam(defaultValue = "10") Integer size) {

        PageDTO<?> search = searchService.search(filterDTO, page, size);
        return search;

    }

}
