package finalproject.ece558.edu.pdx.ece.brailleblackjack;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
}

