package de.ep.team2.core.controller;

import de.ep.team2.core.DbTest.User;
import de.ep.team2.core.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles Http-Requests with the path '/users'.
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
    public String searchUserId(@PathVariable("id") String id, Model model){
        UserService userService = new UserService();
        Integer idInt;
        try {
             idInt = Integer.parseInt(id);
        } catch (NumberFormatException numberFormatException) {
            model.addAttribute("error",numberFormatException.getMessage());
            return "error";
        }
        User searchedUser = userService.getUserByID(idInt);
        if (searchedUser == null) {
            model.addAttribute("error", "User not found!");
            return "error";
        }
        addUserDataToModel(searchedUser, model);
        return "user";
    }

    /**
     * Searches for a specific User in the DB and binds its attributes to the model,
     * to read them with Thymeleaf.
     * @param email Email of the user searched for. Provided via URL.
     * @param model Used by Thymeleaf to get the data.
     * @return 'user' when no Issues occurred;
     * 'error' if the User wasn't found.
     */
    @RequestMapping("email/{email}")
    public String searchUserEmail(@PathVariable("email") String email, Model model){
        UserService userService = new UserService();
        // todo check if really a email
        User searchedUser = userService.getUserByEmail(email);
        if (searchedUser == null) {
            model.addAttribute("error", "User not found!");
            return "error";
        }
        addUserDataToModel(searchedUser, model);
        return "user";
    }

    /**
     * Adds User Data to the model so Thymeleaf can access it.
     * @param u User which data should be added.
     * @param model The Model Thymeleaf uses.
     */
    private void addUserDataToModel(User u, Model model) {
        model.addAttribute("id", u.getId());
        model.addAttribute("email", u.getEmail());
        model.addAttribute("firstName", u.getFirstName());
        model.addAttribute("lastName", u.getLastName());
    }
}
