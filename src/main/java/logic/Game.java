package logic;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;


@Getter
@Setter
public class Game implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private List<Tile> map;
    @Getter
    private List<Player> players;
    private int currentPlayerIndex = 0;

    public Game(List<Tile> map, List<Player> players) {
        this.map = map;
        this.players = players;
    }

    public void nextTurn() {
        Player player = players.get(currentPlayerIndex);

        if (!player.isBankrupt()) {
            takeTurn(player);
        }

        // 判断游戏是否结束
        long alive = players.stream().filter(p -> !p.isBankrupt()).count();
        if (alive <= 1) {
            players.stream().filter(p -> !p.isBankrupt()).findFirst().ifPresent(winner -> JOptionPane.showMessageDialog(null, "游戏结束，胜者是 " + winner.getName()));
            return;
        }

        // 切换到下一个玩家
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).isBankrupt());
    }


    public void runGameLoop() {
        boolean gameOngoing = true;

        while (gameOngoing) {
            for (Player player : players) {
                if (player.isBankrupt()) continue;

                System.out.println("\n--- " + player.getName() + " 的回合 ---");

                if (player.isSkipTurn()) {
                    System.out.println(player.getName() + " 跳过了本回合！");
                    player.setSkipTurn(false);
                    continue;
                }

                // 使用循环处理玩家回合，包括额外回合
                boolean hasExtraTurn = player.hasExtraTurn();
                while (hasExtraTurn) {
                    takeTurn(player);  // 进行回合
                    hasExtraTurn = player.hasExtraTurn();  // 检查是否还有额外回合
                }

                takeTurn(player);  // 进行正常回合

                //判断破产
                if (player.getMoney() < 0) {
                    player.setBankrupt(true);
                    System.out.println(player.getName() + " 破产出局！");
                    clearPlayerAssets(player);  // 清理该玩家的所有资产
                }

                long remaining = players.stream().filter(p -> !p.isBankrupt()).count();
                if (remaining == 1) {
                    gameOngoing = false;
                    break;
                }
            }
        }

        // 游戏结束
        for (Player p : players) {
            if (!p.isBankrupt()) {
                System.out.println("\n🏆 游戏结束，胜者是 " + p.getName() + "！");
            }
        }
    }



    private void takeTurn(Player player) {
        int steps = Dice.roll();
        System.out.println(player.getName() + " 掷出了 " + steps);

        int newPosition = (player.getPosition() + steps) % map.size();
        player.setPosition(newPosition);
        System.out.println(player.getName() + " 移动到了位置 " + newPosition);

        handleTile(player);
    }


    public void handleTile(Player player) {
        Tile tile = map.get(player.getPosition());
        System.out.println(player.getName() + " 停在了 " + tile.getType() + " 地块上。");

        switch (tile.getType()) {
            case LAND:
                if (tile.getOwner() == null) {
                    handleEmptyLand(player, tile);
                } else if (isOwnedByPlayer(tile, player)) {
                    handlePlayerOnOwnLand(player, tile);
                } else {
                    handlePayRent(player, tile);
                }
                break;

            case LUCKY:
                handleLuckyEvent(player);
                break;

            case UNLUCKY:
                handleUnluckyEvent(player);
                break;

            case PRISON:
                System.out.println(player.getName() + " 进了监狱，下一回合跳过！");
                player.setSkipTurn(true);
                break;

            default:
                System.out.println("未知地块类型，暂未实现。");
        }
    }

    private boolean isOwnedByPlayer(Tile tile, Player player) {
        return player.getName().equals(tile.getOwner());
    }

    private void handlePlayerOnOwnLand(Player player, Tile tile) {
        if (!tile.canUpgrade()) {
            System.out.println(player.getName() + " 的土地已满级，不能再升级。");
            return;
        }

        int upgradeCost = tile.getUpgradeCost();

        if (player.getMoney() >= upgradeCost) {
            boolean wantsToUpgrade = !player.isAI() || decideAIWantsToUpgrade();

            if (wantsToUpgrade) {
                tile.upgrade();
                player.setMoney(player.getMoney() - upgradeCost);
                System.out.println(player.getName() + " 升级了自己的土地为等级 " + tile.getLevel() +
                        "，花费 $" + upgradeCost + "，剩余 $" + player.getMoney());
            } else {
                System.out.println(player.getName() + " 选择不升级自己的土地。");
            }
        } else {
            System.out.println(player.getName() + " 金钱不足（$" + player.getMoney() + "），无法升级土地（需 $" + upgradeCost + "）");
        }
    }

    private boolean decideAIWantsToUpgrade() {
        return Math.random() < 0.7;
    }

    private void handleEmptyLand(Player player, Tile tile) {
        if (player.isAI()) {
            if (player.getMoney() >= tile.getPrice() && Math.random() < 0.6) {
                tile.setOwner(player.getName());
                player.setMoney(player.getMoney() - tile.getPrice());
                System.out.println(player.getName() + "（AI）购买了该地块，剩余 $" + player.getMoney());
            } else {
                System.out.println(player.getName() + "（AI）决定不购买该地块。");
            }
        } else {
            int choice = JOptionPane.showConfirmDialog(null,
                    player.getName() + " 遇到空地，是否以 $" + tile.getPrice() + " 购买？", "购买土地",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION && player.getMoney() >= tile.getPrice()) {
                tile.setOwner(player.getName());
                player.setMoney(player.getMoney() - tile.getPrice());
                System.out.println(player.getName() + " 购买了该地块，剩余 $" + player.getMoney());
            } else {
                System.out.println(player.getName() + " 放弃购买或资金不足。");
            }
        }
    }

    private void handlePayRent(Player player, Tile tile) {
        int rent = tile.getRent();
        player.setMoney(player.getMoney() - rent);
        System.out.println(player.getName() + " 支付给 " + tile.getOwner() + " 租金 $" + rent + "，剩余 $" + player.getMoney());

        for (Player p : players) {
            if (p.getName().equals(tile.getOwner()) && !p.isBankrupt()) {
                p.setMoney(p.getMoney() + rent);
                System.out.println(tile.getOwner() + " 获得租金 $" + rent + "，现有 $" + p.getMoney());
                break;
            }
        }
    }

    private void handleLuckyEvent(Player player) {
        int reward = 100 + (int) (Math.random() * 200);
        player.setMoney(player.getMoney() + reward);
        System.out.println(player.getName() + " 触发幸运事件，获得奖金 $" + reward + "，现有 $" + player.getMoney());

        if (!player.isAI()) {
            JOptionPane.showMessageDialog(null, player.getName() + " 获得一次额外掷骰机会！");
            player.setExtraTurn(true);
        }
    }

    private void handleUnluckyEvent(Player player) {
        int penalty = 50 + (int) (Math.random() * 150);
        player.setMoney(player.getMoney() - penalty);
        System.out.println(player.getName() + " 遭遇不幸事件，损失 $" + penalty + "，现有 $" + player.getMoney());

        if (Math.random() < 0.3) {
            System.out.println(player.getName() + " 太倒霉了，下回合也要跳过！");
            player.setSkipTurn(true);
        }
    }

    private void clearPlayerAssets(Player player) {
        for (Tile tile : map) {
            if (player.getName().equals(tile.getOwner())) {
                tile.setOwner(null);
                tile.setLevel(1);
            }
        }
    }

    // Game.java
    public boolean isGameOver() {
        return players.stream().filter(p -> !p.isBankrupt()).count() <= 1;
    }

    public Player getWinner() {
        return players.stream().filter(p -> !p.isBankrupt()).findFirst().orElse(null);
    }

}
