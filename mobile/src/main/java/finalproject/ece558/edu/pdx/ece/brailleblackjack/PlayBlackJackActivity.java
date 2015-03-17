package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Demonstrates a "screen-slide" animation using a {@link android.support.v4.view.ViewPager}. Because {@link android.support.v4.view.ViewPager}
 * automatically plays such an animation when calling {@link android.support.v4.view.ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see finalproject.ece558.edu.pdx.ece.brailleblackjack.LearnBrailleFragment
 */
public class PlayBlackJackActivity extends Activity {
    private static final String TAG = "PlayBlackJackActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_black_jack);

        // Switch to the PlayBlackJackStartFragment
        if (findViewById(R.id.fragment_container) != null) {
            Log.d(TAG, "In onCreate");
            // If we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            Log.d(TAG, "Launching new fragment");
            // Create an instance PlayblackJackStartFragment
            PlayBlackJackStartFragment startFragment = new PlayBlackJackStartFragment();
            startFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, startFragment).commit();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate back to menu
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
