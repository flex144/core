package de.ep.team2.core.controller;

import de.ep.team2.core.DbTest.User;
import de.ep.team2.core.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles Http-Requests with '/users'.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    /**
     * Searches for a specific User in the DB and binds its attributes to the model,
     * to read them with Thymeleaf.
     * @param id ID of the user searched for. Provided via URL.
     * @param model Used by Thymeleaf to get the data.
     * @return 'user' when no Issues occurred;
     * 'error' if ID was no Integer or the User wasn't found.
     */
    @RequestMapping("/{id}")
    public String test(@PathVariable("id") String id, Model model){
        UserService userService = new UserService();
        Integer idInt;
        try {
             idInt = Integer.parseInt(id);
        } catch (NumberFormatException numberFormatException) {
            model.addAttribute("error",numberFormatException.getMessage());
            return "error";
        }
        User searchedUser = userService.getUser(idInt);
        if (searchedUser == null) {
            model.addAttribute("error","User not found!");
            return "error";
        }
        model.addAttribute("id",searchedUser.getId());
        model.addAttribute("firstName",searchedUser.getFirstName());
        model.addAttribute("lastName",searchedUser.getLastName());
        return "user";
    }
}
