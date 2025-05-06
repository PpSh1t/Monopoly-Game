package ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerSelectUI extends JFrame {
    private Image backgroundImage;
    private JButton playerTypeButton;

    private ImageIcon humanIcon;
    private ImageIcon aiIcon;
    private boolean isHuman = true;

    private List<String> selectedPlayers = new ArrayList<>();
    private Map<String, JButton> playerButtons = new HashMap<>();

    private int selectionCount = 0;
    private final int MAX_SELECTIONS = 4;

    public PlayerSelectUI() {
        // 加载背景图并设置窗口大小
        backgroundImage = loadImage("/icons/who_am_i.png");
        int bgWidth = backgroundImage.getWidth(null);
        int bgHeight = backgroundImage.getHeight(null);

        setTitle("选择玩家");
        setSize(bgWidth, bgHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(new BackgroundPanel());
        setLayout(null);

        // 加载按钮图标
        humanIcon = loadIcon("/icons/human_player_button.png");
        aiIcon = loadIcon("/icons/ai_player_button.png");

        // 玩家类型切换按钮（图标原始大小）
        playerTypeButton = new JButton(humanIcon);
        int typeBtnW = humanIcon.getIconWidth();
        int typeBtnH = humanIcon.getIconHeight();
        playerTypeButton.setBounds((bgWidth - typeBtnW) / 2-20, 15, typeBtnW, typeBtnH);
        playerTypeButton.setBorderPainted(false);
        playerTypeButton.setContentAreaFilled(false);
        playerTypeButton.setFocusPainted(false);
        playerTypeButton.addActionListener(e -> togglePlayerType());
        add(playerTypeButton);

        // 添加四个角色按钮（图标原始大小 + 手动布局）
        addPlayerButton("player_issac", "/icons/player_issac.png", 50, 150);
        addPlayerButton("player_lost", "/icons/player_lost.png", 150, 150);
        addPlayerButton("player_az", "/icons/player_az.png", 50, 250);
        addPlayerButton("player_Bethany", "/icons/player_Bethany.png", 150, 250);

        setVisible(true);
    }

    private void addPlayerButton(String key, String iconPath, int x, int y) {
        ImageIcon icon = loadIcon(iconPath);
        JButton btn = new JButton(icon);
        btn.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);

        btn.addActionListener(e -> {
            String playerType = isHuman ? "人类" : "AI";
            selectedPlayers.add(key + " (" + playerType + ")");
            btn.setVisible(false);
            selectionCount++;
            System.out.println("选择了：" + key + "，类型：" + playerType);

            isHuman = true;
            playerTypeButton.setIcon(humanIcon);

            if (selectionCount >= MAX_SELECTIONS) {
                onSelectionComplete();
            }
        });

        add(btn);
        playerButtons.put(key, btn);
    }

    private void togglePlayerType() {
        isHuman = !isHuman;
        playerTypeButton.setIcon(isHuman ? humanIcon : aiIcon);
    }

    private void onSelectionComplete() {
        System.out.println("全部角色已选择完毕：");
        for (String p : selectedPlayers) {
            System.out.println(" - " + p);
        }

        JOptionPane.showMessageDialog(this, "玩家选择完成，准备进入游戏！");
        // TODO: 跳转游戏主界面
    }

    // 加载图标资源
    private ImageIcon loadIcon(String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            throw new RuntimeException("资源未找到: " + path);
        }
        return new ImageIcon(url);
    }

    // 加载背景图
    private Image loadImage(String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            throw new RuntimeException("资源未找到: " + path);
        }
        return new ImageIcon(url).getImage();
    }

    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, this); // 原图大小绘制
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlayerSelectUI::new);
    }
}
