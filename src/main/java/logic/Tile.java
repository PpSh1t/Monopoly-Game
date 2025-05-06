package logic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile {
    public enum TileType { START, LAND, LUCKY, UNLUCKY, PRISON}

    private TileType type;       // 地块类型
    /**
     * -- GETTER --
     *  获取地块的所有
     * -- SETTER --
     *  设置地块的所有者
     *
     */
    @Setter
    @Getter
    private String owner;        // 地块的拥有者（如果为空则为 null，表示空地）
    private int level;           // 当前地块的等级（0-3）
    /**
     * -- SETTER --
     *  设置地块的价格
     *
     */
    @Setter
    private int price;           // 地块价格（用于手动设置）

    public Tile(TileType type) {
        this.type = type;
        this.level = 0;  // 初始等级
        this.owner = null;  // 初始为空
        this.price = 200;   // 默认初始价格
    }

    /**
     * 获取当前地块的价格（用于购买或作为基础价格）
     * @return 地块当前价格
     */
    public int getPrice() {
        if (this.price == 0) {
            return 200 + level * 100;  // 如果 price 为 0，按照默认规则计算价格
        }
        return price;  // 否则返回手动设置的价格
    }

    /**
     * 获取当前地块的租金金额
     * @return 地块当前租金
     */
    public int getRent() {
        return 50 + level * 50;  // 基础租金 + 每级增加 50
    }

    /**
     * 设置地块的租金（如果你想要手动控制）
     * @param rent 租金
     */
    public void setRent(int rent) {
        // 这个方法可以控制租金，但通常我们是依赖 level 来计算租金
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

    /**
     * 检查地块是否为空（没有所有者）
     * @return true 如果地块没有被拥有
     */
    public boolean isEmpty() {
        return owner == null;
    }

    /**
     * 自定义的 toString 方法，显示地块的详细信息
     * @return 以字符串形式返回地块的信息
     */
    @Override
    public String toString() {
        return String.format("地块类型: %s, 所有者: %s, 等级: %d, 价格: $%d, 租金: $%d",
                type, owner == null ? "无" : owner, level, getPrice(), getRent());
    }
}
