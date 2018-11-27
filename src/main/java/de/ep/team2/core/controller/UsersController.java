package de.ep.team2.core.controller;

import de.ep.team2.core.entities.User;
import de.ep.team2.core.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Handles Http-Requests with the path '/users'.
 */
@Controller
@RequestMapping("/users")
public class UsersController {

    /**
     * Searches for a specific User in the DB and binds its attributes to the
     * model,
     * to read them with Thymeleaf.
     *
     * @param query Email or ID of the user searched for. Provided via URL.
     * @param model Used by Thymeleaf to get the data.
     * @return 'user' when no Issues occurred;
     * 'error' if the User wasn't found.
     */
    @RequestMapping(value = "/{query}", method = RequestMethod.GET)
    public String searchUser(@PathVariable("query") String query, Model model) {
        UserService userService = new UserService();
        User searchedUser = null;
        String errorMsg = "Email oder ID ist nicht valide!";
        if (userService.checkEmailPattern(query)) {
            searchedUser = userService.getUserByEmail(query);
            if (searchedUser == null) {
                errorMsg = "Benutzer nicht gefunden!";
            }
        } else if (isInteger(query)) {
            searchedUser = userService.getUserByID(Integer.parseInt(query));
            if (searchedUser == null) {
                errorMsg = "Benutzer nicht gefunden!";
            }
        }
        if (searchedUser == null) {
            model.addAttribute("error", errorMsg);
            return "error";
        }
        model.addAttribute("user", searchedUser);
        return "mod_view_user_profile";
    }

    /**
     * Deletes a User from the Database if he exists and the ID is valid.
     *
     * @param id    Id of the user to delete.
     * @param model The Model Thymeleaf uses.
     * @return "mod_user_search" at success, "error" when an error occurred.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String deleteUserById(@PathVariable("id") String id, Model model) {
        UserService userService = new UserService();
        if (!isInteger(id)) {
            model.addAttribute("error", "ID nicht valide!");
            return "error";
        } else if (userService.getUserByID(Integer.parseInt(id)) == null) {
            model.addAttribute("error", "Benutzer existiert nicht!");
            return "error";
        } else {
            userService.deleteUserByID(Integer.parseInt(id));
            model.addAttribute("users", userService.getAllUsers());
            return "mod_user_search";
        }
    }

    /**
     * Adds a new User to the Database if the email is unique and valid.
     * If an error occurs it adds a fitting error message to the model
     * so thymeleaf can display it.
     *
     * @param user User to add to the DB.
     * @param model Model thymeleaf uses.
     * @return at success redirect to the 'user_data' page. If something went
     * wrong goes back to the login page(returns "login_page".
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public String createUser(@ModelAttribute("user") User user, Model model) {
        UserService userService = new UserService();
        String email = user.getEmail();
        String errorMessage;
        if (email == null || email.isEmpty()) {
            errorMessage = "E-Mail Feld muss ausgefüllt werden!";
        } else if (!userService.checkEmailPattern(email)) {
            errorMessage = "Das ist keine E-Mail!";
        } else if (userService.getUserByEmail(email) != null) {
            errorMessage = "E-Mail existiert bereits!";
        } else {
            userService.createUser(email, null, null);
            User addedUser = userService.getUserByEmail(email);
            return String.format("redirect:/user/new", addedUser.getId());
        }
        model.addAttribute("errorMessage", errorMessage);
        return new IndexController().login(model);
    }


    /**
     * Checks if the String is an Integer.
     *
     * @param toCheck String to check.
     * @return true if String is an Integer, otherwise false.
     */
    private boolean isInteger(String toCheck) {
        try {
            Integer.parseInt(toCheck);
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }
}
