package de.ep.team2.core.controller;

import de.ep.team2.core.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {

    @RequestMapping(value = {"/login", "/"})
    public String login(){
        return "login_page";
    }

}
