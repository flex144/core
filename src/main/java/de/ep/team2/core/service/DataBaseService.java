package de.ep.team2.core.service;

import de.ep.team2.core.entities.*;
import de.ep.team2.core.enums.ExperienceLevel;
import de.ep.team2.core.enums.Gender;
import de.ep.team2.core.enums.TrainingsFocus;
import de.ep.team2.core.enums.WeightType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
                "SELECT * FROM users WHERE id " +
                        "= ?",
                new Integer[]{id},
                new UserRowMapper()
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
                    "SELECT * FROM users WHERE " +
                            "email = ?",
                    new String[]{email.toLowerCase()},
                    new UserRowMapper()));
            if (toReturn.isEmpty()) {
                return null;
            } else {
                return toReturn.getFirst();
            }
        }
    }

    class UserRowMapper implements RowMapper<User> {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setPassword(rs.getString("password"));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setRole(rs.getString("role"));
            user.setHeightInCm(rs.getObject("height_in_cm", Integer.class));
            user.setWeightInKg(rs.getObject("weight_in_kg", Integer.class));
            String genderString = rs.getString("gender");
            if (genderString != null) {
                user.setGender(Gender.getValueByName(genderString));
            }
            String focusString = rs.getString("trainings_focus");
            if (focusString != null) {
                user.setTrainingsFocus(TrainingsFocus.getValueByName(focusString));
            }
            String experienceString = rs.getString("experience");
            if (experienceString != null) {
                user.setExperience(ExperienceLevel.getValueByName(experienceString));
            }
            user.setBirthDate(rs.getDate("birth_date"));
            user.setTrainingsFrequency(rs.getObject("trainings_frequency", Integer.class));
            return user;
        }
    }

    /**
     * Searches in the Database "users" for all Users and adds them to a List.
     *
     * @return Returns a List of Users.
     */
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
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
        Object[] toInsert = {email.toLowerCase(), firstName, lastName, password, false, "ROLE_USER"};
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
     * Updates all provided data in the db to the user. Null values are saved to db too.
     *
     * @param weightInKg weight.
     * @param heightInCm height.
     * @param trainingsFocus Enum TrainingsFocus.
     * @param trainingsFrequency frequency of training.
     * @param gender Enum Gender
     * @param experience Enum Experience
     * @param birthDate Date birthday
     * @param userMail email to identify user.
     */
    public void setAdvancedUserData(Integer weightInKg, Integer heightInCm,
                                    TrainingsFocus trainingsFocus, Integer trainingsFrequency,
                                    Gender gender, ExperienceLevel experience, Date birthDate, String userMail) {
        Object[] values = new Object[8];
        Arrays.fill(values,null);
        values[0] = weightInKg;
        values[1] = heightInCm;
        values[2] = trainingsFocus == null ? "" : trainingsFocus.toString();
        values[3] = trainingsFrequency;
        values[4] = gender == null ? "" : gender.toString();
        values[5] = experience == null ? "" : experience.toString();
        values[6] = birthDate;
        values[7] = userMail.toLowerCase();
        jdbcTemplate.update("update users set weight_in_kg = ?, height_in_cm = ?, trainings_focus" +
                " = ?, trainings_frequency = ?, gender = ?, experience = ?, birth_date = ? where email = ?",
                values);
    }

    /**
     * Deletes a user in the table with the given id.
     *
     * @param id user to delete.
     */
    public void deleteUserById(int id) {
        User toDelete = getUserById(id);
        if (toDelete != null) {
            deleteUserFromPlan(toDelete.getEmail());
            deleteUserPlanOfUser(toDelete);
            jdbcTemplate.update("DELETE FROM users WHERE id = ?",
                    (Object[]) new Integer[]{id});
            User deleter = (User) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            log.debug("User '" + toDelete.getFirstName() + " " + toDelete.getLastName()
                    + "' with mail: '" + toDelete.getEmail()
                    + "' deleted by " + deleter.getEmail() + "!");
        }
    }

    /**
     * Checks if the User to delete has an active plan an if thats the case the deletes the plan from the database.
     *
     * @param user user to delete.
     */
    private void deleteUserPlanOfUser(User user) {
       UserPlan userPlan = getUserPlanByUserMail(user.getEmail());
       if (userPlan != null) {
           deleteUserPlanAndWeightsById(userPlan.getId());
       }
    }

    //checks if user is author of a plan template and deletes him from the plan
    private void deleteUserFromPlan (String email) {
        jdbcTemplate.update("UPDATE plan_templates SET author = NULL WHERE " +
                "author = ?", email.toLowerCase());
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

    /**
     * Enables a user, so he can access the website.
     *
     * @param email Email of the user.
     */
    public void confirmUser(String email) {
        User toChange = getUserByEmail(email);
        if (toChange != null) {
            jdbcTemplate.update("UPDATE users SET enabled = true WHERE email = ?", email.toLowerCase());
            log.debug("Email '" + email + "' is now verificated!");
        }
    }

    /**
     * Adds a new confirmation token to the database.
     *
     * @param confirmationToken Token to be safed.
     */
    public void createConfirmationToken(ConfirmationToken confirmationToken) {
        Object[] token = {confirmationToken.getConfirmationToken(), confirmationToken.getUser(),
                confirmationToken.getCreatedDate()};
        jdbcTemplate.update("INSERT INTO confirmation_token(token, userToConfirm, createdDate) " +
                "VALUES (?,?,?)", token);
        Integer id = jdbcTemplate.query("select currval" +
                        "(pg_get_serial_sequence('confirmation_token','id'));",
                (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
        log.debug("Token for email '" + confirmationToken.getUser() + "' has been created" +
                " with ID: '" + id + "' !");
    }

    /**
     * Searches for a specific Confirmation Token.
     *
     * @param confirmationToken Key of the searched token.
     * @return Searched confirmation token if it's found.
     */
    public ConfirmationToken findConfirmationToken(String confirmationToken) {
        LinkedList<ConfirmationToken> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT id, token, usertoconfirm, createddate FROM confirmation_token WHERE token = ?",
                new String[]{confirmationToken},
                new TokenRowMapper()));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    public void deleteTokenById(int tokenId) {
        jdbcTemplate.update("DELETE FROM confirmation_token WHERE id = ?", tokenId);
    }

    class TokenRowMapper implements RowMapper<ConfirmationToken> {
        public ConfirmationToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConfirmationToken token = new ConfirmationToken();
            token.setTokenId(rs.getInt("id"));
            token.setConfirmationToken(rs.getString("token"));
            token.setUser(rs.getString("usertoconfirm"));
            token.setCreatedDate(rs.getDate("createddate"));
            return token;
        }
    }

    public User changeUserDetails(User user) {
        String email = user.getEmail();
        User toChange = getUserByEmail(email);
        if (toChange != null) {
            if (user.getFirstName() != null) {
                changeDetails("first_name", user.getFirstName(), email);
            }
            if (user.getLastName() != null) {
                changeDetails("last_name", user.getLastName(), email);
            }
            if (user.getPassword() != null) {
                changeDetails("password", user.getPassword(), email);
            }
        }
        return getUserByEmail(email);
    }

    private void changeDetails(String column, String value, String email) {
        jdbcTemplate.update("UPDATE users SET "+column+" = ? WHERE email = ?", value, email.toLowerCase());
        log.debug("User '" + email + "' changed " + column + "!");

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
     * @param name                unique name of the template.
     * @param trainingsFocus      focus of the trainingsplan.
     * @param author              email of the mod who created the template.
     * @param oneShotPlan         boolean if the plan has more than one trainings session.
     * @param numTrainSessions    duration of the plan in sessions.
     * @param exercisesPerSession how many exercises have to be done in one session.
     *                            (number increases initial 1)
     * @return id od the just inserted template.
     */
    public Integer insertPlanTemplate(String name, String trainingsFocus, String targetGroup,
                                      String author, Boolean oneShotPlan, Integer recomSessionsPerWeek,
                                      Integer numTrainSessions, Integer exercisesPerSession) {
        if (name == null || author == null || (oneShotPlan && numTrainSessions > 1)) {
            throw new IllegalArgumentException();
        } else {
            Object[] insertValues = new Object[]{name, trainingsFocus, targetGroup, author.toLowerCase(), oneShotPlan, recomSessionsPerWeek,
                    numTrainSessions,
                    exercisesPerSession};
            jdbcTemplate.update("insert into plan_templates(name,trainings_focus, target_group," +
                    "author,one_shot_plan,recom_sessions_per_week,num_train_sessions," +
                    "exercises_per_session) values (?,?,?,?,?,?,?,?)", insertValues);
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
                    rs.getString("trainings_focus"), rs.getString("target_group"), getUserByEmail(rs.getString("author")),
                    rs.getBoolean("one_shot_plan"), rs.getInt("recom_sessions_per_week"), rs.getInt("num_train_sessions"),
                    rs.getInt("exercises_per_session"), null, rs.getBoolean("complete"));
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
                                "trainings_focus"), resultSet.getString("target_group"), getUserByEmail(resultSet.getString("author")),
                        resultSet.getBoolean("one_shot_plan"), resultSet.getInt("recom_sessions_per_week"), resultSet.getInt(
                        "num_train_sessions"),
                        resultSet.getInt("exercises_per_session"),
                        getExInstancesOfTemplate(id), resultSet.getBoolean("complete"))));
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

    /**
     * Confirms a plan (indicating it's complete), so users can use it.
     * @param idOfTemplate Id of the Template to confirm
     */
    public void confirmPlan(int idOfTemplate) {
        TrainingsPlanTemplate toConfirm = getOnlyPlanTemplateById(idOfTemplate);
        if(toConfirm != null) {
            jdbcTemplate.update("UPDATE plan_templates SET complete = TRUE where id = ?",
                    idOfTemplate);
            String changerMail;
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                User changer = (User) SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal();
                changerMail = changer.getEmail();
            } else {
                changerMail = "Default";
            }
            log.debug("Plan Template '" + toConfirm.getName() + "' with ID: '"
                    + toConfirm.getId() + "' confirmed by " + changerMail + "!");

        }
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
                "SELECT ei.id, ei.is_exercise, ei.category, ex.name, ei.repetition_maximum" +
                        " FROM exercise_instances ei, exercises ex " +
                        " WHERE ei.plan_template = ? " +
                        " AND ei.is_exercise = ex.id ",
                new Integer[]{idOfTemplate},
                (resultSet, i) -> new ExerciseInstance(idOfTemplate, resultSet.getInt(
                        "is_exercise"), resultSet.getInt("id"), resultSet.getString("category"),
                        resultSet.getInt("repetition_maximum"),
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
    public Integer insertExerciseInstance(int isExerciseID, String category, Integer repetitionMaximum, LinkedList<String> tags,
                                          int templateId) {
        Object[] insertValues = new Object[]{isExerciseID, category, repetitionMaximum, templateId};
        jdbcTemplate.update("INSERT INTO exercise_instances(is_exercise, category,repetition_maximum, " +
                        "plan_template) values (?,?,?,?)",
                insertValues);
        Integer id = jdbcTemplate.query("select currval" +
                        "(pg_get_serial_sequence('exercise_instances','id'));",
                (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
        addTagsToExercise(tags, id);
        log.debug("Exercise Instance for Plan Template with ID '" + templateId + "' created with Id: " + id + " !");
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
                "SELECT ei.id, ei.is_exercise, ei.category, ei.plan_template, ex.name, ei.repetition_maximum" +
                        " FROM exercise_instances ei, exercises ex " +
                        " WHERE ei.id = ? " +
                        " AND ei.is_exercise = ex.id ",
                new Integer[]{id},
                (resultSet, i) -> new ExerciseInstance(resultSet.getInt("plan_template"), resultSet.getInt(
                        "is_exercise"), resultSet.getInt("id"), resultSet.getString("category"),
                        resultSet.getInt("repetition_maximum"),
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
                "SELECT ei.id, ei.is_exercise, ei.category, ei.plan_template, ex.name, ei.repetition_maximum" +
                        " FROM exercise_instances ei, exercises ex " +
                        " WHERE ei.is_exercise = ? " +
                        " AND ei.is_exercise = ex.id ",
                new Integer[]{idOfExercise},
                (resultSet, i) -> new ExerciseInstance(resultSet.getInt("plan_template"), resultSet.getInt(
                        "is_exercise"), resultSet.getInt("id"), resultSet.getString("category"),
                        resultSet.getInt("repetition_maximum"),
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
     * @param sets number of the sets.
     * @param reps array with the size {@code sets} where the repetitions of the single sets are saved.
     * @param tempo tempo in which the exercise should be executed.
     * @param pauseInSec pause time between the sets in seconds.
     * @return Id of the just inserted Trainings session.
     */
    public Integer insertTrainingsSession(int exerciseInstanceID, int ordering,
                                          int sets, Integer[] weightDiff, Integer[] reps,
                                          String tempo, Integer pauseInSec) {
        ArrayList<Object> insertValues = new ArrayList<>();
        insertValues.ensureCapacity(13);
        insertValues.add(exerciseInstanceID);
        insertValues.add(ordering);
        insertValues.add(sets);
        insertValues.add(tempo);
        insertValues.add(pauseInSec);
        if (weightDiff.length > 7) {
            throw new IllegalArgumentException("to many sets!");
        }
        insertValues.addAll(Arrays.asList(weightDiff));
        for (int i = insertValues.size(); i < 12; i++) {
            insertValues.add(null);
        }
        if (reps.length > 7) {
            throw new IllegalArgumentException("to many sets!");
        }
        insertValues.addAll(Arrays.asList(reps));
        for (int i = insertValues.size(); i < 19; i++) {
            insertValues.add(null);
        }
        jdbcTemplate.update(
                "insert into trainings_sessions(exercise_instance, ordering, sets, tempo, pause, weightdiff_set1," +
                        " weightdiff_set2, weightdiff_set3, weightdiff_set4, weightdiff_set5, weightdiff_set6," +
                        " weightdiff_set7, reps_set1, reps_set2, reps_set3, reps_set4," +
                        " reps_set5, reps_set6, reps_set7) values " +
                        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                insertValues.toArray());
        Integer id = jdbcTemplate.query("select currval" +
                        "(pg_get_serial_sequence('trainings_sessions','id'));",
                (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
        log.debug("Trainings Session for Exercise Instance with ID '" + exerciseInstanceID + "' created with Id: " + id + " !");
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
                new TrainingsSessionMapper()));
    }

    class TrainingsSessionMapper implements RowMapper<TrainingsSession> {
        @Override
        public TrainingsSession mapRow(ResultSet rs, int rowNum) throws SQLException {
            ArrayList<Integer> weightDiff = new ArrayList<>();
            for (int j = 1; j < 8; j++) {
                Integer toAdd = rs.getObject(String.format("weightdiff_set%d", j), Integer.class);
                if (toAdd != null) {
                    weightDiff.add(toAdd);
                }
            }
            ArrayList<Integer> reps = new ArrayList<>();
            for (int j = 1; j < 8; j++) {
                Integer toAdd = rs.getObject(String.format("reps_set%d", j), Integer.class);
                if (toAdd != null) {
                    reps.add(toAdd);
                }
            }
            return new TrainingsSession(rs.getInt("id"), rs.getInt("ordering"),
                    rs.getInt("exercise_instance"),
                    rs.getInt("sets"), weightDiff.toArray(new Integer[0]),
                    reps.toArray(new Integer[0]),
                    rs.getString("tempo"), rs.getInt("pause"));
        }
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
                new TrainingsSessionMapper()));
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

    // User Plan

    /**
     * Creates a new User plan for the User with mail userMail based on template wit templateId.
     * Sets CurrentSession 0 and MaxSession based on template.
     *
     * Throws exception when either template or User are missing.
     *
     * @param userMail identify user the Plan is for.
     * @param templateId identify template the plan is based on.
     * @return Id of new created plan.
     */
    public Integer insertUserPlan(String userMail, int templateId) {
        TrainingsPlanTemplate template = getOnlyPlanTemplateById(templateId);
        User user = getUserByEmail(userMail);
        if (user == null) {
            log.debug("Can't create User Plan because User doesn't exist. User mail: " + userMail);
            throw new IllegalArgumentException("Can't create User Plan because User doesn't exist. User mail: " + userMail);
        } else if (template == null) {
            log.debug("Can't create User Plan because Template doesn't exist. Template ID: " + templateId);
            throw new IllegalArgumentException("Can't create User Plan because Template doesn't exist. Template ID: " + templateId);
        }
        Object[] insertValues = new Object[]{userMail.toLowerCase(), templateId, template.getNumTrainSessions()};
        jdbcTemplate.update("insert into user_plans(\"user\",template,cursession,maxsession,initial_training_done) values (?,?,0,?,false)"
        , insertValues );
        Integer id = jdbcTemplate.query("select currval" +
                        "(pg_get_serial_sequence('user_plans','id'));",
                (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
        log.debug("User Plan based on template '" + template.getName() + "' created with Id " + id + " for user " + userMail + " !");
        return id;
    }

    class UserPlanMapper implements RowMapper<UserPlan> {
        @Override
        public UserPlan mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserPlan(rs.getInt("id"), rs.getString("user"), rs.getInt("template"),
                    rs.getInt("cursession"), rs.getInt("maxsession"), rs.getBoolean("initial_training_done"));
        }
    }

    public UserPlan getUserPlanById(int idUserPlan) {
        LinkedList<UserPlan> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM user_plans WHERE id = ?",
                new Integer[]{idUserPlan},
                new UserPlanMapper()));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    /**
     * Deletes all userplans, which are based on the searched plan template.
     * @param templateId Id of the searched plan template
     * @return Returns users whose plan was based on the plan template.
     */
    public LinkedList<User> deleteUserPlansByTemplateId(int templateId) {
        LinkedList<UserPlan> userPlans = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM user_plans WHERE template = ?",
                new Integer[]{templateId},
                new UserPlanMapper()));
        LinkedList<User> users = new LinkedList<>();
        for (UserPlan plan : userPlans) {
            users.add(getUserByEmail(plan.getUserMail()));
            deleteUserPlanAndWeightsById(plan.getId());
        }
        return users;
    }

    public UserPlan getUserPlanByUserMail(String userMail) {
        LinkedList<UserPlan> toReturn = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM user_plans WHERE \"user\" = ?",
                new String[]{userMail.toLowerCase()},
                new UserPlanMapper()));
        if (toReturn.isEmpty()) {
            return null;
        } else {
            return toReturn.getFirst();
        }
    }

    /**
     * Increases the CurrentSession of a userPlan by one when the maxSession
     * is reached userplan is deleted.
     *
     * @param userPlanID plan to increase session.
     * @return current session after increase.
     */
    public Integer increaseCurSession(int userPlanID) {
        UserPlan userPlan = getUserPlanById(userPlanID);
        userPlan.setCurrentSession(userPlan.getCurrentSession() + 1);
        if (userPlan.getCurrentSession() > userPlan.getMaxSession()) {
            log.debug("Max Sessions of Plan for User " + userPlan.getUserMail() + " reached! deleting Plan!");
            deleteUserPlanAndWeightsById(userPlanID);
            return -1;
        } else {
            Object[] values = new Object[]{userPlan.getCurrentSession(), userPlanID};
            jdbcTemplate.update("update user_plans set cursession = ? where id = ?", values);
        }
        return userPlan.getCurrentSession();
    }

    public void setInitialTrainDone(int userPlanId) {
        Object[] values = new Object[]{userPlanId};
        jdbcTemplate.update("update user_plans set initial_training_done = true where id = ?", values);
    }

    private void deleteUserPlanAndWeightsById(int userPlanID) {
        UserPlan toDelete = getUserPlanById(userPlanID);
        jdbcTemplate.update("DELETE FROM weights WHERE iduserplan = ?",
                    (Object[]) new Integer[]{userPlanID});
        jdbcTemplate.update("DELETE FROM user_plans where id = ?", (Object[]) new Integer[]{userPlanID});
        log.debug("User plan with id " + userPlanID + " of " + toDelete.getUserMail() + " deleted!");
    }

    // weights

    /**
     * creates a weight instance in its table for a specific exercise instance in a plan.
     *
     * @param idUserPlan plan weight belongs to.
     * @param idOfInstance exercise instance weight belongs to.
     * @param weight value of the weight.
     */
    public void insertWeightsForUserPlan(int idUserPlan, int idOfInstance, Integer weight) {
        jdbcTemplate.update("insert into weights(iduserplan, idexerciseinstance, weight) values (?,?,?)"
                , idUserPlan, idOfInstance, weight);
        Integer id = jdbcTemplate.query("select currval" +
                        "(pg_get_serial_sequence('weights','id'));",
                (resultSet, i) -> resultSet.getInt(i + 1)).get(0);
        log.debug("Weights for User Plan with ID '" + idUserPlan + "', for exercise Instance with ID '" +
                idOfInstance + "' created with Id '" + id + "'!");
    }

    /**
     * get the weight assigned to a specific exercise instance for a specific plan.
     *
     * @param idUserPlan id of the plan.
     * @param idOfInstance id of the exercise instance.
     * @return value of the weight.
     */
    public Integer getWeightForUserPlanExercise(int idUserPlan, int idOfInstance) {
        LinkedList<Integer> result = new LinkedList<>(jdbcTemplate.query(
                "SELECT * FROM weights WHERE iduserplan = ? AND idexerciseinstance = ?",
                new Integer[]{idUserPlan, idOfInstance},
                (rs, i) -> rs.getInt("weight")));
        if (result.isEmpty()) {
            return null;
        } else {
            return result.getFirst();
        }
    }

}
