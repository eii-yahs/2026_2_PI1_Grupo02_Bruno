package br.edu.pi1.ratocego.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RunSequenceInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public RunSequenceInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        String database = jdbcTemplate.execute((ConnectionCallback<String>) connection ->
                connection.getMetaData().getDatabaseProductName());
        if (database != null && database.equalsIgnoreCase("PostgreSQL")) {
            jdbcTemplate.queryForObject(
                    "SELECT setval('runs_id_seq', GREATEST(COALESCE(MAX(id), 1), 1), MAX(id) IS NOT NULL) FROM runs",
                    Long.class);
        }
    }
}
