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

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * This fragment contains the BlackJack Game logic.
 *
 * It uses the Google MessageApi to communicate with an Android Wear device if it exists. It sets
 *  up a Listner within the fragment instead of a full service for the whole application to listen
 *  for any Android Wear messages because the Android Wear portion only augments this portion of the
 *  app and does not communicate with any other part. We do not want to cause any unnecessary
 *  effects associated with that.
 *
 * It uses the Deck and Card objects to access cards from the database to be used for the game logic
 *
 * It uses AccessibilityManager to check if Accessibility Talk-Back is on. If it is then Android's
 *  TextToSpeech API is used for specific events (like speaking the description of a drawn card)
 *  that happen since Android Talk-Back does not speak based on events but on presses.
 *
 *  It uses transition animations for an interactive experience when showing card. If cards
 *   show up instantly it can be jarring.
 *
 *  It uses AsyncTasks to force delays for events and transition animations. Cards should not show
 *   up instantly as mentioned previously. Events such as Result dialogs should also not show
 *   instantly to give the user time to process a given event (like adding up a drawn card to their
 *   total or dealer's total)
 *
 * Blackjack Rules (Source: http://en.wikipedia.org/wiki/Blackjack)
 * "Blackjack is a comparing card game between a player and dealer, meaning that players compete
 * against the dealer but not against any other players. It is played with one or more decks of 52
 * cards. The object of the game is to beat the dealer, which can be done in a number of ways:
 *
 *  - Get 21 points on the player's first two cards (called a blackjack), without a dealer blackjack
 *  - Reach a final score higher than the dealer without exceeding 21 or
 *  - Let the dealer draw additional cards until his or her hand exceeds 21.
 *
 *  The player or players are dealt an initial two-card hand and add together the value of their
 *  cards. Face cards (kings, queens, and jacks) are counted as ten points. A player and the dealer
 *  can count his or her own ace as 1 point or 11 points. All other cards are counted as the numeric
 *  value shown on the card. After receiving their initial two cards, players have the option of
 *  getting a "hit", or taking an additional card. In a given round, the player or the dealer wins
 *  by having a score of 21 or by having the highest score that is less than 21. Scoring higher than
 *  21 (called "busting" or "going bust") results in a loss. A player may win by having any final
 *  score equal to or less than 21 if the dealer busts. If a player holds an ace valued as 11, the
 *  hand is called "soft", meaning that the player cannot go bust by taking an additional card; 11
 *  plus the value of any other card will always be less than or equal to 21. Otherwise, the hand
 *  is "hard".
 *
 * The dealer has to take hits until his or her cards total 17 or more points.
 * (In some casinos the dealer also hits on a "soft" 17, e.g. an initial ace and six.)
 * Players win if they do not bust and have a total that is higher than the dealer's.
 * The dealer loses if he or she busts or has a lesser hand than the player who has not busted.
 * If the player and dealer have the same total, this is called a "push" and the player typically
 * does not win or lose money on that hand."
 */
public class PlayBlackJackGameFragment extends Fragment implements
        MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "PlayBlackJackFragment";

    // Use client to connect to Android Wear
    private GoogleApiClient mGoogleApiClient;

    // Declare a Deck object to access the Deck database and declare 4 card objects representing
    // the 4 cards on the screen.
    private Deck curDeck;
    private Card dealer_left_card;
    private Card dealer_right_card;
    private Card player_left_card;
    private Card player_right_card;

    // UI elements
    private ViewGroup group;

    private ImageView dealer_left_slot;
    private ImageView dealer_right_slot;
    private ImageView player_left_slot;
    private ImageView player_right_slot;

    private ImageView dealer_top_total_slot;
    private ImageView dealer_bot_total_slot;
    private ImageView player_top_total_slot;
    private ImageView player_bot_total_slot;

    private Button button_hit;
    private Button button_stand;
    private Button button_start_over;
    private Button button_hint;

    private View v;
    private Context context = null;

    // Save total values for dealer and player
    // Top value is the default value
    // Bottom value is the alternative value when Ace is encountered which has either a value of 1
    //  or 11
    // If bottom value exceeds 21 it is no longer shown since its a losing value and won't be
    //  considered
    private int dealer_top_total_value;
    private int dealer_bot_total_value;
    private int player_top_total_value;
    private int player_bot_total_value;

    // Flags
    private boolean dealer_had_ace;
    private boolean player_had_ace;
    private boolean dealer_turn;
    private boolean player_turn;
    private boolean button_hit_state;
    private boolean button_stand_state;
    private boolean button_hint_state;
    private boolean button_start_over_state;
    private boolean first_draw_spoken;

    // Android TTS Api
    private TextToSpeech textToSpeech;

    // Check if Android Talk-Back is used
    AccessibilityManager am;
    boolean isAccessibilityEnabled;
    boolean isExploreByTouchEnabled;

    // Phone - Wear Communication Messages
    // These are message ID's to be sent by the phone to the Wear (smartwatch) device
    private final String START_WEAR = "#START";
    private final String PLAYER_WINS = "#WIN";
    private final String PLAYER_LOSES = "#LOSE";
    private final String PLAYER_DRAW = "#DRAW";

    // Declare AsyncTaks to be able to cancel them if Fragment is stopped/destroyed
    //  So no issues or crashes can happen
    FirstDealAnimation firstTask = null;
    AnimateDealerCards animateDealerTask = null;
    AnimatePlayerCards animatePlayerTask = null;
    DelayDealerHit delayDealerTask = null;
    DelayCheckWinner delayCheckTask = null;
    DelayDialog delayDialogTask = null;

    /**
     * Check accessibility and set flags. Set-up The Android TextToSpeech Engine. Also Set-up
     *  the Google MessageApi Client.
     *
     *  Android Developers Website was used to aid in creating the MessageApi Client. For more info
     *   go there
     *  Source: http://developer.android.com/training/wearables/data-layer/events.html
     * @param savedInstanceState
     *
     * A Tutorial from tutorialspoint.com was used to aid with Android TextToSpeech
     *  Source: http://www.tutorialspoint.com/android/android_text_to_speech.htm
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getActivity();

        /* Check if Accessibility is on to Speak upon certain events */
        am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        isAccessibilityEnabled = am.isEnabled();
        isExploreByTouchEnabled = am.isTouchExplorationEnabled();

        /* Set-up the TTS Api */
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "Language chosen is not supported");
                    } else {
                        if(isAccessibilityEnabled) {
                            if (!first_draw_spoken) {
                                String speak = "You drew " + player_left_card.getCardDescription() +
                                        " and " + player_right_card.getCardDescription() +
                                        "\n  Dealer drew " + dealer_right_card.getCardDescription();

                                convertTextToSpeech(speak);
                                first_draw_spoken = true;
                            }
                        }
                    }
                } else {
                    Log.e("error", "Initialization Failed!");
                }
            }
        });

        Log.d(TAG, "Attempting to connect to Google Api Client");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.d(TAG, "Connected to Google Api Client");

        // Initialize state flags
        button_hit_state = true;
        button_stand_state = true;
        button_hint_state = true;
        button_start_over_state = true;
        first_draw_spoken = false;
    }

    /**
     * Set-up button listeners. Restore the state of the fragment if the state was saved. If no
     *  state to restore then go set-up the game. Finally inflate the layout of the fragment
     * @param inflater LayoutInflater object
     * @param container ViewGroup object
     * @param savedInstanceState Bundle object of saved instances
     * @return View to inflate the layout on the screen
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_play_black_jack_game, container, false);
        // Bind views
        group = (ViewGroup) v.findViewById(R.id.playFragment);

        // Set up the card slots
        dealer_left_slot = (ImageView) v.findViewById(R.id.img_view_dealer_left_card);
        dealer_right_slot = (ImageView) v.findViewById(R.id.img_view_dealer_right_card);
        player_left_slot = (ImageView) v.findViewById(R.id.img_view_player_left_card);
        player_right_slot = (ImageView) v.findViewById(R.id.img_view_player_right_card);

        // Set up the total slots
        dealer_top_total_slot = (ImageView) v.findViewById(R.id.img_view_dealer_top_total);
        dealer_bot_total_slot = (ImageView) v.findViewById(R.id.img_view_dealer_bot_total);
        player_top_total_slot = (ImageView) v.findViewById(R.id.img_view_player_top_total);
        player_bot_total_slot = (ImageView) v.findViewById(R.id.img_view_player_bot_total);

        /* Hit button Listener */
        button_hit = (Button) v.findViewById(R.id.button_hit);
        button_hit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerHits();
            }
        });

        /* Stand button Listener */
        button_stand = (Button) v.findViewById(R.id.button_stand);
        button_stand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerStands();
            }
        });

        /* Start Over buttonListener */
        button_start_over = (Button) v.findViewById(R.id.button_start_over);
        button_start_over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fm = getFragmentManager().beginTransaction();
                fm.replace(R.id.fragment_container, new PlayBlackJackGameFragment());
                fm.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fm.commit();
            }
        });

        /* Hint button Listener */
        button_hint = (Button) v.findViewById(R.id.button_hint);
        button_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               hintDialog();
            }
        });

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            /* Restore Variables and Flags */
            dealer_had_ace = savedInstanceState.getBoolean("DEALER_HAD_ACE");
            player_had_ace = savedInstanceState.getBoolean("PLAYER_HAD_ACE");
            dealer_turn = savedInstanceState.getBoolean("DEALER_TURN");
            player_turn = savedInstanceState.getBoolean("PLAYER_TURN");

            first_draw_spoken = savedInstanceState.getBoolean("IS_FIRST_DRAW_SPOKEN");

            dealer_top_total_value = savedInstanceState.getInt("DEALER_TOP_TOTAL_VALUE");
            dealer_bot_total_value = savedInstanceState.getInt("DEALER_BOT_TOTAL_VALUE");
            player_top_total_value = savedInstanceState.getInt("PLAYER_TOP_TOTAL_VALUE");
            dealer_bot_total_value = savedInstanceState.getInt("PLAYER_BOT_TOTAL_VALUE");

            button_hit_state = savedInstanceState.getBoolean("BUTTON_HIT_STATE");
            button_stand_state = savedInstanceState.getBoolean("BUTTON_STAND_STATE");
            button_hint_state = savedInstanceState.getBoolean("BUTTON_HINT_STATE");

            /* Set button Visibility */
            changeAllButtonStates(button_hit_state, button_stand_state, button_hint_state, true);

            /* Restore Cards */
            curDeck = new Deck(context);
            String d_left, d_right, p_left, p_right;
            boolean dealer_left_exists = savedInstanceState.getBoolean("DEALER_LEFT_EXISTS");

            d_right = savedInstanceState.getString("DEALER_RIGHT_CARD");
            p_left = savedInstanceState.getString("PLAYER_LEFT_CARD");
            p_right = savedInstanceState.getString("PLAYER_RIGHT_CARD");

            dealer_right_card = curDeck.getCard(d_right);
            player_left_card = curDeck.getCard(p_left);
            player_right_card = curDeck.getCard(p_right);

            /* Restore Views and Totals and Description for TalkBack */
            dealer_left_slot.setContentDescription("Dealer left card is hidden until you stand");
            dealer_right_slot.setContentDescription("Dealer right card is " + dealer_right_card.getCardDescription());
            player_left_slot.setContentDescription("Your left card is " + player_left_card.getCardDescription());
            player_right_slot.setContentDescription("Your right card is " + player_right_card.getCardDescription());

            dealer_right_slot.setImageResource(dealer_right_card.getCardDrawable());
            player_left_slot.setImageResource(player_left_card.getCardDrawable());
            player_right_slot.setImageResource(player_right_card.getCardDrawable());

            if (dealer_left_exists) {
                d_left = savedInstanceState.getString("DEALER_LEFT_CARD");
                dealer_left_card = curDeck.getCard(d_left);
                dealer_left_slot.setImageResource(dealer_left_card.getCardDrawable());
                dealer_left_slot.setContentDescription("Dealer left card is " + dealer_left_card.getCardDescription());
            }

            dealer_top_total_slot.setImageResource(giveTotalDrawable(dealer_top_total_value));
            dealer_top_total_slot.setContentDescription("Dealer has a total of" +
                    String.valueOf(dealer_top_total_value));

            player_top_total_slot.setImageResource(giveTotalDrawable(player_top_total_value));
            player_top_total_slot.setContentDescription("You have a total of " +
                    String.valueOf(player_top_total_value));

            if (player_bot_total_value > 0) {
                player_bot_total_slot.setImageResource(giveTotalDrawable(player_bot_total_value));
                player_bot_total_slot.setVisibility(v.VISIBLE);
                player_bot_total_slot.setContentDescription("Because of an ace you have an alternative total of" +
                        String.valueOf(player_bot_total_value));
            } else {
                player_bot_total_slot.setVisibility(v.INVISIBLE);
                player_bot_total_slot.setContentDescription("");
            }

            if (dealer_bot_total_value > 0) {
                dealer_bot_total_slot.setImageResource(giveTotalDrawable(dealer_bot_total_value));
                dealer_bot_total_slot.setVisibility(v.VISIBLE);
                dealer_top_total_slot.setContentDescription("Because of an ace dealer has an alternative total of" +
                        String.valueOf(dealer_bot_total_value));
            } else {
                dealer_bot_total_slot.setVisibility(v.INVISIBLE);
                dealer_top_total_slot.setContentDescription("");
            }
        } else {
            // Start Android Wear App if its connected
            sendMessage(START_WEAR);
            gameSetup();
        }

        // Inflate the layout for this fragment
        return v;
    }

    /**
     * Set-up the game by dealing the player's initial 2 cards. The dealer deals 2 cards with one
     *  card shown on the right and the other hidden in the left.
     *
     * A good explanation from wikipedia (http://en.wikipedia.org/wiki/Blackjack):
     *
     * "The player or players are dealt an initial two-card hand and add together the value of their
     * cards. Face cards (kings, queens, and jacks) are counted as ten points. A player and the
     * dealer can count his or her own ace as 1 point or 11 points. All other cards are counted as
     * the numeric value shown on the card. After receiving their initial two cards, players have
     * the option of getting a "hit", or taking an additional card. In a given round, the player or
     * the dealer wins by having a score of 21 or by having the highest score that is less than 21.
     * Scoring higher than 21 (called "busting" or "going bust") results in a loss. A player may win
     * by having any final score equal to or less than 21 if the dealer busts. If a player holds an
     * ace valued as 11, the hand is called "soft", meaning that the player cannot go bust by taking
     * an additional card; 11 plus the value of any other card will always be less than or equal to
     * 21. Otherwise, the hand is "hard"."
     *
     * See Blackjack rules at the top for more information
     */
    public void gameSetup() {
        // Grab the single dealer card on the right and the two player cards
        curDeck = new Deck(context);
        dealer_right_card = curDeck.getCard(generateRandomCard());
        player_left_card = curDeck.getCard(generateRandomCard());
        player_right_card = curDeck.getCard(generateRandomCard());
        Log.d(TAG, "(gameSetup) Dealer drew " + dealer_right_card.getCardValue());
        Log.d(TAG, "(gameSetup) Player old  " + player_left_card.getCardValue());
        Log.d(TAG, "(gameSetup) Player drew  " + player_right_card.getCardValue());

        // check if the dealer pulled an ace
        if (dealer_right_card.getCardValue() == 1)
        {
            // setup the both total values
            dealer_top_total_value =  dealer_right_card.getCardValue();
            dealer_bot_total_value =  11;
        }
        else
        {
            // setup only the top total
            dealer_top_total_value =  dealer_right_card.getCardValue();
            dealer_bot_total_value = 0;
        }

        Log.d(TAG, "(gameSetup) Dealer Top Total " + dealer_top_total_value);
        Log.d(TAG, "(gameSetup) Dealer Bot Total " + dealer_bot_total_value);


        // Grab initial total(s) for player
        // Left card IS an Ace, right card is NOT an Ace
        if (player_left_card.getCardValue() == 1 && player_right_card.getCardValue() > 1) {
            // Player got Black Jack
            if (player_right_card.getCardValue() == 10) {
                player_top_total_value = 21;
                player_bot_total_value = 0;

                blackJackToast();

                if (dealer_right_card.getCardValue() != 1) {
                    Log.d(TAG, "(gameSetup) Player got Black Jack and dealer isn't showing ace");
                    updateView();
                    // Player hit a black jack and dealer first card isn't an ace
                    finishedDialog(getResources().getString(R.string.player_black_jack),
                            getResources().getString(R.string.player_wins) +
                                    "\nUser had " + player_top_total_value);
                    return;
                }
                else
                // Player Hit black jack, but the dealer might hit one too
                {
                    Log.d(TAG, "(gameSetup) Player got Black Jack but dealer might get one too");
                    updateView();
                    // Player hit a black jack and dealer first card isn't an ace
                    finishedDialog(getResources().getString(R.string.player_black_jack),
                            getResources().getString(R.string.player_wins) +
                                    "\nUser had " + player_top_total_value);
                    return;
                }
            } else {
                // Player didn't get a black jack, set up totals for player top and bottom
                player_top_total_value = player_left_card.getCardValue()
                        + player_right_card.getCardValue();
                player_bot_total_value = 11
                        + player_right_card.getCardValue();
                player_had_ace = true;
            }
        }
        // Left card is NOT an Ace, right card IS an Ace
        else if (player_right_card.getCardValue() == 1 && player_left_card.getCardValue() > 1) {
            // Player got Black Jack
            if (player_left_card.getCardValue() == 10) {
                player_top_total_value = 21;
                player_bot_total_value = 0;

                // Announce player black jack
                blackJackToast();

                if (dealer_right_card.getCardValue() > 1) {
                    Log.d(TAG, "(gameSetup) Player got Black Jack and dealer isn't showing ace");
                    updateView();

                    // Player hit a black jack and dealer first card isn't an ace
                    finishedDialog(getResources().getString(R.string.player_black_jack),
                            getResources().getString(R.string.player_wins) +
                                    "\nUser had " + player_top_total_value);

                    return;
                }
                else
                // Player Hit black jack, but the dealer might hit one too
                {
                    Log.d(TAG, "(gameSetup) Player got Black Jack but dealer might get one too");
                    updateView();
                    // Player hit a black jack and dealer first card isn't an ace
                    finishedDialog(getResources().getString(R.string.player_black_jack),
                            getResources().getString(R.string.player_wins) +
                                    "\nUser had " + player_top_total_value);
                    return;
                }
            }else {
                // Player didn't hit a black jack, just set up the two totals
                player_top_total_value = player_left_card.getCardValue()
                        + player_right_card.getCardValue();
                player_bot_total_value = player_left_card.getCardValue()
                        + 11;
                player_had_ace = true;
            }
        }
        // Both cards are aces
        // Set the top totals ace Ace = 1 + 1, bot as Ace = 1 and Ace =11
        else if (player_right_card.getCardValue() == 1 && player_left_card.getCardValue() == 1) {
            player_top_total_value = player_left_card.getCardValue() + player_right_card.getCardValue();
            player_bot_total_value = player_left_card.getCardValue() + 11;
            player_had_ace = true;
        }
        // Both cards are NOT aces Only deal with the top value
        else if (player_right_card.getCardValue() > 1 && player_left_card.getCardValue() > 1) {
            player_top_total_value = player_left_card.getCardValue()
                    + player_right_card.getCardValue();
            player_bot_total_value = 0;
            player_had_ace = false;
        }

            /* UPDATE THE VIEWS */
            updateView();


        Log.d(TAG, "(gameSetup) Player top total  " + player_top_total_value);
        Log.d(TAG, "(gameSetup) Player bot total  " + player_bot_total_value);

        // Next update view should be a player turn
        player_turn = true;


    }

    /**
     * Invoked when the player hits via the wear or the mobile device. The player is dealt a new
     *  card. It checks whether he got 21 or over. If its 21 then the player has dealt blackjack
     *  so go to dealerSetup as its dealer's turn to reveal if the user indeed wins. If its above
     *  21 then player has busted and the game is ended.
     *
     * The new card dealt goes on the right of the screen. The card previously on the right
     *  goes on the left side of the screen. The card previously on the left is discarded but the
     *  totals are still kept in the middle of the screen.
     *
     * See Blackjack rules at the top for more information
     */
    public void playerHits() {
        Log.d(TAG, "In playerHits");
        // Grab a new card.
        player_left_card = player_right_card;
        player_right_card = curDeck.getCard(generateRandomCard());
        if(isAccessibilityEnabled) {
            convertTextToSpeech("You drew " + player_right_card.getCardDescription());
        }

        // Grab the new total
        player_top_total_value += player_right_card.getCardValue();

        if (player_had_ace) {
            player_bot_total_value += player_right_card.getCardValue();

            // The Ace being 11 will cause the player to bust.
            if (player_bot_total_value > 21) {
                // Hide the player's bot total
                player_bot_total_value = 0;
            } else if (player_top_total_value > 21) {
                // Player busted, player loses
                player_bot_total_value = 0;
                updateView();
                finishedDialog(getResources().getString(R.string.player_loses),
                        getResources().getString(R.string.player_busted) +
                                "\nUser had " + player_top_total_value);
                return;
            }
        } else {

            // Player Hits an ace without having one before
            if (player_right_card.getCardValue() == 1) {
                // This is the first ace the player has gotten
                if (!player_had_ace) {
                    player_had_ace = true;

                    // Check if Ace being 1 or 11 causes the player to bust
                    // Display both total values if the player didn't bust
                    if ((11 + player_top_total_value) <= 21
                            && (1 + player_top_total_value) <= 21) {
                        player_bot_total_value = player_top_total_value + 11;
                    }
                    //  Ace being 11 will cause the player to bust, only show the top value
                    else if ((11 + player_top_total_value) > 21
                            && (1 + player_top_total_value) <= 21) {
                        player_bot_total_value = 0;
                    }
                    // Ace being 1 will cause the player to bust
                    // Player loses
                    else if ((11 + player_top_total_value) > 21
                            && (1 + player_top_total_value) > 21) {
                        player_top_total_value += 1;
                        player_bot_total_value = 0;

                        // Player busted and lost
                        updateView();
                        finishedDialog(getResources().getString(R.string.player_loses),
                                getResources().getString(R.string.player_busted) +
                                        "\nUser had " + player_top_total_value);
                        return;

                    }
                    // Player hit 21 not through a black jack, start the dealer turn to see if
                    // the dealer hit 21
                    else if ((1 + player_top_total_value) == 21
                            || (11 + player_top_total_value) == 21) {
                        // Player got 21 see if dealer can get 21
                        blackJackToast();
                        dealerSetup();
                    }
                }
                // Player still hasn't gotten an ace, only deal with 1 total value
                else {
                    if ((player_top_total_value) == 21) {
                        // Player got 21 see if dealer can get 21
                        blackJackToast();
                        dealerSetup();
                    }
                }
            }
            // Player has not gotten an ace
            // only deal with the top value
            else {

                if (player_top_total_value > 21) {
                    // Player busted
                    // Player loses
                    updateView();
                    finishedDialog(getResources().getString(R.string.player_loses),
                            getResources().getString(R.string.player_busted) +
                                    "\nUser had " + player_top_total_value);
                    return;
                }else if (player_top_total_value == 21) {
                    // Player hit 21
                    blackJackToast();
                    playerStands();
                    return;
                }
            }
        }

        updateView();
    }

    /**
     * Invoked when the player stands. It simply calls dealerSetup indicating that its the dealer's
     *  turn now.
     */
    public void playerStands() {
        Log.d(TAG, "(playerStands) Player is done.");
        dealerSetup();
    }

    /**
     * Invoked when the player stands or player hits 21 not through a blackjack. Its the dealers
     *  turn so the dealer reveals his hidden left card. If player has a "hard" hand the Result
     *  dialog is called indicating the user has won. Else the CheckWinner method is called
     *
     * NOTE: if player has a "hard" hand (Ace and a card with a specific value of 10) they win
     * automatically
     *
     * See Blackjack rules at the top for more information
     */
    public void dealerSetup() {
        Log.d(TAG, "In dealerSetup");
        int final_player_total;

        button_hit_state = false;
        button_stand_state = false;

        dealer_turn = true;
        button_hit.setEnabled(button_hit_state);
        button_stand.setEnabled(button_stand_state);

        // Grab a new card.
        dealer_left_card = dealer_right_card;
        dealer_right_card = curDeck.getCard(generateRandomCard());
        if(isAccessibilityEnabled) {
            convertTextToSpeech("Dealer drew " + dealer_right_card.getCardDescription());
        }

        // Grab the player's highest total
        final_player_total = (player_top_total_value > player_bot_total_value)
                ? player_top_total_value
                : player_bot_total_value;


        // Grab cards for dealer
        // Left card IS an Ace, right card is NOT an Ace
        if (dealer_left_card.getCardValue() == 1 && dealer_right_card.getCardValue() > 1) {
            dealer_top_total_value += dealer_right_card.getCardValue();
            dealer_bot_total_value += dealer_right_card.getCardValue();

            // Dealer's first card was an Ace, Dealer's Second Card is 10
            // Dealer got Black Jack
            if (dealer_right_card.getCardValue() == 10) {
                if (final_player_total == 21) {
                    // Dealer and Player Push
                    // Pop up notification
                    blackJackToast();
                    dealer_top_total_value = 21;
                    dealer_bot_total_value = 0;
                    updateView();
                    finishedDialog(getResources().getString(R.string.player_pushed),
                                    "Dealer had " + dealer_top_total_value +
                                    "\nUser had " + player_top_total_value);
                    return;
                } else {
                    // Dealer got 21 and player has < 21
                    updateView();
                    finishedDialog(getResources().getString(R.string.player_loses),
                                    "Dealer had " + dealer_top_total_value +
                                    "\nUser had " + player_top_total_value);
                    return;
                }
            }
            // Dealer didn't get a black jack
            else {
                dealer_top_total_value = dealer_left_card.getCardValue()
                        + dealer_right_card.getCardValue();
                dealer_bot_total_value = 11
                        + dealer_right_card.getCardValue();
                dealer_had_ace = true;
            }
        }
        // Left card is NOT an Ace, right card IS an Ace
        else if (dealer_right_card.getCardValue() == 1 && dealer_left_card.getCardValue() > 1) {
            dealer_top_total_value += dealer_right_card.getCardValue();
            dealer_bot_total_value = dealer_left_card.getCardValue()
                    + 11;
            // Dealer's first card was 10, second card is an Ace
            // Dealer got Black ShouldJack
            if (dealer_left_card.getCardValue() == 10) {
                dealer_top_total_value = 21;
                dealer_bot_total_value = 0;
                if (final_player_total == 21) {
                    // Dealer and Player Push
                    updateView();
                    finishedDialog(getResources().getString(R.string.player_pushed),
                            "Dealer had " + dealer_bot_total_value +
                                    "\nUser had " + player_top_total_value);
                    return;
                } else {
                    // PLayer has < 21
                    updateView();
                    finishedDialog(getResources().getString(R.string.player_loses),
                            "Dealer had " + dealer_top_total_value +
                                    "\nUser had " + player_top_total_value);
                    return;
                }
            }
            // Dealer didn't get a black jack
            else {
                dealer_had_ace = true;
            }
        }
        // Both cards are aces
        else if (dealer_right_card.getCardValue() == 1 && dealer_left_card.getCardValue() == 1) {
            // Bot value shouldn't be shown since 11 + 11 will bust
            dealer_bot_total_value = 0;
            dealer_had_ace = true;
        }
        // Both cards are NOT aces
        else if (dealer_right_card.getCardValue() > 1 && dealer_left_card.getCardValue() > 1) {
            dealer_top_total_value += dealer_right_card.getCardValue();
            dealer_bot_total_value = 0;
            dealer_had_ace = false;
        }
        updateView();

        checkWinner();
        Log.d(TAG, "(dealerSetup) Dealer drew " + dealer_right_card.getCardValue());
        Log.d(TAG, "(dealerSetup) Top Total " + dealer_top_total_value);
        Log.d(TAG, "(dealerSetup) Bot Total " + dealer_bot_total_value);

    }

    /**
     * Called if the dealer's card total has not exceeded 17 yet. The dealer is dealt a new card.
     *  If the new card brings the dealer's total above 21 he busts and the user is declared the
     *  winner and a the results Dialog is. Otherwise CheckWinner is called.
     *
     * This method calls DelayDealerHit AsyncTask to place a delay of 1.5s before the dealer
     *  can hit so the player can process the new information.
     *
     * NOTE: the logic is housed in the DelayDealerHit AsyncTask postExecute
     *
     * The new card dealt goes on the right of the screen. The card previously on the right
     *  goes on the left side of the screen. The card previously on the left is discarded but the
     *  totals are still kept in the middle of the screen.
     *
     *  See Blackjack rules at the top for more information
     */
    public void dealerHits() {
        delayDealerTask = (DelayDealerHit) new DelayDealerHit().execute();
    }

    /**
     * This method delays the dealer hit by 1.5s so the user can process the prior information
     */
    private class DelayDealerHit extends AsyncTask<Void, Void, Void[]> {
        @Override
        protected void onPreExecute() {

            Log.d(TAG, "In Dealer Hit");
            changeAllButtonStates(false, false, false, false);
        }

        @Override
        protected Void[] doInBackground(Void... params) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected void onPostExecute(Void... params) {
            int highest_dealer_total;
            int highest_player_total;
            // Grab a new card.
            dealer_left_card = dealer_right_card;
            dealer_right_card = curDeck.getCard(generateRandomCard());
            if(isAccessibilityEnabled) {
                convertTextToSpeech("Dealer drew " + dealer_right_card.getCardDescription());
            }

            Log.d(TAG, "(DelayDealerHit) Dealer drew " + dealer_right_card.getCardValue());

            // Grab the player's highest total
            highest_player_total = (player_top_total_value > player_bot_total_value)
                    ? player_top_total_value
                    : player_bot_total_value;

            // Dealer drew an ace
            if (dealer_right_card.getCardValue() == 1) {
                // Dealer had an ace before, need to deal with both values
                if (dealer_had_ace) {
                    // Grab the card totals
                    dealer_top_total_value = dealer_top_total_value + dealer_right_card.getCardValue();
                    dealer_bot_total_value = dealer_bot_total_value + dealer_right_card.getCardValue();

                    // Check if dealer has higher total than player
                    // The Ace being 11 will cause the dealer to bust.
                    if (dealer_bot_total_value > 21
                            && dealer_top_total_value < 21) {
                        // Hide the dealer's bot total
                        dealer_bot_total_value = 0;
                    }

                    // Grab the dealers's highest total
                    highest_dealer_total = (dealer_top_total_value > dealer_bot_total_value)
                            ? dealer_top_total_value
                            : dealer_bot_total_value;

                    if (dealer_top_total_value > 21 && dealer_bot_total_value > 21) {
                        // Dealer Busts, player wins
                        updateView();
                        finishedDialog(getResources().getString(R.string.player_wins),
                                getResources().getString(R.string.dealer_busted) +
                                        "\n\nDealer had " + highest_dealer_total +
                                        "\nUser had " + highest_player_total);
                        return;
                    }
                }
                // Dealer just drew his first ace
                else {
                    dealer_had_ace = true;
                    dealer_bot_total_value = dealer_top_total_value + 11;

                    if (dealer_bot_total_value > 21) {
                        // Hide dealer's bot total
                        dealer_bot_total_value = 0;
                    }

                    dealer_top_total_value += 1;

                    // Grab the dealers's highest total
                    highest_dealer_total = (dealer_top_total_value > dealer_bot_total_value)
                            ? dealer_top_total_value
                            : dealer_bot_total_value;

                    if (dealer_top_total_value > 21) {
                        // Dealer Busts, player wins
                        updateView();
                        finishedDialog(getResources().getString(R.string.player_wins),
                                getResources().getString(R.string.dealer_busted) +
                                        "\n\nDealer had " + highest_dealer_total +
                                        "\nUser had " + highest_player_total);
                        return;
                    }
                }
            }
            // Dealer has never drawn an ace
            // Check if the dealer busted
            else {
                dealer_top_total_value = dealer_top_total_value + dealer_right_card.getCardValue();

                // Grab the dealers's highest total
                highest_dealer_total = (dealer_top_total_value > dealer_bot_total_value)
                        ? dealer_top_total_value
                        : dealer_bot_total_value;

                if (dealer_top_total_value > 21) {
                    // Dealer Busts, player wins
                    updateView();
                    finishedDialog(getResources().getString(R.string.player_wins),
                            getResources().getString(R.string.dealer_busted) +
                                    "\n\nDealer had " + highest_dealer_total +
                                    "\nUser had " + highest_player_total);
                    return;
                }
                else
                {
                    checkWinner();
                }
            }

            Log.d(TAG, "(DelayDealerHit) Top Total " + dealer_top_total_value);
            Log.d(TAG, "(DelayDealerHit) Bot Total " + dealer_bot_total_value);
            updateView();

            changeAllButtonStates(false, false, true, true);
        }
    }

    /**
     * Called to decide if there is a winner. If the Dealer has a total that exceeds the player's
     *  while the player's total is below 17 and dealer is below or equal to 21
     *  then the dealer is declared the winner. If the Dealer has 21 and player has a "soft"
     *  21 then it is a draw. If the Dealer has not exceeded the player's total and is below 17
     *  then the dealer needs to hit and dealerHit is called
     *
     * This method calls DelayCheckWinner AsyncTask to place a delay of 1.5s before a winner can
     *  be declared
     *
     * NOTE: the logic is housed in the DelayCheckWinner AsyncTask postExecute
     *
     * The new card dealt goes on the right of the screen. The card previously on the right
     *  goes on the left side of the screen. The card previously on the left is discarded but the
     *  totals are still kept in the middle of the screen.
     *
     *  See Blackjack rules at the top for more information
     */
    public void checkWinner() {
        delayCheckTask = (DelayCheckWinner) new DelayCheckWinner().execute();
    }

    /**
     * This method delays the Check Winner by 1.5s so the user can process the prior information
     */
    private class DelayCheckWinner extends AsyncTask<Void, Void, Void[]> {
        @Override
        protected void onPreExecute() {

            Log.d(TAG, "In CheckWinner");

            changeAllButtonStates(false, false, false, false);
        }

        @Override
        protected Void[] doInBackground(Void... params) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected void onPostExecute(Void... params) {
            int highest_dealer_total;
            int highest_player_total;


            // Grab the player's highest total
            highest_player_total = (player_top_total_value > player_bot_total_value)
                    ? player_top_total_value
                    : player_bot_total_value;

            // Grab the player's highest total
            highest_dealer_total = (dealer_top_total_value > dealer_bot_total_value)
                    ? dealer_top_total_value
                    : dealer_bot_total_value;

            if (highest_dealer_total > 21) {
                // Dealer busted.
                // Player wins.
                updateView();
                finishedDialog(getResources().getString(R.string.player_wins),
                        getResources().getString(R.string.dealer_busted));
                return;
            } else if (highest_dealer_total <= 17) {
                if (highest_player_total > highest_dealer_total && highest_dealer_total < 17) {
                    dealerHits();
                } else if (highest_player_total < highest_dealer_total) {
                    // Player loses
                    updateView();
                    finishedDialog(getResources().getString(R.string.player_loses),
                            "Dealer had " + highest_dealer_total +
                                    "\nUser had " + highest_player_total);
                    return;
                } else if (highest_player_total > highest_dealer_total) {
                    // Player wins
                    updateView();
                    finishedDialog(getResources().getString(R.string.player_wins),
                            "Dealer had " + highest_dealer_total +
                                    "\nUser had " + highest_player_total);
                    return;
                } else if (highest_dealer_total >= 17 && highest_dealer_total <= 21) {
                    dealerHits();
                }

            } else if (dealer_top_total_value > 17) {
                if (highest_player_total < highest_dealer_total) {
                    // Player Loses
                    updateView();
                    finishedDialog(getResources().getString(R.string.player_loses),
                            "Dealer had " + highest_dealer_total +
                                    "\nUser had " + highest_player_total);
                    return;
                } else if (highest_dealer_total == highest_player_total) {
                    // Player pushes
                    updateView();
                    finishedDialog(getResources().getString(R.string.player_pushed),
                            "Dealer had " + highest_dealer_total +
                                    "\nUser had " + highest_player_total);
                    return;
                } else {
                    // Player wins
                    updateView();
                    finishedDialog(getResources().getString(R.string.player_wins),
                            "Dealer had " + highest_dealer_total +
                                    "\nUser had " + highest_player_total);
                    return;
                }
            }

            changeAllButtonStates(false, false, true, true);
        }
    }

    /**
     * This method updates the cards and totals on the screen. It also sets the appropriate
     *  ContentDescription for android Talk-Back. Any Card Transition needed calls an AsyncTask
     *  to process the card switch animation so it is not jarring to the user
     */
    public void updateView() {
        dealer_top_total_slot.setImageResource(giveTotalDrawable(dealer_top_total_value));
        dealer_top_total_slot.setContentDescription("Dealer has a total of" +
                String.valueOf(dealer_top_total_value));

        player_top_total_slot.setImageResource(giveTotalDrawable(player_top_total_value));
        player_top_total_slot.setContentDescription("You have a total of" +
                String.valueOf(player_top_total_value));

        if (player_bot_total_value > 0) {
            player_bot_total_slot.setImageResource(giveTotalDrawable(player_bot_total_value));
            player_bot_total_slot.setVisibility(v.VISIBLE);
            player_bot_total_slot.setContentDescription("Because of an ace you have an alternative total of" +
                    String.valueOf(player_bot_total_value));
        } else {
            player_bot_total_slot.setVisibility(v.INVISIBLE);
            player_bot_total_slot.setContentDescription("");
        }

        if (dealer_bot_total_value > 0) {
            dealer_bot_total_slot.setImageResource(giveTotalDrawable(dealer_bot_total_value));
            dealer_bot_total_slot.setVisibility(v.VISIBLE);
            dealer_bot_total_slot.setContentDescription("Because of an ace dealer has an alternative total of" +
                    String.valueOf(dealer_bot_total_value));
        } else {
            dealer_bot_total_slot.setVisibility(v.INVISIBLE);
            dealer_bot_total_slot.setContentDescription("");
        }

        // Check if its the dealer's turn, if it is then just animate his card switch
        if (dealer_turn) {
            Log.d(TAG, "Dealers Turn");
            dealer_left_slot.setContentDescription("Dealer left card is " + dealer_left_card.getCardDescription());
            dealer_right_slot.setContentDescription("Dealer right card is " + dealer_right_card.getCardDescription());
            animateDealerTask = (AnimateDealerCards) new AnimateDealerCards().execute(dealer_left_card.getCardDrawable(),
                    dealer_right_card.getCardDrawable());
        } else{
            // Check if this is the first deal/turn or if its just a player "hit"
            // If it is not the first turn then its a player hit, animate the card switching of the player cards only
            if(player_turn){
                Log.d(TAG, "Player Turn True");
                player_left_slot.setContentDescription("Your left card is " + player_left_card.getCardDescription());
                player_right_slot.setContentDescription("Your right card is " + player_right_card.getCardDescription());
                animatePlayerTask = (AnimatePlayerCards) new AnimatePlayerCards().execute(player_left_card.getCardDrawable(),
                        player_right_card.getCardDrawable());
            } else{
                //This is the first deal/turn, animate the change of the player and dealer cards
                Log.d(TAG, "Player Turn False");
                dealer_left_slot.setContentDescription("Dealer left card is hidden until you stand");
                dealer_right_slot.setContentDescription("Dealer right card is " + dealer_right_card.getCardDescription());
                player_left_slot.setContentDescription("Your left card is " + player_left_card.getCardDescription());
                player_right_slot.setContentDescription("Your right card is " + player_right_card.getCardDescription());

                firstTask = (FirstDealAnimation) new FirstDealAnimation().execute(dealer_right_card.getCardDrawable(), player_left_card.getCardDrawable(),
                        player_right_card.getCardDrawable());
            }
        }
    }

    /**
     * This AsyncTask is for the first time Deal animation. The dealer right card and players cards
     * are bought into the screen after .75s
     *
     * AndroidDesignPatterns.com has a tutorial about transitions in android and it helped in
     *  doing the animation in the app.
     * Source: http://www.androiddesignpatterns.com/2014/12/activity-fragment-transitions-in-android-lollipop-part1.html
     */
    private class FirstDealAnimation extends AsyncTask<Integer, Void, Integer[]> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "FirstDealAnimation");
            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(dealer_right_slot, player_left_slot, player_right_slot);

            changeAllButtonStates(false, false, false, false);
        }

        @Override
        protected Integer[] doInBackground(Integer... params) {
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected void onPostExecute(Integer... params) {
            dealer_right_slot.setImageResource(params[0]);
            player_left_slot.setImageResource(params[1]);
            player_right_slot.setImageResource(params[2]);

            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(dealer_right_slot, player_left_slot, player_right_slot);
            //mButton.setEnabled(true);

            changeAllButtonStates(true, true, true, true);
        }
    }

    /**
     * This AsyncTask is to Animate the Dealer's card. The dealer right card and left card are thrown
     *  out of the screen for 1 second and the new dealt card is brought into the screen again on the
     *  right and the prior card on the right goes to the left.
     *
     * The buttons are hidden during the process to avoid any unwanted effects
     *
     * AndroidDesignPatterns.com has a tutorial about transitions in android and it helped in
     *  doing the animation in the app.
     * Source: http://www.androiddesignpatterns.com/2014/12/activity-fragment-transitions-in-android-lollipop-part1.html
     */
    private class AnimateDealerCards extends AsyncTask<Integer, Void, Integer[]> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "AnimateDealerCards");
            changeAllButtonStates(false, false, false, false);
            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(dealer_left_slot, dealer_right_slot);


        }

        @Override
        protected Integer[] doInBackground(Integer... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected void onPostExecute(Integer... params) {
            dealer_left_slot.setImageResource(params[0]);
            dealer_right_slot.setImageResource(params[1]);

            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(dealer_left_slot, dealer_right_slot);
            //mButton.setEnabled(true);
            changeAllButtonStates(false, false, true, true);
        }
    }

    /**
     * This AsyncTask is to Animate the Player's card. The player's right card and left card are thrown
     *  out of the screen for 1 second and the new dealt card is brought into the screen again on the
     *  right and the prior card on the right goes to the left.
     *
     *  The buttons are hidden during the process to avoid any unwanted effects
     *
     * AndroidDesignPatterns.com has a tutorial about transitions in android and it helped in
     *  doing the animation in the app.
     * Source: http://www.androiddesignpatterns.com/2014/12/activity-fragment-transitions-in-android-lollipop-part1.html
     */
    private class AnimatePlayerCards extends AsyncTask<Integer, Void, Integer[]> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "AnimatePlayerCards");
            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(player_left_slot, player_right_slot);

            changeAllButtonStates(false, false, false, false);
        }

        @Override
        protected Integer[] doInBackground(Integer... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected void onPostExecute(Integer... params) {
            player_left_slot.setImageResource(params[0]);
            player_right_slot.setImageResource(params[1]);

            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(player_left_slot, player_right_slot);
            //mButton.setEnabled(true);

            changeAllButtonStates(true, true, true, true);
        }
    }

    /**
     * Toggle the visibilities of view objects (i.e. hide and show a an ImageView).
     * AndroidDesignPatterns.com has a tutorial about transitions in android and it helped in
     *  doing the animation in the app.
     * Source: http://www.androiddesignpatterns.com/2014/12/activity-fragment-transitions-in-android-lollipop-part1.html
     * @param views View objects to toggle the visibility of
     */
    private static void toggleVisibility(View... views) {
        for (View view : views) {
            boolean isVisible = view.getVisibility() == View.VISIBLE;
            view.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        }
    }

    /**
     * Save the state of the application
     * @param savedInstanceState Bundle objects to save instances in
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.getBoolean("DEALER_HAD_ACE", dealer_had_ace);
        savedInstanceState.putBoolean("PLAYER_HAD_ACE", player_had_ace);
        savedInstanceState.putBoolean("DEALER_TURN", dealer_turn);
        savedInstanceState.putBoolean("PLAYER_TURN", player_turn);

        savedInstanceState.putInt("DEALER_TOP_TOTAL_VALUE", dealer_top_total_value);
        savedInstanceState.putInt("DEALER_BOT_TOTAL_VALUE", dealer_bot_total_value);
        savedInstanceState.putInt("PLAYER_TOP_TOTAL_VALUE", player_top_total_value);
        savedInstanceState.putInt("PLAYER_BOT_TOTAL_VALUE", dealer_bot_total_value);

        savedInstanceState.putBoolean("BUTTON_HIT_STATE", button_hit_state);
        savedInstanceState.putBoolean("BUTTON_STAND_STATE", button_stand_state);
        savedInstanceState.putBoolean("BUTTON_HINT_STATE", button_hint_state);

        if(dealer_left_card != null) {
            savedInstanceState.putBoolean("DEALER_LEFT_EXISTS", true);
            savedInstanceState.putString("DEALER_LEFT_CARD", dealer_left_card.getCardKey());
        }
        savedInstanceState.putString("DEALER_RIGHT_CARD", dealer_right_card.getCardKey());
        savedInstanceState.putString("PLAYER_LEFT_CARD", player_left_card.getCardKey());
        savedInstanceState.putString("PLAYER_RIGHT_CARD", player_right_card.getCardKey());

        savedInstanceState.putBoolean("IS_FIRST_DRAW_SPOKEN", first_draw_spoken);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * A pseudo-random card generator to generate a random card key to retrieve from the database
     * @return String of a random card key
     */
    public String generateRandomCard() {
        String RandomCard = null;
        int max;
        int min;

        Random r = new Random();
        // Generate pseudo-random number between 1-13 for cards between 2-10
        //  and Ace  = 1, Jack = 11, Queen = 12, King = 13
        max = 13;
        min = 1;
        int card = r.nextInt((max - min) + 1) + min;

        // Generate pseudo-random number between 1-4 for Suit Type
        // Clubs = 1, Diamonds = 2, Hearts = 3, Spades = 4
        max = 4;
        min = 1;
        int suit = r.nextInt((max - min) + 1) + min;

        switch (suit) {
            case 1:
                RandomCard = String.valueOf(card) + "_of_clubs";
                break;
            case 2:
                RandomCard = String.valueOf(card) + "_of_diamonds";
                break;
            case 3:
                RandomCard = String.valueOf(card) + "_of_hearts";
                break;
            case 4:
                RandomCard = String.valueOf(card) + "_of_spades";
                break;
            default:
                break;
        }

        return RandomCard;
    }

    /**
     * Provide the drawable pointer for a given total to be able to display that image resource on
     *  the screen
     * @param total Integer of a given total number
     * @return Integer of the drawable pointer to in image in the resources/drawable folder
     */
    public int giveTotalDrawable(int total) {
        switch (total) {
            case 1:
                return R.drawable.total_1;
            case 2:
                return R.drawable.total_2;
            case 3:
                return R.drawable.total_3;
            case 4:
                return R.drawable.total_4;
            case 5:
                return R.drawable.total_5;
            case 7:
                return R.drawable.total_7;
            case 8:
                return R.drawable.total_8;
            case 9:
                return R.drawable.total_9;
            case 10:
                return R.drawable.total_10;
            case 11:
                return R.drawable.total_11;
            case 12:
                return R.drawable.total_12;
            case 13:
                return R.drawable.total_13;
            case 14:
                return R.drawable.total_14;
            case 15:
                return R.drawable.total_15;
            case 16:
                return R.drawable.total_16;
            case 17:
                return R.drawable.total_17;
            case 18:
                return R.drawable.total_18;
            case 19:
                return R.drawable.total_19;
            case 20:
                return R.drawable.total_20;
            case 21:
                return R.drawable.total_21;
            case 22:
                return R.drawable.total_22;
            case 23:
                return R.drawable.total_23;
            case 24:
                return R.drawable.total_24;
            case 25:
                return R.drawable.total_25;
            case 26:
                return R.drawable.total_26;
            case 27:
                return R.drawable.total_27;
            case 28:
                return R.drawable.total_28;
            case 29:
                return R.drawable.total_29;
            case 30:
                return R.drawable.total_30;
            default:
                return R.drawable.total_1;
        }
    }

    /**
     * Display a notification to the user of an event. So far has not been used
     * @param eventTitle String of the title of the notification
     * @param eventContent String of the content of the notification
     */
    public void notification(String eventTitle, String eventContent){

        int notificationId = 001;
        // Build intent for notification content
        //Intent viewIntent = new Intent();
        //viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        //PendingIntent viewPendingIntent =
        //        PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setVibrate(new long[]{0, 200})
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(eventTitle)
                        .setContentText(eventContent);
        //.setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getActivity());

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    /**
     * Display a dialog with the result of the game.
     *
     * This method calls DelayDialog AsyncTask to place a delay of .1s before a the dialog is
     * shown to the user
     *
     * NOTE: the logic is housed in the DelayDialog AsyncTask postExecute
     * @param header String of the Dialog Title
     * @param body String of the Dialog Body
     */
    public void finishedDialog(String header, String body) {
        changeAllButtonStates(false, false, false, false);
        delayDialogTask = (DelayDialog) new DelayDialog().execute(header, body);
    }



    private class DelayDialog extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "In finshedDialog");

            changeAllButtonStates(false, false, false, false);
        }


        @Override
        protected String[] doInBackground(String... params) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected void onPostExecute(String... params) {
            String compare = getResources().getString(R.string.player_wins);
            String condition = params[0];
            Log.d(TAG, "Condition: " + condition + " Compare: " + compare);
            //Send result to Android Wear Smart Watch
            if(params[0].equals(getResources().getString(R.string.player_wins)) ||
                    params[0].equals(getResources().getString(R.string.player_black_jack))){
                Log.d(TAG, "Win");
                sendMessage(PLAYER_WINS);
            } else if(params[0].equals(getResources().getString(R.string.player_loses))){
                sendMessage(PLAYER_LOSES);
            } else{
                //Both player and dealer push, send DRAW
                Log.d(TAG, "Draw");
                sendMessage(PLAYER_DRAW);
            }


            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(params[0]);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage(params[1])
                    .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FragmentTransaction fm = getFragmentManager().beginTransaction();
                            fm.replace(R.id.fragment_container, new PlayBlackJackGameFragment());
                            fm.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            //fm.addToBackStack(null);
                            fm.commit();
                        }
                    })
                    .setNegativeButton(R.string.give_up, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = builder.create();

            // show it
            alertDialog.show();

            changeAllButtonStates(false, false, false, true);
        }
    }

    /**
     * Connect to the Google Api Client
     */
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /**
     * Set-up the listener once an Android Wear (smartwatch) device is connected
     * @param bundle Bundle object
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to Google Api Service");
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Connected to Google Api Service");
        }
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    /**
     * Used to clean-up the application when its stopped.
     * Cancel any AsyncTasks that exist, shutdown and remove the TextToSpeech Engine, and disconnect
     *  the MessageApi client and remove its listener
     */
    @Override
    public void onStop() {
        /* Cancel any AsyncTask and remove the Api Client and Text To Speech
        *   client
        */
        if(firstTask != null){
            firstTask.cancel(true);
        }
        if(animateDealerTask != null) {
            animateDealerTask.cancel(true);
        }
        if(animatePlayerTask != null) {
            animatePlayerTask.cancel(true);
        }
        if(delayDealerTask != null) {
            delayDealerTask.cancel(true);
        }
        if(delayCheckTask != null) {
            delayCheckTask.cancel(true);
        }
        if(delayDialogTask != null) {
            delayDialogTask.cancel(true);
        }
        textToSpeech.shutdown();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        super.onStop();
    }

    /**
     * Remove the listener once an Android Wear (smartwatch) device is disconnected
     * @param i Integer
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
    }

    /**
     * Remove the listener once an Android Wear (smartwatch) device connection fails
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
    }

    /**
     * Listener method to receive a Message to a possibly connected Android Wear device using
     * the Wearable Data Layer API, which is part of Google Play service. This application
     * specifically uses the MessageApi.
     *
     * @param messageEvent Contains a String of the received message from a connected Wear Device
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //super.onMessageReceived(messageEvent);
        Log.d(TAG, "In onMessageReceived");
        if("#MESSAGE".equals(messageEvent.getPath())) {
            // launch some Activity or do anything you like
            CharSequence text = "Wear Sent a Message! Hit was Pressed!!! :)";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }  else if("#HIT".equals(messageEvent.getPath())){
            getActivity().runOnUiThread(new Runnable(){
                public void run() {
                    if(button_hit_state){
                        button_hit.performClick();
                    }
                }
            });
        } else if("#STAND".equals(messageEvent.getPath())){
            getActivity().runOnUiThread(new Runnable(){
                public void run() {
                    if(button_stand_state){
                        button_stand.performClick();
                    }
                }
            });
        }
    }

    /**
     * Method to send a Message to a possibly connected Android Wear device using
     * the Wearable Data Layer API, which is part of Google Play service. This application
     * specifically uses the MessageApi.
     *
     * A user from stackoverflow.com explained how to send messaged between Phone and Wear
     * Source: http://stackoverflow.com/questions/24711232/button-click-in-android-wear
     * @param message A string consisting of the message to send to the Android Wear Device
     */
    public void sendMessage(String message){
        final String msg = message;
        if (mGoogleApiClient == null) {
            Log.d(TAG, "Don't send anything, Api Client not initialized");
            return;
        }
        /* Portion below goes through nodes (devices/watches) connected to this device
        *   (The Phone) and if node(s) exist send the message to all
        *   connected nodes
        */
        final PendingResult<NodeApi.GetConnectedNodesResult> nodes
                = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                final List<Node> nodes = result.getNodes();
                if (nodes != null) {
                    Log.d(TAG, "Going to send message");
                    for (int i = 0; i < nodes.size(); i++) {
                        final Node node = nodes.get(i);

                        // You can just send a message
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), msg, null);
                    }
                }
            }
        });
    }

    /**
     * Display a toast that the player got Blackjack
     */
    public void blackJackToast()
    {
        changeAllButtonStates(false, false, false, false);

        CharSequence text = "You have 21!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Display a hint Dialog containing the numerical values on the screen if the player is having
     *  trouble reading the braille characters
     */
    public void hintDialog()
    {
        Log.d(TAG, "In hintDialog");
        StringBuilder sb = new StringBuilder();
        int highest_dealer_total;
        int highest_player_total;

        // Grab the player's highest total
        highest_player_total = (player_top_total_value > player_bot_total_value)
                ? player_top_total_value
                : player_bot_total_value;

        // Grab the player's highest total
        highest_dealer_total = (dealer_top_total_value > dealer_bot_total_value)
                ? dealer_top_total_value
                : dealer_bot_total_value;

        if (dealer_left_card!=null) {
            sb.append("Dealer Left Card: " + dealer_left_card.getCardValue() + "\n");
        }
        sb.append("Dealer Right Card: " + dealer_right_card.getCardValue());
        sb.append("\nDealer Highest Total: " + highest_dealer_total);
        sb.append("\n\nPlayer Left Card: " + player_left_card.getCardValue());
        sb.append("\nPlayer Right Card: " + player_right_card.getCardValue());
        sb.append("\nPlayer Highest Total: " + highest_player_total);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.hint));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage(sb.toString())
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();
    }

    /**
     * Set the state of buttons to hide/show them
     * @param hit_state Boolean of hit button
     * @param stand_state Boolean of stand button
     * @param hint_state Boolean of hint/reveal button
     * @param start_over_state Boolean of start over button
     */
    public void changeAllButtonStates(boolean hit_state, boolean stand_state, boolean hint_state, boolean start_over_state)
    {
        button_hit_state = hit_state;
        button_stand_state = stand_state;
        button_hint_state = hint_state;
        button_start_over_state = start_over_state;

        button_hit.setEnabled(button_hit_state);
        button_stand.setEnabled(button_stand_state);
        button_hint.setEnabled(button_hint_state);
        button_start_over.setEnabled(button_start_over_state);
    }

    /**
     * Speak-out a given string
     * @param text String of text to speak-out
     */
    private void convertTextToSpeech(String text) {
        CharSequence t = text;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(t, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}