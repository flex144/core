package de.ep.team2.core.controller;

import de.ep.team2.core.entities.User;
import de.ep.team2.core.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/home")
    public String startUp() { return "user_startup_page"; }

    @RequestMapping("/new")
    public String newUser() {
        return "user_data";
    }

    @RequestMapping("/plan")
    public String plan() {
        return "user_in_exercise";
    }

    @RequestMapping("/plan/done")
    public String planEvalu() {
        return "user_exercise_evaluation";
    }

    @RequestMapping("/plan/overview")
    public String openTraining() {
        return "user_training_overview";
    }

    @RequestMapping("/plan/exercise")
    public String openExercise() {return "user_in_exercise";}

    @RequestMapping(value="/editprofile", method = RequestMethod.GET)
    public String showEditUserProfile(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("editUser", new User());
        return "mod_edit_user_profile";
    }

    @PostMapping(value="/editprofile")
    public String editUserProfile(@ModelAttribute("editUser") User editUser, Model model) {
        UserService service = new UserService();
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        editUser.setEmail(user.getEmail());
        editUser = service.changeUserDetails(editUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(editUser, editUser.getPassword(),
                editUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/users/";

    }
}
