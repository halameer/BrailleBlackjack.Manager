package finalproject.ece558.edu.pdx.ece.brailleblackjack;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.Random;

public class MainActivity extends ListActivity {
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

    /**
     * The collection of all Selections in the app. This gets instantiated in {@link
     * #onCreate(android.os.Bundle)} because the {@link Selection} constructor needs access to {@link
     * android.content.res.Resources}.
     */
    private static Selection[] mSelections;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* TESTING DATABASE ADD, REMOVE LATER */
        Deck deck = new Deck(this);
        deck.insertCard("1_of_clubs", this.getString(R.string.description_1_clubs), R.drawable.clubs_ace, 1);

        Card card = deck.getCard("1_of_clubs");
        testDialog(card);

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

    /**
     * Display of an AlertDialog to show the current question's answer
     */
    private void testDialog(Card card){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setTitle("CARD");
        alertbox.setCancelable(false);

        alertbox.setIcon(card.getCardDrawable());

        alertbox.setMessage("Card Key: " + card.getCardKey()+"\n"
                          + "Card Description: " + card.getCardDescription()+"\n"
                          + "Card Value: " + card.getCardValue());

        alertbox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertbox.show();
    }
}

