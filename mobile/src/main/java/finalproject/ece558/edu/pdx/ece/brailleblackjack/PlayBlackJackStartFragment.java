package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PlayBlackJackStartFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
        }

        View v = inflater.inflate(R.layout.fragment_play_black_jack_start, container, false);
                /* Hit button Listener */
        Button start_black_jack = (Button)v.findViewById(R.id.lpBlackJack);
        start_black_jack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fm = getFragmentManager().beginTransaction();
                fm.replace(R.id.fragment_container, new PlayBlackJackStartFragment());
                fm.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fm.addToBackStack(null);
                fm.commit();

            }
        });


        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
