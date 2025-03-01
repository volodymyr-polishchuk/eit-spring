package com.volodymyrpo.eit;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin
public class IndexController {

    @GetMapping({
            "/",
            "student", "student/*",
            "login", "login/*",
            "sign-up", "sign-up/*"
    })
    public String index() {
        return "forward:index.html";
    }
}
