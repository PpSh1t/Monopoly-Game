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

    /**
     * 获取当前地块的价格（用于购买或作为基础价格）
     * @return 地块当前价格
     */
    public int getPrice() {
        return 200 + level * 100;
    }

    /**
     * 获取当前地块的租金金额
     * @return 地块当前租金
     */
    public int getRent() {
        return 50 + level * 50;
    }

    /**
     * 计算升级当前地块所需的费用（设为价格的 50%）
     * @return 升级费用
     */
    public int getUpgradeCost() {
        return getPrice() / 2;
    }

    /**
     * 判断地块是否还能升级（最大等级为3）
     * @return true 表示还能升级，false 表示已满级
     */
    public boolean canUpgrade() {
        return level < 3;
    }

    /**
     * 将地块等级提升一级（如果未满级）
     */
    public void upgrade() {
        if (canUpgrade()) {
            level++;
        }
    }
}
