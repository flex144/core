package de.ep.team2.core.service;

import de.ep.team2.core.DbTest.User;
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

    public List<User> getAllUsers() { return DataBaseService.getInstance().getAllUsers(); }

    /**
     * This method checks if the given String is a valid E-Mail.
     * @param email E-Mail that needs to be checked.
     * @return "true", if E-Mail is valid, "false", if not.
     */
    public boolean checkEmail(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        return (m.matches() || email.equals(""));
    }
}
