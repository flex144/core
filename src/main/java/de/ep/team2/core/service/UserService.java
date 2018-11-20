package de.ep.team2.core.service;

import de.ep.team2.core.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Forwards User related requests to the Data Access Object.
 */
@Component
public class UserService {

    public User getUserByID(Integer id) {
        return DataBaseService.getInstance().getUserById(id);
    }

    public User getUserByEmail(String email) {
        return DataBaseService.getInstance().getUserByEmail(email);
    }

    public void deleteUserByID(Integer id) {
        DataBaseService.getInstance().deleteUserById(id);
    }

    public List<User> getAllUsers() { return DataBaseService.getInstance().getAllUsers(); }

    /**
     * This method checks if the given String is a valid E-Mail.
     * @param email E-Mail that needs to be checked.
     * @return "true", if E-Mail is valid, "false", if not.
     */
    public boolean checkEmailPattern(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        return (m.matches() || email.equals(""));
    }

    /**
     * Determines the reason why a given email is flawed and returns it,
     * or the String 'valid' if there is nothing wrong.
     * @param email email to get the error.
     * @return error message.
     */
    public String wrongMailReason(String email) {
        String errorMessage = "valid";
        if(email == null || email.isEmpty()) {
            errorMessage = "E-Mail Feld muss ausgefüllt werden!";
        } else if (!checkEmailPattern(email)) {
            errorMessage = "Das ist keine E-Mail!";
        } else if (getUserByEmail(email) == null) {
            errorMessage = "E-Mail existiert nicht";
        }
        return errorMessage;
    }

}
