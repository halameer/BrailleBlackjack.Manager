package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class MainActivity extends Activity {
    // Logcat tag
    private static final String TAG = "WearActivity";
    private TextView mTextView;
    private GoogleApiClient mGoogleApiClient;
    private Button hitButton;
    private Button standButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Attempting to connect to Google Api Client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
        Log.d(TAG, "Connected to Google Api Client");

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);

                // Hit Button listener
                hitButton = (Button) stub.findViewById(R.id.hit_button);
                hitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "In onClick");
                        if (mGoogleApiClient == null) {
                            return;
                        }
                        Log.d(TAG, "Final statement");
                        final PendingResult<NodeApi.GetConnectedNodesResult> nodes
                                = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
                        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                            @Override
                            public void onResult(NodeApi.GetConnectedNodesResult result) {
                                Log.d(TAG, "Inside onResult");
                                final List<Node> nodes = result.getNodes();
                                if (nodes != null) {
                                    Log.d(TAG, "Going to send message");
                                    for (int i = 0; i < nodes.size(); i++) {
                                        Log.d(TAG, "Sending part: " + i);
                                        final Node node = nodes.get(i);

                                        // You can just send a message
                                        Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/MESSAGE", null);
                                    }
                                }
                            }
                        });

                    }
                });

                // Stand Button listener
                standButton = (Button) stub.findViewById(R.id.stand_button);
                standButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        long[] vibrationPattern = {100, 500, 100, 500};
                        //-1 - don't repeat
                        final int indexInPatternToRepeat = -1;
                        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                    }
                });
            }
        });
    }

}
