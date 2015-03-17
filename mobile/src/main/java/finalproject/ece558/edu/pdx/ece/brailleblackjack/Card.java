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

/**
 * This class represents a card object in the deck.
 * Each card contains a Key to identify it, a Description of the card to be used for printing or
 *  Android Talk-Back, a Drawable representing the pointer to the image resource in the drawable
 *  folder, and finally the card's actual value. Note that Ace has a value of both 1 and 11 but
 *  is only saved with the value 1 and since it is unique we also know if its 1 it must be 11.
 *
 * Each of the variables are encapsulated.
 */
public class Card {
    private String cardKey;
    private String cardDescription;
    private int cardDrawable;
    private int cardValue;

    /**
     * Fully Qualified Constructor
     */
    public Card(String cardKey, String cardDescription, int cardDrawable, int cardValue){
        this.cardKey = cardKey;
        this.cardDescription = cardDescription;
        this.cardDrawable = cardDrawable;
        this.cardValue = cardValue;
    }

    /**
     * Get the current card's key
     * @return String of card key
     */
    public String getCardKey() {
        return cardKey;
    }

    /**
     * Get the current card's value
     * @return Integer of card value
     */
    public int getCardValue() {
        return cardValue;
    }

    /**
     * Get the current card's drawable
     * @return String of card drawable
     */
    public int getCardDrawable() {
        return cardDrawable;
    }

    /**
     * Get the current card's description for Voice-Over
     * @return String of card description
     */
    public String getCardDescription() {
        return cardDescription;
    }

    /**
     * Set a card key value
     * @param cardKey
     */
    public void setCardKey(String cardKey) {
        this.cardKey = cardKey;
    }

    /**
     * Set a card description for Voice-Over
     * @param cardDescription
     */
    public void setCardDescription(String cardDescription) {
        this.cardDescription = cardDescription;
    }

    /**
     * Set a card drawable string
     * @param cardDrawable
     */
    public void setCardDrawable(int cardDrawable) {
        this.cardDrawable = cardDrawable;
    }

    /**
     * Set a card numerical value
     * @param cardValue
     */
    public void setCardValue(int cardValue) {
        this.cardValue = cardValue;
    }
}
