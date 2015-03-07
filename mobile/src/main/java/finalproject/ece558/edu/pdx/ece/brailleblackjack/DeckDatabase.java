package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class creates a database to store a whole deck of cards identified
 *  by its suit and number
 */
public class DeckDatabase extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DeckStorage";

    // Database name and version
    private static final String DATABASE_NAME = "deckDB";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private  static final String TABLE_NAME = "Cards";

    // Card Table columns
    private static final String UNIQUE_ID = "_id";
    private static final String CARD_KEY = "key";
    private static final String CARD_DESCRIPTION = "description";
    private static final String CARD_DRAWABLE = "drawable";
    private static final String CARD_VALUE = "value";



    // Table Creation string
    private static final String CREATE_TABLE_CARD = "CREATE TABLE "
            + TABLE_NAME + "("
            + UNIQUE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CARD_KEY + " TEXT,"
            + CARD_DESCRIPTION + " TEXT,"
            + CARD_DRAWABLE + " INTEGER,"
            + CARD_VALUE + " INTEGER"
            + ")";

    private final Context context;

    /** SQLite Database Constructor
     */
    public DeckDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d(LOG, "Constructor called");
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d(LOG, "The Database onCreate called");
        // Create the table
        db.execSQL(CREATE_TABLE_CARD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d(LOG, "The Database onUpgrade called");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        } catch (SQLException e) {
            Log.d(LOG, String.valueOf(e));
        }
    }


    /**
     * Get Table Name identifier
     * @return String of Table Name
     */
    public static String getTableName() {
        return TABLE_NAME;
    }

    /**
     * Get Card key identifier
     * @return String of Card key identifier
     */
    public static String getCardKey() {
        return CARD_KEY;
    }

    /**
     * Get Card description identifier
     * @return String of Card description identifier
     */
    public static String getCardDescription() {
        return CARD_DESCRIPTION;
    }

    /**
     * Get Card drawable identifier
     * @return String of Card drawable identifier
     */
    public static String getCardDrawable() {
        return CARD_DRAWABLE;
    }

    /**
     * Get Card value identifier
     * @return Integer of Card value identifier
     */
    public static String getCardValue() {
        return CARD_VALUE;
    }
}
