package com.Saalai.Salaitvapp.Notification;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    case KeyEvent.KEYCODE_HEADSETHOOK:
                        // Send play/pause action
                        Intent playPauseIntent = new Intent(context, com.Saalai.Salaitvapp.Notification.RadioPlayerService.class);
                        playPauseIntent.setAction(com.Saalai.Salaitvapp.Notification.RadioPlayerService.ACTION_PLAY);
                        context.startService(playPauseIntent);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        Intent nextIntent = new Intent(context, com.Saalai.Salaitvapp.Notification.RadioPlayerService.class);
                        nextIntent.setAction(com.Saalai.Salaitvapp.Notification.RadioPlayerService.ACTION_NEXT);
                        context.startService(nextIntent);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        Intent prevIntent = new Intent(context, com.Saalai.Salaitvapp.Notification.RadioPlayerService.class);
                        prevIntent.setAction(com.Saalai.Salaitvapp.Notification.RadioPlayerService.ACTION_PREVIOUS);
                        context.startService(prevIntent);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_STOP:
                        Intent stopIntent = new Intent(context, com.Saalai.Salaitvapp.Notification.RadioPlayerService.class);
                        stopIntent.setAction(com.Saalai.Salaitvapp.Notification.RadioPlayerService.ACTION_STOP);
                        context.startService(stopIntent);
                        break;
                }
            }
        }
    }
}