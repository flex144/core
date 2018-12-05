package de.ep.team2.core;

import de.ep.team2.core.dataInit.DataInit;
import de.ep.team2.core.service.DataBaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

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
        //new DataInit(jdbcTemplate,log);
    }
}
