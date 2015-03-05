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
    public long insertData(String key,
                           String description,
                           String drawable,
                           String value){
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
}
