package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.app.Activity;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class PlayBlackJackGameFragment extends Fragment {

    private ImageView dealer_left_card;
    private ImageView dealer_right_card;
    private ImageView player_left_card;
    private ImageView player_right_card;

    private ImageView dealer_top_total;
    private ImageView dealer_bot_total;
    private ImageView player_top_total;
    private ImageView player_bot_total;

    private int dealer_left_card_num;
    private int dealer_right_card_num;
    private int player_left_card_num;
    private int player_right_card_num;

    private int dealer_top_total_num;
    private int dealer_bot_total_num;
    private int player_top_total_num;
    private int player_bot_total_num;

    private Button button_hit;
    private Button button_stand;

    public ImageView getRandomCard(View view) {
        ImageView img = (ImageView) view.findViewById(R.id.learnBrailImg);
        return img;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_play_black_jack_game, container, false);
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            dealer_left_card = getRandomCard(v);
            dealer_right_card = getRandomCard(v);
            player_left_card = getRandomCard(v);
            player_right_card = getRandomCard(v);

            retrieveDealerTotal();
            retrievePlayerTotal();
        } else {

        }

        // Inflate the layout for this fragment
        return v;
    }

    public void retrieveDealerTotal() {

    }


    public void retrievePlayerTotal() {

    }


    public void playerHits() {

    }

    public void playerStands() {

    }

    public void getWinner() {

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
