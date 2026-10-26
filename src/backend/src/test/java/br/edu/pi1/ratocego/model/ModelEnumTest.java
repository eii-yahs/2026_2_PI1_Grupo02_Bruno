package br.edu.pi1.ratocego.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelEnumTest {

    @Test
    void exposesSupportedMazeAndBatteryStates() {
        assertEquals(3, MazeType.values().length);
        assertEquals(MazeType.GRID_4X4, MazeType.valueOf("GRID_4X4"));
        assertEquals(BatteryStatus.NORMAL, BatteryStatus.valueOf("NORMAL"));
        assertEquals(BatteryStatus.LOW, BatteryStatus.valueOf("LOW"));
    }
}
