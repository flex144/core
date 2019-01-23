package de.ep.team2.core.dataInit;

import de.ep.team2.core.dtos.TrainingsDayDto;
import de.ep.team2.core.entities.UserPlan;
import de.ep.team2.core.enums.WeightType;
import de.ep.team2.core.service.DataBaseService;
import de.ep.team2.core.service.PlanService;
import de.ep.team2.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Initiates the tables and fills it with example data.
 */
public class DataInit {

    // Variables used to configure the Application

    private final String adminMail = "admin@ep18.com";
    private final String adminPassword = "QxA4cxKWAT2bwmsD";
    private final boolean fillWithExampleData = true;
    // clears Database on each server start, required 'true' to run unit tests
    private final boolean alwaysClearDb = true;

    private JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(DataInit.class);
    private UserService userService = new UserService();

    /**
     * Initiates the data when this wasn't already done.
     *
     * @param jdbcTemplate jdbc Connection to the Database.
     */
    public DataInit(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        if (alwaysClearDb) {
            initateDatabase();
        } else {
            switch (checkTables()) {
            case "AllExisting":
                log.info("All Tables found. Launch Completed.");
                break;
            case "FirstLaunch":
                log.info("No Tables found. Initiating first Launch and creating tables.");
                initateDatabase();
                break;
            case "Error":
                log.error("One or more tables are missing in the Database! This will cause Failures.");
            }
        }
    }

    private void initateDatabase() {
        initTables();
        addAdmin();
        if (fillWithExampleData) {
            fillUsers();
            fillExercises();
            fillPlanTemplates();
            fillExerciseInstances();
            fillTrainingSessions();
            fillUserPlans();
            fillWeights();
        }
    }

    /**
     * Checks if all, no or just a few tables are present.
     *
     * @return 'AllExisting' when all Tables are present;
     * 'FirstLaunch' when there are none of the tables in the Database;
     * 'Error' when some tables are missing cause this shouldn't be the case.
     */
    private String checkTables() {
        boolean allExist = true;
        boolean noneExist = true;
        String[] tableNames = new String[]{"users", "confirmation_token", "exercises", "images",
                "plan_templates", "exercise_instances", "execution_tags", "ex_tags_map",
                "trainings_sessions", "user_plans", "weights"};
        try {
            for (String tableName : tableNames) {
                Connection connection = jdbcTemplate.getDataSource().getConnection();
                DatabaseMetaData md = connection.getMetaData();
                ResultSet rs = md.getTables(null, null, tableName, null);
                if (rs.next()) {
                    log.debug("Table '" + tableName + "' exists!");
                    noneExist = false;
                } else {
                    log.debug("Table '" + tableName + "' missing!");
                    allExist = false;
                }
            }
        } catch (Exception exception) {
            log.error("Server couldn't be started because of an SQL Exception! check the Database." + exception.getMessage());
            return "Error";
        }
        if (allExist) {
            return "AllExisting";
        } else if (noneExist) {
            return "FirstLaunch";
        } else {
            return "Error";
        }
    }

    private void addAdmin() {
        int id = DataBaseService.getInstance().insertUser(adminMail, null, null, userService.encode(adminPassword));
        DataBaseService.getInstance().confirmUser(adminMail);
        DataBaseService.getInstance().changeToMod(id);
    }

