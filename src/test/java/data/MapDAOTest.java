package data;

import logic.Tile;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapDAOTest {

    @Test
    public void testLoadMap() {
        List<Tile> map = MapDAO.loadMap();
        assertNotNull(map, "地图加载失败！");
        assertTrue(map.size() > 0, "地图没有加载任何地块！");

        // 输出地图中每个地块的信息
        map.forEach(tile -> System.out.println(tile));
    }
}
