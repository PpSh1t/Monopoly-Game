package logic;

public class AIPlayer extends Player {

    public AIPlayer(String name) {
        super(name, true);
    }
    // 示例：AI是否买地（简单策略）
    public boolean decideToBuy(Tile tile) {
        return tile.getLevel() == 0 && getMoney() > 200;
    }

    // 示例：AI是否升级房产
    //public boolean decideToUpgrade(Tile tile) {
    //   return getOwnedLandPositions().contains(tile) && getMoney() > 300;


// 更多 AI 策略以后扩展
}

