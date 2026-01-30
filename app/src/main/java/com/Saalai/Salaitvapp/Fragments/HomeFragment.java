package com.Saalai.Salaitvapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.Saalai.Salaitvapp.Models.AudioModel;
import com.Saalai.Salaitvapp.PlayerManager;
import com.Saalai.Salaitvapp.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private TextView tabAll, tabMusic, tabDevotion;
    private AudioFragment allFragment;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        setupTabListeners();
        setDefaultTab();

        return view;
    }

    private void initializeViews(View view) {
        tabAll = view.findViewById(R.id.tab_all);
        tabMusic = view.findViewById(R.id.tab_music);
        tabDevotion = view.findViewById(R.id.tab_devotion);

        // Initialize fragments
        allFragment = new AudioFragment();

    }

    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> switchTab(0));
        tabMusic.setOnClickListener(v -> switchTab(1));
        tabDevotion.setOnClickListener(v -> switchTab(2));
    }

    private void setDefaultTab() {
        switchTab(0); // Default to All tab
    }

    private void switchTab(int position) {
        // Reset all tabs to unselected state
        resetTabs();

        Fragment selectedFragment = null;

        switch (position) {
            case 0: // All Tab
                tabAll.setBackgroundResource(R.drawable.tab_background_selected);
                tabAll.setTextColor(getResources().getColor(R.color.white));
                selectedFragment = allFragment;
                break;

            case 1: // Music Tab
                tabMusic.setBackgroundResource(R.drawable.tab_background_selected);
                tabMusic.setTextColor(getResources().getColor(R.color.white));

                break;

            case 2: // Devotion Tab
                tabDevotion.setBackgroundResource(R.drawable.tab_background_selected);
                tabDevotion.setTextColor(getResources().getColor(R.color.white));

                break;
        }

        // Replace fragment
        if (selectedFragment != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.fragment_container, selectedFragment);
            transaction.commit();
        }
    }

    private void resetTabs() {
        // Reset All tab
        tabAll.setBackgroundResource(R.drawable.tab_background_unselected);
        tabAll.setTextColor(getResources().getColor(R.color.gray));

        // Reset Music tab
        tabMusic.setBackgroundResource(R.drawable.tab_background_unselected);
        tabMusic.setTextColor(getResources().getColor(R.color.gray));

        // Reset Devotion tab
        tabDevotion.setBackgroundResource(R.drawable.tab_background_unselected);
        tabDevotion.setTextColor(getResources().getColor(R.color.gray));
    }

    // Add this method to force update mini player
    private void broadcastMiniPlayerUpdate() {
        if (getContext() != null) {
            Intent intent = new Intent("UPDATE_MINI_PLAYER");
            intent.setPackage(getContext().getPackageName());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getContext().sendBroadcast(intent, null); // No permission required for app-scoped broadcasts
            } else {
                getContext().sendBroadcast(intent);
            }
            Log.d("AudioHomeFragment", "Broadcasting mini player update");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check if current song is null and play saved audio
        checkAndPlaySavedAudio();

        // Broadcast to update mini player
        broadcastMiniPlayerUpdate();
    }
    private void checkAndPlaySavedAudio() {
        // Check if no audio is currently playing
        if (PlayerManager.getCurrentAudio() == null) {
            AudioModel savedAudio = loadSavedAudio();
            ArrayList<AudioModel> savedPlaylist = loadFullPlaylist();

            if (savedAudio != null && !savedPlaylist.isEmpty()) {
                // Set the full playlist
                PlayerManager.setAudioList(savedPlaylist);

                // Find the saved audio in the playlist
                AudioModel audioToPlay = findAudioInPlaylist(savedAudio, savedPlaylist);

                if (audioToPlay != null) {
                    // ACTUALLY PLAY THE SAVED AUDIO
                    PlayerManager.playAudio(audioToPlay, () -> {
                        Log.d("AudioHomeFragment", "Saved audio started playing: " + audioToPlay.getAudioName());

                        if (PlayerManager.isPlaying()){
                            PlayerManager.pausePlayback();
                        }
                        broadcastMiniPlayerUpdate();
                    });
                    Log.d("AudioHomeFragment", "Playing saved audio from playlist: " + audioToPlay.getAudioName());
                }
            }
        } else {
            Log.d("AudioHomeFragment", "Audio is already playing: " + PlayerManager.getCurrentAudio().getAudioName());
        }
    }
    private AudioModel loadSavedAudio() {
        SharedPreferences prefs = requireContext().getSharedPreferences("SavedAudio", Context.MODE_PRIVATE);
        String name = prefs.getString("audioName", null);
        String url = prefs.getString("audioUrl", null);
        String image = prefs.getString("imageUrl", null);
        String artist = prefs.getString("audioArtist", null);

        Log.d("SavedAudio", "Loading saved audio - Name: " + name + ", URL: " + url + ", Artist: " + artist);

        if (name != null && url != null) {
            AudioModel savedAudio = new AudioModel(name, url, image);
            savedAudio.setAudioArtist(artist != null ? artist : "Unknown Artist");

            Log.d("SavedAudio", "Successfully loaded: " + savedAudio.getAudioName());
            return savedAudio;
        } else {
            Log.d("SavedAudio", "No saved audio found or incomplete data");
            return null;
        }
    }

    private ArrayList<AudioModel> loadFullPlaylist() {
        ArrayList<AudioModel> playlist = new ArrayList<>();
        SharedPreferences prefs = requireContext().getSharedPreferences("SavedPlaylist", Context.MODE_PRIVATE);

        int playlistSize = prefs.getInt("playlistSize", 0);

        if (playlistSize > 0) {
            for (int i = 0; i < playlistSize; i++) {
                String name = prefs.getString("songName_" + i, null);
                String url = prefs.getString("songUrl_" + i, null);
                String image = prefs.getString("songImage_" + i, null);
                String artist = prefs.getString("songArtist_" + i, null);

                if (name != null && url != null) {
                    AudioModel song = new AudioModel(name, url, image);
                    song.setAudioArtist(artist != null ? artist : "Unknown Artist");
                    playlist.add(song);
                }
            }
            Log.d("AudioHomeFragment", "Loaded full playlist with " + playlist.size() + " songs");
        } else {
            Log.d("AudioHomeFragment", "No saved playlist found");
        }

        return playlist;
    }

    private AudioModel findAudioInPlaylist(AudioModel savedAudio, ArrayList<AudioModel> playlist) {
        for (AudioModel song : playlist) {
            if (song.getAudioUrl().equals(savedAudio.getAudioUrl())) {
                return song;
            }
        }
        return savedAudio; // Return the original saved audio if not found in playlist
    }
}