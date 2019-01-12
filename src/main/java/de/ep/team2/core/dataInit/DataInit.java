package de.ep.team2.core.dataInit;

import de.ep.team2.core.enums.WeightType;
import de.ep.team2.core.service.DataBaseService;
import de.ep.team2.core.service.UserService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Initiates the tables and fills it with example data.
 */
public class DataInit {

    public static final int MAX_SETS = 7;

    private JdbcTemplate jdbcTemplate;
    private Logger log;
    private UserService userService = new UserService();

    public DataInit(JdbcTemplate jdbcTemplate, Logger log) {
        this.jdbcTemplate = jdbcTemplate;
        this.log = log;
        initTables();
        fillUsers();
        fillExercises();
        fillPlanTemplates();
        fillExerciseInstances();
        fillTrainingSessions();
    }

    private void initTables() {
        log.info("Creating tables");
        // Users
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE ");
        jdbcTemplate.execute("CREATE TABLE users(" +
                "id SERIAL, email VARCHAR(255) NOT NULL ," +
                " first_name VARCHAR(255), last_name VARCHAR(255) ," +
                " password varchar(60) not null, " +
                " enabled boolean not null default false , " +
                " role varchar(20) not null," +
                " primary key(email))");

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
        // Images
        jdbcTemplate.execute("DROP TABLE IF EXISTS images");
        jdbcTemplate.execute("CREATE TABLE images(" +
                "exercise integer references exercises not null," +
                "path varchar(400) PRIMARY KEY," +
                "img_type varchar(10) NOT NULL)");
        initImages();
        // Plan Templates
        jdbcTemplate.execute("DROP TABLE IF EXISTS plan_templates cascade ");
        jdbcTemplate.execute("CREATE TABLE plan_templates(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "name varchar(255) NOT NULL UNIQUE," +
                "trainings_focus varchar(255)," +
                "author varchar(255) references users NOT NULL," +
                "one_shot_plan boolean," +
                "num_train_sessions integer NOT NULL," +
                "exercises_per_session integer NOT NULL)");
        // Exercise Instance
        jdbcTemplate.execute("DROP TABLE IF EXISTS exercise_instances CASCADE ");
        jdbcTemplate.execute("CREATE TABLE exercise_instances(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "is_exercise integer references exercises not null," +
                "category varchar(50)," +
                "plan_template integer not null references plan_templates)");
        // Execution Tags for Instance
        jdbcTemplate.execute("DROP TABLE IF EXISTS execution_tags CASCADE ");
        jdbcTemplate.execute("CREATE TABLE execution_tags(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "name varchar(255) NOT NULL UNIQUE)");
        jdbcTemplate.execute("DROP TABLE IF EXISTS ex_tags_map CASCADE ");
        jdbcTemplate.execute("CREATE TABLE ex_tags_map(" +
                "ex_inst_id INTEGER NOT NULL," +
                "ex_tag_id INTEGER NOT NULL)");
        // Training Sessions
        jdbcTemplate.execute("DROP TABLE IF EXISTS trainings_sessions cascade ");
        jdbcTemplate.execute("CREATE TABLE trainings_sessions(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "exercise_instance integer references exercise_instances not null," +
                "ordering integer not null," +
                "rep_maximum integer not null," +
                "sets integer not null," +
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
    }

    private void initImages() {
        try {
            FileUtils.deleteDirectory(new File("src/main/resources/static/images/"));
            String sourceBank = "src/main/resources/static/initFileBackup/Bankdrücken";
            String destBank = "src/main/resources/static/images/Bankdrücken";
            String sourceLieg = "src/main/resources/static/initFileBackup/Liegestütz";
            String destLieg = "src/main/resources/static/images/Liegestütz";
            Files.createDirectories(Paths.get(destBank));
            Files.createDirectories(Paths.get(destLieg));
            Files.copy(Paths.get(sourceBank + "/3-Narrow-grip-bench-press_zpsysfv9zvj.png"), Paths.get(destBank + "/3-Narrow-grip-bench-press_zpsysfv9zvj.png"));
            Files.copy(Paths.get(sourceBank + "/Bankdruecken-Bench-Press.jpg"), Paths.get(destBank + "/Bankdruecken-Bench-Press.jpg"));
            Files.copy(Paths.get(sourceLieg + "/Liegestuetze-Startposition.jpg"), Paths.get(destLieg + "/Liegestuetze-Startposition.jpg"));
            Files.copy(Paths.get(sourceLieg + "/Liegestuetze-Muskelgruppen.jpg"), Paths.get(destLieg + "/Liegestuetze-Muskelgruppen.jpg"));
            Files.copy(Paths.get(sourceLieg + "/Liegestuetze-Endposition.jpg"), Paths.get(destLieg + "/Liegestuetze-Endposition.jpg"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void fillUsers() {
        LinkedList<String[]> initTestData = new LinkedList<>();
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
        DataBaseService.getInstance().changeToMod(3);
    }

    private void fillExercises() {
        List<String[]> paths = new LinkedList<>();
        paths.add(new String[]{"/images/Bankdrücken/Bankdruecken-Bench-Press.jpg", "muscle"});
        paths.add(new String[]{"/images/Bankdrücken/3-Narrow-grip-bench-press_zpsysfv9zvj.png", "other"});
        DataBaseService.getInstance().insertExercise("Bankdrücken", "Lege " +
                        "dich mit dem Rücken auf die Flachbank und positioniere deinen Oberkörper so, dass sich die in der Halterung liegende Langhantel etwa auf Augenhöhe befindet. Platziere die Füße fest auf dem Boden, um Stabilität zu gewinnen. Spanne außerdem den unteren Rücken an, sodass ein leichtes Hohlkreuz entsteht.\n" +
                        "\n" +
                        "Wichtig ist auch die Haltung der Schulterblätter: " +
                        "Ziehe sie nach hinten, um die Schultergelenke zu " +
                        "stabilisieren.\n" +
                        "\n" +
                        "Wenn du deinen Körper in die oben beschriebene " +
                        "Position gebracht hast, kannst du die Langhantel " +
                        "etwas breiter als schulterbreit umgreifen und sie " +
                        "aus der Halterung heben.\n" +
                        "\n" +
                        "Zur Griffweise beim Bankdrücken: Im Kraftsport ist " +
                        "es mitunter üblich, die Hantelstange nicht mit dem " +
                        "Daumen zu umgreifen, sondern selbigen neben die " +
                        "anderen Finger zu legen. Die Stange ist dann nicht " +
                        "fest umschlossen, sondern ruht zwischen Handflächen " +
                        "und Fingern. Man nennt dies den Affengriff. Diese " +
                        "Grifftechnik birgt ein hohes Risiko, denn die Stange" +
                        " kann abrutschen. Wir empfehlen deshalb, die " +
                        "Hantelstange mit dem Daumen und den anderen Fingern " +
                        "vollständig zu umschließen ",
                WeightType.FIXED_WEIGHT,
                "https://www.youtube.com/embed/jYQtBKRs_D8", paths);
        List<String[]> paths2 = new LinkedList<>();
        paths2.add(new String[]{"/images/Liegestütz/Liegestuetze-Muskelgruppen.jpg", "muscle"});
        paths2.add(new String[]{"/images/Liegestütz/Liegestuetze-Startposition.jpg", "other"});
        paths2.add(new String[]{"/images/Liegestütz/Liegestuetze-Endposition.jpg", "other"});
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
        DataBaseService.getInstance().insertPlanTemplate("Test Plan", "muscle",
                "felix@gmail.com", false, 6, 2);
    }

    private void fillTrainingSessions() {
        DataBaseService db = DataBaseService.getInstance();
        //Bankdrücken
        db.insertTrainingsSession(1, 1, 15, 3, new Integer[]{12, 12, 12}, "Langsam", 90);
        db.insertTrainingsSession(1, 2, 15, 3, new Integer[]{12, 12, 12}, "Langsam", 90);
        db.insertTrainingsSession(1, 3, 15, 3, new Integer[]{13, 13, 13}, "Langsam", 90);
        db.insertTrainingsSession(1, 4, 15, 3, new Integer[]{13, 13, 13}, "Schnell", 90);
        db.insertTrainingsSession(1, 5, 15, 3, new Integer[]{15, 14, 13}, "Langsam", 90);
        db.insertTrainingsSession(1, 6, 15, 4, new Integer[]{15, 14, 14, 15}, "Langsam", 90);
        //Liegestütz
        db.insertTrainingsSession(2, 1, 15, 4, new Integer[]{20, 25, 25, 20}, "Langsam", 90);
        db.insertTrainingsSession(2, 2, 15, 4, new Integer[]{22, 30, 30, 22}, "Langsam", 90);
        db.insertTrainingsSession(2, 3, 15, 4, new Integer[]{20, 30, 30, 35}, "Langsam", 90);
        db.insertTrainingsSession(2, 4, 15, 4, new Integer[]{30, 30, 30, 30}, "Schnell", 90);
        db.insertTrainingsSession(2, 5, 15, 4, new Integer[]{35, 35, 35, 35}, "Langsam", 90);
        db.insertTrainingsSession(2, 6, 15, 4, new Integer[]{35, 40, 40, 35}, "Schnell", 90);
    }

    private void fillExerciseInstances() {
        LinkedList<String> toAdd = new LinkedList<>();
        toAdd.add("Liegend");
        toAdd.add("Schrägbank");
        LinkedList<String> toAdd2 = new LinkedList<>();
        toAdd2.add("Eng");
        DataBaseService db = DataBaseService.getInstance();
        db.insertExerciseInstance(1, "A1", toAdd, 1);
        db.insertExerciseInstance(2, "A2", toAdd2, 1);
    }
}
