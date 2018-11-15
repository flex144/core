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


    public User getUserByID(Integer id) {
        return DataBaseService.getInstance().getUserById(id);
    }
}
