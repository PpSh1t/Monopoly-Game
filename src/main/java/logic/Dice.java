package logic;

import java.util.Random;

public class Dice {
    private static final Random rand = new Random();

    /**
     * 生成两个随机数（1~6），并返回它们的总和。
     * @return 返回两个随机数总和
     */
    public static int roll() {
        int d1 = rand.nextInt(6) + 1;
        int d2 = rand.nextInt(6) + 1;
        return d1 + d2;
    }
}
