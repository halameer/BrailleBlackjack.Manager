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

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
/**
 * This activity class represents the Learn Braille part of the app. It shows images/cards to teach
 *  braille characters to a user by using a pager and LearnBrailleFragment to house the cards in.
 *
 * Demonstrates a "screen-slide" animation using a {@link ViewPager}. Because {@link ViewPager}
 * automatically plays such an animation when calling {@link ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see LearnBrailleFragment
 */
public class LearnBrailleActivity extends FragmentActivity {
     /* The number of pages (wizard steps) to show in this demo. */
    private static final int NUM_PAGES = 4;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /*  The pager adapter, which provides the pages to the view pager widget. */
    private PagerAdapter mPagerAdapter;

    /**
     * Start the LearnBraillePagerAdapter
     * @param savedInstanceState Bundle object of any saved instances
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_braille);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new LearnBraillePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // reset the action bar actions since they are dependent
                // on which page is currently active.
                invalidateOptionsMenu();
            }
        });
    }

    /**
     * Create the action menu to provide buttons to navigate the Pager
     * @param menu Menu object
     * @return Boolean true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Add/enable previous button depending on which page is active
        // is currently selected.
        MenuItem prev_item = menu.add(Menu.NONE, R.id.action_previous, Menu.NONE, R.string.action_previous);

        prev_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "next" or "finished" button to the action bar, depending on which page
        // is currently active.
        MenuItem next_item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        next_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.findItem(R.id.action_next).setEnabled(!(mPager.getCurrentItem() == 3));

        menu.findItem(R.id.action_next).setEnabled(mPager.getCurrentItem() < 3);
        return true;
    }

    /**
     * Handler of a menu button click
     * @param item MenuItem object
     * @return Boolean true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate back to the main activity
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A simple pager adapter that represents 5 {@link LearnBrailleFragment} objects, in
     * sequence.
     */
    private class LearnBraillePagerAdapter extends FragmentStatePagerAdapter {
        public LearnBraillePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return LearnBrailleFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
