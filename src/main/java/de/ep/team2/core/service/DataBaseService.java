package de.ep.team2.core.service;

import de.ep.team2.core.CoreApplication;
import de.ep.team2.core.DbTest.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedList;
import java.util.List;

/**
 * Singleton class to store the JdbcTemplate for the Database.
 */
public class DataBaseService {

    private static DataBaseService instance;
    private JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(CoreApplication.class);

    private DataBaseService() { }

    /**
     * Returns the Instance of this Singleton.
     * If there is none a Instance is created.
     *
     * @return Instance of this Singleton.
     */
    public static DataBaseService getInstance() {
        if (instance == null) {
            instance = new DataBaseService();
            return instance;
        } else {
            return instance;
        }
    }

    public void setJdbcTemplate(JdbcTemplate template) {
        jdbcTemplate = template;
    }

    /*public JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate != null) {
            return jdbcTemplate;
        } else {
            throw new IllegalStateException("No Template set!");
        }
    }
    */
    /**
     * Searches for a User with a specific ID in the DB and returns him if he exists.
     * @param id ID of the searched User.
     * @return null if User doesn't exist; Object of the class User, which represents the found User,
     * if the User exists.
     */
    User getUserById(Integer id) {
        LinkedList<User> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM users WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> new User(rs.getLong("id"),
                        rs.getString("first_name"), rs.getString("last_name"))
        ));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    public void insertUser(String firstName, String lastName){
        Object[] toInsert = {firstName, lastName};
        jdbcTemplate.update("INSERT INTO users(first_name, last_name) VALUES (?,?)",
                toInsert);
        log.info("User '" + firstName + " " + lastName + "' inserted in Table users!");
    }
}
