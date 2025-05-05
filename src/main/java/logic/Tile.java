package logic;

public class Tile {
    public enum TileType {
        START, LAND, LUCKY, UNLUCKY, PRISON, HOSPITAL, TAX
    }

    private TileType type;  // 地块类型
    private int level;      // 房产等级
    private String owner;   // 房产拥有者（可为空）

    // 构造函数
    public Tile(TileType type) {
        this.type = type;
        this.level = 0;  // 默认等级为 0
        this.owner = null;  // 默认没有拥有者
    }

    public void handleTile(Player player, Tile tile) {
        switch (tile.getType()) {
            case LAND:
                if (tile.getOwner() == null) {
                    // 空地：提示购买
                } else if (tile.getOwner().equals(player.getName())) {
                    // 自己的地：允许升级
                } else {
                    // 别人的地：支付租金
                }
                break;
            case LUCKY:
                // 给玩家奖励
                break;
            case UNLUCKY:
                // 扣钱或跳过一回合
                break;
            case PRISON:
                // 设置玩家状态为“暂停一回合”
                break;
            case HOSPITAL:
                // 扣医疗费 + 暂停一回合
                break;
            case TAX:
                // 扣税
                break;
            default:
                break;
        }
    }

    /**
     * 获取购买价格
     * @return  当前地块房价
     */
    public int getPrice() {
        return 200 + level * 100;
    }

    /**
     * 获取租金价格
     * @return  当前地块租金
     */
    public int getRent() {
        return 50 + level * 50;
    }


    // Getter 和 Setter 方法
    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "type=" + type +
                ", level=" + level +
                ", owner='" + owner + '\'' +
                '}';
    }
}
