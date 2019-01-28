package de.ep.team2.core.controller;

import de.ep.team2.core.entities.User;
import de.ep.team2.core.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
     * Checks if user is authorized to view the profile.
     *
     * @param query Email or ID of the user searched for. Provided via URL.
     * @param model Used by Thymeleaf to get the data.
     * @return 'user' when no Issues occurred;
     * 'error' if the User wasn't found.
     */
    @PreAuthorize("#query == principal.getId().toString() ||" +
            "#query == principal.getEmail() || hasRole('ROLE_MOD')")
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
     * Searches for the users ID and redirects him to his profile.
     *
     * @return Profile Website.
     */
    @RequestMapping("/")
    public String getUserProfile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int id = user.getId();
        return "redirect:/users/" + id;
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
            User user = (User) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            userService.deleteUserByID(Integer.parseInt(id));

            //check if the logged in user deletes himself;
            //if so, he gets redirected to the login and his session expires;
            if (user.getId() == Integer.parseInt(id)) {
                SecurityContextHolder.getContext().setAuthentication(null);
                return "redirect:/login";
            }
            model.addAttribute("users", userService.getAllUsers());
            return "mod_user_search";
        }
    }

    /**
     * Promotes a user to a moderator if he exists and the ID is valid.
     *
     * @param id Id of the user to promote
     * @param model The model thymeleaf users.
     * @return The user's profile at success; "error" when an error occurred.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public String setMod(@PathVariable("id") String id, Model model) {
        UserService userService = new UserService();
        if (!isInteger(id)) {
            model.addAttribute("error", "ID nicht valide!");
            return "error";
        } else if (userService.getUserByID(Integer.parseInt(id)) == null) {
            model.addAttribute("error", "Benutzer existiert nicht!");
            return "error";
        } else {
            userService.changeToMod(Integer.parseInt(id));
            model.addAttribute("users", userService.getAllUsers());
            return "redirect:/users/" + id;
        }
    }

    /**
     * Checks if the String is an Integer.
     *
     * @param toCheck String to check.
     * @return true if String is an Integer, otherwise false.
     */
    static boolean isInteger(String toCheck) {
        try {
            Integer.parseInt(toCheck);
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }
}
