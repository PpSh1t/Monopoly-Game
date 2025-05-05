package logic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Tile {
    public enum TileType { START, LAND, LUCKY, UNLUCKY, PRISON, HOSPITAL, TAX }

    private TileType type;
    private String owner;
    private int level;

    public Tile(TileType type) {
        this.type = type;
        this.level = 0;
        this.owner = null;
    }

    public int getPrice() {
        return 200 + level * 100;
    }

    public int getRent() {
        return 50 + level * 50;
    }
}
