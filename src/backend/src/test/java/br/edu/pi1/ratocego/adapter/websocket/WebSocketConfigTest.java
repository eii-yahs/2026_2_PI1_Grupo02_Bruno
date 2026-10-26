package br.edu.pi1.ratocego.adapter.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSocketConfigTest {

    @Test
    void registersFrontendEndpointAndSimpleBroker() {
        TaskScheduler scheduler = mock(TaskScheduler.class);
        WebSocketConfig config = new WebSocketConfig(scheduler);
        StompEndpointRegistry endpointRegistry = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration endpoint = mock(StompWebSocketEndpointRegistration.class);
        when(endpointRegistry.addEndpoint("/ws")).thenReturn(endpoint);
        config.registerStompEndpoints(endpointRegistry);
        verify(endpoint).setAllowedOriginPatterns("http://localhost:5173", "http://127.0.0.1:5173");

        MessageBrokerRegistry brokerRegistry = mock(MessageBrokerRegistry.class);
        SimpleBrokerRegistration broker = mock(SimpleBrokerRegistration.class);
        when(brokerRegistry.enableSimpleBroker("/topic")).thenReturn(broker);
        when(broker.setHeartbeatValue(argThat(value -> java.util.Arrays.equals(value, new long[]{10_000, 10_000}))))
                .thenReturn(broker);
        config.configureMessageBroker(brokerRegistry);
        verify(broker).setTaskScheduler(scheduler);
        verify(brokerRegistry).setApplicationDestinationPrefixes("/app");
    }

    @Test
    void createsSingleThreadHeartbeatScheduler() {
        ThreadPoolTaskScheduler scheduler = assertInstanceOf(ThreadPoolTaskScheduler.class,
                new WebSocketHeartbeatConfig().websocketHeartbeatScheduler());

        assertEquals(1, scheduler.getPoolSize());
        assertEquals("websocket-heartbeat-", scheduler.getThreadNamePrefix());
        scheduler.shutdown();
    }
}