    private void initTables() {
        log.debug("Creating tables");
        // Users
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE ");
        jdbcTemplate.execute("CREATE TABLE users(" +
                "id SERIAL, email VARCHAR(255) NOT NULL ," +
                "first_name VARCHAR(255), last_name VARCHAR(255) ," +
                "height_in_cm INTEGER," +
                "weight_in_kg INTEGER," +
                "gender varchar(20)," +
                "trainings_focus varchar(20)," +
                "experience varchar(20)," +
                "birth_date date," +
                "trainings_frequency INTEGER," +
                " password varchar(60) not null, " +
                " enabled boolean not null default false, " +
                " role varchar(20) not null," +
                " primary key(email))");
        log.debug("Created table users");

        //Confirmation Token
        jdbcTemplate.execute("DROP TABLE IF EXISTS confirmation_token CASCADE");
        jdbcTemplate.execute("CREATE TABLE confirmation_token (" +
                "id SERIAL NOT NULL PRIMARY KEY, token VARCHAR(36), " +
                "userToConfirm VARCHAR(255) REFERENCES users, createdDate DATE)");

        // Exercises
        jdbcTemplate.execute("DROP TABLE IF EXISTS exercises CASCADE ");
        jdbcTemplate.execute("CREATE TABLE exercises(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL UNIQUE," +
                "description VARCHAR(2000)," +
                "weight_type varchar(15) NOT NULL," +
                "video_link varchar(400))");
        log.debug("Created table exercises");
        // Images
        jdbcTemplate.execute("DROP TABLE IF EXISTS images");
        jdbcTemplate.execute("CREATE TABLE images(" +
                "exercise integer references exercises not null," +
                "path varchar(400) PRIMARY KEY," +
                "img_type varchar(10) NOT NULL)");
        //initImages();
        log.debug("Created table images");
        // Plan Templates
        jdbcTemplate.execute("DROP TABLE IF EXISTS plan_templates cascade ");
        jdbcTemplate.execute("CREATE TABLE plan_templates(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "name varchar(255) NOT NULL UNIQUE," +
                "trainings_focus varchar(255)," +
                "target_group varchar(255)," +
                "author varchar(255) references users ," +
                "one_shot_plan boolean," +
                "recom_sessions_per_week integer," +
                "num_train_sessions integer NOT NULL," +
                "exercises_per_session integer NOT NULL, " +
                "complete boolean NOT NULL DEFAULT FALSE )");
        log.debug("Created table plan_templates");
        // Exercise Instance
        jdbcTemplate.execute("DROP TABLE IF EXISTS exercise_instances CASCADE ");
        jdbcTemplate.execute("CREATE TABLE exercise_instances(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "is_exercise integer references exercises not null," +
                "category varchar(50)," +
                "repetition_maximum integer," +
                "plan_template integer not null references plan_templates)");
        log.debug("Created table exercise_instances");
        // Execution Tags for Instance
        jdbcTemplate.execute("DROP TABLE IF EXISTS execution_tags CASCADE ");
        jdbcTemplate.execute("CREATE TABLE execution_tags(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "name varchar(255) NOT NULL UNIQUE)");
        log.debug("Created table execution_tags");
        jdbcTemplate.execute("DROP TABLE IF EXISTS ex_tags_map CASCADE ");
        jdbcTemplate.execute("CREATE TABLE ex_tags_map(" +
                "ex_inst_id INTEGER NOT NULL," +
                "ex_tag_id INTEGER NOT NULL)");
        log.debug("Created table ex_tags_map");
        // Training Sessions
        jdbcTemplate.execute("DROP TABLE IF EXISTS trainings_sessions cascade ");
        jdbcTemplate.execute("CREATE TABLE trainings_sessions(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "exercise_instance integer references exercise_instances not null," +
                "ordering integer not null," +
                "sets integer not null," +
                "weightdiff_set1 integer," +
                "weightdiff_set2 integer," +
                "weightdiff_set3 integer," +
                "weightdiff_set4 integer," +
                "weightdiff_set5 integer," +
                "weightdiff_set6 integer," +
                "weightdiff_set7 integer," +
                "tempo varchar(50)," +
                "pause integer," +
                "reps_set1 integer," +
                "reps_set2 integer," +
                "reps_set3 integer," +
                "reps_set4 integer," +
                "reps_set5 integer," +
                "reps_set6 integer," +
                "reps_set7 integer," +
                "CHECK (sets <= 7)," +
                "CHECK (ordering <= 15 AND ordering >= 1))");
        log.debug("Created table trainings_sessions");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_plans cascade ");
        jdbcTemplate.execute("CREATE TABLE user_plans(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "\"user\" varchar(255) NOT NULL REFERENCES users," +
                "template integer NOT NULL REFERENCES plan_templates," +
                "curSession integer NOT NULL," +
                "maxSession integer NOT NULL," +
                "initial_training_done boolean NOT NULL)");
        log.debug("Created table user_plans");
        jdbcTemplate.execute("DROP TABLE IF EXISTS weights cascade ");
        jdbcTemplate.execute("CREATE TABLE weights(" +
                "  id SERIAL NOT NULL PRIMARY KEY," +
                "  idUserPlan integer not null references user_plans," +
                "  idExerciseInstance integer not null references exercise_instances," +
                "  weight integer not null)");
        log.debug("Created table weights");
    }

    private void fillUsers() {
        LinkedList<String[]> initTestData = new LinkedList<>();
        String[] admin = {"admin@ep18.com", null, null, userService.encode("QxA4cxKWAT2bwmsD")};
        String[] timo = {"timo@gmail.com", "Timo", "Heinrich", userService.encode("hello")};
        String[] alex = {"alex@gmail.com", "Alexander", "Reißig", userService.encode("password")};
        String[] felix = {"felix@gmail.com", "Felix", "Wilhelm", userService.encode("123")};
        String[] yannick = {"yannick@gmail.com", null, null, userService.encode("123")};
        initTestData.add(timo);
        initTestData.add(alex);
        initTestData.add(felix);
        initTestData.add(yannick);
        for (String[] o : initTestData) {
            DataBaseService.getInstance().insertUser(o[0], o[1], o[2], o[3]);
            DataBaseService.getInstance().confirmUser(o[0]);
        }
        DataBaseService.getInstance().changeToMod(4);
    }

