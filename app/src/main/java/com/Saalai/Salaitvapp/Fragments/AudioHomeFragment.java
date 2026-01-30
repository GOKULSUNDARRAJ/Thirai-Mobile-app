package com.Saalai.Salaitvapp.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Saalai.Salaitvapp.Adapters.ArtistAdapter;
import com.Saalai.Salaitvapp.Adapters.PlaylistSectionAdapter;
import com.Saalai.Salaitvapp.Models.ArtistCategory;
import com.Saalai.Salaitvapp.Models.AudioModel;
import com.Saalai.Salaitvapp.Models.PlaylistSection;
import com.Saalai.Salaitvapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioHomeFragment extends Fragment implements ArtistAdapter.OnArtistClickListener {

    private RecyclerView sectionsRecyclerView;
    private PlaylistSectionAdapter sectionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_home, container, false);

        sectionsRecyclerView = view.findViewById(R.id.sectionsRecyclerView);
        setupRecyclerView();
        loadData();

        return view;
    }

    private void setupRecyclerView() {
        sectionAdapter = new PlaylistSectionAdapter(new ArrayList<>(), this);
        sectionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionsRecyclerView.setAdapter(sectionAdapter);
    }

    private void loadData() {
        List<PlaylistSection> sections = new ArrayList<>();

        // Latest Releases Section
        List<ArtistCategory> latestReleases = new ArrayList<>();

        // Tamil Romantic Songs category
        List<AudioModel> tamilRomanticSongs = Arrays.asList(
                new AudioModel("Adada Mazhaida",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/Adada-Mazhaida.mp3?alt=media&token=9f3cbc54-bff8-4762-8e52-67d45a7dedf0",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(2).jpeg?alt=media&token=1f8290d2-3c93-4000-8f41-52f2dadaffbb"),
                new AudioModel("En Kadhal Solla",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/En-Kadhal-Solla.mp3?alt=media&token=a6242e73-e73d-43f4-bf62-d738715b0f5d",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(3).jpeg?alt=media&token=ed1386de-16e4-4cba-a4c0-fb3c9e245f0b"),
                new AudioModel("Poongatre Poongatre",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/Poongatre-Poongatre.mp3?alt=media&token=941661d7-f209-46c2-a2a4-bd8e10fff43d",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(4).jpeg?alt=media&token=6b712a64-65be-4eaf-9311-88d9048b5c1d"),
                new AudioModel("Thuli Thuli Mazhaiyaai",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/Thuli-Thuli-Mazhaiyaai.mp3?alt=media&token=0c82b85c-5af0-437f-9203-06fca39d5b14",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(6).jpeg?alt=media&token=630ee223-4385-4ac8-bf75-d7b81aae3e9e")
        );

        // Set artist for romantic songs
        for (AudioModel song : tamilRomanticSongs) {
            song.setAudioArtist("Various Artists");
        }

        // Movie Hits category
        List<AudioModel> movieHits = Arrays.asList(
                new AudioModel("Eppadio Mattikiten",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/Eppadio%20Mattikiten.mp3?alt=media&token=36018104-bac6-452b-ae0b-623cf4a4eda8",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(7).jpeg?alt=media&token=aab77e56-702b-4dbf-b859-ad9d4fbb4218"),
                new AudioModel("Suthuthe Suthuthe Bhoomi",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/Suthuthe-Suthuthe-Bhoomi.mp3?alt=media&token=0ba440ea-6bfe-4192-b2c9-8e488ce6e50c",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(5).jpeg?alt=media&token=5c1e3d77-35de-4132-b3dd-42f57adc7e01"),
                new AudioModel("Yedho Ondru Ennai",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/Yedho-Ondru-Ennai.mp3?alt=media&token=2cedae42-0550-49cf-afd3-8dc7c7b4533b",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/maxresdefault.jpg?alt=media&token=d1eda643-b5f5-49eb-812a-6b13cd4121cc")
        );

        // Set artist for movie hits
        for (AudioModel song : movieHits) {
            song.setAudioArtist("Movie Hits");
        }

        latestReleases.add(new ArtistCategory("Tamil Romantic", tamilRomanticSongs,
                "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(2).jpeg?alt=media&token=1f8290d2-3c93-4000-8f41-52f2dadaffbb", 1));
        latestReleases.add(new ArtistCategory("Movie Hits", movieHits,
                "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(7).jpeg?alt=media&token=aab77e56-702b-4dbf-b859-ad9d4fbb4218", 1));

        sections.add(new PlaylistSection("Latest Releases", latestReleases));

        // Popular Songs Section
        List<ArtistCategory> popularSongs = new ArrayList<>();

        // Emotional Songs category
        List<AudioModel> emotionalSongs = Arrays.asList(
                new AudioModel("Suthuthe Suthuthe Bhoomi",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/Suthuthe-Suthuthe-Bhoomi.mp3?alt=media&token=0ba440ea-6bfe-4192-b2c9-8e488ce6e50c",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(5).jpeg?alt=media&token=5c1e3d77-35de-4132-b3dd-42f57adc7e01"),
                new AudioModel("Yedho Ondru Ennai",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/Yedho-Ondru-Ennai.mp3?alt=media&token=2cedae42-0550-49cf-afd3-8dc7c7b4533b",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/maxresdefault.jpg?alt=media&token=d1eda643-b5f5-49eb-812a-6b13cd4121cc"),
                new AudioModel("Eppadio Mattikiten",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/Eppadio%20Mattikiten.mp3?alt=media&token=36018104-bac6-452b-ae0b-623cf4a4eda8",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(7).jpeg?alt=media&token=aab77e56-702b-4dbf-b859-ad9d4fbb4218")
        );

        // Classic Hits category
        List<AudioModel> classicHits = Arrays.asList(
                new AudioModel("Adada Mazhaida",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/Adada-Mazhaida.mp3?alt=media&token=9f3cbc54-bff8-4762-8e52-67d45a7dedf0",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(2).jpeg?alt=media&token=1f8290d2-3c93-4000-8f41-52f2dadaffbb"),
                new AudioModel("En Kadhal Solla",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/En-Kadhal-Solla.mp3?alt=media&token=a6242e73-e73d-43f4-bf62-d738715b0f5d",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(3).jpeg?alt=media&token=ed1386de-16e4-4cba-a4c0-fb3c9e245f0b"),
                new AudioModel("Thuli Thuli Mazhaiyaai",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/Thuli-Thuli-Mazhaiyaai.mp3?alt=media&token=0c82b85c-5af0-437f-9203-06fca39d5b14",
                        "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(6).jpeg?alt=media&token=630ee223-4385-4ac8-bf75-d7b81aae3e9e")
        );

        // Set artists
        for (AudioModel song : emotionalSongs) {
            song.setAudioArtist("Emotional Hits");
        }
        for (AudioModel song : classicHits) {
            song.setAudioArtist("Classic Favorites");
        }

        popularSongs.add(new ArtistCategory("Emotional Songs", emotionalSongs,
                "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(5).jpeg?alt=media&token=5c1e3d77-35de-4132-b3dd-42f57adc7e01", 2));
        popularSongs.add(new ArtistCategory("Classic Hits", classicHits,
                "https://firebasestorage.googleapis.com/v0/b/drugadmin-236eb.appspot.com/o/download%20(3).jpeg?alt=media&token=ed1386de-16e4-4cba-a4c0-fb3c9e245f0b", 2));

        sections.add(new PlaylistSection("Popular Songs", popularSongs));

        // Update the adapter
        sectionAdapter.updateData(sections);
    }

    @Override
    public void onArtistClick(String artistName, List<AudioModel> songs, String artistImageUrl) {
        // Navigate to AudioFragment with the selected artist's songs
        AudioFragment fragment = new AudioFragment();
        Bundle args = new Bundle();
        args.putString("artist_name", artistName);
        args.putString("artist_image", artistImageUrl);
        args.putSerializable("songs_list", new ArrayList<>(songs));
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

        // Broadcast to update mini player immediately
        broadcastMiniPlayerUpdate();
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

    // Add this to check and play saved audio when fragment becomes visible
    @Override
    public void onResume() {
        super.onResume();

        // Broadcast to update mini player
        broadcastMiniPlayerUpdate();
    }


}