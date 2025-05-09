package data;

import logic.Game;
import logic.Player;
import logic.Tile;

import javax.swing.*;
import java.io.*;
import java.util.List;

public class GameSave {
    private static final String SAVE_FILE = "monopoly_save.dat";

    public static void saveGame(Game game) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            // 保存游戏状态
            oos.writeObject(game.getMap());
            oos.writeObject(game.getPlayers());
            oos.writeInt(game.getCurrentPlayerIndex());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "保存游戏失败: " + e.getMessage());
        }
    }

    public static Game loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            @SuppressWarnings("unchecked")
            List<Tile> map = (List<Tile>) ois.readObject();
            @SuppressWarnings("unchecked")
            List<Player> players = (List<Player>) ois.readObject();
            int currentPlayerIndex = ois.readInt();

            Game game = new Game(map, players);
            game.setCurrentPlayerIndex(currentPlayerIndex);
            return game;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "加载存档失败: " + e.getMessage());
            return null;
        }
    }

    public static boolean saveExists() {
        return new File(SAVE_FILE).exists();
    }
}