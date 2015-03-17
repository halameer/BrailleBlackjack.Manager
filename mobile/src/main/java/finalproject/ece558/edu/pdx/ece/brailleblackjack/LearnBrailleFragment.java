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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

/**
 * This fragment class represent a card being used in the Learn Braille Section of the app.
 * It takes a specific page (image/card of what to show like braille numbers) builds and inflates it
 *  into view
 */
public class LearnBrailleFragment extends android.support.v4.app.Fragment {
     /* The argument key for the page number this fragment represents. */
    public static final String ARG_PAGE = "page";

    /* The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}. */
    private int mPageNumber;

    /**
     * Fully Qualified Constructor. Constructs a new fragment for the given page number.
     * @param pageNumber Build the fragment around the given page
     * @return Fragment
     */
    public static LearnBrailleFragment create(int pageNumber) {
        LearnBrailleFragment fragment = new LearnBrailleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Default Constructor
     */
    public LearnBrailleFragment() {
    }

    /**
     * Gets the arguments passed to it from the calling activity upon fragment creation
     * @param savedInstanceState Bundle object of any saved instances
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    /**
     * Brings the fragment into view by showing the image/card associated with th  page number
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return ViewGroup
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_learn_braille, container, false);

        ImageView  img = (ImageView)rootView.findViewById(R.id.learnBrailImg);

        switch (mPageNumber+1) {
            case 1:
                // Switch to number card
                img.setImageDrawable(getResources().getDrawable(R.drawable.numbers));
                img.setContentDescription(getResources().getString(R.string.description_numbers));
                break;
            case 2:
                // Switch to capitalization card
                img.setImageDrawable(getResources().getDrawable(R.drawable.capital_letters));
                img.setContentDescription(getResources().getString(R.string.description_capitalization));
                break;
            case 3:
                // Switch to Alphabet a-m card
                img.setImageDrawable(getResources().getDrawable(R.drawable.letters_atom));
                img.setContentDescription(getResources().getString(R.string.description_letters_atom));
                break;
            case 4:
                // Switch to Alphabet n-z card
                img.setImageDrawable(getResources().getDrawable(R.drawable.letters_ntoz));
                img.setContentDescription(getResources().getString(R.string.description_letters_ntoz));
                break;
            default:
                break;
        }

        return rootView;
    }

    /**
     *Gets the page number represented by this fragment object.
     * @return Integer representing a page number
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
