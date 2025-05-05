package logic;

import java.util.Random;

public class Dice {
    private static final Random rand = new Random();

    public static int roll() {
        int d1 = rand.nextInt(6) + 1;
        int d2 = rand.nextInt(6) + 1;
        return d1 + d2;
    }
}
