package com.Saalai.Salaitvapp.Notification;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media.session.MediaButtonReceiver;

import com.Saalai.Salaitvapp.Activity.MainActivity;
import com.Saalai.Salaitvapp.Models.RadioModel;
import com.Saalai.Salaitvapp.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class RadioPlayerService extends Service {
    private static final String TAG = "RadioPlayerService";
    private static final String CHANNEL_ID = "radio_player_channel";
    private static final int NOTIFICATION_ID = 101;

    private NotificationManager notificationManager;
    private MediaSessionCompat mediaSession;

    private RadioModel currentRadio;
    private boolean isPlaying = false;

    // Notification actions
    public static final String ACTION_PLAY = "com.Saalai.Salaitvapp.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.Saalai.Salaitvapp.ACTION_PAUSE";
    public static final String ACTION_NEXT = "com.Saalai.Salaitvapp.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.Saalai.Salaitvapp.ACTION_PREVIOUS";
    public static final String ACTION_STOP = "com.Saalai.Salaitvapp.ACTION_STOP";
    public static final String ACTION_CLOSE = "com.Saalai.Salaitvapp.ACTION_CLOSE";

    // Binder for activity/fragment to communicate with service
    public class LocalBinder extends Binder {
        public RadioPlayerService getService() {
            return RadioPlayerService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
        setupMediaSession();

        // Register broadcast receiver for updates from fragment
        IntentFilter filter = new IntentFilter();
        filter.addAction("UPDATE_NOTIFICATION");
        filter.addAction("UPDATE_RADIO_INFO");
        filter.addAction("UPDATE_PLAYBACK_STATE");
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationUpdateReceiver, filter);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Radio Player",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Radio playback controls");
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setSound(null, null); // No sound for notifications
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupMediaSession() {
        try {
            mediaSession = new MediaSessionCompat(this, "RadioPlayer");
            mediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            );

            ComponentName mediaButtonReceiver = new ComponentName(getPackageName(),
                    MediaButtonReceiver.class.getName());
            mediaSession.setMediaButtonReceiver(
                    PendingIntent.getBroadcast(this, 0,
                            new Intent(Intent.ACTION_MEDIA_BUTTON).setComponent(mediaButtonReceiver),
                            PendingIntent.FLAG_IMMUTABLE
                    )
            );

            mediaSession.setCallback(new MediaSessionCompat.Callback() {
                @Override
                public void onPlay() {
                    sendActionToFragment(ACTION_PLAY);
                }

                @Override
                public void onPause() {
                    sendActionToFragment(ACTION_PAUSE);
                }

                @Override
                public void onSkipToNext() {
                    sendActionToFragment(ACTION_NEXT);
                }

                @Override
                public void onSkipToPrevious() {
                    sendActionToFragment(ACTION_PREVIOUS);
                }

                @Override
                public void onStop() {
                    sendActionToFragment(ACTION_STOP);
                }
            });

            mediaSession.setActive(true);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up media session: " + e.getMessage());
        }
    }

    private void sendActionToFragment(String action) {
        Intent intent = new Intent("NOTIFICATION_ACTION");
        intent.putExtra("action", action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void setCurrentRadio(RadioModel radio) {
        this.currentRadio = radio;
        updateNotification();
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
        updateNotification();
    }

    private void updateNotification() {
        Notification notification = buildNotification();
        if (notification != null) {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private Notification buildNotification() {
        if (currentRadio == null) {
            return createDefaultNotification();
        }

        // Create intent for notification tap
        Intent openAppIntent = new Intent(this, MainActivity.class);
        openAppIntent.putExtra("open_radio_player", true);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setContentTitle(currentRadio.getChannelName())
                .setContentText("Radio Station")
                .setContentIntent(contentIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setOngoing(true)
                .setShowWhen(false)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession != null ? mediaSession.getSessionToken() : null)
                        .setShowActionsInCompactView(0, 1, 2))
                .setDeleteIntent(getActionPendingIntent(ACTION_CLOSE))
                .setAutoCancel(false);

        // Add actions based on playback state
        builder.addAction(createAction(R.drawable.baseline_skip_previous_24, "Previous", ACTION_PREVIOUS));

        if (isPlaying) {
            builder.addAction(createAction(R.drawable.ic_pauseicon, "Pause", ACTION_PAUSE));
        } else {
            builder.addAction(createAction(R.drawable.baseline_play_circle_filled_24, "Play", ACTION_PLAY));
        }

        builder.addAction(createAction(R.drawable.baseline_skip_next_24, "Next", ACTION_NEXT));
        builder.addAction(R.drawable.baseline_close_24, "Close", getActionPendingIntent(ACTION_CLOSE));

        // Load large icon asynchronously
        if (currentRadio.getChannelLogo() != null && !currentRadio.getChannelLogo().isEmpty()) {
            try {
                Picasso.get().load(currentRadio.getChannelLogo())
                        .resize(256, 256)
                        .centerCrop()
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                if (bitmap != null) {
                                    builder.setLargeIcon(bitmap);
                                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                // Continue without large icon
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                // Do nothing
                            }
                        });
            } catch (Exception e) {
                Log.e(TAG, "Error loading notification image: " + e.getMessage());
            }
        }

        return builder.build();
    }

    private Notification createDefaultNotification() {
        Intent openAppIntent = new Intent(this, MainActivity.class);
        openAppIntent.putExtra("open_radio_player", true);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setContentTitle("Radio Player")
                .setContentText("Playing radio...")
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();
    }

    private NotificationCompat.Action createAction(int icon, String title, String action) {
        return new NotificationCompat.Action.Builder(
                icon,
                title,
                getActionPendingIntent(action)
        ).build();
    }

    private PendingIntent getActionPendingIntent(String action) {
        Intent intent = new Intent(this, RadioPlayerService.class);
        intent.setAction(action);
        return PendingIntent.getService(
                this,
                getRequestCode(action),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private int getRequestCode(String action) {
        switch (action) {
            case ACTION_PREVIOUS: return 1;
            case ACTION_PLAY: return 2;
            case ACTION_PAUSE: return 3;
            case ACTION_NEXT: return 4;
            case ACTION_CLOSE: return 5;
            default: return 0;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            handleAction(intent.getAction());
        }
        return START_STICKY;
    }

    private void handleAction(String action) {
        sendActionToFragment(action);

        if (ACTION_CLOSE.equals(action) || ACTION_STOP.equals(action)) {
            stopSelf();
        }
    }

    // Broadcast receiver to update notification from fragment
    private BroadcastReceiver notificationUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("UPDATE_NOTIFICATION".equals(intent.getAction())) {
                updateNotification();
            } else if ("UPDATE_RADIO_INFO".equals(intent.getAction())) {
                RadioModel radio = (RadioModel) intent.getSerializableExtra("radio");
                if (radio != null) {
                    setCurrentRadio(radio);
                }
            } else if ("UPDATE_PLAYBACK_STATE".equals(intent.getAction())) {
                boolean playing = intent.getBooleanExtra("isPlaying", false);
                setPlaying(playing);
            }
        }
    };

    public void updateNotificationFromFragment(RadioModel radio, boolean isPlaying) {
        this.currentRadio = radio;
        this.isPlaying = isPlaying;
        updateNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy");

        // Clean up
        if (mediaSession != null) {
            mediaSession.setActive(false);
            mediaSession.release();
        }

        // Unregister receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationUpdateReceiver);

        // Stop foreground and remove notification
        stopForeground(true);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}