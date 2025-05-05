package logic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Player {
    private String name;
    private boolean isAI;
    private int money;
    private int position;

    public Player(String name, boolean isAI, int initialMoney) {
        this.name = name;
        this.isAI = isAI;
        this.money = initialMoney;
        this.position = 0;
    }
}
