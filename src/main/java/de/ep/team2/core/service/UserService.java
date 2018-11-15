package de.ep.team2.core.service;

import de.ep.team2.core.DbTest.User;
import org.springframework.stereotype.Component;


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
}
