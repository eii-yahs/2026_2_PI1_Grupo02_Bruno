package br.edu.pi1.ratocego.adapter.mqtt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Status;
import org.springframework.integration.mqtt.core.MqttPahoComponent;
import org.springframework.integration.mqtt.event.MqttConnectionFailedEvent;
import org.springframework.integration.mqtt.event.MqttSubscribedEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MqttConnectionHealthIndicatorTest {

    @Test
    void reportsUnknownUntilAllInboundAdaptersSubscribe() {
        var indicator = new MqttConnectionHealthIndicator();
        assertEquals(Status.UNKNOWN, indicator.health().getStatus());

        subscribed(indicator, "mqttTelemetryInbound");
        subscribed(indicator, "mqttRunStartedInbound");
        subscribed(indicator, "mqttRunFinishedInbound");
        subscribed(indicator, "mqttRunInterruptedInbound");

        assertEquals(Status.UP, indicator.health().getStatus());
    }

    @Test
    void reportsDownWhenAnInboundConnectionFails() {
        var indicator = new MqttConnectionHealthIndicator();
        MqttPahoComponent source = source("mqttTelemetryInbound");

        indicator.onConnectionFailed(new MqttConnectionFailedEvent(source));

        assertEquals(Status.DOWN, indicator.health().getStatus());
    }

    @Test
    void reportsUpAgainAfterFailedInboundAdapterResubscribes() {
        var indicator = new MqttConnectionHealthIndicator();
        subscribed(indicator, "mqttTelemetryInbound");
        subscribed(indicator, "mqttRunStartedInbound");
        subscribed(indicator, "mqttRunFinishedInbound");
        subscribed(indicator, "mqttRunInterruptedInbound");

        indicator.onConnectionFailed(new MqttConnectionFailedEvent(source("mqttTelemetryInbound")));
        assertEquals(Status.DOWN, indicator.health().getStatus());

        subscribed(indicator, "mqttTelemetryInbound");
        assertEquals(Status.UP, indicator.health().getStatus());
    }

    @Test
    void ignoresEventsFromUntrackedComponents() {
        var indicator = new MqttConnectionHealthIndicator();
        MqttPahoComponent source = source("mqttOutbound");

        indicator.onSubscribed(new MqttSubscribedEvent(source, "subscribed"));
        indicator.onConnectionFailed(new MqttConnectionFailedEvent(source));

        assertEquals(Status.UNKNOWN, indicator.health().getStatus());
    }

    private void subscribed(MqttConnectionHealthIndicator indicator, String beanName) {
        indicator.onSubscribed(new MqttSubscribedEvent(source(beanName), "subscribed"));
    }

    private MqttPahoComponent source(String beanName) {
        MqttPahoComponent source = mock(MqttPahoComponent.class);
        when(source.getBeanName()).thenReturn(beanName);
        return source;
    }
}
