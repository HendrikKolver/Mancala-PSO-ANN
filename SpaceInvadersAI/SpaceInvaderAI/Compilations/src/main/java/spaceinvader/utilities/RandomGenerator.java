package spaceinvader.utilities;

import java.util.Random;

/**
 *
 * @author Hendrik Kolver
 */
public class RandomGenerator {
    private static Random rand = new Random();
    public static int randInt(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
