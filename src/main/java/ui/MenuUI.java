package ui;

import javax.swing.*;
import java.awt.*;

public class MenuUI extends JFrame {

    public MenuUI() {
        setTitle("大富翁：忏悔++");
        setSize(300, 400); // 可根据你的背景图调整
        setLocationRelativeTo(null); // 居中
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 设置背景面板
        setContentPane(new BackgroundPanel(new ImageIcon("icons/menu.png").getImage()));

        // 使用空布局，方便自定义位置
        setLayout(null);

        // 创建“开始游戏”按钮
        JButton newGameButton = new JButton(new ImageIcon("icons/new_game_button.png"));
        newGameButton.setBounds(75, 150, 134, 36); // 位置和大小根据图片调整
        newGameButton.setBorderPainted(false);
        newGameButton.setContentAreaFilled(false);
        newGameButton.setFocusPainted(false);

        // 创建“继续游戏”按钮
        JButton continueGameButton = new JButton(new ImageIcon("icons/continue_game_button.png"));
        continueGameButton.setBounds(75, 200, 134, 36);
        continueGameButton.setBorderPainted(false);
        continueGameButton.setContentAreaFilled(false);
        continueGameButton.setFocusPainted(false);

        // 添加事件监听（你可以在这里连接你的控制器逻辑）
        newGameButton.addActionListener(e -> {
            System.out.println("点击了新游戏");
            // TODO: 进入选人界面
            this.dispose();
            SwingUtilities.invokeLater(PlayerSelectUI::new);
        });

        continueGameButton.addActionListener(e -> {
            System.out.println("点击了继续游戏");
            // TODO: 加载保存的游戏进度
        });

        // 添加按钮
        add(newGameButton);
        add(continueGameButton);

        setVisible(true);
    }

    // 背景面板类
    class BackgroundPanel extends JPanel {
        private Image background;

        public BackgroundPanel(Image bg) {
            this.background = bg;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // 测试主函数
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MenuUI::new);
    }
}
