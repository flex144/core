package de.ep.team2.core.dataInit;

import de.ep.team2.core.enums.WeightType;
import de.ep.team2.core.service.DataBaseService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
                " first_name VARCHAR(255)," +
                "last_name VARCHAR(255))");
        jdbcTemplate.execute("DROP TABLE IF EXISTS exercises CASCADE ");
        jdbcTemplate.execute("CREATE TABLE exercises(" +
                "id SERIAL NOT NULL PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL UNIQUE," +
                "description VARCHAR(255)," +
                "weight_type varchar(15) NOT NULL," +
                "video_link varchar(400))");
        jdbcTemplate.execute("DROP TABLE IF EXISTS images");
        jdbcTemplate.execute("CREATE TABLE images(" +
                "exercise integer references exercises not null," +
                "path varchar(400) PRIMARY KEY," +
                "img_type varchar(10) NOT NULL)");
        try {
            FileUtils.deleteDirectory(new File("src/main/resources/static/images/"));
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
        paths.add(new String[]{"/images/Bankdrücken/test1", "other"});
        DataBaseService.getInstance().insertExercise("Bankdrücken", "Drücke die Bank!",
                WeightType.FIXED_WEIGHT,
                null, paths);
        DataBaseService.getInstance().insertExercise("Rudern", null, WeightType.FIXED_WEIGHT,
                null, null);
    }
}
