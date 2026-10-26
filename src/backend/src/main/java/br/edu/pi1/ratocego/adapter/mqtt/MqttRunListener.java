package br.edu.pi1.ratocego.adapter.mqtt;

import br.edu.pi1.ratocego.service.RunLifecycleService;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MqttRunListener {

    private static final Logger log = LoggerFactory.getLogger(MqttRunListener.class);

    private final RunLifecycleService runLifecycleService;

    public MqttRunListener(RunLifecycleService runLifecycleService) {
        this.runLifecycleService = runLifecycleService;
    }

    @ServiceActivator(inputChannel = "mqttRunStartedInputChannel")
    public void onRunStarted(
            @Payload String payload) {
        runLifecycleService.processStarted(payload);
    }

    @ServiceActivator(inputChannel = "mqttRunFinishedInputChannel")
    public void onRunFinished(@Payload String payload) {
        try {
            runLifecycleService.processFinished(payload);
        } catch (RuntimeException exception) {
            log.error("Could not process MQTT run.finished event: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @ServiceActivator(inputChannel = "mqttRunInterruptedInputChannel")
    public void onRunInterrupted(@Payload String payload) {
        runLifecycleService.processInterrupted(payload);
    }
}
