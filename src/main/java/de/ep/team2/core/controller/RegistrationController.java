package de.ep.team2.core.controller;

import de.ep.team2.core.dtos.RegistrationDto;
import de.ep.team2.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller, which handles the registration process.
 */
@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @ModelAttribute("user")
    public RegistrationDto registrationDto() {
        return new RegistrationDto();
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        return "registration";
    }

    /**
     * This method is called if a new User wants to register to the website. It creates a new user and gives back
     * a success message if the values are valid. Otherwise it gives back a error message, containing the cause
     * of the failed registration
     * @param userDto DTO filled with information of the user.
     * @param model Model used by thymeleaf to receive values
     * @return Hands back the registration.html website, containing either an error message or a success message.
     */
    @PostMapping
    public String registerUser(@ModelAttribute("user") RegistrationDto userDto, Model model) {
        String errorMessage = userService.checkToCreateUser(userDto);
        if (errorMessage.equals("")) {
            return "redirect:/welcome";
        } else {
            model.addAttribute("errorMessage", errorMessage);
            return "registration";
        }
    }
}
