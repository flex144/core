package de.ep.team2.core.service;

import de.ep.team2.core.CoreApplication;
import de.ep.team2.core.DbTest.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.LinkedList;
import java.util.List;


/**
 * Singleton Data Access Object which handles all SQL queries with the Database.
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

    /**
     * Sets the JdbcTemplate which references the Data Source.
     * @param template Jdbc Template linked with the Datasource.
     */
    public void setJdbcTemplate(JdbcTemplate template) {
        jdbcTemplate = template;
    }

    /**
     * Searches for a User with a specific ID in the DB and returns him if he exists.
     * @param id ID of the searched User.
     * @return null if User doesn't exist; Object of the class User, which represents the found User,
     * if the User exists.
     */
    User getUserById(Integer id) {
        LinkedList<User> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT id, email, first_name, last_name FROM users WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("email"),
                        rs.getString("first_name"), rs.getString("last_name"))
        ));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    /**
     * Searches for a User with a specific Email in the DB and returns him if he exists.
     * @param email Email of the searched User.
     * @return null if User doesn't exist; Object of the class User, which
     * represents the found User, if the User exists.
     */
    public User getUserByEmail(String email) {
        LinkedList<User> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT id, email, first_name, last_name FROM users WHERE email = ?",
                new Object[]{email},
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("email"),
                        rs.getString("first_name"), rs.getString("last_name"))
        ));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    /**
     * Searches in the Database "users" for all Users and adds them to a List.
     * @return Returns a List of Users.
     */
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> toReturn = jdbcTemplate.query(sql, new BeanPropertyRowMapper(User.class));

        return toReturn;
    }

    /**
     * Adds a new User to the Table 'users'.
     *
     * @param email Email of the User.
     * @param firstName First Name of the User.
     * @param lastName Last Name of the User.
     */
    public void insertUser(String email, String firstName, String lastName){
        Object[] toInsert = {email, firstName, lastName};
        jdbcTemplate.update("INSERT INTO users(email, first_name, last_name) VALUES (?,?,?)",
                toInsert);
        log.info("User '" + firstName + " " + lastName + "' with mail: '"
                + email + "' inserted in Table 'users'!");
    }
}
