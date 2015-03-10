package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demonstrates a "screen-slide" animation using a {@link android.support.v4.view.ViewPager}. Because {@link android.support.v4.view.ViewPager}
 * automatically plays such an animation when calling {@link android.support.v4.view.ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see LearnBrailleFragment
 */
public class AboutActivity extends Activity  implements
        MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "WearActivity";
    private GoogleApiClient mGoogleApiClient;
    private Button mButton;
    private ImageView mCard;
    private ImageView mCard2;
    private ViewGroup group;
    private View view;
    final Context context = this;
    final ReentrantLock lock = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Log.d(TAG, "Attempting to connect to Google Api Client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.d(TAG, "Connected to Google Api Client");

        //Talk to watch, start its app
        sendMessage("#START");
        group = (ViewGroup) findViewById(R.id.relativeLayout);

        mCard = (ImageView) findViewById(R.id.cardImage);
        mCard2 = (ImageView) findViewById(R.id.cardImage2);
        mButton = (Button) findViewById(R.id.cardButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("#MESSAGE");
                Log.d(TAG, "Lock button until animation done");
                // Make button non-pressable pending animation
                mButton.setEnabled(false);
                /* Testing Database */
                // Create a new deck database
                Deck deck = new Deck(context);
                Card card = deck.getCard(generateRandomCard());
                Card card2 = deck.getCard(generateRandomCard());
                new MyTask().execute(card.getCardDrawable(), card2.getCardDrawable());
            }
        });


        //testDialog(card);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to Google Api Service");
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Connected to Google Api Service");
        }
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //super.onMessageReceived(messageEvent);
        Log.d(TAG, "In onMessageReceived");
        if("/MESSAGE".equals(messageEvent.getPath())) {
            // launch some Activity or do anything you like
            Context context = getApplicationContext();
            CharSequence text = "Wear Sent a Message! Hit was Pressed!!! :)";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }  else if("#HIT".equals(messageEvent.getPath())){
            runOnUiThread(new Runnable(){
                public void run() {
                    mButton.performClick();
                }
            });
        }
    }


    private class MyTask extends AsyncTask<Integer, Void, Integer[]> {
        @Override
        protected void onPreExecute() {
            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(mCard, mCard2);
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
            mCard.setImageResource(params[0]);
            mCard2.setImageResource(params[1]);
            TransitionManager.beginDelayedTransition(group, new Explode());
            toggleVisibility(mCard, mCard2);
            mButton.setEnabled(true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void sendMessage(String message){
        final String msg = message;
        if (mGoogleApiClient == null) {
            Log.d(TAG, "Don't send anything, no wear exists");
            return;
        }
        Log.d(TAG, "Final statement");
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
}
