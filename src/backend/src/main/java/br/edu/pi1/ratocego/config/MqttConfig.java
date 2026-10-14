package br.edu.pi1.ratocego.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
public class MqttConfig {

    @Value("${app.mqtt.client-id}")
    private String clientId;

    @Value("${app.mqtt.topic-prefix}")
    private String topicPrefix;

    @Value("${app.mqtt.server-uri}")
    private String brokerUri;

    @Value("${app.mqtt.qos:1}")
    private int qos;

    @Bean
    MqttPahoClientFactory mqttClientFactory(
            @Value("${app.mqtt.username}") String username,
            @Value("${app.mqtt.password}") String password,
            @Value("${app.mqtt.connection-timeout-seconds:10}") int connectionTimeoutSeconds,
            @Value("${app.mqtt.keep-alive-seconds:10}") int keepAliveSeconds,
            @Value("${app.mqtt.automatic-reconnect:true}") boolean automaticReconnect) {

        var options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(connectionTimeoutSeconds);
        options.setKeepAliveInterval(keepAliveSeconds);
        options.setAutomaticReconnect(automaticReconnect);

        var factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    MessageChannel mqttTelemetryInputChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageChannel mqttRunStartedInputChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageChannel mqttRunFinishedInputChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageChannel mqttRunInterruptedInputChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageProducer mqttTelemetryInbound(
            MqttPahoClientFactory factory,
            @Qualifier("mqttTelemetryInputChannel") MessageChannel inputChannel) {
        return inboundAdapter("-telemetry", "/runs/+/telemetry", factory, inputChannel);
    }

    @Bean
    MessageProducer mqttRunStartedInbound(
            MqttPahoClientFactory factory,
            @Qualifier("mqttRunStartedInputChannel") MessageChannel inputChannel) {
        return inboundAdapter("-run-started", "/runs/+/started", factory, inputChannel);
    }

    @Bean
    MessageProducer mqttRunFinishedInbound(
            MqttPahoClientFactory factory,
            @Qualifier("mqttRunFinishedInputChannel") MessageChannel inputChannel) {
        return inboundAdapter("-run-finished", "/runs/+/finished", factory, inputChannel);
    }

    @Bean
    MessageProducer mqttRunInterruptedInbound(
            MqttPahoClientFactory factory,
            @Qualifier("mqttRunInterruptedInputChannel") MessageChannel inputChannel) {
        return inboundAdapter("-run-interrupted", "/runs/+/interrupted", factory, inputChannel);
    }

    @Bean
    MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    MessageHandler mqttOutbound(
            MqttPahoClientFactory factory,
            @Value("${app.mqtt.outbound-client-id:ratocego-backend-out}") String outboundClientId,
            @Value("${app.mqtt.publish-completion-timeout-ms:5000}") long completionTimeoutMs,
            @Value("${app.mqtt.qos:1}") int qos) {
        var handler = new MqttPahoMessageHandler(
                outboundClientId,
                factory
        );
        handler.setCompletionTimeout(completionTimeoutMs);
        handler.setDefaultQos(qos);
        return handler;
    }

    private MessageProducer inboundAdapter(
            String clientIdSuffix,
            String topicSuffix,
            MqttPahoClientFactory factory,
            MessageChannel outputChannel) {
        var adapter = new MqttPahoMessageDrivenChannelAdapter(
                brokerUri,
                clientId + clientIdSuffix,
                factory,
                topicPrefix + topicSuffix
        );
        adapter.setQos(qos);
        adapter.setOutputChannel(outputChannel);
        return adapter;
    }
}
