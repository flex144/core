package de.ep.team2.core;

import de.ep.team2.core.dataInit.DataInit;
import de.ep.team2.core.service.DataBaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class CoreApplication implements CommandLineRunner{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CoreApplication(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static void main(String args[]) {
        SpringApplication.run(CoreApplication.class, args);
    }

    @Override
    public void run(String... strings) {
        //Sets the JDBC-Template in DataBaseServices to us it in other classes.
        DataBaseService.getInstance().setJdbcTemplate(jdbcTemplate);
        new DataInit(jdbcTemplate);
    }
}
