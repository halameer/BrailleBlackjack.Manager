package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class PlayBlackJackGameFragment extends Fragment {

    private Deck curDeck;

    private ViewGroup group;

    private ImageView dealer_left_slot;
    private ImageView dealer_right_slot;
    private ImageView player_left_slot;
    private ImageView player_right_slot;

    private ImageView dealer_top_total_slot;
    private ImageView dealer_bot_total_slot;
    private ImageView player_top_total_slot;
    private ImageView player_bot_total_slot;

    private int dealer_top_total_value;
    private int dealer_bot_total_value;
    private int player_top_total_value;
    private int player_bot_total_value;

    private Card dealer_left_card;
    private Card dealer_right_card;
    private Card player_left_card;
    private Card player_right_card;

    private boolean dealer_had_ace;
    private boolean player_had_ace;
    private boolean dealers_turn;
    private boolean player_turn;
    private boolean first_time_dealer = true;

    private Button button_hit;
    private Button button_stand;

    private View v;
    private Context context = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_play_black_jack_game, container, false);
        // Bind views
        group = (ViewGroup) v.findViewById(R.id.playFragment);

        dealer_left_slot = (ImageView) v.findViewById(R.id.img_view_dealer_left_card);
        dealer_right_slot = (ImageView) v.findViewById(R.id.img_view_dealer_right_card);
        player_left_slot = (ImageView) v.findViewById(R.id.img_view_player_left_card);
        player_right_slot = (ImageView) v.findViewById(R.id.img_view_player_right_card);

        dealer_top_total_slot = (ImageView) v.findViewById(R.id.img_view_dealer_top_total);
        dealer_bot_total_slot = (ImageView) v.findViewById(R.id.img_view_dealer_bot_total);
        player_top_total_slot = (ImageView) v.findViewById(R.id.img_view_player_top_total);
        player_bot_total_slot = (ImageView) v.findViewById(R.id.img_view_player_bot_total);

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            // Grab the single dealer card on the right and the two player cards
            curDeck = new Deck(context);
            dealer_right_card = curDeck.getCard(generateRandomCard());
            player_left_card = curDeck.getCard(generateRandomCard());
            player_right_card = curDeck.getCard(generateRandomCard());

            dealer_right_slot.setImageDrawable(getResources()
                    .getDrawable(dealer_right_card.getCardDrawable()));
            dealer_right_slot.setContentDescription(dealer_right_card.getCardDescription());

            player_left_slot.setImageDrawable(getResources()
                    .getDrawable(player_left_card.getCardDrawable()));
            player_left_slot.setContentDescription(player_left_card.getCardDescription());

            player_right_slot.setImageDrawable(getResources()
                    .getDrawable(player_right_card.getCardDrawable()));
            player_right_slot.setContentDescription(player_right_card.getCardDescription());

            // Grab initial total(s) for player
            // Left card IS an Ace, right card is NOT an Ace
            if (player_left_card.getCardValue() == 1 && player_right_card.getCardValue() > 1) {
                // Player got Black Jack
                if (player_right_card.getCardValue() == 10) {
                    // End player's turn
                    // Check if Dealer got Black Jack
                } else {
                    player_top_total_value = player_left_card.getCardValue()
                            + player_right_card.getCardValue();
                    player_bot_total_value = 11
                            + player_right_card.getCardValue();
                    player_had_ace = true;
                }
            }
            // Left card is NOT an Ace, right card IS an Ace
            else if (player_right_card.getCardValue() == 1 && player_left_card.getCardValue() > 1) {
                if (player_left_card.getCardValue() == 10) {
                    // End player's turn
                    // Check if Dealer got Black Jack
                } else {
                    player_top_total_value = player_left_card.getCardValue()
                            + player_right_card.getCardValue();
                    player_bot_total_value = 11
                            + player_right_card.getCardValue();
                    player_had_ace = true;
                }
            }
            // Both cards are aces
            else if (player_right_card.getCardValue() == 1 && player_left_card.getCardValue() == 1) {
                player_top_total_value = 2;
                player_bot_total_value = 12;
                player_had_ace = true;
            }
            // Both cards are NOT aces
            else if (player_right_card.getCardValue() > 1 && player_left_card.getCardValue() > 1) {
                player_top_total_value = player_left_card.getCardValue()
                        + player_right_card.getCardValue();
                player_bot_total_value = 0;
                player_had_ace = false;
            }

            /* UPDATE THE VIEWS */
            updateView();
            // Next update view should be a player turn
            player_turn = true;

        } else {

            // save the values and states

        }

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
        // Inflate the layout for this fragment
        return v;
    }

    /*
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_hit:
                playerHits();
                break;
            case R.id.button_stand:
                playerStands();
                break;
            default:
                Log.i("Play Black Jack Game Fragment", "onClick Error: " + view.getId());
                break;
        }
    } */

    public void playerHits() {
        // Grab a new card.
        player_left_card = player_right_card;
        player_right_card = curDeck.getCard(generateRandomCard());

        if (player_had_ace) {
            player_top_total_value = player_top_total_value + player_right_card.getCardValue();
            player_bot_total_value = player_bot_total_value + player_right_card.getCardValue();

            // The Ace being 11 will cause the player to bust.
            if (player_bot_total_value > 21) {
                // Hide the player's bot total
                player_bot_total_value = 0;
            } else if (player_top_total_value > 21) {
                // Player loses.
                // Pop up a notification.
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
                        player_top_total_value += 1;
                    }
                    //  Ace being 11 will cause the player to bust, only show the top value
                    else if ((11 + player_top_total_value) > 21
                            && (1 + player_top_total_value) <= 21) {
                        player_bot_total_value = 0;
                        player_top_total_value += 1;
                    }
                    // Ace being 1 will cause the player to bust, show both totals for 1 and 11
                    // Player loses
                    else if ((11 + player_top_total_value) > 21
                            && (1 + player_top_total_value) > 21) {
                        player_bot_total_value = player_top_total_value + 11;
                        player_top_total_value += 1;

                        // Player busted and lost
                    }
                    // Player hit 21 not through a black jack, start the dealer turn to see if
                    // the dealer hit 21
                    else if ((1 + player_top_total_value) == 21
                            || (11 + player_top_total_value) == 21) {
                        // Player got 21 see if dealer can get 21
                        dealerSetup();
                    }
                }
                // Player still hasn't gotten an ace, only deal with 1 total value
                else {
                    player_top_total_value += 1;

                    if ((player_top_total_value) == 21) {
                        // Player got 21 see if dealer can get 21
                        dealerSetup();
                    }
                }
            }
            // Player has not gotten an ace
            else {
                player_top_total_value += player_right_card.getCardValue();

                if (player_top_total_value > 21) {
                    // Player busted
                    // Player loses
                }
            }
        }

        updateView();
    }


    public void playerStands() {
        dealerSetup();
    }


    public void dealerSetup() {
        int final_player_total;

        dealers_turn = true;

        // Implement Delay

        // Grab the player's highest total
        final_player_total = (player_top_total_value > player_bot_total_value)
                ? player_top_total_value
                : player_bot_total_value;

        // Grab a new card.
        dealer_left_card = dealer_right_card;
        dealer_right_card = curDeck.getCard(generateRandomCard());

        // Grab cards for dealer
        // Left card IS an Ace, right card is NOT an Ace
        if (dealer_left_card.getCardValue() == 1 && dealer_right_card.getCardValue() > 1) {
            // Dealer's first card was an Ace, Dealer's Second Card is 10
            // Dealer got Black Jack
            if (dealer_right_card.getCardValue() == 10) {
                if (final_player_total == 21) {
                    // Dealer and Player Push
                    // Pop up notification
                } else {
                    // Player Loses
                    // Pop up notification
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
            // Dealer's first card was 10, second card is an Ace
            // Dealer got Black ShouldJack
            if (dealer_right_card.getCardValue() == 10) {
                if (final_player_total == 21) {
                    // Dealer and Player Push
                    // Pop up notification
                } else {
                    // Player Loses
                    // Pop up notification
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
        // Both cards are aces
        else if (dealer_right_card.getCardValue() == 1 && dealer_left_card.getCardValue() == 1) {
            dealer_top_total_value = 2;
            dealer_bot_total_value = 12;
            dealer_had_ace = true;
        }
        // Both cards are NOT aces
        else if (dealer_right_card.getCardValue() > 1 && dealer_left_card.getCardValue() > 1) {
            dealer_top_total_value = dealer_left_card.getCardValue()
                    + player_right_card.getCardValue();
            dealer_bot_total_value = 0;
            dealer_had_ace = false;
        }


        checkWinner();

        updateView();

    }


    public void dealerHits() {
        int highest_dealer_total;

        // Implement delay


        // Grab a new card.
        dealer_left_card = dealer_right_card;
        dealer_right_card = curDeck.getCard(generateRandomCard());

        // Dealer drew an ace
        if (dealer_right_card.getCardValue() == 1) {
            // Dealer had an ace before, need to deal with both values
            if (dealer_had_ace) {
                // Grab the card totals
                dealer_top_total_value = dealer_top_total_value + dealer_right_card.getCardValue();
                dealer_bot_total_value = dealer_bot_total_value + dealer_right_card.getCardValue();

                // Grab the player's highest total
                highest_dealer_total = (dealer_top_total_value > dealer_bot_total_value)
                        ? dealer_top_total_value
                        : dealer_bot_total_value;

                // Check if dealer has higher total than player
                // The Ace being 11 will cause the dealer to bust.
                if (dealer_bot_total_value > 21
                        && dealer_top_total_value < 21) {
                    // Hide the dealer's bot total
                    dealer_bot_total_value = 0;
                } else if (dealer_top_total_value > 21) {
                    // Dealer Busts, player wins
                    // Display Notification
                }
            }
            // Dealer just drew his first ace
            else {
                dealer_had_ace = true;
                dealer_bot_total_value = dealer_top_total_value + 11;

                if (dealer_bot_total_value > 21) {
                    dealer_bot_total_value = 0;
                }

                dealer_top_total_value += 1;
                if (dealer_top_total_value > 21) {
                    // Dealer Busted
                    // Player Wins
                }
            }
        }
        // Dealer has never drawn an ace
        // Check if the dealer busted
        else {
            dealer_top_total_value = dealer_top_total_value + dealer_right_card.getCardValue();

            if (dealer_top_total_value > 21) {
                // Dealer Busted
                // Player Wins
            }
        }

        updateView();

    }

    public void checkWinner() {
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
            // Player wins.
            // Pop up a notification.
        } else if (highest_dealer_total <= 17) {
            if (highest_player_total > highest_dealer_total) {
                dealerHits();
            } else if (highest_player_total < highest_dealer_total) {
                // Player loses
            } else if (highest_player_total > highest_dealer_total) {
                // Player wins
            }

        } else if (dealer_top_total_value > 17) {
            if (highest_player_total < highest_dealer_total) {
                // Player Loses
            } else {
                // Player wins
            }
        }
    }

    public void threadDelay() {
        // Delay for 3 seconds
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void updateView() {
        dealer_top_total_slot.setImageDrawable(getResources()
                .getDrawable(dealer_top_total_value));

        player_top_total_slot.setImageDrawable(getResources()
                .getDrawable(player_top_total_value));

        if (player_bot_total_value > 0) {
            player_bot_total_slot.setImageDrawable(getResources()
                    .getDrawable(player_bot_total_value));
        } else {
            player_bot_total_slot.setVisibility(v.INVISIBLE);
        }

        if (dealer_bot_total_value > 0) {
            dealer_bot_total_slot.setImageDrawable(getResources()
                    .getDrawable(dealer_bot_total_value));
        } else {
            dealer_bot_total_slot.setVisibility(v.INVISIBLE);
        }

        if (dealers_turn) {
            // Is it the first time for the dealer?
            //  if yes then only reveal the hidden (left card)
            if(first_time_dealer){
                first_time_dealer = false;
                dealer_left_slot.setContentDescription(dealer_left_card.getCardDescription());
                new LeftDealerAnimation().execute(dealer_left_card.getCardDrawable());
            } else{
                dealer_left_slot.setContentDescription(dealer_left_card.getCardDescription());
                dealer_right_slot.setContentDescription(dealer_right_card.getCardDescription());
                new AnimateDealerCards().execute(dealer_left_card.getCardDrawable(),
                        dealer_right_card.getCardDrawable());
            }
        } else{
            // Check if this is the first deal/turn or if its just a player "hit"
            if(player_turn){
                player_left_slot.setContentDescription(player_left_card.getCardDescription());
                player_right_slot.setContentDescription(player_right_card.getCardDescription());
                new AnimatePlayerCards().execute(player_left_card.getCardDrawable(),
                        player_right_card.getCardDrawable());
            } else{
                //This is the first deal/turn
                dealer_right_slot.setContentDescription(dealer_right_card.getCardDescription());
                player_left_slot.setContentDescription(player_left_card.getCardDescription());
                player_right_slot.setContentDescription(player_right_card.getCardDescription());

                new FirstDealAnimation().execute(player_left_card.getCardDrawable(),
                        player_right_card.getCardDrawable());
            }
        }
    }

    private class FirstDealAnimation extends AsyncTask<Integer, Void, Integer[]> {
        @Override
        protected void onPreExecute() {
            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(dealer_right_slot, player_left_slot, player_right_slot);
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
            dealer_right_slot.setImageResource(params[0]);
            player_left_slot.setImageResource(params[1]);
            player_right_slot.setImageResource(params[2]);

            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(dealer_right_slot, player_left_slot, player_right_slot);
            //mButton.setEnabled(true);
        }
    }

    private class LeftDealerAnimation extends AsyncTask<Integer, Void, Integer[]> {
        @Override
        protected void onPreExecute() {
            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(dealer_left_slot);
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
            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(dealer_left_slot);
            //mButton.setEnabled(true);
        }
    }

    private class AnimateDealerCards extends AsyncTask<Integer, Void, Integer[]> {
        @Override
        protected void onPreExecute() {
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
        }
    }

    private class AnimatePlayerCards extends AsyncTask<Integer, Void, Integer[]> {
        @Override
        protected void onPreExecute() {
            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(player_left_slot, player_right_slot);
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
            toggleVisibility(dealer_left_slot, dealer_right_slot, player_left_slot, player_right_slot);
            //mButton.setEnabled(true);
        }
    }

    /**
     * Toggle the visibilities of view objects (i.e. hide and show a an ImageView. Taken from a website that introduces
     *  transition animations on android
     * Source: http://www.androiddesignpatterns.com/2014/12/activity-fragment-transitions-in-android-lollipop-part1.html
     * @param views View objects toggle visibility of
     */
    private static void toggleVisibility(View... views) {
        for (View view : views) {
            boolean isVisible = view.getVisibility() == View.VISIBLE;
            view.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

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
                return -1;
        }
    }
}
