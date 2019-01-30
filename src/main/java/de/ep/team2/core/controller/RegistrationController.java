package de.ep.team2.core.controller;

import de.ep.team2.core.dtos.RegistrationDto;
import de.ep.team2.core.entities.ConfirmationToken;
import de.ep.team2.core.service.EmailSenderService;
import de.ep.team2.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.xml.ws.spi.http.HttpContext;

/**
 * Controller, which handles the registration process.
 */
@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private EmailSenderService emailSenderService;

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
     *
     * @param userDto DTO filled with information of the user.
     * @param model   Model used by thymeleaf to receive values
     * @return Hands back the registration.html website, containing either an error message or a success message.
     */
    @PostMapping
    public String registerUser(@ModelAttribute("user") RegistrationDto userDto, Model model,
                               RedirectAttributes redirectAttributes) {
        String errorMessage = userService.checkToCreateUser(userDto);
        if (errorMessage.equals("")) {
            ConfirmationToken confirmationToken = new ConfirmationToken(userDto.getEmail());
            userService.createConfirmationToken(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(userDto.getEmail());
            mailMessage.setSubject("Complete Registration!");
            String uri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
            String url = uri + "/confirm?token=" + confirmationToken.getConfirmationToken();
            mailMessage.setText("To confirm your account, please click here : '" + url + "' . (If that" +
                    " doesn't work, please copy and paste the link into your browser.)");

            emailSenderService.sendEmail(mailMessage);
            redirectAttributes.addFlashAttribute("email", userDto.getEmail());
            return "redirect:/welcome";
        } else {
            model.addAttribute("errorMessage", errorMessage);
            return "registration";
        }
    }
}
