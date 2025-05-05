package logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTest {

    @Test
    public void testPlayerOperations() {
        Player p = new Player("Alice", false, "avatar1.png", 1000);
        assertEquals(1000, p.getMoney());
        p.changeMoney(-200);
        assertEquals(800, p.getMoney());

        p.addLand(3);
        assertTrue(p.getOwnedLandPositions().contains(3));

        System.out.println(p);  // 输出测试信息
    }

}
