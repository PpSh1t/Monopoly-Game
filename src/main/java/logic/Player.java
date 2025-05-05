package logic;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private boolean isAI;
    private String avatarPath;     // 可扩展为头像路径
    private int position;          // 当前所在地图位置
    private int money;
    private List<Integer> ownedLandPositions;  // 拥有的地块编号

    public Player(String name, boolean isAI, String avatarPath, int initialMoney) {
        this.name = name;
        this.isAI = isAI;
        this.avatarPath = avatarPath;
        this.money = initialMoney;
        this.position = 0; // 默认从0位置开始
        this.ownedLandPositions = new ArrayList<>();
    }

    // ==== Getter / Setter ====
    public String getName() {
        return name;
    }

    public boolean isAI() {
        return isAI;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMoney() {
        return money;
    }

    public void changeMoney(int amount) {
        this.money += amount;
    }

    public List<Integer> getOwnedLandPositions() {
        return ownedLandPositions;
    }

    public void addLand(int position) {
        ownedLandPositions.add(position);
    }

    public void removeLand(int position) {
        ownedLandPositions.remove((Integer) position);
    }

    public boolean isBankrupt() {
        return money < 0;
    }

    @Override
    public String toString() {
        return name + " (" + (isAI ? "AI" : "Human") + ") | $: " + money + " | Pos: " + position;
    }
}
