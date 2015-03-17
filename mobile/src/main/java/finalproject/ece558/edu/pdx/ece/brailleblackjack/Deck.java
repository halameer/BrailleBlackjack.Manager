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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class represent a deck and it manages the deck database.
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
     *  the card's drawable image integer
     *  and the card's value as an integer (1-10)
     *  NOTE: Ace will be identified as 1 but it has a possible value of 11
     * @param key String of Card id
     * @param description String of card description
     * @param drawable Integer of resource pointer
     * @param value Integer of card value
     * @return Long of the id of the database insert
     */
    public long insertCard(String key,
                           String description,
                           int drawable,
                           int value){
        SQLiteDatabase db = storage.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeckDatabase.getCardKey(), key);
        contentValues.put(DeckDatabase.getCardDescription(), description);
        contentValues.put(DeckDatabase.getCardDrawable(), drawable);
        contentValues.put(DeckDatabase.getCardValue(), value);

        long id = db.insert(DeckDatabase.getTableName(), null, contentValues);
        db.close();
        return id;
    }

    /**
     * This method queries and retrieves a card's information from the database
     * using the key to query the database
     * @param key String of the card ID
     * @return Card object, see Class Card
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
                cursor.getInt(index3),
                cursor.getInt(index4));
        cursor.close();
        db.close();
        return card;
    }

    /**
     * Add a whole deck of cards to the database.
     * @param context Context of the caller
     */
    public  void addCardsToDB(Context context){
        // Adding club suit
        insertCard("1_of_clubs", context.getString(R.string.description_1_clubs), R.drawable.clubs_ace, 1);
        insertCard("2_of_clubs", context.getString(R.string.description_2_clubs), R.drawable.clubs_2, 2);
        insertCard("3_of_clubs", context.getString(R.string.description_3_clubs), R.drawable.clubs_3, 3);
        insertCard("4_of_clubs", context.getString(R.string.description_4_clubs), R.drawable.clubs_4, 4);
        insertCard("5_of_clubs", context.getString(R.string.description_5_clubs), R.drawable.clubs_5, 5);
        insertCard("6_of_clubs", context.getString(R.string.description_6_clubs), R.drawable.clubs_6, 6);
        insertCard("7_of_clubs", context.getString(R.string.description_7_clubs), R.drawable.clubs_7, 7);
        insertCard("8_of_clubs", context.getString(R.string.description_8_clubs), R.drawable.clubs_8, 8);
        insertCard("9_of_clubs", context.getString(R.string.description_9_clubs), R.drawable.clubs_9, 9);
        insertCard("10_of_clubs", context.getString(R.string.description_10_clubs), R.drawable.clubs_10, 10);
        insertCard("11_of_clubs", context.getString(R.string.description_11_clubs), R.drawable.clubs_jack, 10);
        insertCard("12_of_clubs", context.getString(R.string.description_12_clubs), R.drawable.clubs_queen, 10);
        insertCard("13_of_clubs", context.getString(R.string.description_13_clubs), R.drawable.clubs_king, 10);
        // Adding diamonds suit
        insertCard("1_of_diamonds", context.getString(R.string.description_1_diamonds), R.drawable.diamonds_ace, 1);
        insertCard("2_of_diamonds", context.getString(R.string.description_2_diamonds), R.drawable.diamonds_2, 2);
        insertCard("3_of_diamonds", context.getString(R.string.description_3_diamonds), R.drawable.diamonds_3, 3);
        insertCard("4_of_diamonds", context.getString(R.string.description_4_diamonds), R.drawable.diamonds_4, 4);
        insertCard("5_of_diamonds", context.getString(R.string.description_5_diamonds), R.drawable.diamonds_5, 5);
        insertCard("6_of_diamonds", context.getString(R.string.description_6_diamonds), R.drawable.diamonds_6, 6);
        insertCard("7_of_diamonds", context.getString(R.string.description_7_diamonds), R.drawable.diamonds_7, 7);
        insertCard("8_of_diamonds", context.getString(R.string.description_8_diamonds), R.drawable.diamonds_8, 8);
        insertCard("9_of_diamonds", context.getString(R.string.description_9_diamonds), R.drawable.diamonds_9, 9);
        insertCard("10_of_diamonds", context.getString(R.string.description_10_diamonds), R.drawable.diamonds_10, 10);
        insertCard("11_of_diamonds", context.getString(R.string.description_11_diamonds), R.drawable.diamonds_jack, 10);
        insertCard("12_of_diamonds", context.getString(R.string.description_12_diamonds), R.drawable.diamonds_queen, 10);
        insertCard("13_of_diamonds", context.getString(R.string.description_13_diamonds), R.drawable.diamonds_king, 10);
        // Adding hearts suit
        insertCard("1_of_hearts", context.getString(R.string.description_1_hearts), R.drawable.heart_ace, 1);
        insertCard("2_of_hearts", context.getString(R.string.description_2_hearts), R.drawable.heart_2, 2);
        insertCard("3_of_hearts", context.getString(R.string.description_3_hearts), R.drawable.heart_3, 3);
        insertCard("4_of_hearts", context.getString(R.string.description_4_hearts), R.drawable.heart_4, 4);
        insertCard("5_of_hearts", context.getString(R.string.description_5_hearts), R.drawable.heart_5, 5);
        insertCard("6_of_hearts", context.getString(R.string.description_6_hearts), R.drawable.heart_6, 6);
        insertCard("7_of_hearts", context.getString(R.string.description_7_hearts), R.drawable.heart_7, 7);
        insertCard("8_of_hearts", context.getString(R.string.description_8_hearts), R.drawable.heart_8, 8);
        insertCard("9_of_hearts", context.getString(R.string.description_9_hearts), R.drawable.heart_9, 9);
        insertCard("10_of_hearts", context.getString(R.string.description_10_hearts), R.drawable.heart_10, 10);
        insertCard("11_of_hearts", context.getString(R.string.description_11_hearts), R.drawable.heart_jack, 10);
        insertCard("12_of_hearts", context.getString(R.string.description_12_hearts), R.drawable.heart_queen, 10);
        insertCard("13_of_hearts", context.getString(R.string.description_13_hearts), R.drawable.heart_king, 10);
        // Adding spades suit
        insertCard("1_of_spades", context.getString(R.string.description_1_spades), R.drawable.spades_ace, 1);
        insertCard("2_of_spades", context.getString(R.string.description_2_spades), R.drawable.spades_2, 2);
        insertCard("3_of_spades", context.getString(R.string.description_3_spades), R.drawable.spades_3, 3);
        insertCard("4_of_spades", context.getString(R.string.description_4_spades), R.drawable.spades_4, 4);
        insertCard("5_of_spades", context.getString(R.string.description_5_spades), R.drawable.spades_5, 5);
        insertCard("6_of_spades", context.getString(R.string.description_6_spades), R.drawable.spades_6, 6);
        insertCard("7_of_spades", context.getString(R.string.description_7_spades), R.drawable.spades_7, 7);
        insertCard("8_of_spades", context.getString(R.string.description_8_spades), R.drawable.spades_8, 8);
        insertCard("9_of_spades", context.getString(R.string.description_9_spades), R.drawable.spades_9, 9);
        insertCard("10_of_spades", context.getString(R.string.description_10_spades), R.drawable.spades_10, 10);
        insertCard("11_of_spades", context.getString(R.string.description_11_spades), R.drawable.spades_jack, 10);
        insertCard("12_of_spades", context.getString(R.string.description_12_spades), R.drawable.spades_queen, 10);
        insertCard("13_of_spades", context.getString(R.string.description_13_spades), R.drawable.spades_king, 10);

    }
}
