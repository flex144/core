package de.ep.team2.core.dataInit;

import de.ep.team2.core.enums.WeightType;
import de.ep.team2.core.service.DataBaseService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Initiates the tables and fills it with example data.
 */
public class DataInit {

    private JdbcTemplate jdbcTemplate;
    private Logger log;

    public DataInit(JdbcTemplate jdbcTemplate, Logger log) {
        this.jdbcTemplate = jdbcTemplate;
        this.log = log;
        initTables();
        fillUsers();
        fillExercises();
    }

    private void initTables() {
        log.info("Creating tables");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users");
        jdbcTemplate.execute("CREATE TABLE users(" +
                "id SERIAL NOT NULL, " +
                "email VARCHAR(255) PRIMARY KEY NOT NULL," +
                "first_name VARCHAR(255)," +
                "last_name VARCHAR(255))");
        jdbcTemplate.execute("DROP TABLE IF EXISTS exercises CASCADE ");
        jdbcTemplate.execute("CREATE TABLE exercises(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL UNIQUE," +
                "description VARCHAR(2000)," +
                "weight_type varchar(15) NOT NULL," +
                "video_link varchar(400))");
        jdbcTemplate.execute("DROP TABLE IF EXISTS images");
        jdbcTemplate.execute("CREATE TABLE images(" +
                "exercise integer references exercises not null," +
                "path varchar(400) PRIMARY KEY," +
                "img_type varchar(10) NOT NULL)");
        initImages();
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
            Files.copy(Paths.get(sourceBank + "/3-Narrow-grip-bench-press_zpsysfv9zvj.png" ), Paths.get(destBank + "/3-Narrow-grip-bench-press_zpsysfv9zvj.png"));
            Files.copy(Paths.get(sourceBank + "/Bankdruecken-Bench-Press.jpg" ), Paths.get(destBank + "/Bankdruecken-Bench-Press.jpg"));
            Files.copy(Paths.get(sourceLieg + "/Liegestuetze-Startposition.jpg" ), Paths.get(destLieg + "/Liegestuetze-Startposition.jpg"));
            Files.copy(Paths.get(sourceLieg + "/Liegestuetze-Muskelgruppen.jpg" ), Paths.get(destLieg + "/Liegestuetze-Muskelgruppen.jpg"));
            Files.copy(Paths.get(sourceLieg + "/Liegestuetze-Endposition.jpg" ), Paths.get(destLieg + "/Liegestuetze-Endposition.jpg"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void fillUsers() {
        LinkedList<String[]> initTestData = new LinkedList<>();
        String[] timo = {"timo@gmail.com", "Timo", "Heinrich"};
        String[] alex = {"alex@gmail.com", "Alexander", "Reißig"};
        String[] felix = {"felix@gmail.com", "Felix", "Wilhelm"};
        String[] yannick = {"yannick@gmail.com", null, null};
        initTestData.add(timo);
        initTestData.add(alex);
        initTestData.add(felix);
        initTestData.add(yannick);
        for (String[] o : initTestData) {
            try {
                DataBaseService.getInstance().insertUser(o[0], o[1], o[2]);
            } catch (IllegalArgumentException exception) {
                //do nothing
            }
        }
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
                "https://www.youtube.com/watch?v=Br8FJCuR4a4", paths );
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
                "https://youtu.be/e_1BDnOVKso", paths2);
    }
}
