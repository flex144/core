package de.ep.team2.core.service;

import de.ep.team2.core.CoreApplication;
import de.ep.team2.core.entities.*;
import de.ep.team2.core.enums.WeightType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
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
                "SELECT id, email, first_name, last_name, password, role FROM users WHERE " +
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
        String sql = "SELECT id, email, first_name, last_name, role FROM users";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    /**
     * Adds a new User to the Table 'users'.
     * Transforms the Email to lowercase.
     *
     * @param email     Email of the User.
     * @param firstName First Name of the User.
     * @param lastName  Last Name of the User.
     * @param password  Password, as Hash, of the User.
     */
    public void insertUser(String email, String firstName, String lastName, String password) {
        Object[] toInsert = {email.toLowerCase(), firstName, lastName, password, true, "ROLE_USER"};
        if (getUserByEmail(email) != null) {
            log.info("Insert User failed! Email " + email + " already in the " +
                    "Database!");
            throw new IllegalArgumentException("Email already in the Database!");
        }
        jdbcTemplate.update("INSERT INTO users(email, first_name, last_name, password, enabled, " +
                "role) " +
                "VALUES (?,?,?,?,?,?)", toInsert);
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
                    (Object[]) new Integer[]{id});
            log.info("User '" + toDelete.getFirstName() + " " + toDelete.getLastName()
                    + "' with mail: '" + toDelete.getEmail() + "' deleted!");
        }
    }

    /**
     * Promotes a regular user to a moderator.
     *
     * @param id user to promote.
     */
    public void changeToMod(Integer id) {
        User toChange = getUserById(id);
        if (toChange != null) {
            jdbcTemplate.update("UPDATE users SET role = 'ROLE_MOD' WHERE id = ?", id);
            log.info("User '" + toChange.getFirstName() + " " + toChange.getLastName() + "' is " +
                    "now Mod!");
        }
    }

    // Exercise

    /**
     * Inserts a new Exercise into the Table.
     * The name has to be Unique.
     * Also saves the images of the exercise in the table images.
     *
     * @param name        Unique name of the Exercise
     * @param description Optional Description of the Exercise.
     * @param weightType  weight type of the exercise.
     * @param videoLink   optional videolink to a example video.
     * @param imgPaths    Optional List of the Paths to the images
     *                    of the Exercise with type(muscle , other).
     * @return The ID of the inserted exercise.
     */
    public Integer insertExercise(String name, String description, WeightType weightType,
                                  String videoLink, List<String[]> imgPaths) {
        if (exerciseNameUnique(name)) {
            String[] toInsert = {name, description, weightType.toString(), videoLink};
            jdbcTemplate.update(
                    "INSERT INTO exercises(name, description, weight_type, video_link)" +
                            " VALUES (?,?,?,?)", (Object[]) toInsert);
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
                "SELECT * FROM exercises WHERE lower(name) = ?",
                new String[]{name.toLowerCase()},
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
                    (Object[]) new Integer[]{id});
            jdbcTemplate.update("DELETE FROM exercises WHERE id = ?",
                    (Object[]) new Integer[]{id});
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
     * null if the Parameter was empty.
     */
    public List<Exercise> getExerciseListByName(String name) {
        if (name != null && !name.isEmpty()) {
            String sql = String.format("SELECT * FROM exercises WHERE lower(name) " +
                    "LIKE '%%%s%%'", name.toLowerCase());
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

    // TrainingsPlanTemplate

    public Integer insertPlanTemplate(String name, String descpription,
                                      String author, Boolean oneShotPlan,
                                      Integer numTrainSessions, Integer exercisesPerSession) {
        if (name == null || author == null || (oneShotPlan && numTrainSessions > 1)) {
            throw new IllegalArgumentException();
        } else {
            Object[] insertValues = new Object[]{name, descpription, author, oneShotPlan,
                    numTrainSessions,
                    exercisesPerSession};
            jdbcTemplate.update("insert into plan_templates(name,description," +
                    "author,one_shot_plan,num_train_sessions," +
                    "exercises_per_session) values (?,?,?,?,?,?)", insertValues);
            Integer id = jdbcTemplate.query("select currval" +
                            "(pg_get_serial_sequence('plan_templates','id'));",
                    (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
            log.info("Plan Template '" + name + "' created with Id: " + id + " !");
            return id;
        }
    }

    public TrainingsPlanTemplate getPlanTemplateByID(Integer id) {
        LinkedList<TrainingsPlanTemplate> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM plan_templates WHERE id = ?",
                new Integer[]{id},
                (resultSet, i) -> new TrainingsPlanTemplate(id, resultSet.getString("name"),
                        resultSet.getString(
                                "description"), getUserByEmail(resultSet.getString("author")),
                        resultSet.getBoolean("one_shot_plan"), resultSet.getInt(
                        "num_train_sessions"),
                        resultSet.getInt("exercises_per_session"), getSessionsOfTemplateWithInstances(id))));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    public boolean isTemplateInDatabase(String name) {
        LinkedList<Integer> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT id FROM plan_templates WHERE name = ?",
                new String[]{name},
                (resultSet, i) -> 1));
        return !toReturn.isEmpty();
    }

    public void deletePlanTemplateByID(Integer id) {
        TrainingsPlanTemplate toDelete = getPlanTemplateByID(id);
        if (toDelete != null) {
            jdbcTemplate.update("DELETE FROM plan_templates WHERE id = ?",
                    (Object[]) new Integer[]{id});
            log.info("Plan Template '" + toDelete.getName() + "' with ID: '"
                    + toDelete.getId() + "' deleted!");
        }
    }

    public Integer increaseNumOfExercises(Integer idOfTemplate) {
       Integer numOfExes = new LinkedList<>(jdbcTemplate.query(
                "SELECT exercises_per_session FROM plan_templates WHERE id = ?",
                new Integer[]{idOfTemplate},
                (resultSet,i) -> resultSet.getInt("exercises_per_session"))).getFirst();
        numOfExes++;
        jdbcTemplate.update("update plan_templates set exercises_per_session = ? where id = ?",
                (Object[]) new Integer[]{numOfExes,idOfTemplate});
        return numOfExes;
    }

    public Integer decreaseNumOfExercises(Integer idOfTemplate) {
        Integer numOfExes = new LinkedList<>(jdbcTemplate.query(
                "SELECT exercises_per_session FROM plan_templates WHERE id = ?",
                new Integer[]{idOfTemplate},
                (resultSet,i) -> resultSet.getInt("exercises_per_session"))).getFirst();
        numOfExes--;
        jdbcTemplate.update("update plan_templates set exercises_per_session = ? where id = ?",
                (Object[]) new Integer[]{numOfExes,idOfTemplate});
        return numOfExes;
    }

    // Trainings Session

    public Integer insertTrainingsSession(int planTemplate, int ordering) {
        if (sessionOrderValid(planTemplate, ordering)) {
            Integer[] insertValues = new Integer[]{planTemplate, ordering};
            jdbcTemplate.update("insert into trainings_sessions(plan_template,ordering) values " +
                            "(?,?)",
                    (Object[]) insertValues);
            Integer id = jdbcTemplate.query("select currval" +
                            "(pg_get_serial_sequence('trainings_sessions','id'));",
                    (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
            log.info("Trainings Session created with Id: " + id + " !");
            return id;
        } else {
            throw new IllegalArgumentException("Order of The Session Wrong! Order Already exists " +
                    "or isn't in the valid number range!");
        }
    }

    private boolean sessionOrderValid(int idPlanTemplate, int ordering) {
        if (ordering > 15 || ordering < 1) {
            return false;
        }
        TrainingsPlanTemplate planTemplate = getPlanTemplateByID(idPlanTemplate);
        boolean orderingNotPresent = true;
        if (!planTemplate.getTrainingsSessions().isEmpty()) {
            for (TrainingsSession ts : planTemplate.getTrainingsSessions()) {
                orderingNotPresent = (ts.getOrdering() != ordering);
            }
        }
        return orderingNotPresent;
    }

    public TrainingsSession getTrainingsSessionById(int id) {
        LinkedList<TrainingsSession> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM trainings_sessions WHERE id = ?",
                new Integer[]{id},
                (resultSet, i) -> new TrainingsSession(id, resultSet.getInt("plan_template"),
                        resultSet.getInt("ordering"), getExInstancesOfSession(id))));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }


    public LinkedList<TrainingsSession> getSessionsOfTemplateWithInstances(int idOfTemplate) {
        LinkedList<TrainingsSession> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM trainings_sessions WHERE plan_template = ? ORDER BY ordering",
                new Integer[]{idOfTemplate},
                (resultSet, i) -> new TrainingsSession(resultSet.getInt("id"),
                        idOfTemplate,
                        resultSet.getInt("ordering"), getExInstancesOfSession(resultSet.getInt(
                        "id")))));
        return toReturn;
    }

    public LinkedList<TrainingsSession> getOnlySessionsOfTemplate(int idOfTemplate) {
        LinkedList<TrainingsSession> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM trainings_sessions WHERE plan_template = ? ORDER BY ordering",
                new Integer[]{idOfTemplate},
                (resultSet, i) -> new TrainingsSession(resultSet.getInt("id"),
                        idOfTemplate,
                        resultSet.getInt("ordering"), null)));
        return toReturn;
    }

    // Exercises Instances

    private LinkedList<ExerciseInstance> getExInstancesOfSession(int idOfSession) {
        return new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM exercise_instances WHERE trainings_session = ?",
                new Integer[]{idOfSession},
                (resultSet, i) -> {
                    Integer[] reps = new Integer[7];
                    for (int j = 1; j < 8; j++) {
                        reps[j-1] = resultSet.getInt(String.format("reps_ex%d",j));
                    }
                    return new ExerciseInstance(resultSet.getInt("is_exercise"),
                            resultSet.getInt("trainings_session"), resultSet.getInt("id"),
                            resultSet.getInt("rep_maximum"), resultSet.getInt("sets"), reps,
                            resultSet.getString("tempo"), resultSet.getInt("pause"));
                }));
    }

    public Integer insertExerciseInstance(int isExerciseID, int trainingsSessionID,
                                          int repetitionMaximum, int sets, Integer[] reps,
                                          String tempo, Integer pauseInSec) {
        ArrayList<Object> insertValues = new ArrayList<>();
        insertValues.ensureCapacity(13);
        insertValues.add(isExerciseID);
        insertValues.add(trainingsSessionID);
        insertValues.add(repetitionMaximum);
        insertValues.add(sets);
        insertValues.add(tempo);
        insertValues.add(pauseInSec);
        if (reps.length > 7) {
            throw new IllegalArgumentException("to many sets!");
        }
        insertValues.addAll(Arrays.asList(reps));
        for (int i = insertValues.size(); i < 13; i++) {
            insertValues.add(null);
        }
        jdbcTemplate.update("INSERT INTO exercise_instances(is_exercise, trainings_session, " +
                "rep_maximum, sets, tempo, pause, reps_ex1, reps_ex2, reps_ex3, reps_ex4, " +
                "reps_ex5, reps_ex6, reps_ex7) values (?,?,?,?,?,?,?,?,?,?,?,?,?)",
                insertValues.toArray());
        Integer id = jdbcTemplate.query("select currval" +
                        "(pg_get_serial_sequence('exercise_instances','id'));",
                (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
        log.info("Exercise Instance created with Id: " + id + " !");
        return id;
    }
}
