package br.edu.pi1.ratocego.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RunSequenceInitializerTest {

    @Test
    void resetsSequenceForPostgres() throws Exception {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);
        when(connection.getMetaData()).thenReturn(metadata);
        when(metadata.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(jdbc.execute(org.mockito.ArgumentMatchers.<ConnectionCallback<String>>any())).thenAnswer(invocation -> {
            ConnectionCallback<String> callback = invocation.getArgument(0);
            return callback.doInConnection(connection);
        });
        when(jdbc.queryForObject("SELECT setval('runs_id_seq', GREATEST(COALESCE(MAX(id), 1), 1), MAX(id) IS NOT NULL) FROM runs",
                Long.class)).thenReturn(1L);

        new RunSequenceInitializer(jdbc).run(new DefaultApplicationArguments());

        verify(jdbc).queryForObject("SELECT setval('runs_id_seq', GREATEST(COALESCE(MAX(id), 1), 1), MAX(id) IS NOT NULL) FROM runs",
                Long.class);
    }

    @Test
    void leavesOtherDatabaseSequencesUntouched() throws Exception {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);
        when(connection.getMetaData()).thenReturn(metadata);
        when(metadata.getDatabaseProductName()).thenReturn("H2");
        when(jdbc.execute(org.mockito.ArgumentMatchers.<ConnectionCallback<String>>any())).thenAnswer(invocation -> {
            ConnectionCallback<String> callback = invocation.getArgument(0);
            return callback.doInConnection(connection);
        });

        new RunSequenceInitializer(jdbc).run(new DefaultApplicationArguments());

        verify(jdbc, never()).queryForObject(anyString(), eq(Long.class));
    }
}
