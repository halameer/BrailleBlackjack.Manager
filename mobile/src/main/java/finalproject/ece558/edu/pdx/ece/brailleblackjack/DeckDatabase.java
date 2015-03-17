/*  Braille BlackJack - An android program that aims to teach Braille Numbers is a fun way by playing the
 *   game blackjack
 *
 *   Copyright (C) 2015 Hussein AlAmeer, and Tu Truong
 *
 *   This file is part of Braille BlackJack.
 *
 *   Braille BlackJack is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Braille BlackJack is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class enables the creation of an SQLite database. The database houses decks of cards.
 * Each deck consists of 52 cards and each card is identified by its number and suite type.
 *
 * This website helped explain how to create an SQLite database in Android:
 *  http://hmkcode.com/android-simple-sqlite-database-tutorial/
 */
public class DeckDatabase extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DeckDatabase";

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

    /**
     * SQLite Database Constructor
     * @param context Give context of application
     */
    public DeckDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d(LOG, "Constructor called");
    }

    /**
     * Create the SQLite database
     * @param db the SQLite database to create
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d(LOG, "The Database onCreate called");
        // Create the table
        db.execSQL(CREATE_TABLE_CARD);
    }

    /**
     * Upgrade the SQLite database
     * @param db SQLiteDatabase object to upgrade to
     * @param oldVersion Integer of old version number
     * @param newVersion Integer of new version number
     */
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
