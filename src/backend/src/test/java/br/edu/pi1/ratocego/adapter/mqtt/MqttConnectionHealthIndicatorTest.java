package br.edu.pi1.ratocego.adapter.mqtt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MqttConnectionHealthIndicatorTest {

    @Test
    void reportsUpWhenConnectionAndSubscriptionsAreReady() {
        ManagedMqttConnection connection =
                mock(ManagedMqttConnection.class);

        when(connection.isConnected()).thenReturn(true);

        MqttConnectionHealthIndicator indicator =
                new MqttConnectionHealthIndicator(connection);

        assertEquals(
                Status.UP,
                indicator.health().getStatus()
        );
    }

    @Test
    void reportsDownWhenDisconnected() {
        ManagedMqttConnection connection =
                mock(ManagedMqttConnection.class);

        when(connection.isConnected()).thenReturn(false);

        MqttConnectionHealthIndicator indicator =
                new MqttConnectionHealthIndicator(connection);

        assertEquals(
                Status.DOWN,
                indicator.health().getStatus()
        );
    }
}