package br.edu.pi1.ratocego.adapter.websocket;

import br.edu.pi1.ratocego.dto.websocket.TelemetryUpdate;
import br.edu.pi1.ratocego.service.WebSocketUpdateEvent;
import br.edu.pi1.ratocego.service.port.TelemetryPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(WebSocketUpdateTransactionTest.Config.class)
class WebSocketUpdateTransactionTest {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    TelemetryPublisher telemetryPublisher;

    @Test
    void publishesUpdateAfterCommit() {
        var update = TelemetryUpdate.builder().runId(2).sequence(7).build();
        new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                eventPublisher.publishEvent(new WebSocketUpdateEvent(update)));

        verify(telemetryPublisher).publishTelemetry(update);
    }

    @Test
    void doesNotPublishUpdateWhenTransactionRollsBack() {
        var update = TelemetryUpdate.builder().runId(2).sequence(8).build();
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            eventPublisher.publishEvent(new WebSocketUpdateEvent(update));
            status.setRollbackOnly();
        });

        verify(telemetryPublisher, never()).publishTelemetry(update);
    }

    @Configuration
    @EnableTransactionManagement
    static class Config {

        @Bean
        DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
        }

        @Bean
        PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        TelemetryPublisher telemetryPublisher() {
            return mock(TelemetryPublisher.class);
        }

        @Bean
        WebSocketUpdateEventListener webSocketUpdateEventListener(TelemetryPublisher publisher) {
            return new WebSocketUpdateEventListener(publisher);
        }
    }
}
