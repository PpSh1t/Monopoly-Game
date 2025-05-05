package logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerMoveTest {

    @Test
    public void testMoveWrapAround() {
        Player p = new Player("Test", false, null, 1000);
        p.move(5, 10);
        assertEquals(5, p.getPosition());

        p.move(7, 10); // 应该从 5 -> (5+7)%10 = 2
        assertEquals(2, p.getPosition());
    }
}
