/*  Braille BlackJack - An android program that aims to teach Braille Numbers is a fun way by playing the
 *   game blackjack
 *
 *   Copyright (C) 2015 Hussein AlAmeer, and Tu Truong
 *
 *   This file is part of Braille BlackJack.
 *
 *   Braille BlackJack is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Braille BlackJack is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
 *
 * ToastDroid.com has a tutorial about how to set-up a MessageApi listener service
 * Source: http://toastdroid.com/2014/08/18/messageapi-simple-conversations-with-android-wear/
 */
public class ListenerService extends WearableListenerService {

    final String TEST_MESSAGE = "#MESSAGE";
    final String START_ACTIVITY_MESSAGE = "#START";
    final String WIN_MESSAGE = "#WIN";
    final String LOSE_MESSAGE = "#LOSE";
    final String DRAW_MESSAGE = "#DRAW";


    /**
     * Handle messages received from a device through the Google Messaging API
     * If the message is simply a start then start the activity
     * If its a win then the Wear vibrates once for a long time
     * If its a loss then the Wear vibrates twice for a short time
     * If its a draw then the Wear vibrates thrice for a very short time
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
            Intent intent = new Intent(this, WearActivity.class);
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

        } else if(DRAW_MESSAGE.equals(messageEvent.getPath())){
            /* A lose should have a two short vibrations */
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] vibrationPattern = {0, 300, 50, 300, 50, 300};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
        }
    }

}