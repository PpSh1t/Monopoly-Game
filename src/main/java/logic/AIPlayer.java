package logic;

public class AIPlayer extends Player {

    public AIPlayer(String name) {
        super(name, true);
    }
    // AI是否买地（简单策略）
    public boolean decideToBuy(Tile tile) {
        return tile.getLevel() == 0 && getMoney() > 200;
    }

}

