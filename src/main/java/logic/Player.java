package logic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 玩家类，含金钱、位置等状态
 */
@Getter
@Setter
@ToString
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private boolean isAI;
    private int money;
    private int position;
    private boolean bankrupt = false;
    private boolean extraTurn = false; // 是否获得额外回合
    private boolean skipTurn = false; //是否跳过回合


    public boolean hasExtraTurn() {
        return extraTurn;
    }

    public Player(String name, boolean isAI) {
        this.name = name;
        this.isAI = isAI;
        this.money = 1000;
        this.position = 0;
    }
}
