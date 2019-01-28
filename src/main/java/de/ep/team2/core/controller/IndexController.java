package de.ep.team2.core.controller;

import de.ep.team2.core.entities.ConfirmationToken;
import de.ep.team2.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String confirmUserAccount(RedirectAttributes redirectAttributes, @RequestParam("token") String
            confirmationToken, Model model) {
        ConfirmationToken token = userService.findConfirmationToken(confirmationToken);

        if (token != null) {
            userService.confirmUser(token.getUser());
            redirectAttributes.addFlashAttribute("verified", "Account wurde verifiziert");
            userService.deleteTokenById(token.getTokenId());
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Link is invalid or broken!");
            return "error";
        }
    }

}
