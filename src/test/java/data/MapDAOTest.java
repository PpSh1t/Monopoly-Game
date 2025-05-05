package data;

import logic.Tile;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapDAOTest {

    @Test
    public void testLoadMap_NotEmpty() {
        List<Tile> map = MapDAO.loadMap();
        assertNotNull(map, "地图加载结果不应为 null");
        assertFalse(map.isEmpty(), "地图不应为空");
        System.out.println("地图加载成功，共有 " + map.size() + " 个地块。");
    }

    @Test
    public void testTileTypesValid() {
        List<Tile> map = MapDAO.loadMap();
        for (Tile tile : map) {
            assertNotNull(tile.getType(), "地图地块类型不能为空");
        }
    }

    // 你可以添加更多测试，例如：
    // testLoadMap_ContainsStartTile
}
