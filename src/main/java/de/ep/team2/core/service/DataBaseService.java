package de.ep.team2.core.service;


import de.ep.team2.core.CoreApplication;
import de.ep.team2.core.entities.Exercise;
import de.ep.team2.core.entities.User;
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
    public void insertUser(String email, String firstName, String lastName) {
        String mail = email.toLowerCase();
        if (getUserByEmail(mail) != null) {
            log.info("Insert User failed! Email " + email + " already in the " +
                    "Database!");
            throw new IllegalArgumentException("Email already in the Database!");
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
     * Inserts a new Exercise into the Table.
     * The name has to be Unique.
     * Also saves the images of the exercise in the table images.
     *
     * @param name Unique name of the Exercise
     * @param description Optional Description of the Exercise.
     * @param imgPaths Optional List of the Paths to the images
     *                of the Exercise with type(muscle , other).
     * @return The ID of the inserted exercise.
     */
    public Integer insertExercise(String name, String description,
                               List<String[]> imgPaths) {
        if (exerciseNameUnique(name)) {
            String[] toInsert = {name, description};
            jdbcTemplate.update("INSERT INTO exercises(name, description) VALUES (?,?)",
                    toInsert);
            Integer id = jdbcTemplate.query("select currval" +
                            "(pg_get_serial_sequence('exercises','id'));",
                    (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
            if (imgPaths != null) {
                for (String[] s : imgPaths) {
                    Object[] insert = {id, s[0], s[1]};
                    jdbcTemplate.update(
                            "insert into images(exercise, path, img_type) values (?,?,?)",
                            insert);
                }
            }
            log.info("Exercise '" + name + "' inserted in Table 'exercises' with " +
                    "Id "
                    + id + " !");
            return id;
        } else {
            throw new IllegalArgumentException("Exercise name already in use");
        }
    }

    boolean exerciseNameUnique(String name) {
        return getExerciseByName(name) == null;
    }

    /**
     * Returns a List of all Exercises stored in the DB.
     *
     * @return List of all stored Exercises
     */
    public List<Exercise> getAllExercises() {
        String sql = "SELECT * FROM exercises";
        return addImagesToBean(new LinkedList<>(jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(Exercise.class))));
    }

    /**
     * Searches for an Exercise with a specific ID an returns it.
     *
     * @param id ID to search for
     * @return Exercise object representing the Database entry.
     */
    public Exercise getExerciseById(int id) {
        LinkedList<Exercise> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM exercises WHERE id = ?",
                new Integer[]{id},
                new BeanPropertyRowMapper<>(Exercise.class)));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return addImagesToBean(toReturn).getFirst();
        }
    }



    /**
     * Searches for an Exercise with this exact name.
     *
     * @param name name to search for.
     * @return Exercise object representing the Database entry.
     */
    public Exercise getExerciseByName(String name) {
        LinkedList<Exercise> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM exercises WHERE exercises.name = ?",
                new String[]{name},
                new BeanPropertyRowMapper<>(Exercise.class)));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return addImagesToBean(toReturn).getFirst();
        }
    }

    /**
     * Deletes the entry, in the Table exercises, with the given id.
     *
     * @param id id of the Exercise to delete.
     */
    public void deleteExerciseById(int id) {
        Exercise toDelete = getExerciseById(id);
        if (toDelete != null) {
            jdbcTemplate.update("DELETE FROM images WHERE exercise = ?",
                    new Integer[]{id});
            jdbcTemplate.update("DELETE FROM exercises WHERE id = ?",
                    new Integer[]{id});
            log.info("Exercise '" + toDelete.getName() + "' with ID: '"
                    + toDelete.getId() + "' deleted!");
        }
    }

    /**
     * Searches for all exercises in the Database,
     * where the given String {@code name} is part of the name.
     *
     * @param name String name fragment to search for.
     * @return List of all found exercises; empty list if nothing was found;
     *          null if the Parameter was empty.
     */
    public List<Exercise> getExerciseListByName(String name) {
        if (name != null && !name.isEmpty()) {
            String sql = String.format("SELECT * FROM exercises WHERE name " +
                    "LIKE '%%%s%%'", name);
            LinkedList<Exercise> toReturn = new LinkedList<>(jdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(Exercise.class)));
            return addImagesToBean(toReturn);
        }
        return null;
    }

    /**
     * Used to create Exercise objects when retrieving them from the database.
     * Adds all Imagepaths of all Exercises in the List
     * to the Exercise object by running additional sql queries.
     *
     * @param exercises List where the images have to be added.
     * @return List of exercises with its img paths attached.
     */
    private LinkedList<Exercise> addImagesToBean(LinkedList<Exercise> exercises) {
        for (Exercise exercise : exercises) {
            LinkedList<String[]> paths = new LinkedList<>(jdbcTemplate.query(
                    "SELECT path, img_type FROM images WHERE exercise = ?",
                    new Integer[]{exercise.getId()},
                    (rs, rowNum) -> new String[]{rs.getString("path"), rs.getString("img_type")}));
            if (!paths.isEmpty()) {
                for (String[] s : paths) {
                   if (s[1].equals("muscle")) {
                       exercise.addMuscleImgPath(s[0]);
                   } else {
                       exercise.addOtherImgPath(s[0]);
                   }
                }
            }
        }
        return exercises;
    }
}
