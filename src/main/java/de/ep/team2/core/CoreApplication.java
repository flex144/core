package de.ep.team2.core;

import de.ep.team2.core.DataInit.DataInit;
import de.ep.team2.core.service.DataBaseService;
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
    public void run(String... strings) {
        //Sets the JDBC-Template in DataBaseServices to us it in other classes.
        DataBaseService.getInstance().setJdbcTemplate(jdbcTemplate);
        testDB();
    }

    /**
     * Method to Test the DB Connection.
     * Creates some test users writes them in the Database and runs a query on the Database.
     * Logs the results to the Serverconsole.
     */
    private void testDB(){
        new DataInit(jdbcTemplate,log);
        log.info("Querying for user records where first_name = 'Timo':");
        jdbcTemplate.query(
                "SELECT id, email, first_name, last_name FROM users WHERE first_name = ?", new Object[] { "Timo" },
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("email"),
                        rs.getString("first_name"), rs.getString("last_name"))
        ).forEach(user -> log.info(user.toString()));
    }
}
