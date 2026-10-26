package br.edu.pi1.ratocego.adapter.mqtt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MqttReconnectPolicyTest {

    private final MqttReconnectPolicy policy =
            new MqttReconnectPolicy();

    @Test
    void followsRequiredSequence() {
        long[] expected = {
                1, 2, 4, 8, 16, 30, 30, 30
        };

        for (int index = 0; index < expected.length; index++) {
            assertEquals(
                    expected[index],
                    policy.delaySeconds(index)
            );
        }
    }

    @Test
    void keepsThirtySecondsForLaterAttempts() {
        assertEquals(30, policy.delaySeconds(1000));
    }

    @Test
    void rejectsNegativeIndex() {
        assertThrows(
                IllegalArgumentException.class,
                () -> policy.delaySeconds(-1)
        );
    }
}