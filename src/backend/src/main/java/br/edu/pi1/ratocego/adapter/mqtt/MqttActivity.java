package br.edu.pi1.ratocego.adapter.mqtt;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public final class MqttActivity {

    private final AtomicLong lastActivityNanos =
            new AtomicLong(System.nanoTime());

    public void receivedControlPacket() {
        lastActivityNanos.set(System.nanoTime());
    }

    public boolean isExpired(Duration timeout) {
        long elapsed =
                System.nanoTime() - lastActivityNanos.get();

        return elapsed >= timeout.toNanos();
    }
}