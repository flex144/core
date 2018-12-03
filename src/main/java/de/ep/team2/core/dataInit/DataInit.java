package de.ep.team2.core.dataInit;

import de.ep.team2.core.service.DataBaseService;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.LinkedList;

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
    }

    private void initTables() {
        log.info("Creating tables");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users ");
        jdbcTemplate.execute("CREATE TABLE users(" +
                "id SERIAL, email VARCHAR(255) NOT NULL ," +
                " first_name VARCHAR(255), last_name VARCHAR(255) ," +
                " password varchar(20) not null, " +
                " enabled boolean not null default false, " +
                " role varchar(20) not null," +
                " primary key(email))");
    }

    private void fillUsers() {
        LinkedList<String[]> initTestData = new LinkedList<>();
        String[] timo = {"timo@gmail.com", "Timo", "Heinrich", "123"};
        String[] alex = {"alex@gmail.com", "Alexander", "Reißig", "123"};
        String[] felix = {"felix@gmail.com", "Felix", "Wilhelm", "123"};
        String[] yannick = {"yannick@gmail.com", null, null, "123"};
        initTestData.add(timo);
        initTestData.add(alex);
        initTestData.add(felix);
        initTestData.add(yannick);
        for (String[] o : initTestData) {
            DataBaseService.getInstance().insertUser(o[0], o[1], o[2], o[3]);
        }
    }
}
