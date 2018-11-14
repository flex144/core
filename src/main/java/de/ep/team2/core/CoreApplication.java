package de.ep.team2.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import de.ep.team2.core.DbTest.User;

import java.util.LinkedList;


@SpringBootApplication
public class CoreApplication implements CommandLineRunner{

    private static final Logger log = LoggerFactory.getLogger(CoreApplication.class);

    public static void main(String args[]) {
        SpringApplication.run(CoreApplication.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) throws Exception {
        testDB();
    }

    /**
     * Method to Test the DB Connection.
     * Creates some test users writes them in the Database and runs a query on the Database.
     * Logs the results to the Serverconsole.
     */
    private void testDB(){
        log.info("Creating tables");

        jdbcTemplate.execute("DROP TABLE IF EXISTS users");
        jdbcTemplate.execute("CREATE TABLE users(" +
                "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

        LinkedList<Object[]> test = new LinkedList<>();
        Object[] timo = {"Timo","Heinrich"};
        Object[] alex = {"Alexander","Reißig"};
        Object[] felix = {"Felix","Wilhelm"};
        Object[] yannick = {"Yannick","Osenstätter"};
        test.add(timo);
        test.add(alex);
        test.add(felix);
        test.add(yannick);

        for (Object[] o:test) {
            log.info(String.format("Inserting user record for %s %s", o[0], o[1]));
        }

        jdbcTemplate.batchUpdate("INSERT INTO users(first_name, last_name) VALUES (?,?)", test);

        log.info("Querying for user records where first_name = 'Timo':");
        jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM users WHERE first_name = ?", new Object[] { "Timo" },
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
        ).forEach(user -> log.info(user.toString()));
    }
}
