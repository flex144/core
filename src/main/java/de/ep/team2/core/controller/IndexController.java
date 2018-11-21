package de.ep.team2.core.controller;

import de.ep.team2.core.entities.User;
import de.ep.team2.core.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {

    @PostMapping(value = {"/login"})
    public String checkuser(Model model, @ModelAttribute("user") User user){
        String email = user.getEmail();
        UserService userService = new UserService();
        String errorMessage = (userService.wrongMailReason(email));
        if(errorMessage.equals("valid")) {
            return "redirect:user/home";
        } else {
            model.addAttribute("errorMessage", errorMessage);
            return "login_page";
        }
    }

    @GetMapping(value = {"/login"})
    public String login(Model model){
        User user = new User();
        model.addAttribute(user);
        return "login_page";
    }

    @RequestMapping(value = {"/create"}, method=RequestMethod.GET)
    public String create(){
        return "formular_create_exercise";
    }

    @RequestMapping(value = {"/usersearch"}, method = RequestMethod.GET)
    public String userSearch() {
        return "mod_user_search";
    }

    /**
     * This method searches for all Users in the Database and adds them to the model
     * of the Thymeleaf Html-Website.
     * @param model Adds the existing Users to the model, for Thymeleaf to work with.
     * @return Returns mod_user_search.html
     */
    @RequestMapping(value = {"/usersearch"}, method = RequestMethod.POST)
    public String getUserList(Model model, @ModelAttribute("searchtext") String searchtext) {
        UserService userService = new UserService();
        model.addAttribute("users", userService.getAllUsers());
        return "mod_user_search";
    }


}
