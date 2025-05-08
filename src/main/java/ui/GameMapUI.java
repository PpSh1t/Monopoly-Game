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

    public GameMapUI(List<Player> selectedPlayers) {
        this.players = selectedPlayers;
        this.game = new Game(MapDAO.loadMap(), players);
        this.playerMarkers = new JLabel[players.size()];

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
        int steps = Dice.roll();
        JOptionPane.showMessageDialog(this, getCurrentPlayer().getName() + " 掷出了 " + steps + "点！");
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

        // 切换到下一个玩家
        nextPlayerTurn();
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

        switch (tile.getType()) {
            case LAND:
                if (tile.getOwner() == null) {
                    message += "这是一块空地。";
                } else if (tile.getOwner().equals(player.getName())) {
                    message += "这是你自己的土地。";
                } else {
                    message += "这是" + tile.getOwner() + "的土地，你需要支付租金 $" + tile.getRent();
                }
                break;
            case LUCKY:
                message += "幸运事件！获得奖金 $" + (100 + (int)(Math.random() * 200));
                break;
            case UNLUCKY:
                message += "不幸事件！损失 $" + (50 + (int)(Math.random() * 150));
                break;
            case PRISON:
                message += "你被关进监狱，下一回合将跳过！";
                break;
            case START:
                message += "你回到了起点！";
                break;
        }

        JOptionPane.showMessageDialog(this, message);
    }

    private void nextPlayerTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

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
                {15, 15}, {85, 15}, {155, 15}, {225, 15}, // 上边
                {225, 85}, {225, 155},                     // 右边
                {225, 225}, {155, 225}, {85, 225}, {15, 225}, // 下边
                {15, 155}, {15, 85}                         // 左边
        };

        for (int i = 0; i < map.size(); i++) {
            Tile tile = map.get(i);
            String iconPath = getIconPathForTile(tile.getType());
            if (iconPath == null) continue;

            ImageIcon icon = loadIcon(iconPath);
            JLabel label = new JLabel(icon);
            label.setBounds(positions[i][0], positions[i][1],
                    icon.getIconWidth(), icon.getIconHeight());

            pane.add(label, JLayeredPane.DEFAULT_LAYER); // 地块在默认层
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
            moneyLabel.setBounds(playerAvatarPositions[i][0]+40, playerAvatarPositions[i][1] + 73, 100, 20);
            pane.add(moneyLabel, JLayeredPane.MODAL_LAYER);

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

    private String getIconPathForTile(TileType type) {
        return switch (type) {
            case START -> "/icons/start_icon.png";
            case UNLUCKY -> "/icons/unlucky_icon.png";
            case LUCKY -> "/icons/lucky_icon.png";
            case PRISON -> "/icons/prison_icon.png";
            case LAND -> "/icons/land_icon.png";
            default -> null;
        };
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