package br.edu.pi1.ratocego.adapter.mqtt;

import br.edu.pi1.ratocego.service.TelemetryService;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class MqttTelemetryListener {

    private final TelemetryService telemetryService;

    public MqttTelemetryListener(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @ServiceActivator(inputChannel = "mqttTelemetryInputChannel")
    public void onTelemetrySample(@Payload String payload) {
        telemetryService.processSample(payload);
    }
}
