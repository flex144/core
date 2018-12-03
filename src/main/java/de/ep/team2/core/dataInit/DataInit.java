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
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE ");
        jdbcTemplate.execute("CREATE TABLE users(" +
                "id SERIAL, email VARCHAR(255) NOT NULL ," +
                " first_name VARCHAR(255), last_name VARCHAR(255) ," +
                " password varchar(20) not null, " +
                " enabled boolean not null default false, " +
                " primary key(email))");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_roles CASCADE ");
        jdbcTemplate.execute("CREATE TABLE user_roles(" +
                " user_role_id SERIAL PRIMARY KEY, email varchar(255) not null, " +
                " role varchar (20) not null, UNIQUE (email, role),  " +
                " FOREIGN KEY (email) references users (email))");

    }

    private void fillUsers() {
        /**
        LinkedList<String[]> initTestData = new LinkedList<>();
        String[] timo = {"timo@gmail.com", "Timo", "Heinrich", "123"};
        String[] alex = {"alex@gmail.com", "Alexander", "Reißig"};
        String[] felix = {"felix@gmail.com", "Felix", "Wilhelm"};
        String[] yannick = {"yannick@gmail.com", null, null};
        initTestData.add(timo);
        initTestData.add(alex);
        initTestData.add(felix);
        initTestData.add(yannick);
        for (String[] o : initTestData) {
            DataBaseService.getInstance().insertUser(o[0], o[1], o[2]);
        }
        */
        jdbcTemplate.execute("INSERT into users (id, email, first_name, last_name, password, enabled) " +
                "VALUES (1, 'timo@gmail.com', 'Timo', 'Heinrich', '123', TRUE)");
        jdbcTemplate.execute("INSERT into users (email, first_name, last_name, password, enabled) " +
                "VALUES ('alex@gmail.com', 'Alex', 'Reißig', '123', TRUE)");
        jdbcTemplate.execute("INSERT into users (email, first_name, last_name, password, enabled) " +
                "VALUES ('felix@gmail.com', 'Felix', 'Wilhelm', '123', TRUE)");
        jdbcTemplate.execute("INSERT into users (email, first_name, last_name, password, enabled) " +
                "VALUES ('yannick@gmail.com', 'Yannick', 'Osenstätter', '123', TRUE)");
        jdbcTemplate.execute("INSERT INTO user_roles (email, role)" +
                "VALUES ('timo@gmail.com', 'ROLE_USER')" );
        jdbcTemplate.execute("INSERT INTO user_roles (email, role)" +
                "VALUES ('alex@gmail.com', 'ROLE_USER')" );
        jdbcTemplate.execute("INSERT INTO user_roles (email, role)" +
                "VALUES ('felix@gmail.com', 'ROLE_MOD')" );
        jdbcTemplate.execute("INSERT INTO user_roles (email, role)" +
                "VALUES ('yannick@gmail.com', 'ROLE_MOD')" );
    }
}
