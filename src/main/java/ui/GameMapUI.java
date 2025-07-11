package ui;

import data.GameSave;
import data.MapDAO;
import logic.Dice;
import logic.Game;
import logic.Player;
import logic.Tile;
import logic.Tile.TileType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

/**
 * 主地图界面，渲染棋盘、头像、骰子
 */
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
    private JLabel[] tileLabels;
    private JLabel currentPlayerIcon;
    private Timer animationTimer;
    private int currentStep = 0;
    private int totalSteps = 0;
    private int fromPosition = 0;
    private int toPosition = 0;
    private JLabel movingPlayerMarker;

    public GameMapUI(List<Player> selectedPlayers) {
        this(new Game(MapDAO.loadMap(), selectedPlayers));
    }

    public GameMapUI(Game game) {
        this.game = game;
        this.players = game.getPlayers();
        this.currentPlayerIndex = game.getCurrentPlayerIndex();
        this.playerMarkers = new JLabel[players.size()];
        this.moneyLabels = new JLabel[players.size()];
        this.tileLabels = new JLabel[12];

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

        // 添加当前回合玩家图标到地图中央（原始大小）
        addCurrentPlayerIcon(layeredPane);

        // 添加骰子和保存按钮
        addDiceButton();
        addSaveButton();

        setVisible(true);

        // 初始化动画定时器
        animationTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep < totalSteps) {
                    currentStep++;
                    updatePlayerPositionDuringAnimation();
                } else {
                    animationTimer.stop();
                    completePlayerMove();
                }
            }
        });

        // 初始显示当前玩家图标
        updateCurrentPlayerIcon();

        // 如果是AI玩家，自动开始回合
        if (getCurrentPlayer().isAI()) {
            startAITurn();
        }
    }

    private void addSaveButton() {
        ImageIcon saveIcon = loadIcon("/icons/save_button.png");
        JButton saveButton = new JButton(saveIcon);
        saveButton.setBounds(225, 380, saveIcon.getIconWidth(), saveIcon.getIconHeight());
        saveButton.setBorderPainted(false);
        saveButton.setContentAreaFilled(false);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> {
            GameSave.saveGame(game);
            JOptionPane.showMessageDialog(this, "游戏已保存！");
        });
        layeredPane.add(saveButton, JLayeredPane.MODAL_LAYER);
    }

    private void addCurrentPlayerIcon(JLayeredPane pane) {
        currentPlayerIcon = new JLabel();
        currentPlayerIcon.setBounds(70, 75, 100, 100);
        currentPlayerIcon.setVisible(false);
        pane.add(currentPlayerIcon, JLayeredPane.PALETTE_LAYER);
    }

    private void updateCurrentPlayerIcon() {
        if (currentPlayerIcon == null) return;

        Player currentPlayer = getCurrentPlayer();
        String iconPath = "/icons/player_" + currentPlayer.getName() + ".png";

        try {
            ImageIcon icon = loadIcon(iconPath);
            currentPlayerIcon.setIcon(icon);
            currentPlayerIcon.setVisible(true);
        } catch (Exception e) {
            currentPlayerIcon.setIcon(null);
            currentPlayerIcon.setVisible(false);
        }
    }

    private void addDiceButton() {
        ImageIcon diceIcon = loadIcon("/icons/dice.png");
        JButton diceButton = new JButton(diceIcon);
        diceButton.setBounds(145, 145, diceIcon.getIconWidth(), diceIcon.getIconHeight());
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
        int steps = Dice.roll();
        JOptionPane.showMessageDialog(this, getCurrentPlayer().getName() + " 掷出了 " + steps + "点！");
        startPlayerMovement(steps);
    }

    private void startPlayerMovement(int steps) {
        Player player = getCurrentPlayer();
        fromPosition = player.getPosition();
        toPosition = (fromPosition + steps) % 12;
        totalSteps = steps;
        currentStep = 0;

        movingPlayerMarker = playerMarkers[players.indexOf(player)];
        animationTimer.start();
    }

    private void updatePlayerPositionDuringAnimation() {
        if (movingPlayerMarker == null) return;

        int currentPosition = (fromPosition + currentStep) % 12;
        updateSinglePlayerPosition(movingPlayerMarker, currentPosition);
    }

    private void completePlayerMove() {
        Player player = getCurrentPlayer();
        player.setPosition(toPosition);
        updateSinglePlayerPosition(movingPlayerMarker, toPosition);
        handleTileEvent(player);
        updateMoneyDisplay();
        movingPlayerMarker = null;
    }

    private void updateSinglePlayerPosition(JLabel playerMarker, int position) {
        Player player = getCurrentPlayer();
        int index = players.indexOf(player);
        if (index < 0 || index >= playerMarkers.length) return;

        int[][] positions = {
                {15, 15}, {85, 15}, {155, 15}, {225, 15},
                {225, 85}, {225, 155},
                {225, 225}, {155, 225}, {85, 225}, {15, 225},
                {15, 155}, {15, 85}
        };

        int posX = positions[position][0] + playerStartOffsets[index][0];
        int posY = positions[position][1] + playerStartOffsets[index][1];

        playerMarker.setBounds(posX, posY,
                playerMarker.getWidth(), playerMarker.getHeight());
    }

    private void handleTileEvent(Player player) {
        Tile tile = game.getMap().get(player.getPosition());
        String message = player.getName() + " 停在了 " + tile.getType() + " 地块上。\n";
        boolean showDialog = true;
        boolean tileChanged = false;

        switch (tile.getType()) {
            case LAND:
                if (tile.getOwner() == null) {
                    if (player.isAI()) {
                        if (player.getMoney() >= tile.getPrice() && Math.random() < 0.6) {
                            tile.setOwner(player.getName());
                            player.setMoney(player.getMoney() - tile.getPrice());
                            message += "AI决定购买该地块，花费 $" + tile.getPrice() + "，剩余 $" + player.getMoney();
                            tileChanged = true;
                        } else {
                            message += "AI决定不购买该地块。";
                        }
                    } else {
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
                    if (tile.canUpgrade()) {
                        int upgradeCost = tile.getUpgradeCost();
                        if (player.isAI()) {
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
                            int choice = JOptionPane.showConfirmDialog(this,
                                    "这是你的土地，是否花费 $" + upgradeCost + " 升级到等级 " + (tile.getLevel()+1) + "？",
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
                    int rent = tile.getRent();
                    player.setMoney(player.getMoney() - rent);

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
                int reward = 100 + (int)(Math.random() * 200);
                player.setMoney(player.getMoney() + reward);
                if (!player.isAI()) {
                    player.setExtraTurn(true);
                }
                message += "幸运事件！你获得了奖金 $" + reward + "，现有资金 $" + player.getMoney();
                if (!player.isAI()) {
                    message += "\n你获得了一次额外掷骰机会！";
                }
                break;

            case UNLUCKY:
                int penalty = 50 + (int)(Math.random() * 150);
                player.setMoney(player.getMoney() - penalty);
                if (Math.random() < 0.3) {
                    player.setSkipTurn(true);
                    message += "不幸事件！你损失了 $" + penalty + "，现有资金 $" + player.getMoney() +
                            "\n更倒霉的是，你下回合将被跳过！";
                } else {
                    message += "不幸事件！你损失了 $" + penalty + "，现有资金 $" + player.getMoney();
                }
                break;

            case PRISON:
                player.setSkipTurn(true);
                message += "你被关进监狱，下一回合将跳过！";
                break;

            case START:
                message += "你回到了起点！";
                break;
        }

        if (showDialog) {
            JOptionPane.showMessageDialog(this, message);
        }

        if (tileChanged) {
            updateTileIcon(player.getPosition());
        }

        if (player.getMoney() < 0) {
            player.setBankrupt(true);
            JOptionPane.showMessageDialog(this, player.getName() + " 破产出局了！");
            for (Tile t : game.getMap()) {
                if (player.getName().equals(t.getOwner())) {
                    t.setOwner(null);
                    t.setLevel(0);
                    updateTileIcon(game.getMap().indexOf(t));
                }
            }
        }

        nextPlayerTurn();
    }

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
        if (getCurrentPlayer().hasExtraTurn()) {
            getCurrentPlayer().setExtraTurn(false);
            JOptionPane.showMessageDialog(this, getCurrentPlayer().getName() + " 开始额外回合！");
            updateCurrentPlayerIcon();

            if (getCurrentPlayer().isAI()) {
                startAITurn();
            }
            return;
        }

        if (currentPlayerIcon != null) {
            currentPlayerIcon.setVisible(false);
        }

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).isBankrupt());

        if (game.isGameOver()) {
            Player winner = game.getWinner();
            JOptionPane.showMessageDialog(this, "游戏结束！胜利者是 " + winner.getName());
            return;
        }

        Player nextPlayer = getCurrentPlayer();
        if (nextPlayer.isSkipTurn()) {
            nextPlayer.setSkipTurn(false);
            JOptionPane.showMessageDialog(this, nextPlayer.getName() + " 跳过本回合！");
            nextPlayerTurn();
            return;
        }

        updateCurrentPlayerIcon();

        if (getCurrentPlayer().isAI()) {
            startAITurn();
        }
    }

    private void startAITurn() {
        Timer timer = new Timer(1000, e -> {
            rollDice();
            ((Timer)e.getSource()).stop();
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
                {15, 15}, {85, 15}, {155, 15}, {225, 15},
                {225, 85}, {225, 155},
                {225, 225}, {155, 225}, {85, 225}, {15, 225},
                {15, 155}, {15, 85}
        };

        for (int i = 0; i < map.size(); i++) {
            Tile tile = map.get(i);
            String iconPath = getIconPathForTile(tile);
            if (iconPath == null) continue;

            ImageIcon icon = loadIcon(iconPath);
            JLabel label = new JLabel(icon);
            label.setBounds(positions[i][0], positions[i][1],
                    icon.getIconWidth(), icon.getIconHeight());

            pane.add(label, JLayeredPane.DEFAULT_LAYER);
            tileLabels[i] = label;
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

            JLabel avatarLabel = new JLabel(loadIcon("/icons/player_" + name + ".png"));
            avatarLabel.setBounds(playerAvatarPositions[i][0], playerAvatarPositions[i][1],
                    avatarLabel.getIcon().getIconWidth(), avatarLabel.getIcon().getIconHeight());
            pane.add(avatarLabel, JLayeredPane.MODAL_LAYER);

            JLabel moneyLabel = new JLabel("" + money);
            moneyLabel.setFont(new Font("Arial", Font.BOLD, 14));
            moneyLabel.setForeground(Color.WHITE);
            moneyLabel.setBounds(playerAvatarPositions[i][0]+40, playerAvatarPositions[i][1] + 73, 100, 20);
            pane.add(moneyLabel, JLayeredPane.MODAL_LAYER);
            moneyLabels[i] = moneyLabel;

            ImageIcon originalIcon = loadIcon("/icons/player_" + name + ".png");
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                    originalIcon.getIconWidth() / 2, originalIcon.getIconHeight() / 2, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            JLabel playerOnMap = new JLabel(scaledIcon);
            int offsetX = playerStartOffsets[i][0];
            int offsetY = playerStartOffsets[i][1];
            playerOnMap.setBounds(startX + offsetX, startY + offsetY,
                    scaledIcon.getIconWidth(), scaledIcon.getIconHeight());
            pane.add(playerOnMap, JLayeredPane.PALETTE_LAYER);

            playerMarkers[i] = playerOnMap;
        }
    }

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

        if (tile.getOwner() == null) {
            return "/icons/land_icon.png";
        }

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
        List<Player> players = List.of(
                new Player("issac", false),
                new Player("lost", false),
                new Player("az", true),
                new Player("Bethany", true)
        );
        SwingUtilities.invokeLater(() -> new GameMapUI(players));
    }
}