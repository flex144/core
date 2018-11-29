package de.ep.team2.core.service;

import com.sun.javaws.exceptions.InvalidArgumentException;
import de.ep.team2.core.CoreApplication;
import de.ep.team2.core.entities.Exercise;
import de.ep.team2.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * Singleton Data Access Object which handles all SQL queries with the Database.
 */
public class DataBaseService {

    private static DataBaseService instance;
    private JdbcTemplate jdbcTemplate;
    private static final Logger log =
            LoggerFactory.getLogger(CoreApplication.class);

    private DataBaseService() {
    }

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
     *
     * @param template Jdbc Template linked with the Datasource.
     */
    public void setJdbcTemplate(JdbcTemplate template) {
        jdbcTemplate = template;
    }

    // User

    /**
     * Searches for a User with a specific ID in the DB and returns him if he
     * exists.
     *
     * @param id ID of the searched User.
     * @return null if User doesn't exist; Object of the class User, which
     * represents the found User,
     * if the User exists.
     */
    public User getUserById(Integer id) {
        LinkedList<User> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT id, email, first_name, last_name FROM users WHERE id " +
                        "= ?",
                new Integer[]{id},
                new BeanPropertyRowMapper<>(User.class)
        ));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    /**
     * Searches for a User with a specific Email in the DB and returns him if
     * he exists.
     * Transforms the given Email to lowercase.
     *
     * @param email Email of the searched User.
     * @return null if User doesn't exist; Object of the class User, which
     * represents the found User, if the User exists.
     */
    public User getUserByEmail(String email) {
        LinkedList<User> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT id, email, first_name, last_name FROM users WHERE " +
                        "email = ?",
                new String[]{email.toLowerCase()},
                new BeanPropertyRowMapper<>(User.class)));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    /**
     * Searches in the Database "users" for all Users and adds them to a List.
     *
     * @return Returns a List of Users.
     */
    public List<User> getAllUsers() {
        return jdbcTemplate.query(
                "SELECT * FROM users", new BeanPropertyRowMapper<>(User.class));
    }

    /**
     * Adds a new User to the Table 'users'.
     * Transforms the Email to lowercase.
     *
     * @param email     Email of the User.
     * @param firstName First Name of the User.
     * @param lastName  Last Name of the User.
     */
    public void insertUser(String email, String firstName, String lastName) throws InvalidArgumentException {
        String mail = email.toLowerCase();
        if (getUserByEmail(mail) != null) {
            log.info("Insert User failed! Email " + email + " already in the " +
                    "Database!");
            throw new InvalidArgumentException(new String[]{"Email already in" +
                    " the Database!"});
        }
        String[] toInsert = {email.toLowerCase(), firstName, lastName};
        jdbcTemplate.update("INSERT INTO users(email, first_name, last_name) " +
                        "VALUES (?,?,?)",
                toInsert);
        log.info("User '" + firstName + " " + lastName + "' with mail: '"
                + email + "' inserted in Table 'users' with Id "
                + getUserByEmail(email).getId() + " !");
    }

    /**
     * Deletes a user in the table with the given id.
     *
     * @param id user to delete.
     */
    public void deleteUserById(Integer id) {
        User toDelete = getUserById(id);
        if (toDelete != null) {
            jdbcTemplate.update("DELETE FROM users WHERE id = ?",
                    new Integer[]{id});
            log.info("User '" + toDelete.getFirstName() + " " + toDelete.getLastName()
                    + "' with mail: '" + toDelete.getEmail() + "' deleted!");
        }
    }

    // Exercise

    /**
     * @param name
     * @param description
     * @param imgPath
     */
    public void insertExercise(String name, String description,
                               String imgPath) {
        String[] toInsert = {name, description, imgPath};
        jdbcTemplate.update("INSERT INTO exercises(name, description, " +
                        "img_path) VALUES (?,?,?)",
                toInsert);
        List<Integer> id = jdbcTemplate.query("select currval" +
                        "(pg_get_serial_sequence('exercises','id'));",
                (resultSet, i) -> resultSet.getInt(i + 1));
        log.info("Exercise '" + name + "' inserted in Table 'exercises' with " +
                "Id "
                + id.get(0) + " !");
    }

    /**
     * todo
     *
     * @return
     */
    public List<Exercise> getAllExercises() {
        String sql = "SELECT * FROM exercises";
        List<Exercise> toReturn = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(Exercise.class));
        return toReturn;
    }

    /**
     * todo
     *
     * @param id
     * @return
     */
    public Exercise getExerciseById(int id) {
        LinkedList<Exercise> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM exercises WHERE id = ?",
                new Integer[]{id},
                new BeanPropertyRowMapper<>(Exercise.class)));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    /**
     * todo
     *
     * @param id
     */
    public void deleteExerciseById(int id) {
        Exercise toDelete = getExerciseById(id);
        if (toDelete != null) {
            jdbcTemplate.update("DELETE FROM exercises WHERE id = ?",
                    new Integer[]{id});
            log.info("Exercise '" + toDelete.getName() + "' with ID: '"
                    + toDelete.getId() + "' deleted!");
        }
    }

    /**
     * todo
     *
     * @param name
     * @return
     */
    public List<Exercise> getExercisesByName(String name) {
        if (name != null && !name.isEmpty()) {
            String sql = String.format("SELECT * FROM exercises WHERE name " +
                    "LIKE '%%%s%%'", name);
            LinkedList<Exercise> toReturn = new LinkedList<>(jdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(Exercise.class)));
            return toReturn;
        }
        return null;
    }
}
