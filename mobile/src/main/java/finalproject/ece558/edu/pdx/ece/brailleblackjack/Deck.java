package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class manages the deck database by adding cards and retrieving card
 */
public class Deck {
    DeckDatabase storage;

    /** Deck Constructor
     */
    public Deck(Context context){
        storage = new DeckDatabase(context);
    }

    /**
     * This method adds a card to the database
     * Each row must have a card key (what the card is)
     *  the card's description for Android Voice-Over
     *  the card's drawable image string
     *  and the card's value as an integer (1-10)
     *  NOTE: Ace will be identified as 1 but it has a possible value of 11
     */
    public long insertCard(String key,
                           String description,
                           String drawable,
                           int value){
        SQLiteDatabase db = storage.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeckDatabase.getCardKey(), key);
        contentValues.put(DeckDatabase.getCardDescription(), description);
        contentValues.put(DeckDatabase.getCardDrawable(), drawable);
        contentValues.put(DeckDatabase.getCardValue(), value);

        long id = db.insert(DeckDatabase.getTableName(), null, contentValues);
        return id;
    }

    /**
     * This method retrieves a card's information from the database
     * using the key to query the database
     */
    public Card getCard(String key){

        // Reference database
        SQLiteDatabase db = storage.getWritableDatabase();

        // Build the query
        String[] columns = {DeckDatabase.getCardKey(), DeckDatabase.getCardDescription(),
                            DeckDatabase.getCardDrawable(), DeckDatabase.getCardValue()};
        Cursor cursor = db.query(
                // Table
                DeckDatabase.getTableName(),
                // Columns in table
                columns,
                // Selections
                DeckDatabase.getCardKey() + " ='" + key + "'",
                // Selection Arguments
                null,
                // Group By
                null,
                // Having
                null,
                // Order By
                null,
                // Limit
                null
        );

        // Get the queried item
        // CHECK THIS METHOD - while (cursor.moveToNext()){
        if (cursor != null) {
            cursor.moveToFirst();
        }
        int index1 = cursor.getColumnIndex(DeckDatabase.getCardKey());
        int index2 = cursor.getColumnIndex(DeckDatabase.getCardDescription());
        int index3 = cursor.getColumnIndex(DeckDatabase.getCardDrawable());
        int index4 = cursor.getColumnIndex(DeckDatabase.getCardValue());

        // Declare new instance of Card class
        Card card = new Card(
                cursor.getString(index1),
                cursor.getString(index2),
                cursor.getString(index3),
                cursor.getInt(index4));
        return card;
    }

    public  void addCardsToDB(Context context){
        // Adding club suit
        insertCard("1_of_clubs", context.getString(R.string.description_1_clubs), null, 1);
        insertCard("2_of_clubs", context.getString(R.string.description_2_clubs), null, 2);
        insertCard("3_of_clubs", context.getString(R.string.description_3_clubs), null, 3);
        insertCard("4_of_clubs", context.getString(R.string.description_4_clubs), null, 4);
        insertCard("5_of_clubs", context.getString(R.string.description_5_clubs), null, 5);
        insertCard("6_of_clubs", context.getString(R.string.description_6_clubs), null, 6);
        insertCard("7_of_clubs", context.getString(R.string.description_7_clubs), null, 7);
        insertCard("8_of_clubs", context.getString(R.string.description_8_clubs), null, 8);
        insertCard("9_of_clubs", context.getString(R.string.description_9_clubs), null, 9);
        insertCard("10_of_clubs", context.getString(R.string.description_10_clubs), null, 10);
        insertCard("11_of_clubs", context.getString(R.string.description_11_clubs), null, 10);
        insertCard("12_of_clubs", context.getString(R.string.description_12_clubs), null, 10);
        insertCard("13_of_clubs", context.getString(R.string.description_13_clubs), null, 10);
        // Adding diamonds suit
        insertCard("1_of_diamonds", context.getString(R.string.description_1_diamonds), null, 1);
        insertCard("2_of_diamonds", context.getString(R.string.description_2_diamonds), null, 2);
        insertCard("3_of_diamonds", context.getString(R.string.description_3_diamonds), null, 3);
        insertCard("4_of_diamonds", context.getString(R.string.description_4_diamonds), null, 4);
        insertCard("5_of_diamonds", context.getString(R.string.description_5_diamonds), null, 5);
        insertCard("6_of_diamonds", context.getString(R.string.description_6_diamonds), null, 6);
        insertCard("7_of_diamonds", context.getString(R.string.description_7_diamonds), null, 7);
        insertCard("8_of_diamonds", context.getString(R.string.description_8_diamonds), null, 8);
        insertCard("9_of_diamonds", context.getString(R.string.description_9_diamonds), null, 9);
        insertCard("10_of_diamonds", context.getString(R.string.description_10_diamonds), null, 10);
        insertCard("11_of_diamonds", context.getString(R.string.description_11_diamonds), null, 10);
        insertCard("12_of_diamonds", context.getString(R.string.description_12_diamonds), null, 10);
        insertCard("13_of_diamonds", context.getString(R.string.description_13_diamonds), null, 10);
        // Adding hearts suit
        insertCard("1_of_hearts", context.getString(R.string.description_1_hearts), null, 1);
        insertCard("2_of_hearts", context.getString(R.string.description_2_hearts), null, 2);
        insertCard("3_of_hearts", context.getString(R.string.description_3_hearts), null, 3);
        insertCard("4_of_hearts", context.getString(R.string.description_4_hearts), null, 4);
        insertCard("5_of_hearts", context.getString(R.string.description_5_hearts), null, 5);
        insertCard("6_of_hearts", context.getString(R.string.description_6_hearts), null, 6);
        insertCard("7_of_hearts", context.getString(R.string.description_7_hearts), null, 7);
        insertCard("8_of_hearts", context.getString(R.string.description_8_hearts), null, 8);
        insertCard("9_of_hearts", context.getString(R.string.description_9_hearts), null, 9);
        insertCard("10_of_hearts", context.getString(R.string.description_10_hearts), null, 10);
        insertCard("11_of_hearts", context.getString(R.string.description_11_hearts), null, 10);
        insertCard("12_of_hearts", context.getString(R.string.description_12_hearts), null, 10);
        insertCard("13_of_hearts", context.getString(R.string.description_13_hearts), null, 10);
        // Adding spades suit
        insertCard("1_of_spades", context.getString(R.string.description_1_spades), null, 1);
        insertCard("2_of_spades", context.getString(R.string.description_2_spades), null, 2);
        insertCard("3_of_spades", context.getString(R.string.description_3_spades), null, 3);
        insertCard("4_of_spades", context.getString(R.string.description_4_spades), null, 4);
        insertCard("5_of_spades", context.getString(R.string.description_5_spades), null, 5);
        insertCard("6_of_spades", context.getString(R.string.description_6_spades), null, 6);
        insertCard("7_of_spades", context.getString(R.string.description_7_spades), null, 7);
        insertCard("8_of_spades", context.getString(R.string.description_8_spades), null, 8);
        insertCard("9_of_spades", context.getString(R.string.description_9_spades), null, 9);
        insertCard("10_of_spades", context.getString(R.string.description_10_spades), null, 10);
        insertCard("11_of_spades", context.getString(R.string.description_11_spades), null, 10);
        insertCard("12_of_spades", context.getString(R.string.description_12_spades), null, 10);
        insertCard("13_of_spades", context.getString(R.string.description_13_spades), null, 10);

    }
}
