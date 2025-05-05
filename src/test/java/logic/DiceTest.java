package logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiceTest {

    @Test
    public void testRollInRange() {
        for (int i = 0; i < 100; i++) {
            int roll = Dice.roll();
            assertTrue(roll >= 2 && roll <= 12, "骰子结果应在 2~12 之间");
        }
    }
}
