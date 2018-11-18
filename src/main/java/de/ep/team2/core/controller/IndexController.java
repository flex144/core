package de.ep.team2.core.controller;

import de.ep.team2.core.DbTest.User;
import de.ep.team2.core.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

    private String errorMessage = "Invalid E-Mail";

    @RequestMapping(value = {"/login"}, method=RequestMethod.GET)
    public String login(Model model){
        User user = new User();
        model.addAttribute(user);
        return "login_page";
    }

    @RequestMapping(value = {"/login"}, method=RequestMethod.POST)
    public String checkuser(Model model, @ModelAttribute("user") User user){

        UserService userservice = new UserService();
        String email = user.getEmail();
        if(email != null && userservice.checkEmail(email) && userservice.getUserByEmail(email) != null) {
            return "formular_create_exercise";
        }

        model.addAttribute("errorMessage", errorMessage);
        return "login_page";
    }

    @RequestMapping(value = {"/create"}, method=RequestMethod.GET)
    public String create(){
        return "formular_create_exercise";
    }

    /**
     * This method searches for all Users in the Database and adds them to the model
     * of the Thymeleaf Html-Website.
     * @param model Adds the existing Users to the model, for Thymeleaf to work with.
     * @return Returns userlist.html
     */
    @RequestMapping(value = {"/userlist"}, method = RequestMethod.GET)
    public String getUserList(Model model) {
        UserService userService = new UserService();
        model.addAttribute("users", userService.getAllUsers());
        return "userlist";
    }


}
