package com.example.wiki.api;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Profile("front")
@Controller
public class SpaController {
    @GetMapping("/")
    public String redirectToApp() {
        return "redirect:/app";
    }

    @GetMapping({ "/app", "/app/", "/app/{path:^(?!images$|assets$|icon.svg$).*$}/**" })
    public String forwardToIndex(@PathVariable(required = false) String path) {
        return "forward:/index.html";
    }
}
