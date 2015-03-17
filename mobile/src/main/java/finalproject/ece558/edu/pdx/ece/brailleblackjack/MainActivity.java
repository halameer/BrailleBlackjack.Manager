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

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Random;

/**
 * MainActivity class houses the first activity and is shown on the launch of the app. It has a
 *  SharedPreferences to check if this is the first time the app is launches, if it is then a
 *  a new database needs to be created of a single deck of cards. This database is then permanently
 *  stored on the device. On subsequent calls to the SharedPreferences it checks a flag if the
 *  database has been created before and it will not do anything once the database has been created
 *  prior.
 *
 * The List of actions point to 3 other activities to start when they are pushed representing our
 *  app layout.
 *
 * List Item 1: Learn Braille - Section to Teach Braille
 * List Item 2: Play BlackJack - Section that houses the actual Game portion
 * List Item 3: About - Section that shows the authors and acknowledgments
 */
public class MainActivity extends ListActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    // Logcat tag
    private static final String LOG = "MainActivity";

    /**
     * This class describes an individual Selection (the Selection title, and the activity class that
     * demonstrates this Selection).
     */
    private class Selection {
        private CharSequence title;
        private Class<? extends Activity> activityClass;

        public Selection(int titleResId, Class<? extends Activity> activityClass) {
            this.activityClass = activityClass;
            this.title = getResources().getString(titleResId);
        }

        @Override
        public String toString() {
            return title.toString();
        }
    }

    /*
     * The collection of all Selections in the app. This gets instantiated in {@link
     * #onCreate(android.os.Bundle)} because the {@link Selection} constructor needs access to {@link
     * android.content.res.Resources}.
     */
    private static Selection[] mSelections;


    /**
     * Check SharedPreferences flag if database exists, if not create a new one. Also set-up the
     *  list menu items.
     * @param savedInstanceState Bundle object of any saved instances
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean doesDatabaseExist = settings.getBoolean("DatabaseExists", false);

        // Database does not exist, must be a newly installed application
        //  or application data got wiped, create a new deck database
        if(!doesDatabaseExist){
            Log.d(LOG, "There is no Database, go create one");

            // Create a new deck database
            Deck deck = new Deck(this);
            deck.addCardsToDB(this);
            Log.d(LOG, "All cards added to database");
            // We need an Editor object to make preference changes.
            // All objects are from android.context.Context
            SharedPreferences.Editor editor = settings.edit();
            // Database created
            doesDatabaseExist = true;
            editor.putBoolean("DatabaseExists", doesDatabaseExist);
            // Commit the edits!
            editor.commit();
        }else{
            Log.d(LOG, "Database exists! No need to create a new one");
        }

        // Instantiate the list of Selections.
        mSelections = new Selection[]{
                new Selection(R.string.learn_braille, LearnBrailleActivity.class),
                new Selection(R.string.play_blackjack, PlayBlackJackActivity.class),
                new Selection(R.string.about, AboutActivity.class),
        };

        setListAdapter(new ArrayAdapter<Selection>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                mSelections));
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        // Launch the Selection associated with this list position.
        startActivity(new Intent(MainActivity.this, mSelections[position].activityClass));
    }
}

