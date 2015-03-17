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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This Activity class houses the play blackjack fragments. It shows the play start page fragment
 * on its creation.
 */
public class PlayBlackJackActivity extends Activity {
    private static final String TAG = "PlayBlackJackActivity";

    /**
     * Start the PlayBlackJackStartFragment
     * @param savedInstanceState Bundle object of any saved instances
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_black_jack);

        // Switch to the PlayBlackJackStartFragment
        if (findViewById(R.id.fragment_container) != null) {
            Log.d(TAG, "In onCreate");
            // If we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            Log.d(TAG, "Launching new fragment");
            // Create an instance PlayblackJackStartFragment
            PlayBlackJackStartFragment startFragment = new PlayBlackJackStartFragment();
            startFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, startFragment).commit();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate back to menu
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
