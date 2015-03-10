package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by halameer on 3/7/2015.
 */
public class ListenerService extends WearableListenerService {

    /**
     * Handle messages received from a device through the Google Messaging API
     *
     * @param messageEvent contains message of which event to trigger
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if("#MESSAGE".equals(messageEvent.getPath())) {
            // launch some Activity or do anything you like
            Context context = getApplicationContext();
            CharSequence text = "Phone Sent a Message! :)";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] vibrationPattern = {0, 1500, 0, 0};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
        } else if("#START".equals(messageEvent.getPath())){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }

}