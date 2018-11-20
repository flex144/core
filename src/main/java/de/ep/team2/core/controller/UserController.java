package de.ep.team2.core.controller;

import de.ep.team2.core.entities.User;
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
     * Searches for a specific User in the DB and binds its attributes to the
     * model,
     * to read them with Thymeleaf.
     *
     * @param query Email or ID of the user searched for. Provided via URL.
     * @param model Used by Thymeleaf to get the data.
     * @return 'user' when no Issues occurred;
     * 'error' if the User wasn't found.
     */
    @RequestMapping("/{query}")
    public String searchUserEmail(@PathVariable("query") String query, Model model) {
        UserService userService = new UserService();
        User searchedUser = null;
        String errorMsg = "Email oder ID sind nicht valide!";
        if (userService.checkEmailPattern(query)) {
            searchedUser = userService.getUserByEmail(query);
            if (searchedUser == null) { errorMsg = "Benutzer nicht gefunden!";}
        } else if (isInteger(query)) {
            searchedUser = userService.getUserByID(Integer.parseInt(query));
            if (searchedUser == null) { errorMsg = "Benutzer nicht gefunden!";}
        }
        if (searchedUser == null) {
            model.addAttribute("error", errorMsg);
            return "error";
        }
        model.addAttribute("user", searchedUser);
        return "user";
    }

    /**
     * Checks if the String is an Integer.
     * @param toCheck String to check.
     * @return true if String is an Integer, otherwise false.
     */
    private boolean isInteger (String toCheck){
        try {
            Integer.parseInt(toCheck);
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }
}
