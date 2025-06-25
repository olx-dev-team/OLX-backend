package uz.pdp.backend.olxapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/open")
public class homeController {

    @GetMapping("/product")
    public String getHome(){
        return "product";
    }

    @GetMapping("/product-update")
    public String getUpdatePage(){
        return "productupdate";
    }

    @GetMapping("/chat")
    public String chat(){
        return "chat";
    }
}
