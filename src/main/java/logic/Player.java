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
    private boolean extraTurn = false; // 是否获得额外回合
    private boolean skipTurn = false; //是否跳过回合


    public boolean hasExtraTurn() {
        return extraTurn;
    }



    public Player(String name, boolean isAI, int initialMoney) {
        this.name = name;
        this.isAI = isAI;
        this.money = initialMoney;
        this.position = 0;
    }
}
