package de.ep.team2.core.controller;

import de.ep.team2.core.entities.ConfirmationToken;
import de.ep.team2.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = {"/login", "/"})
    public String login() {
        return "login_page";
    }

    @RequestMapping(value = {"/welcome"}, method = RequestMethod.GET)
    public String welcome() {
        return "user_welcome_page";
    }

    @RequestMapping(value = "/confirm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token") String
            confirmationToken) {
        ConfirmationToken token = userService.findConfirmationToken(confirmationToken);

        if (token != null) {
            userService.confirmUser(token.getUser());
            modelAndView.addObject("message", "Account has been verified!");
        } else {
            modelAndView.addObject("message", "Link is broken, or invalid!");
        }
        modelAndView.setViewName("account_verified");
        return modelAndView;
    }

}
