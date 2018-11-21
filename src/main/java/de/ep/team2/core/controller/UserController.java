package de.ep.team2.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/home")
    public String startUp() {
        return "user_startup_page";
    }
}
