package ui;

import logic.AIPlayer;
import logic.Player;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择玩家界面
 */
public class PlayerSelectUI extends JFrame {
    private Image backgroundImage;
    private JButton playerTypeButton;

    private ImageIcon humanIcon;
    private ImageIcon aiIcon;
    private boolean isHuman = true;

    private final List<Player> selectedPlayers = new ArrayList<>();
    private final Map<String, JButton> playerButtons = new HashMap<>();

    private int selectionCount = 0;
    private final int MAX_SELECTIONS = 4;

    public PlayerSelectUI() {
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

        humanIcon = loadIcon("/icons/human_player_button.png");
        aiIcon = loadIcon("/icons/ai_player_button.png");

        // 顶部切换按钮
        playerTypeButton = new JButton(humanIcon);
        int typeBtnW = humanIcon.getIconWidth();
        int typeBtnH = humanIcon.getIconHeight();
        playerTypeButton.setBounds((bgWidth - typeBtnW) / 2 - 20, 15, typeBtnW, typeBtnH);
        playerTypeButton.setBorderPainted(false);
        playerTypeButton.setContentAreaFilled(false);
        playerTypeButton.setFocusPainted(false);
        playerTypeButton.addActionListener(e -> togglePlayerType());
        add(playerTypeButton);

        // 角色按钮
        addPlayerButton("issac", "/icons/player_issac.png", 50, 150);
        addPlayerButton("lost", "/icons/player_lost.png", 150, 150);
        addPlayerButton("az", "/icons/player_az.png", 50, 250);
        addPlayerButton("Bethany", "/icons/player_Bethany.png", 150, 250);

        setVisible(true);
    }

    private void addPlayerButton(String name, String iconPath, int x, int y) {
        ImageIcon icon = loadIcon(iconPath);
        JButton btn = new JButton(icon);
        btn.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);

        btn.addActionListener(e -> {
            Player player = isHuman ? new Player(name, false) : new AIPlayer(name);
            selectedPlayers.add(player);
            btn.setVisible(false);
            selectionCount++;
            System.out.println("选择了：" + name + "，类型：" + (isHuman ? "人类" : "AI"));

            isHuman = true;
            playerTypeButton.setIcon(humanIcon);

            if (selectionCount >= MAX_SELECTIONS) {
                onSelectionComplete();
            }
        });

        add(btn);
        playerButtons.put(name, btn);
    }

    private void togglePlayerType() {
        isHuman = !isHuman;
        playerTypeButton.setIcon(isHuman ? humanIcon : aiIcon);
    }

    private void onSelectionComplete() {
        System.out.println("全部角色已选择完毕：");
        for (Player p : selectedPlayers) {
            System.out.println(p.getName() + " - " + (p.isAI() ? "AI" : "人类"));
        }

        JOptionPane.showMessageDialog(this, "玩家选择完成，准备进入游戏！");
        // TODO: 跳转游戏主界面
        this.dispose(); // 关闭当前界面
        SwingUtilities.invokeLater(() -> new GameMapUI(selectedPlayers)); // 打开地图界面
    }

    private ImageIcon loadIcon(String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            throw new RuntimeException("资源未找到: " + path);
        }
        return new ImageIcon(url);
    }

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
            g.drawImage(backgroundImage, 0, 0, this); // 原图尺寸绘制
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlayerSelectUI::new);
    }
}
