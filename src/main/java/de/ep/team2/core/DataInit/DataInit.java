package de.ep.team2.core.DataInit;

import de.ep.team2.core.service.DataBaseService;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedList;

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
        jdbcTemplate.execute("DROP TABLE IF EXISTS users");
        jdbcTemplate.execute("CREATE TABLE users(" +
                "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
    }

    private void fillUsers() {
        LinkedList<String[]> initTestData = new LinkedList<>();
        String[] timo = {"Timo","Heinrich"};
        String[] alex = {"Alexander","Reißig"};
        String[] felix = {"Felix","Wilhelm"};
        String[] yannick = {"Yannick","Osenstätter"};
        initTestData.add(timo);
        initTestData.add(alex);
        initTestData.add(felix);
        initTestData.add(yannick);
        for (String[] o : initTestData) {
            DataBaseService.getInstance().insertUser(o[0], o[1]);
        }
    }
}
