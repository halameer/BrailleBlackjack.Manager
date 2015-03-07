package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class PlayBlackJackGameFragment extends Fragment {

    private Deck curDeck;

    private ImageView dealer_left_slot;
    private ImageView dealer_right_slot;
    private ImageView player_left_slot;
    private ImageView player_right_slot;

    private int dealer_top_total_value;
    private int dealer_bot_total_value;
    private int player_top_total_value;
    private int player_bot_total_value;

    private ImageView dealer_top_total_slot;
    private ImageView dealer_bot_total_slot;
    private ImageView player_top_total_slot;
    private ImageView player_bot_total_slot;

    private Card dealer_left_card;
    private Card dealer_right_card;
    private Card player_left_card;
    private Card player_right_card;

    private boolean dealer_had_ace;
    private boolean player_had_ace;
    private boolean dealer_stands;
    private boolean player_stands;

    private Button button_hit;
    private Button button_stand;

    private Context context = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_play_black_jack_game, container, false);
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {

            // Grab the single dealer card on the right and the two player cards
            curDeck = new Deck(context);
            dealer_left_card = curDeck.getCard(generateRandomCard());
            dealer_right_card = curDeck.getCard(generateRandomCard());
            player_left_card = curDeck.getCard(generateRandomCard());
            player_right_card = curDeck.getCard(generateRandomCard());

            // Grab initial total(s) for player
            // Left card IS an Ace, right card is NOT an Ace
            if (player_left_card.getCardValue() == 1 && player_right_card.getCardValue() > 1){
                    player_top_total_value = player_left_card.getCardValue()
                            + player_right_card.getCardValue();
                    player_bot_total_value = 11
                            + player_right_card.getCardValue();
                    player_had_ace = true;
            }
            // Left card is NOT an Ace, right card IS an Ace
            else if (player_right_card.getCardValue() == 1 && player_left_card.getCardValue() > 1){
                player_top_total_value = player_left_card.getCardValue()
                        + player_right_card.getCardValue();
                player_bot_total_value = 11
                        + player_right_card.getCardValue();
                player_had_ace = true;
            }
            // Both cards are aces
            else if (player_right_card.getCardValue() == 1 && player_left_card.getCardValue() == 1)
            {
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

            button_hit.findViewById(R.id.button_hit);
            button_stand.findViewById(R.id.button_stand);


        } else {

        }

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

        if (player_had_ace){
            player_top_total_value = player_top_total_value + player_right_card.getCardValue();
            player_bot_total_value = player_bot_total_value + player_right_card.getCardValue();

            // The Ace being 11 will cause the player to bust.
            if (player_bot_total_value > 21){
                player_bot_total_value = 0;

                // Hide the player's bot total
                player_bot_total_slot.setVisibility(View.INVISIBLE);
            }
        }
        else{
            player_top_total_value = player_top_total_value + player_right_card.getCardValue();

            if (player_top_total_value > 21){
                // Player loses.
                // Pop up a notification.
            }
        }
    }

    public void playerStands() {
        dealerHits();
    }

    public void dealerHits() {
        int final_player_total;
        int final_dealer_total;

        // Grab the player's highest total
        final_player_total = (player_top_total_value > player_bot_total_value)
                        ? player_top_total_value
                        : player_bot_total_value;

        // Grab the dealer's highest total
        final_dealer_total = (dealer_top_total_value > dealer_bot_total_value)
                ? dealer_top_total_value
                : dealer_bot_total_value;

        // Grab a new card.
        dealer_left_card = dealer_right_card;
        dealer_right_card = curDeck.getCard(generateRandomCard());

        if (dealer_had_ace){
            dealer_top_total_value = dealer_top_total_value + dealer_right_card.getCardValue();
            dealer_bot_total_value = dealer_bot_total_value + dealer_right_card.getCardValue();

            // The Ace being 11 will cause the dealer to bust.
            if (dealer_bot_total_value > 21){
                dealer_bot_total_value = 0;

                // Hide the dealer's bot total
                dealer_bot_total_slot.setVisibility(View.INVISIBLE);
            }
        }
        else{
            dealer_top_total_value = dealer_top_total_value + dealer_right_card.getCardValue();

            if (dealer_top_total_value > 21){
                // Player wins.
                // Pop up a notification.
            }
            else if (dealer_top_total_value <= 17) {
                if (final_player_total < final_dealer_total){
                    dealerHits();
                } else if (final_player_total < final_dealer_total) {
                    // Player loses
                } else if (final_player_total > final_dealer_total) {
                    // Player wins
                }

            }
            else if (dealer_top_total_value > 17) {
                if (final_player_total < final_dealer_total){
                    // Player Loses
                } else {
                    // Player wins
                }
            }
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

    public String generateRandomCard(){
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

        switch (suit){
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
}
