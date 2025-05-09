package ui;

import data.MapDAO;
import logic.Dice;
import logic.Game;
import logic.Player;
import logic.Tile;
import logic.Tile.TileType;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class GameMapUI extends JFrame {
    private Image backgroundImage;
    private List<Player> players;
    private final int[][] playerStartOffsets = {
            {0, 0}, {15, 0}, {0, 15}, {15, 15}
    };
    private int currentPlayerIndex = 0;
    private Game game;
    private JLayeredPane layeredPane;
    private JLabel[] playerMarkers;
    private JLabel[] moneyLabels;
    private JLabel[] tileLabels; // 新增：保存地块标签引用

    public GameMapUI(List<Player> selectedPlayers) {
        this.players = selectedPlayers;
        this.game = new Game(MapDAO.loadMap(), players);
        this.playerMarkers = new JLabel[players.size()];
        this.moneyLabels = new JLabel[players.size()];
        this.tileLabels = new JLabel[12]; // 12个地块

        setTitle("游戏地图");
        backgroundImage = loadImage("/icons/game.png");
        int width = backgroundImage.getWidth(null);
        int height = backgroundImage.getHeight(null);
        setSize(width + 10, height + 37);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(width, height));
        setContentPane(layeredPane);

        // 添加背景图到底层
        JLabel bgLabel = new JLabel(new ImageIcon(backgroundImage));
        bgLabel.setBounds(0, 0, width, height);
        layeredPane.add(bgLabel, JLayeredPane.FRAME_CONTENT_LAYER);

        // 添加地块图标到默认层
        loadMapTiles(layeredPane);

        // 添加玩家头像到底部和地图起点
        renderPlayers(layeredPane);

        // 添加骰子按钮
        addDiceButton();

        setVisible(true);

        // 如果是AI玩家，自动开始回合
        if (getCurrentPlayer().isAI()) {
            startAITurn();
        }
    }

    private void addDiceButton() {
        ImageIcon diceIcon = loadIcon("/icons/dice.png");
        JButton diceButton = new JButton(diceIcon);
        diceButton.setBounds(120, 120, diceIcon.getIconWidth(), diceIcon.getIconHeight());
        diceButton.setBorderPainted(false);
        diceButton.setContentAreaFilled(false);
        diceButton.setFocusPainted(false);

        diceButton.addActionListener(e -> {
            if (!getCurrentPlayer().isAI()) {
                rollDice();
            }
        });

        layeredPane.add(diceButton, JLayeredPane.MODAL_LAYER);
    }

    private void rollDice() {
        Player currentPlayer = getCurrentPlayer();

        // 如果玩家应该跳过回合，直接返回
        if (currentPlayer.isSkipTurn()) {
            JOptionPane.showMessageDialog(this, currentPlayer.getName() + " 正在监狱中，无法行动！");
            nextPlayerTurn();
            return;
        }

        // 如果玩家有额外回合标志，先清除它
        if (currentPlayer.hasExtraTurn()) {
            currentPlayer.setExtraTurn(false);
        }

        int steps = Dice.roll();
        JOptionPane.showMessageDialog(this, currentPlayer.getName() + " 掷出了 " + steps + "点！");
        movePlayer(steps);
    }

    private void movePlayer(int steps) {
        Player player = getCurrentPlayer();
        int newPosition = (player.getPosition() + steps) % 12; // 12个地块

        // 更新玩家位置
        player.setPosition(newPosition);

        // 更新玩家在地图上的显示
        updatePlayerMarker(player);

        // 处理地块事件
        handleTileEvent(player);

        // 更新金钱显示
        updateMoneyDisplay();
    }

    private void updatePlayerMarker(Player player) {
        int index = players.indexOf(player);
        if (index < 0 || index >= playerMarkers.length) return;

        int[][] positions = {
                {15, 15}, {85, 15}, {155, 15}, {225, 15}, // 上边
                {225, 85}, {225, 155},                     // 右边
                {225, 225}, {155, 225}, {85, 225}, {15, 225}, // 下边
                {15, 155}, {15, 85}                         // 左边
        };

        int posX = positions[player.getPosition()][0] + playerStartOffsets[index][0];
        int posY = positions[player.getPosition()][1] + playerStartOffsets[index][1];

        playerMarkers[index].setBounds(posX, posY,
                playerMarkers[index].getWidth(), playerMarkers[index].getHeight());
    }

    private void handleTileEvent(Player player) {
        Tile tile = game.getMap().get(player.getPosition());
        String message = player.getName() + " 停在了 " + tile.getType() + " 地块上。\n";
        boolean showDialog = true;
        boolean tileChanged = false; // 标记地块是否发生变化

        switch (tile.getType()) {
            case LAND:
                if (tile.getOwner() == null) {
                    // 空地，询问是否购买
                    if (player.isAI()) {
                        // AI自动决定是否购买
                        if (player.getMoney() >= tile.getPrice() && Math.random() < 0.6) {
                            tile.setOwner(player.getName());
                            player.setMoney(player.getMoney() - tile.getPrice());
                            message += "AI决定购买该地块，花费 $" + tile.getPrice() + "，剩余 $" + player.getMoney();
                            tileChanged = true;
                        } else {
                            message += "AI决定不购买该地块。";
                        }
                    } else {
                        // 玩家选择是否购买
                        int choice = JOptionPane.showConfirmDialog(this,
                                "这是一块空地，是否以 $" + tile.getPrice() + " 购买？",
                                "购买土地", JOptionPane.YES_NO_OPTION);

                        if (choice == JOptionPane.YES_OPTION) {
                            if (player.getMoney() >= tile.getPrice()) {
                                tile.setOwner(player.getName());
                                player.setMoney(player.getMoney() - tile.getPrice());
                                message += "你购买了该地块，花费 $" + tile.getPrice() + "，剩余 $" + player.getMoney();
                                tileChanged = true;
                            } else {
                                message += "你的资金不足，无法购买该地块！";
                            }
                        } else {
                            message += "你选择不购买该地块。";
                        }
                    }
                } else if (tile.getOwner().equals(player.getName())) {
                    // 自己的土地，询问是否升级
                    if (tile.canUpgrade()) {
                        int upgradeCost = tile.getUpgradeCost();
                        if (player.isAI()) {
                            // AI自动决定是否升级
                            if (player.getMoney() >= upgradeCost && Math.random() < 0.7) {
                                tile.upgrade();
                                player.setMoney(player.getMoney() - upgradeCost);
                                message += "AI决定升级该地块到等级 " + tile.getLevel() +
                                        "，花费 $" + upgradeCost + "，剩余 $" + player.getMoney();
                                tileChanged = true;
                            } else {
                                message += "AI决定不升级该地块。";
                            }
                        } else {
                            // 玩家选择是否升级
                            int choice = JOptionPane.showConfirmDialog(this,
                                    "这是你的土地，是否花费 $" + upgradeCost + " 升级到等级 " + (tile.getLevel() + 1) + "？",
                                    "升级土地", JOptionPane.YES_NO_OPTION);

                            if (choice == JOptionPane.YES_OPTION) {
                                if (player.getMoney() >= upgradeCost) {
                                    tile.upgrade();
                                    player.setMoney(player.getMoney() - upgradeCost);
                                    message += "你升级了该地块到等级 " + tile.getLevel() +
                                            "，花费 $" + upgradeCost + "，剩余 $" + player.getMoney();
                                    tileChanged = true;
                                } else {
                                    message += "你的资金不足，无法升级该地块！";
                                }
                            } else {
                                message += "你选择不升级该地块。";
                            }
                        }
                    } else {
                        message += "这是你的土地，已经满级了。";
                    }
                } else {
                    // 别人的土地，支付租金
                    int rent = tile.getRent();
                    player.setMoney(player.getMoney() - rent);

                    // 找到地主并给他钱
                    for (Player owner : players) {
                        if (owner.getName().equals(tile.getOwner())) {
                            owner.setMoney(owner.getMoney() + rent);
                            break;
                        }
                    }

                    message += "这是" + tile.getOwner() + "的土地，你支付了租金 $" + rent +
                            "，剩余 $" + player.getMoney();
                }
                break;

            case LUCKY:
                // 幸运事件
                int reward = 100 + (int) (Math.random() * 200);
                player.setMoney(player.getMoney() + reward);
                player.setExtraTurn(true);
                message += "幸运事件！你获得了奖金 $" + reward + "，现有资金 $" + player.getMoney();
                if (!player.isAI()) {
                    message += "\n你获得了一次额外掷骰机会！";
                }
                break;

            case UNLUCKY:
                // 不幸事件
                int penalty = 50 + (int) (Math.random() * 150);
                player.setMoney(player.getMoney() - penalty);
                if (Math.random() < 0.3) {
                    player.setSkipTurn(true); // 30%几率下回合跳过
                    message += "不幸事件！你损失了 $" + penalty + "，现有资金 $" + player.getMoney() +
                            "\n更倒霉的是，你下回合将被跳过！";
                } else {
                    message += "不幸事件！你损失了 $" + penalty + "，现有资金 $" + player.getMoney();
                }
                break;

            case PRISON:
                // 监狱事件
                player.setSkipTurn(true);
                message += "你被关进监狱，下一回合将跳过！";
                break;

            case START:
                // 起点事件
                message += "你回到了起点！";
                break;
        }

        if (showDialog) {
            JOptionPane.showMessageDialog(this, message);
        }

        // 如果地块发生变化，更新图标
        if (tileChanged) {
            updateTileIcon(player.getPosition());
        }

        // 检查玩家是否破产
        if (player.getMoney() < 0) {
            player.setBankrupt(true);
            JOptionPane.showMessageDialog(this, player.getName() + " 破产出局了！");
            // 释放玩家拥有的所有土地
            for (Tile t : game.getMap()) {
                if (player.getName().equals(t.getOwner())) {
                    t.setOwner(null);
                    t.setLevel(0);
                    updateTileIcon(game.getMap().indexOf(t));
                }
            }
        }

        // 切换到下一个玩家
        nextPlayerTurn();
    }

    // 新增方法：更新指定位置的地块图标
    private void updateTileIcon(int position) {
        Tile tile = game.getMap().get(position);
        String iconPath = getIconPathForTile(tile);
        if (iconPath == null) return;

        ImageIcon newIcon = loadIcon(iconPath);
        tileLabels[position].setIcon(newIcon);
    }

    private void updateMoneyDisplay() {
        for (int i = 0; i < players.size(); i++) {
            moneyLabels[i].setText("" + players.get(i).getMoney());
        }
    }

    private void nextPlayerTurn() {
        // 正常切换到下一个玩家
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).isBankrupt());

        Player nextPlayer = getCurrentPlayer();

        // 检查新玩家是否应该跳过回合（比如因为上回合进了监狱）
        // 这里我们不需要特别处理，因为监狱效果应该在进入监狱时就已经设置

        // 处理额外回合或跳过回合
        if (nextPlayer.hasExtraTurn()) {
            nextPlayer.setExtraTurn(false);
            JOptionPane.showMessageDialog(this, nextPlayer.getName() + " 获得额外回合！");
            // 立即开始当前玩家的回合，不切换玩家
            if (nextPlayer.isAI()) {
                startAITurn();
            }
            return;
        }
        else if (nextPlayer.isSkipTurn()) {
            nextPlayer.setSkipTurn(false);
            JOptionPane.showMessageDialog(this, nextPlayer.getName() + " 跳过本回合！");

            // 跳过当前玩家，直接移动到下一个玩家
            do {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            } while (players.get(currentPlayerIndex).isBankrupt());

            // 检查游戏是否结束
            if (game.isGameOver()) {
                Player winner = game.getWinner();
                JOptionPane.showMessageDialog(this, "游戏结束！胜利者是 " + winner.getName());
                return;
            }

            // 如果是AI玩家，自动开始回合
            if (getCurrentPlayer().isAI()) {
                startAITurn();
            }
            return;
        }

        // 检查游戏是否结束
        if (game.isGameOver()) {
            Player winner = game.getWinner();
            JOptionPane.showMessageDialog(this, "游戏结束！胜利者是 " + winner.getName());
            return;
        }

        // 如果是AI玩家，自动开始回合
        if (getCurrentPlayer().isAI()) {
            startAITurn();
        }
    }

    private void startAITurn() {
        Timer timer = new Timer(1000, e -> {
            rollDice();
            ((Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    private void loadMapTiles(JLayeredPane pane) {
        List<Tile> map = game.getMap();
        if (map.size() != 12) {
            JOptionPane.showMessageDialog(this, "地图地块数量应为12个！");
            return;
        }

        int[][] positions = {
                {15, 15}, {85, 15}, {155, 15}, {225, 15}, // 上边
                {225, 85}, {225, 155},                     // 右边
                {225, 225}, {155, 225}, {85, 225}, {15, 225}, // 下边
                {15, 155}, {15, 85}                         // 左边
        };

        for (int i = 0; i < map.size(); i++) {
            Tile tile = map.get(i);
            String iconPath = getIconPathForTile(tile);
            if (iconPath == null) continue;

            ImageIcon icon = loadIcon(iconPath);
            JLabel label = new JLabel(icon);
            label.setBounds(positions[i][0], positions[i][1],
                    icon.getIconWidth(), icon.getIconHeight());

            pane.add(label, JLayeredPane.DEFAULT_LAYER); // 地块在默认层
            tileLabels[i] = label; // 保存地块标签引用
        }
    }

    private void renderPlayers(JLayeredPane pane) {
        int[][] playerAvatarPositions = {
                {5, 285}, {75, 285}, {145, 285}, {215, 285}
        };
        int startX = 15;
        int startY = 15;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String name = player.getName();
            int money = player.getMoney();

            // 加载头像（原图大小）
            JLabel avatarLabel = new JLabel(loadIcon("/icons/player_" + name + ".png"));
            avatarLabel.setBounds(playerAvatarPositions[i][0], playerAvatarPositions[i][1],
                    avatarLabel.getIcon().getIconWidth(), avatarLabel.getIcon().getIconHeight());
            pane.add(avatarLabel, JLayeredPane.MODAL_LAYER);

            // 显示余额
            JLabel moneyLabel = new JLabel("" + money);
            moneyLabel.setFont(new Font("Arial", Font.BOLD, 14));
            moneyLabel.setForeground(Color.WHITE);
            moneyLabel.setBounds(playerAvatarPositions[i][0] + 40, playerAvatarPositions[i][1] + 73, 100, 20);
            pane.add(moneyLabel, JLayeredPane.MODAL_LAYER);
            moneyLabels[i] = moneyLabel;

            // 缩放玩家图标（缩小50%）
            ImageIcon originalIcon = loadIcon("/icons/player_" + name + ".png");
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                    originalIcon.getIconWidth() / 2, originalIcon.getIconHeight() / 2, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            // 放置到地图起始位置
            JLabel playerOnMap = new JLabel(scaledIcon);
            int offsetX = playerStartOffsets[i][0];
            int offsetY = playerStartOffsets[i][1];
            playerOnMap.setBounds(startX + offsetX, startY + offsetY,
                    scaledIcon.getIconWidth(), scaledIcon.getIconHeight());
            pane.add(playerOnMap, JLayeredPane.PALETTE_LAYER); // 玩家浮层

            // 保存玩家标记引用
            playerMarkers[i] = playerOnMap;
        }
    }

    // 修改后的方法：根据地块类型、所有者和等级返回对应的图标路径
    private String getIconPathForTile(Tile tile) {
        if (tile.getType() != TileType.LAND) {
            return switch (tile.getType()) {
                case START -> "/icons/start_icon.png";
                case UNLUCKY -> "/icons/unlucky_icon.png";
                case LUCKY -> "/icons/lucky_icon.png";
                case PRISON -> "/icons/prison_icon.png";
                default -> null;
            };
        }

        // 如果是LAND类型，根据所有者和等级返回对应的图标
        if (tile.getOwner() == null) {
            return "/icons/land_icon.png"; // 默认未购买的地块图标
        }

        // 根据所有者名称和等级返回对应的图标
        String ownerName = tile.getOwner().toLowerCase();
        int level = tile.getLevel();
        return "/icons/" + ownerName + "_land" + level + "_icon.png";
    }

    private Image loadImage(String path) {
        URL url = getClass().getResource(path);
        if (url == null) throw new RuntimeException("未找到图片：" + path);
        return new ImageIcon(url).getImage();
    }

    private ImageIcon loadIcon(String path) {
        URL url = getClass().getResource(path);
        if (url == null) throw new RuntimeException("未找到图标：" + path);
        return new ImageIcon(url);
    }

    public static void main(String[] args) {
        // 示例玩家列表测试
        List<Player> players = List.of(
                new Player("issac", false),
                new Player("lost", false),
                new Player("az", true),
                new Player("Bethany", true)
        );
        SwingUtilities.invokeLater(() -> new GameMapUI(players));
    }
}