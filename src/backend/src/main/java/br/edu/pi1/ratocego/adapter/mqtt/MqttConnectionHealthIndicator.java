package br.edu.pi1.ratocego.adapter.mqtt;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("mqttConnection")
public class MqttConnectionHealthIndicator
        implements HealthIndicator {

    private final ManagedMqttConnection connection;

    public MqttConnectionHealthIndicator(
            ManagedMqttConnection connection
    ) {
        this.connection = connection;
    }

    @Override
    public Health health() {
        if (connection.isConnected()) {
            return Health.up()
                    .withDetail("connected", true)
                    .withDetail("subscriptionsReady", true)
                    .build();
        }

        return Health.down()
                .withDetail("connected", false)
                .build();
    }
}