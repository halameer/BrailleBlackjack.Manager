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
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * This fragment class represent an introductory page before playing the game. It shows the UI
 *  layout of the game page to familiarize the player of where his card lie and where the dealers
 *  cards lie.
 */
public class PlayBlackJackStartFragment extends Fragment {
    /**
     * Set-up a button listener to switch to the PlayBlackJackGameFragment
     * @param inflater LayoutInflater object
     * @param container ViewGroup object
     * @param savedInstanceState Bundle item of any saved instances
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        if (savedInstanceState != null) {
        }

        View v = inflater.inflate(R.layout.fragment_play_black_jack_start, container, false);

        // Button Listener to start the game fragment
        Button start_black_jack = (Button)v.findViewById(R.id.lpBlackJack);
        start_black_jack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fm = getFragmentManager().beginTransaction();
                fm.replace(R.id.fragment_container, new PlayBlackJackGameFragment());
                fm.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fm.commit();

            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
