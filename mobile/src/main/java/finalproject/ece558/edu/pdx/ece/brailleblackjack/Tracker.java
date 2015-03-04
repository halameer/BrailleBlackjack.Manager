package finalproject.ece558.edu.pdx.ece.brailleblackjack;

/**
 *  Contains tracked flags to be used between the entire app
 */
public class Tracker {
    // Fragment IDs
    public static final int HOME_FRAGMENT = 0;
    public static final int TEACHING_FRAGMENT = 1;
    public static final int BLACKJACK_FRAGMENT = 2;
    public static final int ABOUT_FRAGMENT = 3;

    // Tracks the current fragment that's inflated
    public static int curFragment = 0;
}