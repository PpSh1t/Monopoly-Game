package logic;

import javax.swing.*;
import java.util.List;

public class Game {
    private List<Tile> map;

    public Game(List<Tile> map) {
        this.map = map;
    }

    /**
     * 统一调度
     * 处理玩家落点：根据地块类型分别调用不同逻辑
     */
    public void handleTile(Player player) {
        Tile tile = map.get(player.getPosition());
        System.out.println(player.getName() + " 停在了 " + tile.getType() + " 地块上。");

        switch (tile.getType()) {
            case LAND:
                if (tile.getOwner() == null) {
                    handleEmptyLand(player, tile);          // 空地购买逻辑
                } else if (isOwnedByPlayer(tile, player)) {
                    handlePlayerOnOwnLand(player, tile);    // 自己地：升级逻辑
                } else {
                    handlePayRent(player, tile);            // 他人地：付租金逻辑
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

            case HOSPITAL:
                System.out.println(player.getName() + " 进医院，交费 $100 并跳过一回合！");
                player.setMoney(player.getMoney() - 100);
                player.setSkipTurn(true);
                break;

            case TAX:
                int tax = player.getMoney() / 10;
                player.setMoney(player.getMoney() - tax);
                System.out.println(player.getName() + " 缴纳税金 $" + tax + "，剩余 $" + player.getMoney());
                break;

            default:
                System.out.println("未知地块类型，暂未实现。");
        }
    }


    /**
     * 判断该地块是否属于当前玩家
     *
     * @param tile   当前地块
     * @param player 当前玩家
     * @return true 表示是自己的地块
     */
    private boolean isOwnedByPlayer(Tile tile, Player player) {
        return player.getName().equals(tile.getOwner());
    }

    /**
     * 处理玩家到达自己地块时的升级操作
     *
     * @param player 当前玩家
     * @param tile   当前地块（必须是自己的）
     */
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

    /**
     * AI 玩家是否决定升级自己地块的策略
     *
     * @return true 表示愿意升级，false 表示跳过升级
     */
    private boolean decideAIWantsToUpgrade() {
        // 简单策略：70% 的概率愿意升级
        return Math.random() < 0.7;
    }


    /**
     * 玩家走到空地时的购买逻辑
     */
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

    /**
     * 玩家走到别人地时付租金
     */
    private void handlePayRent(Player player, Tile tile) {
        int rent = tile.getRent();
        player.setMoney(player.getMoney() - rent);
        System.out.println(player.getName() + " 支付给 " + tile.getOwner() + " 租金 $" + rent + "，剩余 $" + player.getMoney());
        // TODO: 将租金加到 tile.getOwner() 的玩家身上（需玩家列表支持）
    }

    /**
     * 玩家走到幸运地块的处理逻辑
     */
    private void handleLuckyEvent(Player player) {
        int reward = 100 + (int) (Math.random() * 200); // $100~$299 随机奖励
        player.setMoney(player.getMoney() + reward);
        System.out.println(player.getName() + " 触发幸运事件，获得奖金 $" + reward + "，现有 $" + player.getMoney());

        // BONUS（可选）：再掷一次骰子
        if (!player.isAI()) {
            JOptionPane.showMessageDialog(null, player.getName() + " 获得一次额外掷骰机会！");
            player.setExtraTurn(true); // 你需要在游戏回合中支持额外回合逻辑
        }
    }

    /**
     * 玩家走到不幸地块的处理逻辑
     */
    private void handleUnluckyEvent(Player player) {
        int penalty = 50 + (int) (Math.random() * 150); // $50~$199 随机惩罚
        player.setMoney(player.getMoney() - penalty);
        System.out.println(player.getName() + " 遭遇不幸事件，损失 $" + penalty + "，现有 $" + player.getMoney());

        // BONUS（可选）：跳过下一回合
        double skipChance = 0.3;
        if (Math.random() < skipChance) {
            System.out.println(player.getName() + " 太倒霉了，下回合也要跳过！");
            player.setSkipTurn(true);
        }
    }






}
