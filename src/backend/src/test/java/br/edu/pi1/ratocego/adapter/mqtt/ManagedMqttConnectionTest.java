package br.edu.pi1.ratocego.adapter.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManagedMqttConnectionTest {

    @Test
    void connectsSubscribesPublishesAndRoutesMessages() throws Exception {
        MqttAsyncClient client = mock(MqttAsyncClient.class);
        MqttConnectOptions options = new MqttConnectOptions();
        MessageChannel channel = mock(MessageChannel.class);
        when(client.getClientId()).thenReturn("test-client");
        AtomicBoolean mqttConnected = new AtomicBoolean();
        when(client.isConnected()).thenAnswer(ignored -> mqttConnected.get());
        when(channel.send(any(Message.class))).thenReturn(true);
        IMqttDeliveryToken delivery = mock(IMqttDeliveryToken.class);
        when(client.publish(eq("runs/command"), any(MqttMessage.class))).thenReturn(delivery);

        ManagedMqttConnection connection = new ManagedMqttConnection(
                client, options, Duration.ofSeconds(30), 100, Map.of("runs/#", channel));
        try {
            assertFalse(connection.isRunning());
            assertFalse(connection.isConnected());
            assertTrue(connection.isAutoStartup());

            connection.start();
            connection.start();
            verify(client, timeout(1_000)).connect(eq(options), isNull(), any(IMqttActionListener.class));
            var connectListener = org.mockito.ArgumentCaptor.forClass(IMqttActionListener.class);
            verify(client).connect(eq(options), isNull(), connectListener.capture());
            mqttConnected.set(true);
            connectListener.getValue().onSuccess(mock(IMqttToken.class));

            var subscribeListener = org.mockito.ArgumentCaptor.forClass(IMqttActionListener.class);
            verify(client, timeout(1_000)).subscribe(eq(new String[]{"runs/#"}), eq(new int[]{1}),
                    isNull(), subscribeListener.capture());
            subscribeListener.getValue().onSuccess(mock(IMqttToken.class));
            assertTrue(connection.isRunning());
            assertTrue(connection.isConnected());

            connection.publish("runs/command", "payload");
            var published = org.mockito.ArgumentCaptor.forClass(MqttMessage.class);
            verify(client).publish(eq("runs/command"), published.capture());
            assertEquals("payload", new String(published.getValue().getPayload()));
            assertEquals(1, published.getValue().getQos());
            verify(delivery).waitForCompletion(100);

            var callback = org.mockito.ArgumentCaptor.forClass(MqttCallback.class);
            verify(client).setCallback(callback.capture());
            MqttMessage incoming = new MqttMessage("telemetry".getBytes());
            incoming.setQos(1);
            callback.getValue().messageArrived("runs/42/telemetry", incoming);
            var routedMessage = org.mockito.ArgumentCaptor.forClass(Message.class);
            verify(channel).send(routedMessage.capture());
            assertEquals("telemetry", routedMessage.getValue().getPayload());
            assertEquals("runs/42/telemetry", routedMessage.getValue().getHeaders().get("mqtt_receivedTopic"));
        } finally {
            connection.destroy();
        }
    }

    @Test
    void rejectsPublishUntilSubscribedAndCallsStopCallback() throws Exception {
        MqttAsyncClient client = mock(MqttAsyncClient.class);
        ManagedMqttConnection connection = new ManagedMqttConnection(
                client, new MqttConnectOptions(), Duration.ofSeconds(30), 100, Map.of());
        try {
            assertThrows(IllegalStateException.class, () -> connection.publish("topic", "payload"));
            AtomicBoolean callbackCalled = new AtomicBoolean();
            connection.stop(() -> callbackCalled.set(true));
            assertTrue(callbackCalled.get());
            assertFalse(connection.isRunning());
        } finally {
            connection.destroy();
        }
    }

    @Test
    void rejectsMessagesWhenNoRouteAcceptsThem() throws Exception {
        MqttAsyncClient client = mock(MqttAsyncClient.class);
        MessageChannel channel = mock(MessageChannel.class);
        when(channel.send(any(Message.class))).thenReturn(false);
        ManagedMqttConnection connection = new ManagedMqttConnection(
                client, new MqttConnectOptions(), Duration.ofSeconds(30), 100, Map.of("runs/#", channel));
        try {
            var callback = org.mockito.ArgumentCaptor.forClass(MqttCallback.class);
            verify(client).setCallback(callback.capture());
            assertThrows(IllegalStateException.class,
                    () -> callback.getValue().messageArrived("runs/42", new MqttMessage("x".getBytes())));
            callback.getValue().messageArrived("other/topic", new MqttMessage("x".getBytes()));
        } finally {
            connection.destroy();
        }
    }
}
