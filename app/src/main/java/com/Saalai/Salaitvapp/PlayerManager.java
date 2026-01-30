package com.Saalai.Salaitvapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.Saalai.Salaitvapp.Models.AudioModel;
import com.Saalai.Salaitvapp.Notification.NotificationHelper;

import java.io.IOException;
import java.util.List;

public class PlayerManager {

    private static final String TAG = "PlayerManager";
    private static PlayerManager instance;

    private MediaPlayer mediaPlayer;
    private AudioModel currentAudio;
    private List<AudioModel> audioList;
    private int currentIndex = 0;
    private Context context;

    // Notification helper
    private static NotificationHelper notificationHelper;

    // Listener for UI updates
    public interface OnAudioChangedListener {
        void onAudioChanged(AudioModel newAudio);
    }

    private OnAudioChangedListener listener;

    public void setOnAudioChangedListener(OnAudioChangedListener listener) {
        this.listener = listener;
    }

    private void notifyAudioChanged() {
        if (listener != null && currentAudio != null) {
            listener.onAudioChanged(currentAudio);
        }
    }

    // Private constructor
    private PlayerManager(Context ctx) {
        this.context = ctx.getApplicationContext();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            Log.d(TAG, "Song finished, playing next...");
            playNext(null);
        });

        // Initialize notification helper
        notificationHelper = new NotificationHelper(context);
    }

    public static void init(Context ctx) {
        if (instance == null) {
            instance = new PlayerManager(ctx);
        }
    }

    public static PlayerManager getInstance() {
        if (instance == null) throw new IllegalStateException("PlayerManager not initialized.");
        return instance;
    }

    public static MediaPlayer getPlayer() {
        return getInstance().mediaPlayer;
    }

    public static AudioModel getCurrentAudio() {
        return getInstance().currentAudio;
    }

    public static boolean isPlaying() {
        MediaPlayer mp = getInstance().mediaPlayer;
        return mp != null && mp.isPlaying();
    }

    public static void setAudioList(List<AudioModel> list) {
        PlayerManager manager = getInstance();
        manager.audioList = list;
        Log.d(TAG, "Audio list set, size=" + (list != null ? list.size() : 0));
    }

    // Play audio with optional callback when prepared
    public static void playAudio(AudioModel audio, Runnable onPreparedCallback) {
        PlayerManager manager = getInstance();
        if (audio == null || audio.getAudioUrl() == null) return;

        if (manager.audioList != null) {
            manager.currentIndex = manager.audioList.indexOf(audio);
        }

        manager.startAudio(audio, onPreparedCallback);
    }

    private void startAudio(AudioModel audio, Runnable onPreparedCallback) {
        try {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audio.getAudioUrl());

            // Remove old completion listener
            mediaPlayer.setOnCompletionListener(null);

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                currentAudio = audio;
                notifyAudioChanged();
                broadcastAudioChange();

                // Show notification when audio starts playing
                updateNotification(audio, true);

                // Set completion listener AFTER starting
                mediaPlayer.setOnCompletionListener(m -> {
                    Log.d(TAG, "Song finished, playing next...");
                    playNext(null);
                });

                if (onPreparedCallback != null) onPreparedCallback.run();

                Log.d(TAG, "Started playing: " + audio.getAudioName());
            });

            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to start audio: " + e.getMessage());
        }
    }

    public static void pausePlayback() {
        PlayerManager manager = getInstance();
        MediaPlayer mp = manager.mediaPlayer;
        if (mp != null && mp.isPlaying()) {
            mp.pause();
            // Update notification to show play button
            manager.updateNotification(manager.currentAudio, false);
        }
        manager.broadcastAudioChange();
    }

    public static void resumePlayback() {
        PlayerManager manager = getInstance();
        MediaPlayer mp = manager.mediaPlayer;
        if (mp != null && !mp.isPlaying()) {
            mp.start();
            // Update notification to show pause button
            manager.updateNotification(manager.currentAudio, true);
        }
        manager.broadcastAudioChange();
    }

    public static void stopPlayback() {
        PlayerManager manager = getInstance();
        MediaPlayer mp = manager.mediaPlayer;
        if (mp != null) {
            if (mp.isPlaying()) mp.stop();
            mp.reset();
            manager.currentAudio = null;
            manager.broadcastAudioChange();
            manager.notifyAudioChanged();
            // Cancel notification when playback stops
            stopNotification();
        }
    }

    public static void releasePlayer() {
        PlayerManager manager = getInstance();
        MediaPlayer mp = manager.mediaPlayer;
        if (mp != null) {
            mp.release();
            manager.mediaPlayer = null;
            manager.currentAudio = null;
            // Cancel notification when player is released
            stopNotification();
            instance = null;
            Log.d(TAG, "Player released");
        }
    }

    // Add Runnable callback parameter
    public static void playNext(Runnable onPreparedCallback) {
        PlayerManager manager = getInstance();
        if (manager.audioList == null || manager.audioList.isEmpty()) return;

        manager.currentIndex++;
        if (manager.currentIndex >= manager.audioList.size()) manager.currentIndex = 0;

        AudioModel nextAudio = manager.audioList.get(manager.currentIndex);
        manager.startAudio(nextAudio, onPreparedCallback);
    }

    public static void playPrevious(Runnable onPreparedCallback) {
        PlayerManager manager = getInstance();
        if (manager.audioList == null || manager.audioList.isEmpty()) return;

        manager.currentIndex--;
        if (manager.currentIndex < 0) manager.currentIndex = manager.audioList.size() - 1;

        AudioModel prevAudio = manager.audioList.get(manager.currentIndex);
        manager.startAudio(prevAudio, onPreparedCallback);
    }

    private void broadcastAudioChange() {
        if (context != null) {
            context.sendBroadcast(new Intent("UPDATE_MINI_PLAYER"));
            context.sendBroadcast(new Intent("UPDATE_AUDIO_ADAPTER"));
        }
    }

    // Notification methods
    private void updateNotification(AudioModel audio, boolean isPlaying) {
        if (notificationHelper != null && audio != null) {
            notificationHelper.showNotification(audio, isPlaying);
        }
    }

    public static void updateNotification() {
        PlayerManager manager = getInstance();
        if (manager.currentAudio != null) {
            manager.updateNotification(manager.currentAudio, isPlaying());
        }
    }

    public static void stopNotification() {
        if (notificationHelper != null) {
            notificationHelper.cancelNotification();
        }
    }
}