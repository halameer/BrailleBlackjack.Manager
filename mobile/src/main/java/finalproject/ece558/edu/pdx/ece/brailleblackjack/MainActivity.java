package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On first visit
        if (savedInstanceState == null) {
            switchFragment(Tracker.HOME_FRAGMENT);
        } else {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }


    /**
     *  Handles Switching between fragments
     *
     *  @param whichFragment - The fragment ID to switch to
     */
    public void switchFragment(int whichFragment) {
        // Replace whatever is in the fragment_container view with this fragment
        Fragment fragment = null;

        switch (whichFragment) {
            // Open the Home fragment
            case Tracker.HOME_FRAGMENT:
                Tracker.curFragment = Tracker.HOME_FRAGMENT;
                Log.d("MainActivity", "Case Hit Home Switch");
                fragment = new Home();
                break;

            // Open the Teaching fragment
            case Tracker.TEACHING_FRAGMENT:
                Tracker.curFragment = Tracker.TEACHING_FRAGMENT;
                Log.d("MainActivity", "Case Hit Teaching Switch");
                fragment = new Teaching();
                break;

            // Open the BlackJack fragment
            case Tracker.BLACKJACK_FRAGMENT:
                Tracker.curFragment = Tracker.BLACKJACK_FRAGMENT;
                Log.d("MainActivity", "Case Hit BlackJack Switch");
                fragment = new BlackJack();
                break;

            // Open the About fragment
            case Tracker.ABOUT_FRAGMENT:
                Tracker.curFragment = Tracker.ABOUT_FRAGMENT;
                Log.d("MainActivity", "Case Hit About Switch");
                fragment = new About();
                break;

            // Something wrong happened, reopen Home fragment
            default: // Switch to Home Screen
                Tracker.curFragment = Tracker.HOME_FRAGMENT;
                Log.e("MainActivity", "CASE HIT DEFAULT!!! Opening Start Fragment");
                fragment = new Home();
                break;
        }


        // Handle the opening of the new fragment specified in the switch statement
        if (fragment != null) {
            Log.d("MainActivity", "Switching Fragments");
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();

            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            // Error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
}
