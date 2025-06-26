package uz.pdp.backend.olxapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/open")
public class homeController {

    @GetMapping("/product")
    public String getHome() {
        return "product";
    }

    @GetMapping("/product-update")
    public String getUpdatePage() {
        return "productupdate";
    }

    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/create")
    public String create() {
        return "create";
    }
}
