package de.ep.team2.core.controller;

import de.ep.team2.core.dtos.RegistrationDto;
import de.ep.team2.core.entities.ConfirmationToken;
import de.ep.team2.core.entities.User;
import de.ep.team2.core.service.EmailSenderService;
import de.ep.team2.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailSenderService emailSenderService;

    @RequestMapping(value = {"/login", "/"})
    public String login() {
        return "login_page";
    }

    @RequestMapping(value = {"/welcome"}, method = RequestMethod.GET)
    public String welcome() {
        return "user_welcome_page";
    }

    @RequestMapping(value = "/confirm", method = {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(RedirectAttributes redirectAttributes, @RequestParam("token") String
            confirmationToken, Model model) {
        ConfirmationToken token = userService.findConfirmationToken(confirmationToken);

        if (token != null) {
            userService.confirmUser(token.getUser());
            redirectAttributes.addFlashAttribute("verified", "Account wurde verifiziert");
            userService.deleteTokenById(token.getTokenId());
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Link is invalid or broken!");
            return "error";
        }
    }

    @RequestMapping(value="/forgotPassword", method = RequestMethod.GET)
    public String getForgotPassword(Model model) {
        RegistrationDto dto = new RegistrationDto();
        model.addAttribute("dto", dto);
        return "forgot_password";
    }

    @RequestMapping(value="/forgotPassword", method = RequestMethod.POST)
    public String postForgotPassword(RedirectAttributes redirectAttributes,
                                     @ModelAttribute("dto") RegistrationDto dto) {
        String errorMessage = "";
        User checkUser = userService.getUserByEmail(dto.getEmail());
        if (dto.getEmail() == null || dto.getEmail().equals("")) {
            errorMessage = "E-Mail Feld muss ausgefüllt werden";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/forgotPassword";
        } else if (checkUser == null) {
            errorMessage = "Nutzer nicht registriert!";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/forgotPassword";
        } else {
            String newPassword = userService.setRandomUserPassword(dto.getEmail());

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(dto.getEmail());
            mailMessage.setSubject("Neues Passwort!");
            String uri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
            String url = uri + "/login";
            mailMessage.setText("Your new password is '"+newPassword+"' . Please login with it " +
                    "at '"+url+"' and change it as soon as possible!");
            emailSenderService.sendEmail(mailMessage);

            redirectAttributes.addFlashAttribute("verified", "Passwort wurde zurückgesetzt!");
            return "redirect:/login";
        }
    }

}
