package de.ep.team2.core.dataInit;

import de.ep.team2.core.dtos.TrainingsDayDto;
import de.ep.team2.core.enums.ExperienceLevel;
import de.ep.team2.core.enums.Gender;
import de.ep.team2.core.enums.TrainingsFocus;
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Initiates the tables and fills it with example data.
 */
public class DataInit {

    // Variables used to configure the Application

    public static final String ADMIN_MAIL = "admin@ep18.com"; // Should only be used to set your account as Moderator. This will receive no emails of users.
    private final String adminPassword = "QxA4cxKWAT2bwmsD";
    // clears Database on each server start, required 'true' to run unit tests
    private final boolean alwaysClearDb = true;
    private final boolean fillWithExampleData = true;

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
                "trainings_sessions", "user_plans", "weights", "user_stats"};
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
        int id = DataBaseService.getInstance().insertUser(ADMIN_MAIL, null, null, userService.encode(adminPassword));
        DataBaseService.getInstance().confirmUser(ADMIN_MAIL);
        DataBaseService.getInstance().changeToMod(id);
    }

    private void initTables() {
        log.debug("Creating tables");
        DataBaseService.getInstance().createTables();
    }

    private void fillUsers() {
        DataBaseService db = DataBaseService.getInstance();
        LinkedList<String[]> initTestData = new LinkedList<>();
        String[] timo = {"timo@gmail.com", "Timo", "Heinrich", userService.encode("hello")};
        String[] alex = {"alex@gmail.com", "Alexander", "Reißig", userService.encode("password")};
        String[] felix = {"felix@gmail.com", "Felix", "Wilhelm", userService.encode("123")};
        String[] yannick = {"yannick@gmail.com", null, null, userService.encode("123")};
        String[] ben = {"benedikt.schwarz@gmail.com", "Benedikt", "Schwarz", userService.encode("XOXO")};
        initTestData.add(timo);
        initTestData.add(alex);
        initTestData.add(felix);
        initTestData.add(yannick);
        initTestData.add(ben);
        for (String[] o : initTestData) {
            db.insertUser(o[0], o[1], o[2], o[3]);
            db.confirmUser(o[0]);
        }
        for (int i = 2; i < 6; i++) {
            db.changeToMod(i);
        }
        Calendar cal = Calendar.getInstance();
        cal.set(1998,Calendar.JANUARY,4);
        Date date = cal.getTime();
        db.setAdvancedUserData(77,180, TrainingsFocus.MUSCLE,2, Gender.MALE, ExperienceLevel.EXPERT,null,"timo@gmail.com");
        db.setAdvancedUserData(77,180, TrainingsFocus.WEIGHT,2, Gender.FEMALE, ExperienceLevel.BEGINNER,null,"felix@gmail.com");
        db.setAdvancedUserData(77,180, TrainingsFocus.STAMINA,3, Gender.FEMALE, ExperienceLevel.BEGINNER,null,"alex@gmail.com");
        db.setAdvancedUserData(77,180, TrainingsFocus.STAMINA,1, Gender.MALE, ExperienceLevel.BEGINNER,null,"yannick@gmail.com");
        db.setAdvancedUserData(80,183, TrainingsFocus.MUSCLE,2, Gender.MALE, ExperienceLevel.BEGINNER,date,"benedikt.schwarz@gmail.com");
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
        int id = DataBaseService.getInstance().insertPlanTemplate("Test Plan", TrainingsFocus.MUSCLE, ExperienceLevel.BEGINNER,
                "felix@gmail.com",false,1,6,2);
        DataBaseService.getInstance().confirmPlan(id);
        id = DataBaseService.getInstance().insertPlanTemplate("Test One Shot Plan", TrainingsFocus.WEIGHT, ExperienceLevel.EXPERT,
                "felix@gmail.com",true,1,1,4);
        DataBaseService.getInstance().confirmPlan(id);
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
        // Test One Shot Plan
        //Liegestütz
        db.insertTrainingsSession(5,1,4, new Integer[]{0,0,5,10}, new Integer[]{20,25,25,20},"Langsam",90);
        db.insertTrainingsSession(6,1,4, new Integer[]{0,0,5,10}, new Integer[]{22,30,30,22},"Langsam",90);
        //Bankdrücken
        db.insertTrainingsSession(3,1,3, new Integer[]{0,0,5}, new Integer[]{12,12,12},"Langsam",90);
        db.insertTrainingsSession(4,1,3, new Integer[]{0,0,5}, new Integer[]{12,12,12},"Langsam",90);
    }

    private void fillExerciseInstances() {
        LinkedList<String> toAdd = new LinkedList<>();
        toAdd.add("Liegend");
        toAdd.add("Schrägbank");
        LinkedList<String> toAdd2 = new LinkedList<>();
        toAdd2.add("Eng");
        DataBaseService db = DataBaseService.getInstance();
        // Test Plan
        db.insertExerciseInstance(1, "A1", 15, toAdd, 1);
        db.insertExerciseInstance(2, "A2", 15, toAdd2, 1);
        // Test One Shot Plan
        LinkedList<String> toAdd3 = new LinkedList<>();
        db.insertExerciseInstance(1, "A1", 15, toAdd3, 2);
        db.insertExerciseInstance(1, "B1", 25, toAdd3, 2);
        db.insertExerciseInstance(2, "A2", 20, toAdd3, 2);
        db.insertExerciseInstance(2, "B2", 10, toAdd3, 2);
    }

    private void fillUserPlans() {
        DataBaseService db = DataBaseService.getInstance();
        db.insertUserPlan("timo@gmail.com", 1);
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
