package data;

import logic.Tile;
import logic.Tile.TileType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MapDAO {
    public static List<Tile> loadMap() {
        List<Tile> map = new ArrayList<>();
        String sql = "SELECT * FROM map ORDER BY position ASC";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TileType type = TileType.valueOf(rs.getString("type").toUpperCase());
                Tile tile = new Tile(type);
                tile.setLevel(rs.getInt("level"));
                // owner 暂不处理，后面配合玩家表做
                map.add(tile);
            }
        } catch (SQLException e) {
            System.err.println("Failed to load map: " + e.getMessage());
        }

        return map;
    }
}
