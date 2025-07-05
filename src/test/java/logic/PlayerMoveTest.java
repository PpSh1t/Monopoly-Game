package logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PlayerMoveTest {

    @Test
    public void testMoveWrapAround() {
        // 模拟地图 12 个地块
        List<Tile> mockMap = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            mockMap.add(new Tile(Tile.TileType.LAND));
        }

        // 创建一个玩家，设置初始位置为10
        Player player = new Player("TestPlayer", false);
        player.setPosition(10);

        // 假设掷出了3点（即移动3格，应该到 (10 + 3) % 12 = 1）
        int roll = 3;
        int from = player.getPosition();
        int to = (from + roll) % mockMap.size();
        player.setPosition(to);

        // 断言最终位置是否正确
        Assertions.assertEquals(1, player.getPosition(),
                "玩家移动后的最终位置应为1格（绕回起点）");
    }

    @Test
    public void testNoWrapAround() {
        Player player = new Player("TestPlayer", false);
        player.setPosition(2);

        int roll = 4;
        int to = (player.getPosition() + roll) % 12;
        player.setPosition(to);

        Assertions.assertEquals(6, player.getPosition(),
                "玩家移动后的最终位置应为6");
    }

    @Test
    public void testMultipleRounds() {
        Player player = new Player("TestPlayer", false);
        player.setPosition(11);

        int roll = 25; // 超过两圈
        int to = (player.getPosition() + roll) % 12;
        player.setPosition(to);

        Assertions.assertEquals((11 + 25) % 12, player.getPosition(),
                "玩家多圈移动后应在正确位置");
    }


}
