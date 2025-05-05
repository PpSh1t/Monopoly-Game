package logic;

import javax.swing.*;
import java.util.List;

public class Game {
    private List<Tile> map;

    public Game(List<Tile> map) {
        this.map = map;
    }

    public void handleTile(Player player) {
        Tile tile = map.get(player.getPosition());
        switch (tile.getType()) {
            case LAND:
                handleLandTile(player, tile);
                break;
            default:
                // 其他类型地块之后再处理
                System.out.println("该地块类型暂未实现逻辑");
        }
    }

    private void handleLandTile(Player player, Tile tile) {
        if (tile.getOwner() == null) {
            int choice = JOptionPane.showConfirmDialog(null,
                    player.getName() + " 遇到空地，是否以 $" + tile.getPrice() + " 购买？", "购买土地",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION && player.getMoney() >= tile.getPrice()) {
                player.setMoney(player.getMoney() - tile.getPrice());
                tile.setOwner(player.getName());
                System.out.println(player.getName() + " 购买了该地块！");
            } else {
                System.out.println(player.getName() + " 放弃购买或资金不足。");
            }
        } else if (tile.getOwner().equals(player.getName())) {
            System.out.println("这是你自己的地。可以考虑升级（后续开发）");
        } else {
            int rent = tile.getRent();
            System.out.println(player.getName() + " 进入了 " + tile.getOwner() + " 的地，需支付租金 $" + rent);
            player.setMoney(player.getMoney() - rent);
            // 此处可查找 tile.getOwner() 的 Player 并加钱（后续添加玩家列表）
        }
    }
}
