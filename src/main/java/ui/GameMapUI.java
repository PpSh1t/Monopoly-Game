package ui;

import data.MapDAO;
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

    public GameMapUI(List<Player> selectedPlayers) {
        this.players = selectedPlayers;

        setTitle("游戏地图");
        backgroundImage = loadImage("/icons/game.png");
        int width = backgroundImage.getWidth(null);
        int height = backgroundImage.getHeight(null);
        setSize(width + 10, height + 37);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
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

        setVisible(true);
    }

    private void loadMapTiles(JLayeredPane pane) {
        List<Tile> map = MapDAO.loadMap();
        if (map.size() != 12) {
            JOptionPane.showMessageDialog(this, "地图地块数量应为12个！");
            return;
        }

        int[][] positions = {
                {15, 15}, {85, 15}, {155, 15}, {225, 15},// 上边
                {225, 85}, {225, 155},      // 右
                {225, 225}, {155, 225}, {85, 225}, {15, 225},   // 下边
                {15, 155}, {15, 85}         // 左
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
