package de.ep.team2.core.service;

import de.ep.team2.core.entities.*;
import de.ep.team2.core.enums.WeightType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    private static final Logger log = LoggerFactory.getLogger(DataBaseService.class);

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
                "SELECT id, email, first_name, last_name, role FROM users WHERE id " +
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
        if (email == null) {
            return null;
        } else {
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
    public Integer insertUser(String email, String firstName, String lastName, String password) {
        Object[] toInsert = {email.toLowerCase(), firstName, lastName, password, true, "ROLE_USER"};
        if (getUserByEmail(email) != null) {
            log.debug("Insert User failed! Email " + email + " already in the " +
                    "Database!");
            throw new IllegalArgumentException("Email already in the Database!");
        }
        jdbcTemplate.update("INSERT INTO users(email, first_name, last_name, password, enabled, " +
                "role) " +
                "VALUES (?,?,?,?,?,?)", toInsert);
        Integer id = jdbcTemplate.query("select currval" +
                        "(pg_get_serial_sequence('users','id'));",
                (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
        log.debug("User '" + firstName + " " + lastName + "' with mail: '"
                + email + "' inserted in Table 'users' with Id "
                + id + " !");
        return id;
    }

    /**
     * Deletes a user in the table with the given id.
     *
     * @param id user to delete.
     */
    public void deleteUserById(Integer id) {
        User toDelete = getUserById(id);
        if (toDelete != null) {
            deleteUserFromPlan(toDelete.getEmail());
            jdbcTemplate.update("DELETE FROM users WHERE id = ?",
                    (Object[]) new Integer[]{id});
            User deleter = (User) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            log.debug("User '" + toDelete.getFirstName() + " " + toDelete.getLastName()
                    + "' with mail: '" + toDelete.getEmail()
                    + "' deleted by " + deleter.getEmail() + "!");
        }
    }

    //checks if user is author of a plan template and deletes him from the plan
    private void deleteUserFromPlan (String email) {
        jdbcTemplate.update("UPDATE plan_templates SET author = NULL WHERE " +
                "author = ?", email);
    }

    /**
     * Promotes a regular user to a moderator.
     *
     * @param id user to promote.
     */
    public void changeToMod(Integer id) {
        User toChange = getUserById(id);
        String changerMail;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            User changer = (User) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            changerMail = changer.getEmail();
        } else {
            changerMail = "Default";
        }
        if (toChange != null) {
            jdbcTemplate.update("UPDATE users SET role = 'ROLE_MOD' WHERE id = ?", id);
            log.debug("User '" + toChange.getEmail() + "' was " +
                        "upgraded to Mod by " + changerMail + "!");
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
            log.debug("Exercise '" + name + "' inserted in Table 'exercises' with " +
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
            User deleter = (User) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            log.debug("Exercise '" + toDelete.getName() + "' with ID: '"
                    + toDelete.getId() + "' deleted by " + deleter.getEmail() + "!");
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

    /**
     * Inserts a new Trainings plan Template in teh Database with the provided values.
     *
     * @param name unique name of the template.
     * @param trainingsFocus focus of the trainingsplan.
     * @param author email of the mod who created the template.
     * @param oneShotPlan boolean if the plan has more than one trainings session.
     * @param numTrainSessions duration of the plan in sessions.
     * @param exercisesPerSession how many exercises have to be done in one session.
     *                            (number increases initial 1)
     * @return id od the just inserted template.
     */
    public Integer insertPlanTemplate(String name, String trainingsFocus,
                                      String author, Boolean oneShotPlan,
                                      Integer numTrainSessions, Integer exercisesPerSession) {
        if (name == null || author == null || (oneShotPlan && numTrainSessions > 1)) {
            throw new IllegalArgumentException();
        } else {
            Object[] insertValues = new Object[]{name, trainingsFocus, author, oneShotPlan,
                    numTrainSessions,
                    exercisesPerSession};
            jdbcTemplate.update("insert into plan_templates(name,trainings_focus," +
                    "author,one_shot_plan,num_train_sessions," +
                    "exercises_per_session) values (?,?,?,?,?,?)", insertValues);
            Integer id = jdbcTemplate.query("select currval" +
                            "(pg_get_serial_sequence('plan_templates','id'));",
                    (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
            log.debug("Plan Template '" + name + "' created with Id: " + id + " !");
            return id;
        }
    }

    class PlanTemplateMapperNoChildren implements RowMapper<TrainingsPlanTemplate> {

        @Override
        public TrainingsPlanTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TrainingsPlanTemplate(rs.getInt("id"), rs.getString("name"),
                    rs.getString("trainings_focus"), getUserByEmail(rs.getString("author")),
                    rs.getBoolean("one_shot_plan"), rs.getInt("num_train_sessions"),
                    rs.getInt("exercises_per_session"), null);
        }

    }

    /**
     * Searches for a Template and appends all connected exercises and Sessions by searching for them too.
     *
     * @param id id of the the template to search for.
     * @return TrainingsPlanTemplate object with all connected exercises and Sessions.
     */
    public TrainingsPlanTemplate getPlanTemplateAndSessionsByID(int id) {
        LinkedList<TrainingsPlanTemplate> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM plan_templates WHERE id = ?",
                new Integer[]{id},
                (resultSet, i) -> new TrainingsPlanTemplate(id, resultSet.getString("name"),
                        resultSet.getString(
                                "trainings_focus"), getUserByEmail(resultSet.getString("author")),
                        resultSet.getBoolean("one_shot_plan"), resultSet.getInt(
                        "num_train_sessions"),
                        resultSet.getInt("exercises_per_session"),
                        getExInstancesOfTemplate(id))));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    /**
     * Searches for an Template but returns only the template, doesn't search for the connected exercises and sessions.
     *
     * @param id id of the the template to search for.
     * @return TrainingsPlanTemplate object.
     */
    public TrainingsPlanTemplate getOnlyPlanTemplateById(int id) {
        LinkedList<TrainingsPlanTemplate> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM plan_templates WHERE id = ?",
                new Integer[]{id},
                new PlanTemplateMapperNoChildren()));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    /**
     * Returns a List of Templates without their children where the name matches.
     *
     * @param name name to look for.
     * @return List of the found exercises.
     */
    public LinkedList<TrainingsPlanTemplate> getOnlyPlanTemplateListByName(String name) {
        String sql = String.format("SELECT * FROM plan_templates WHERE lower(name) " +
                "LIKE '%%%s%%'", name.toLowerCase());
        return new LinkedList<>(jdbcTemplate.query(sql, new PlanTemplateMapperNoChildren()));
    }

    /**
     * Returns a List of all saved Templates without their children.
     *
     * @return List of all Templates without their children.
     */
    public LinkedList<TrainingsPlanTemplate> getAllPlanTemplatesNoChildren() {
        return new LinkedList<>(jdbcTemplate.query("Select * FROM plan_templates",
                new PlanTemplateMapperNoChildren()));
    }

    /**
     * renames a template in the Database.
     *
     * @param newName new name of the template.
     * @param idToRename id of the template to be renamed.
     */
    public void renameTemplate(String newName, int idToRename) {
        jdbcTemplate.update("update plan_templates set name = ? where id = ?",
                newName, idToRename);
    }

    /**
     * sets a new focus on a template in the database.
     *
     * @param newFocus new focus of the template.
     * @param idToRename id of the template to be altered.
     */
    public void changeTrainingsFocus(String newFocus, int idToRename) {
        jdbcTemplate.update("update plan_templates set trainings_focus = ? where id = ?",
                newFocus, idToRename);
    }

    /**
     * checks if a template with the given name is already in teh database.
     *
     * @param name name to look for.
     * @return true if name is already in the database, false otherwise.
     */
    public boolean isTemplateInDatabase(String name) {
        LinkedList<Integer> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT id FROM plan_templates WHERE name = ?",
                new String[]{name},
                (resultSet, i) -> 1));
        return !toReturn.isEmpty();
    }

    /**
     * deletes a Template form the database.
     * Children will not be deleted, they have to be deleted beforehand.
     *
     * @param id id of the template to be deleted.
     */
    public void deletePlanTemplateByID(int id) {
        TrainingsPlanTemplate toDelete = getOnlyPlanTemplateById(id);
        if (toDelete != null) {
            jdbcTemplate.update("DELETE FROM plan_templates WHERE id = ?",
                    (Object[]) new Integer[]{id});
            User deleter = (User) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            log.debug("Plan Template '" + toDelete.getName() + "' with ID: '"
                    + toDelete.getId() + "' deleted by " + deleter.getEmail() + "!");
        }
    }

    /**
     * Increases the value of number of exercises by 1 and returns the value after the increase.
     *
     * @param idOfTemplate id of template where the value should be increased.
     * @return number of exercises after the increase.
     */
    public Integer increaseNumOfExercises(int idOfTemplate) {
        Integer numOfExes = new LinkedList<>(jdbcTemplate.query(
                "SELECT exercises_per_session FROM plan_templates WHERE id = ?",
                new Integer[]{idOfTemplate},
                (resultSet, i) -> resultSet.getInt("exercises_per_session"))).getFirst();
        numOfExes++;
        jdbcTemplate.update("update plan_templates set exercises_per_session = ? where id = ?",
                (Object[]) new Integer[]{numOfExes, idOfTemplate});
        return numOfExes;
    }

    /**
     * Decreases the value of number of exercises by 1 an returns the value after the decrease.
     *
     * @param idOfTemplate id of template where the value should be decreased.
     * @return number of exercises after the decrease.
     */
    public Integer decreaseNumOfExercises(int idOfTemplate) {
        Integer numOfExes = new LinkedList<>(jdbcTemplate.query(
                "SELECT exercises_per_session FROM plan_templates WHERE id = ?",
                new Integer[]{idOfTemplate},
                (resultSet, i) -> resultSet.getInt("exercises_per_session"))).getFirst();
        numOfExes--;
        jdbcTemplate.update("update plan_templates set exercises_per_session = ? where id = ?",
                (Object[]) new Integer[]{numOfExes, idOfTemplate});
        return numOfExes;
    }

    // Exercises Instances

    /**
     * Returns a List of all exercise instances which are connected to a given template.
     * Also searches for the Sessions of the Exercise instances and adds them to the Object.
     *
     * @param idOfTemplate id of the template whose exercise instances should be returned.
     * @return Linked List of the Instances with the sessions of this exercise connected to it.
     */
    public LinkedList<ExerciseInstance> getExInstancesOfTemplate(int idOfTemplate) {
        return new LinkedList<>(jdbcTemplate.query(
                "SELECT ei.id, ei.is_exercise, ei.category, ex.name" +
                        " FROM exercise_instances ei, exercises ex " +
                        " WHERE ei.plan_template = ? " +
                        " AND ei.is_exercise = ex.id ",
                new Integer[]{idOfTemplate},
                (resultSet, i) -> new ExerciseInstance(idOfTemplate, resultSet.getInt(
                        "is_exercise"), resultSet.getInt("id"), resultSet.getString("category"),
                        getTagsOfExInstance(resultSet.getInt("id")),
                        getSessionsOfExerciseInstance(resultSet.getInt("id")),
                        resultSet.getString("name"))));
    }

    private LinkedList<String> getTagsOfExInstance(int id) {
        return new LinkedList<>(jdbcTemplate.query("SELECT et.name FROM execution_tags et," +
                        "ex_tags_map etm WHERE etm.ex_inst_id = ? AND etm.ex_tag_id = et.id",
                new Integer[]{id},
                (rs, i) -> rs.getString("name")));
    }

    /**
     * Inserts a new Exercise instance in the database with the provided data.
     *
     * @param isExerciseID reference to the table exercises which Exercise this exercise instance is.
     * @param category category of the exercise instance(tells in which order the exercises should be done)
     * @param tags tags for the execution of the exercise.
     * @param templateId id of the plan template which contains this exercise instance.
     * @return Id of the just inserted exercise instance.
     */
    public Integer insertExerciseInstance(int isExerciseID, String category, LinkedList<String> tags,
                                          int templateId) {
        Object[] insertValues = new Object[]{isExerciseID, category, templateId};
        jdbcTemplate.update("INSERT INTO exercise_instances(is_exercise, category, " +
                        "plan_template) values (?,?,?)",
                insertValues);
        Integer id = jdbcTemplate.query("select currval" +
                        "(pg_get_serial_sequence('exercise_instances','id'));",
                (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
        addTagsToExercise(tags, id);
        log.debug("Exercise Instance created with Id: " + id + " !");
        return id;
    }

    private void addTagsToExercise(LinkedList<String> tags, int idOfEx) {
        LinkedList<String> presentTags = getAllTagNames();
        for (String tag : tags) {
            int idOfTag;
            if (presentTags.contains(tag)) {
                 idOfTag = jdbcTemplate.query("SELECT id FROM execution_tags WHERE name = ?",
                        new String[]{tag}, (rs, i) -> rs.getInt("id")).get(0);
            } else {
                jdbcTemplate.update("INSERT INTO execution_tags(name) values (?)",
                        (Object[]) new String[]{tag});
                idOfTag = jdbcTemplate.query("select currval" +
                                "(pg_get_serial_sequence('execution_tags','id'));",
                        (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
            }
            jdbcTemplate.update("INSERT INTO ex_tags_map(ex_inst_id, ex_tag_id) values (?,?)",
                    (Object[]) new Integer[]{idOfEx, idOfTag});
        }
    }

    /**
     * Returns the names of all tags saved in the database.
     *
     * @return the names of all tags saved in the database.
     */
    public LinkedList<String> getAllTagNames() {
        return new LinkedList<>(jdbcTemplate.query("SELECT name FROM execution_tags",
                (rs, i) -> rs.getString("name")));
    }

    /**
     * Gets an Exercise instance by id and appends all linked trainings sessions.
     *
     * @param id id exercise instance to search for.
     * @return exercise instance with children, null when nothing was found with this id.
     */
    public ExerciseInstance getExercisInstanceById(int id) {
        LinkedList<ExerciseInstance> toReturn =  new LinkedList<>(jdbcTemplate.query(
                "SELECT ei.id, ei.is_exercise, ei.category, ei.plan_template, ex.name" +
                        " FROM exercise_instances ei, exercises ex " +
                        " WHERE ei.id = ? " +
                        " AND ei.is_exercise = ex.id ",
                new Integer[]{id},
                (resultSet, i) -> new ExerciseInstance(resultSet.getInt("plan_template"), resultSet.getInt(
                        "is_exercise"), resultSet.getInt("id"), resultSet.getString("category"),
                        getTagsOfExInstance(resultSet.getInt("id")),
                        getSessionsOfExerciseInstance(resultSet.getInt("id")),
                        resultSet.getString("name"))));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    public LinkedList<ExerciseInstance> getInstancesOfExercise(int idOfExercise) {
        return new LinkedList<>(jdbcTemplate.query(
                "SELECT ei.id, ei.is_exercise, ei.category, ei.plan_template, ex.name" +
                        " FROM exercise_instances ei, exercises ex " +
                        " WHERE ei.is_exercise = ? " +
                        " AND ei.is_exercise = ex.id ",
                new Integer[]{idOfExercise},
                (resultSet, i) -> new ExerciseInstance(resultSet.getInt("plan_template"), resultSet.getInt(
                        "is_exercise"), resultSet.getInt("id"), resultSet.getString("category"),
                        getTagsOfExInstance(resultSet.getInt("id")),
                        getSessionsOfExerciseInstance(resultSet.getInt("id")),
                        resultSet.getString("name"))));
    }

    /**
     * Deletes an Exercise instance, with the given id, from the database.
     * (If the instance exists and has no children anymore)
     * Children will not be deleted, they have to be deleted beforehand.
     *
     * @param id id of exercise instance to delete.
     */
    public void deleteExerciseInstanceByID(int id) {
        ExerciseInstance toDelete = getExercisInstanceById(id);
        if (toDelete != null && toDelete.getTrainingsSessions().isEmpty()) {
            jdbcTemplate.update("DELETE FROM exercise_instances WHERE id = ?",
                    (Object[]) new Integer[]{id});
            User deleter = (User) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            log.debug("Exercise instance with id " + id + " deleted by " + deleter.getEmail() + "!");
        } else {
            throw new IllegalArgumentException("Exercise Instance doesn't exist or still has dependent children");
        }
    }

    // Trainings Session

    /**
     * Inserts a new Trainings session in the Database with the provided data.
     *
     * @param exerciseInstanceID id of the exercise instance the session belongs to.
     * @param ordering indicates in which sequence the sessions have to be performed.
     * @param repetitionMaximum used to determine the weights for each individual user.
     * @param sets number of the sets.
     * @param reps array with the size {@code sets} where the repetitions of the single sets are saved.
     * @param tempo tempo in which the exercise should be executed.
     * @param pauseInSec pause time between the sets in seconds.
     * @return Id of the just inserted Trainings session.
     */
    public Integer insertTrainingsSession(int exerciseInstanceID, int ordering,
                                          int repetitionMaximum, int sets, Integer[] reps,
                                          String tempo, Integer pauseInSec) {
        ArrayList<Object> insertValues = new ArrayList<>();
        insertValues.ensureCapacity(13);
        insertValues.add(exerciseInstanceID);
        insertValues.add(ordering);
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
        jdbcTemplate.update(
                "insert into trainings_sessions(exercise_instance, ordering, rep_maximum, " +
                        "sets, tempo, pause, reps_set1, reps_set2, reps_set3, reps_set4,reps_set5," +
                        " reps_set6, reps_set7) values " +
                        "(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                insertValues.toArray());
        Integer id = jdbcTemplate.query("select currval" +
                        "(pg_get_serial_sequence('trainings_sessions','id'));",
                (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
        log.debug("Trainings Session created with Id: " + id + " !");
        return id;
    }

    /**
     * returns a list of all Sessions connected to a specific exercise instance.
     *
     * @param idOfInstance id of the exercise instance.
     * @return linked list of all to the instance connected sessions.
     */
    public LinkedList<TrainingsSession> getSessionsOfExerciseInstance(int idOfInstance) {
        return new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM trainings_sessions WHERE exercise_instance = ?",
                new Integer[]{idOfInstance},
                (resultSet, i) -> {
                    Integer[] reps = new Integer[7];
                    for (int j = 1; j < 8; j++) {
                        reps[j - 1] = resultSet.getInt(String.format("reps_set%d", j));
                    }
                    return new TrainingsSession(resultSet.getInt("id"), resultSet.getInt("ordering"),
                            resultSet.getInt("exercise_instance"),
                            resultSet.getInt("rep_maximum"), resultSet.getInt("sets"), reps,
                            resultSet.getString("tempo"), resultSet.getInt("pause"));
                }));
    }

    /**
     * Searches for a TrainingsSession with a the given id.
     *
     * @param id id to look for.
     * @return The TrainingsSession if found otherwise null.
     */
    public TrainingsSession getTrainingsSessionById(int id) {
        LinkedList<TrainingsSession> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM trainings_sessions WHERE id = ?",
                new Integer[]{id},
                (resultSet, i) -> {
                    Integer[] reps = new Integer[7];
                    for (int j = 1; j < 8; j++) {
                        reps[j - 1] = resultSet.getInt(String.format("reps_set%d", j));
                    }
                    return new TrainingsSession(id, resultSet.getInt("ordering"),
                            resultSet.getInt("exercise_instance"),
                            resultSet.getInt("rep_maximum"), resultSet.getInt("sets"), reps,
                            resultSet.getString("tempo"), resultSet.getInt("pause"));
                }));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    /**
     * Deletes an trainingSession, with the given id, from the database.
     *
     * @param id id of trainings session to delete.
     */
    public void deleteTrainingsSessionById(int id) {
        if (getTrainingsSessionById(id) != null) {
            jdbcTemplate.update("DELETE FROM trainings_sessions WHERE id = ?",
                    (Object[]) new Integer[]{id});
            User deleter = (User) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            log.debug("Trainings-session with ID: '"
                    + id + "' deleted by " + deleter.getEmail() + "!");
        } else {
            throw new  IllegalArgumentException("Trainingssession doesn't exist!");
        }
    }


}
