package logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AIPlayerTest {

    @Test
    public void testAIDecideToBuy() {
        AIPlayer ai = new AIPlayer("Bot");
        Tile tile = new Tile(Tile.TileType.LAND);

        // AI 有钱，且 tile 等级为0，应返回 true
        ai.setMoney(500);
        tile.setLevel(0);
        Assertions.assertTrue(ai.decideToBuy(tile), "AI 应该选择购买");

        // AI 没钱，不应购买
        ai.setMoney(100);
        Assertions.assertFalse(ai.decideToBuy(tile), "AI 资金不足不应购买");

        // AI 面对已升级地块，不应购买
        ai.setMoney(1000);
        tile.setLevel(2);
        Assertions.assertFalse(ai.decideToBuy(tile), "AI 不应购买已升级地块");
    }
}