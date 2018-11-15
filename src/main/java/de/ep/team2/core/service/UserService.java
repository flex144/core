package de.ep.team2.core.service;

import de.ep.team2.core.CoreApplication;
import de.ep.team2.core.DbTest.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;

/**
 * Handles SQL-Queries related to Users.
 */
@Component
public class UserService {

    private JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(CoreApplication.class);

    /**
     * Searches for a User with a specific ID in the DB and returns him if he exists.
     * @param id ID of the searched User.
     * @return null if User doesn't exist; Object of the class User, which represents the found User,
     * if the User exists.
     */
    public User getUser(Integer id) {
        LinkedList<User> toReturn = new LinkedList<>();
        jdbcTemplate = DataBaseService.getInstance().getJdbcTemplate();
        jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM users WHERE id = ?", new Object[] { id },
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
        ).forEach(user -> toReturn.add(user));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }
}
