package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Hidden
@Controller
@RequestMapping("/api/open")
public class HomeController {

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