    private void fillExercises() {
        List<String[]> paths = new LinkedList<>();
        paths.add(new String[]{"/images/Exercises/Bankdrücken/Bankdruecken-Bench-Press.jpg", "muscle"});
        paths.add(new String[]{"/images/Exercises/Bankdrücken/3-Narrow-grip-bench-press_zpsysfv9zvj.png", "other"});
        DataBaseService.getInstance().insertExercise("Bankdrücken", "Lege " +
                        "dich mit dem Rücken auf die Flachbank und positioniere deinen Oberkörper so," +
                        " dass sich die in der Halterung liegende Langhantel etwa auf Augenhöhe befindet. " +
                        "Platziere die Füße fest auf dem Boden, um Stabilität zu gewinnen. Spanne außerdem den" +
                        " unteren Rücken an, sodass ein leichtes Hohlkreuz entsteht.\n" +
                        "\n" +
                        "Wichtig ist auch die Haltung der Schulterblätter: " +
                        "Ziehe sie nach hinten, um die Schultergelenke zu " +
                        "stabilisieren.\n",
                WeightType.FIXED_WEIGHT,
                "https://www.youtube.com/embed/jYQtBKRs_D8", paths);
        List<String[]> paths2 = new LinkedList<>();
        paths2.add(new String[]{"/images/Exercises/Liegestütz/Liegestuetze-Muskelgruppen.jpg", "muscle"});
        paths2.add(new String[]{"/images/Exercises/Liegestütz/Liegestuetze-Startposition.jpg", "other"});
        paths2.add(new String[]{"/images/Exercises/Liegestütz/Liegestuetze-Endposition.jpg", "other"});
        DataBaseService.getInstance().insertExercise("Liegestütz", "Genauso " +
                        "wie beim Bankdrücken. Nur dass du deinen Körper bewegt, anstelle der Stange.\n" +
                        "Hände sind schulterweit auf dem Boden.\n" +
                        "Oberarme sollten von oben gesehen ca. in einem 45° " +
                        "Winkel vom Torso abstehen (in der tiefsten Position)" +
                        ".\n" +
                        "Der Kopf ist in einer Linie mit der Wirbelsäule " +
                        "(“neutrale Wirbelsäule”) wie beim Kreuzheben.\n" +
                        "Berühre den Boden unten mit deiner Brust, nicht mit " +
                        "dem Kopf.", WeightType.SELF_WEIGHT,
                "https://www.youtube.com/embed/e_1BDnOVKso", paths2);
    }

    private void fillPlanTemplates() {
        DataBaseService.getInstance().insertPlanTemplate("Test Plan", "muscle","beginner",
                "felix@gmail.com",false,1,6,2);
    }

    private void fillTrainingSessions() {
        DataBaseService db = DataBaseService.getInstance();
        //Bankdrücken
        db.insertTrainingsSession(1,1,3, new Integer[]{0,0,5}, new Integer[]{12,12,12},"Langsam",90);
        db.insertTrainingsSession(1,2,3, new Integer[]{0,0,5}, new Integer[]{12,12,12},"Langsam",90);
        db.insertTrainingsSession(1,3,3, new Integer[]{0,0,5}, new Integer[]{13,13,13},"Langsam",90);
        db.insertTrainingsSession(1,4,3, new Integer[]{0,0,5}, new Integer[]{13,13,13},"Schnell",90);
        db.insertTrainingsSession(1,5,3, new Integer[]{0,0,5}, new Integer[]{15,14,13},"Langsam",90);
        db.insertTrainingsSession(1,6,4, new Integer[]{0,0,5,10}, new Integer[]{15,14,14,15},"Langsam",90);
        //Liegestütz
        db.insertTrainingsSession(2,1,4, new Integer[]{0,0,5,10}, new Integer[]{20,25,25,20},"Langsam",90);
        db.insertTrainingsSession(2,2,4, new Integer[]{0,0,5,10}, new Integer[]{22,30,30,22},"Langsam",90);
        db.insertTrainingsSession(2,3,4, new Integer[]{0,0,5,10}, new Integer[]{20,30,30,35},"Langsam",90);
        db.insertTrainingsSession(2,4,4, new Integer[]{0,0,5,10}, new Integer[]{30,30,30,30},"Schnell",90);
        db.insertTrainingsSession(2,5,4, new Integer[]{0,0,5,10}, new Integer[]{35,35,35,35},"Langsam",90);
        db.insertTrainingsSession(2,6,4, new Integer[]{0,0,5,10}, new Integer[]{35,40,40,35},"Schnell",90);
    }

    private void fillExerciseInstances() {
        LinkedList<String> toAdd = new LinkedList<>();
        toAdd.add("Liegend");
        toAdd.add("Schrägbank");
        LinkedList<String> toAdd2 = new LinkedList<>();
        toAdd2.add("Eng");
        DataBaseService db = DataBaseService.getInstance();
        db.insertExerciseInstance(1, "A1", 15, toAdd, 1);
        db.insertExerciseInstance(2, "A2", 15, toAdd2, 1);
    }

    private void fillUserPlans() {
        DataBaseService db = DataBaseService.getInstance();
        db.insertUserPlan("timo@gmail.com", 1);
        UserPlan test = db.getUserPlanById(1);
    }

    private void fillWeights() {
        DataBaseService db = DataBaseService.getInstance();
        PlanService service = new PlanService();
        db.insertWeightsForUserPlan(1,1,50);
        db.insertWeightsForUserPlan(1,2,100);
        Integer test = db.getWeightForUserPlanExercise(1,1);
        TrainingsDayDto test2 = service.fillTrainingsDayDto("timo@gmail.com", new TrainingsDayDto());
    }
}
