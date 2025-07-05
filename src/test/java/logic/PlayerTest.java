package logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PlayerTest {

    @Test
    public void testPlayerOperations() {
        // 创建一个玩家
        Player player = new Player("Alice", false);

        // 测试基础属性初始化
        Assertions.assertEquals("Alice", player.getName(), "玩家名称应为 Alice");
        Assertions.assertFalse(player.isAI(), "玩家应为人类玩家");
        Assertions.assertEquals(1000, player.getMoney(), "初始金钱应为 1000");
        Assertions.assertEquals(0, player.getPosition(), "初始位置应为 0");
        Assertions.assertFalse(player.isBankrupt(), "初始应不破产");
        Assertions.assertFalse(player.hasExtraTurn(), "初始应无额外回合");
        Assertions.assertFalse(player.isSkipTurn(), "初始应不跳过回合");

        // 修改金钱
        player.setMoney(800);
        Assertions.assertEquals(800, player.getMoney(), "金钱应更新为 800");

        // 设置位置
        player.setPosition(5);
        Assertions.assertEquals(5, player.getPosition(), "位置应为 5");

        // 测试额外回合逻辑
        player.setExtraTurn(true);
        Assertions.assertTrue(player.hasExtraTurn(), "应有额外回合");
        player.setExtraTurn(false);
        Assertions.assertFalse(player.hasExtraTurn(), "应无额外回合");

        // 测试跳过回合逻辑
        player.setSkipTurn(true);
        Assertions.assertTrue(player.isSkipTurn(), "应跳过回合");
        player.setSkipTurn(false);
        Assertions.assertFalse(player.isSkipTurn(), "不应跳过回合");

        // 测试破产逻辑
        player.setBankrupt(true);
        Assertions.assertTrue(player.isBankrupt(), "应破产");
    }

    @Test
    public void testBankruptStateEffect() {
        Player player = new Player("Bob", false);
        player.setBankrupt(true);
        player.setMoney(-100);
        Assertions.assertTrue(player.isBankrupt(), "玩家应已破产");
        Assertions.assertTrue(player.getMoney() < 0, "破产后金钱应为负数");
    }
}
