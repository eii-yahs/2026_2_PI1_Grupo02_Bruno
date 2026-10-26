package br.edu.pi1.ratocego.config;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeConfigTest {

    @Test
    void createsUtcClock() {
        Clock clock = new TimeConfig().clock();

        assertEquals(ZoneOffset.UTC, clock.getZone());
    }
}
