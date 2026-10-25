package br.edu.pi1.ratocego.adapter.mqtt;

public final class MqttReconnectPolicy {

    public long delaySeconds(int attemptIndex) {
        if (attemptIndex < 0) {
            throw new IllegalArgumentException(
                    "attemptIndex não pode ser negativo"
            );
        }

        if (attemptIndex < 5) {
            return 1L << attemptIndex;
        }

        return 30L;
    }
}