package logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TileTest {

    @Test
    public void testTileInitialization() {
        Tile tile = new Tile(Tile.TileType.LAND);

        Assertions.assertEquals(Tile.TileType.LAND, tile.getType());
        Assertions.assertNull(tile.getOwner());
        Assertions.assertEquals(0, tile.getLevel());
        Assertions.assertEquals(200, tile.getPrice());
        Assertions.assertEquals(50, tile.getRent());
    }

    @Test
    public void testUpgradeLogic() {
        Tile tile = new Tile(Tile.TileType.LAND);

        Assertions.assertTrue(tile.canUpgrade());
        tile.upgrade();
        Assertions.assertEquals(1, tile.getLevel());

        tile.setLevel(3);
        Assertions.assertFalse(tile.canUpgrade(), "等级达到3不能再升级");
    }

    @Test
    public void testRentCalculation() {
        Tile tile = new Tile(Tile.TileType.LAND);
        tile.setLevel(2);
        Assertions.assertEquals(150, tile.getRent());
    }

    @Test
    public void testUpgradeCost() {
        Tile tile = new Tile(Tile.TileType.LAND);
        tile.setLevel(1);
        Assertions.assertEquals(tile.getPrice() / 2, tile.getUpgradeCost());
    }

    @Test
    public void testOwnership() {
        Tile tile = new Tile(Tile.TileType.LAND);
        Assertions.assertTrue(tile.isEmpty());

        tile.setOwner("Alice");
        Assertions.assertFalse(tile.isEmpty());
    }
}
