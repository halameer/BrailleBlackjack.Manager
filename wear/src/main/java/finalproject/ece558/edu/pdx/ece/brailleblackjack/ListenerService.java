package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * This Class sets up a Listener Service to receive messages and handle the messages.
 *  This service is declared in the Android Manifest to be able to listen for messages.
 */
public class ListenerService extends WearableListenerService {

    final String TEST_MESSAGE = "#MESSAGE";
    final String START_ACTIVITY_MESSAGE = "#START";
    final String WIN_MESSAGE = "#WIN";
    final String LOSE_MESSAGE = "#LOSE";


    /**
     * Handle messages received from a device through the Google Messaging API
     *
     * @param messageEvent contains message of which event to trigger
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        if(TEST_MESSAGE.equals(messageEvent.getPath())) {
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
        } else if(START_ACTIVITY_MESSAGE.equals(messageEvent.getPath())){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if(WIN_MESSAGE.equals(messageEvent.getPath())){
            /* A win should have a single long vibration*/
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] vibrationPattern = {0, 1500, 0, 0};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

        } else if(LOSE_MESSAGE.equals(messageEvent.getPath())){
            /* A lose should have a two short vibrations */
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] vibrationPattern = {0, 500, 100, 500};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

        }
    }

}