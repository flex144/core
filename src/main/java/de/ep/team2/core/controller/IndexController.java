package de.ep.team2.core.controller;

import de.ep.team2.core.DbTest.User;
import de.ep.team2.core.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

    @RequestMapping(value = {"/login"}, method=RequestMethod.GET)
    public String login(Model model){
        User user = new User();
        model.addAttribute(user);
        return "login_page";
    }

    @RequestMapping(value = {"/login"}, method=RequestMethod.POST)
    public String checkuser(Model model, @ModelAttribute("user") User user){
        String email = user.getEmail();
        String errorMessage = checkMail(email);
        if(errorMessage.isEmpty()) {
            return "User-StartupPage";
        } else {
            model.addAttribute("errorMessage", errorMessage);
            return "login_page";
        }
    }

    private String checkMail(String email) {
        String errorMessage = "";
        UserService userservice = new UserService();
        if(email == null || email.isEmpty()) {
            errorMessage = "E-Mail Feld muss ausgefüllt werden!";
        } else if (!userservice.checkEmail(email)) {
            errorMessage = "Das ist keine E-Mail!";
        } else if (userservice.getUserByEmail(email) == null) {
            errorMessage = "E-Mail existiert nicht";
        }
        return errorMessage;
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
