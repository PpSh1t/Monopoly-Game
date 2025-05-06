package ui;

import data.MapDAO;
import logic.Tile;
import logic.Tile.TileType;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class GameMapUI extends JFrame {
    private Image backgroundImage;

    public GameMapUI() {
        setTitle("游戏地图");
        backgroundImage = loadImage("/icons/game.png");
        int width = backgroundImage.getWidth(null);
        int height = backgroundImage.getHeight(null);
        setSize(width+10, height+37);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        MapPanel panel = new MapPanel();
        setContentPane(panel);
        setLayout(null);

        loadMapTiles(panel);

        setVisible(true);
    }

    private void loadMapTiles(JPanel panel) {
        List<Tile> map = MapDAO.loadMap();
        if (map.size() != 12) {
            JOptionPane.showMessageDialog(this, "地图地块数量应为12个！");
            return;
        }

        // 定义12个地块的像素坐标（手动根据game.png中的位置调整）
        int[][] positions = {
                {15, 15}, {85, 15}, {155, 15}, {225, 15},// 上边
                {225, 85}, {225, 155},      // 右
                {225, 225}, {155,225}, {85, 225}, {15, 225},   // 下边
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

            panel.add(label);
        }
    }

    private String getIconPathForTile(TileType type) {
        return switch (type) {
            case START -> "/icons/start_icon.png";
            case UNLUCKY -> "/icons/unlucky_icon.png";
            case LUCKY -> "/icons/lucky_icon.png";
            case PRISON -> "/icons/prison_icon.png";
            case LAND -> "/icons/land_icon.png"; // 可选：普通地块图标
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

    class MapPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, this);
        }

        @Override
        public boolean isOptimizedDrawingEnabled() {
            return false; // 允许图层覆盖
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameMapUI::new);
    }
}
