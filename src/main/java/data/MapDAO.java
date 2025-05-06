package data;

import logic.Tile;
import logic.Tile.TileType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MapDAO {

    /**
     * 从数据库加载地图地块数据，按 position 顺序构建地图列表
     * @return List<Tile> 地图地块列表
     */
    public static List<Tile> loadMap() {
        List<Tile> map = new ArrayList<>();
        String sql = "SELECT * FROM map ORDER BY position ASC";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // 地块类型
                TileType type = TileType.valueOf(rs.getString("type").toUpperCase());

                // 创建 Tile 对象
                Tile tile = new Tile(type);

                // 设置等级（默认 1）
                tile.setLevel(rs.getInt("level"));

                // 设置价格与租金（适用于 LAND 类型）
                tile.setPrice(rs.getInt("price"));
                tile.setRent(rs.getInt("rent"));

                // 设置所有者（可以为 null）
                String owner = rs.getString("owner");
                if (owner != null && !owner.trim().isEmpty()) {
                    tile.setOwner(owner);
                }

                map.add(tile);
            }

        } catch (SQLException e) {
            System.err.println("❌ 地图加载失败：" + e.getMessage());
        }

        return map;
    }
}
