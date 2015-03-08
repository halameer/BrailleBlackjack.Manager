package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by halameer on 3/7/2015.
 */
public class ListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if("/MESSAGE".equals(messageEvent.getPath())) {
            // launch some Activity or do anything you like
            Context context = getApplicationContext();
            CharSequence text = "Wear Sent a Message! Hit was Pressed!!! :)";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}