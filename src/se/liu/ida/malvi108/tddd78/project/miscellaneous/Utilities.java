package se.liu.ida.malvi108.tddd78.project.miscellaneous;

import java.awt.*;
import java.util.Random;

/**
 * Class for miscellaneous utility methods.
 */
public final class Utilities
{
    private final static int HIGHEST_COLOR_VALUE = 256;

    private Utilities(){}

    public static Color generateRandomColor() {
	Random random = new Random();
	int red = random.nextInt(HIGHEST_COLOR_VALUE);
	int green = random.nextInt(HIGHEST_COLOR_VALUE);
	int blue = random.nextInt(HIGHEST_COLOR_VALUE);
	return new Color(red, green, blue);
    }
}
