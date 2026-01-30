package com.Saalai.Salaitvapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.Saalai.Salaitvapp.Fragments.CatchUpDetailFragment;
import com.Saalai.Salaitvapp.Fragments.MovieVideoPlayerFragment;
import com.Saalai.Salaitvapp.Fragments.RadioPlayerFragment;
import com.Saalai.Salaitvapp.Fragments.TvShowEpisodeFragment;
import com.Saalai.Salaitvapp.Fragments.VideoPlayerFragment;
import com.Saalai.Salaitvapp.Fragments.ViewMoreAllFragment;
import com.Saalai.Salaitvapp.Models.ChildItemAllFragment;
import com.Saalai.Salaitvapp.Models.RadioModel;
import com.Saalai.Salaitvapp.R;
import com.Saalai.Salaitvapp.SubscriptionBottomSheetFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChildAdapterAllFragment extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CONTINUE_WATCHING = 1;
    private static final int TYPE_CHANNEL = 2;
    private static final int TYPE_DEFAULT = 3;

    private List<ChildItemAllFragment> childItemList;
    private boolean isAccountBlocked = false;
    private String blockedMessage = "";
    private Context context;

    // Default total durations for different content types (in seconds)
    private static final int DEFAULT_MOVIE_DURATION = 7200; // 2 hours
    private static final int DEFAULT_TVSHOW_DURATION = 2700; // 45 minutes per episode
    private static final int DEFAULT_CHANNEL_DURATION = 3600; // 1 hour (for recorded content)

    public ChildAdapterAllFragment(List<ChildItemAllFragment> childItemList) {
        this.childItemList = childItemList;
    }

    // Add this method to set account blocked status
    public void setAccountBlocked(boolean isBlocked, String message) {
        this.isAccountBlocked = isBlocked;
        this.blockedMessage = message;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        ChildItemAllFragment item = childItemList.get(position);
        String type = item.getType();
        String playedTime = item.getPlayedDuration();

        // Check if it's a continue watching item
        if (playedTime != null && !playedTime.isEmpty() && !playedTime.equals("00:00:00") && !playedTime.equals("0:00:00")) {
            return TYPE_CONTINUE_WATCHING;
        }

        if ("Channels".equalsIgnoreCase(type) ||
                "Radio".equalsIgnoreCase(type) ||
                "TVShows".equalsIgnoreCase(type)) {
            return TYPE_CHANNEL;
        } else {
            return TYPE_DEFAULT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        if (viewType == TYPE_CONTINUE_WATCHING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_child_continue_watching, parent, false);
            return new ContinueWatchingViewHolder(view);
        } else if (viewType == TYPE_CHANNEL) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_channel_design, parent, false);
            return new ChannelViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_child_all_fragment, parent, false);
            return new ChildViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChildItemAllFragment item = childItemList.get(position);

        if (holder instanceof ContinueWatchingViewHolder) {
            ContinueWatchingViewHolder continueHolder = (ContinueWatchingViewHolder) holder;
            continueHolder.posterTitle.setText(item.getName());

            // Load image
            Picasso.get()
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.video_placholder)
                    .error(R.drawable.video_placholder)
                    .into(continueHolder.posterImage);

            // Set played time and progress
            String playedTime = item.getPlayedDuration();
            if (playedTime != null && !playedTime.isEmpty() && !playedTime.equals("00:00:00") && !playedTime.equals("0:00:00")) {
                // Calculate progress percentage
                int progress = calculateProgressPercentage(item.getType(), playedTime);

                // Set progress bar
                continueHolder.progressBar.setProgress(progress);


                continueHolder.playedTimeText.setText(item.getChannelDuration());
                continueHolder.playedTimeText.setVisibility(View.VISIBLE);

                // Show progress percentage text
                if (continueHolder.progressText != null) {
                    continueHolder.progressText.setText(progress + "%");
                    continueHolder.progressText.setVisibility(View.VISIBLE);
                }

                // Show resume icon if progress between 5% and 95%
                if (progress >= 5 && progress <= 95) {
                    continueHolder.resumeIcon.setVisibility(View.VISIBLE);
                    continueHolder.resumeIcon.setAlpha(1.0f);
                } else if (progress > 95) {
                    // Almost finished - maybe show different icon or hide
                    continueHolder.resumeIcon.setVisibility(View.GONE);
                } else {
                    // Just started
                    continueHolder.resumeIcon.setVisibility(View.GONE);
                }

                Log.d("ProgressDebug", "Item: " + item.getName() +
                        ", Raw Time: " + playedTime +
                        ", Progress: " + progress + "%");
            } else {
                // No progress or just started
                continueHolder.playedTimeText.setVisibility(View.GONE);
                continueHolder.progressBar.setProgress(0);
                continueHolder.resumeIcon.setVisibility(View.GONE);
                if (continueHolder.progressText != null) {
                    continueHolder.progressText.setVisibility(View.GONE);
                }
            }

            continueHolder.itemView.setOnClickListener(v -> {
                handleItemClick(item, v, TYPE_CONTINUE_WATCHING);
            });

        } else if (holder instanceof ChannelViewHolder) {
            ChannelViewHolder channelHolder = (ChannelViewHolder) holder;
            channelHolder.posterTitle.setText(item.getName());
            Picasso.get()
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.video_placholder)
                    .error(R.drawable.video_placholder)
                    .into(channelHolder.posterImage);

            channelHolder.itemView.setOnClickListener(v -> {
                handleItemClick(item, v, TYPE_CHANNEL);
            });

        } else if (holder instanceof ChildViewHolder) {
            ChildViewHolder defaultHolder = (ChildViewHolder) holder;
            defaultHolder.posterTitle.setText(item.getName());
            Picasso.get()
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.movieplaceholder)
                    .error(R.drawable.movieplaceholder)
                    .into(defaultHolder.posterImage);

            defaultHolder.itemView.setOnClickListener(v -> {
                handleItemClick(item, v, TYPE_DEFAULT);
            });
        }
    }

    private void handleItemClick(ChildItemAllFragment item, View v, int viewType) {
        // Check if account is blocked
        if (isAccountBlocked) {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            showAccountBlockedAlert(activity);
            return;
        }

        // If not blocked, proceed with normal navigation
        AppCompatActivity activity = (AppCompatActivity) v.getContext();

        if (viewType == TYPE_CONTINUE_WATCHING) {
            // Handle continue watching item click
            handleContinueWatchingClick(item, activity);
        } else if (viewType == TYPE_CHANNEL) {
            switch (item.getType()) {
                case "Channels":
                    navigateToVideoPlayer(item, activity);
                    break;

                case "Radio":
                    RadioModel radioModel = new RadioModel(
                            item.getChannelId(),
                            item.getName(),
                            item.getImageUrl(),
                            item.getUrl()
                    );
                    RadioPlayerFragment radioPlayerFragment = RadioPlayerFragment.newInstance(radioModel);
                    activity.getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, radioPlayerFragment)
                            .addToBackStack("radio_player")
                            .commit();
                    break;

                case "TVShows":
                    TvShowEpisodeFragment tvShowFragment = TvShowEpisodeFragment.newInstance(String.valueOf(item.getChannelId()));
                    FragmentTransaction tvTransaction = activity.getSupportFragmentManager().beginTransaction();
                    tvTransaction.add(R.id.fragment_container, tvShowFragment);
                    tvTransaction.addToBackStack("tv_show_fragment");
                    tvTransaction.commit();
                    break;
            }
        } else if (viewType == TYPE_DEFAULT) {
            switch (item.getType()) {
                case "more":
                    ViewMoreAllFragment fragment = new ViewMoreAllFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("Title", item.getTitle());
                    bundle.putString("SeeAllType", item.getType());
                    fragment.setArguments(bundle);
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                    break;

                case "Movies":
                    String resumeTime = item.getPlayedDuration();
                    MovieVideoPlayerFragment movieFragment = MovieVideoPlayerFragment.newInstance(
                            String.valueOf(item.getChannelId()),
                            (resumeTime != null && !resumeTime.isEmpty()) ? resumeTime : ""
                    );
                    FragmentTransaction movieTransaction = activity.getSupportFragmentManager().beginTransaction();
                    movieTransaction.replace(R.id.fragment_container, movieFragment);
                    movieTransaction.addToBackStack("movie_player_fragment");
                    movieTransaction.commit();
                    break;

                case "CatchUp":
                    CatchUpDetailFragment catchUpFragment = CatchUpDetailFragment.newInstance(String.valueOf(item.getChannelId()));
                    FragmentTransaction catchUpTransaction = activity.getSupportFragmentManager().beginTransaction();
                    catchUpTransaction.add(R.id.fragment_container, catchUpFragment);
                    catchUpTransaction.addToBackStack("catch_up_fragment");
                    catchUpTransaction.commit();
                    break;

                default:
                    Log.d("ChildAdapter", "Unknown type: " + item.getType());
                    break;
            }
        }
    }

    private void handleContinueWatchingClick(ChildItemAllFragment item, AppCompatActivity activity) {
        String type = item.getType();
        String playedTime = item.getPlayedDuration();

        Log.d("ContinueWatching", "Clicked item: " + item.getName() + ", Type: " + type + ", Time: " + playedTime);
        Log.d("ContinueWatching", "Calculated Progress: " + calculateProgressPercentage(type, playedTime) + "%");

        switch (type) {
            case "Movies":
                // Navigate to movie player with resume position
                MovieVideoPlayerFragment movieFragment = MovieVideoPlayerFragment.newInstance(
                        String.valueOf(item.getChannelId()),
                        playedTime
                );
                FragmentTransaction movieTransaction = activity.getSupportFragmentManager().beginTransaction();
                movieTransaction.replace(R.id.fragment_container, movieFragment);
                movieTransaction.addToBackStack("movie_player_fragment");
                movieTransaction.commit();
                break;

            case "TVShows":
                // Navigate to TV show episode with resume position
                TvShowEpisodeFragment tvShowFragment = TvShowEpisodeFragment.newInstance(
                        String.valueOf(item.getChannelId())
                );
                FragmentTransaction tvTransaction = activity.getSupportFragmentManager().beginTransaction();
                tvTransaction.add(R.id.fragment_container, tvShowFragment);
                tvTransaction.addToBackStack("tv_show_fragment");
                tvTransaction.commit();
                break;

            case "Channels":
                // For channels, you might want to store the last position
                VideoPlayerFragment videoPlayerFragment = VideoPlayerFragment.newInstance(
                        item.getUrl(),
                        item.getName()
                );
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container, videoPlayerFragment);
                transaction.addToBackStack("video_player");
                transaction.commit();
                break;

            default:
                // Fallback to regular navigation
                if ("Movies".equalsIgnoreCase(type)) {
                    MovieVideoPlayerFragment fallbackMovieFragment = MovieVideoPlayerFragment.newInstance(
                            String.valueOf(item.getChannelId()),
                            playedTime);
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, fallbackMovieFragment);
                    ft.addToBackStack("movie_player_fragment");
                    ft.commit();
                }
                break;
        }
    }

    private void showAccountBlockedAlert(AppCompatActivity activity) {
        if (context == null) return;

        SubscriptionBottomSheetFragment bottomSheetFragment = new SubscriptionBottomSheetFragment();
        bottomSheetFragment.show(activity.getSupportFragmentManager(), "MenuBottomSheet");
    }

    private void navigateToVideoPlayer(ChildItemAllFragment channel, AppCompatActivity activity) {
        Log.d("ChildAdapter", "Navigating to video player: " + channel.getName());

        VideoPlayerFragment videoPlayerFragment = VideoPlayerFragment.newInstance(
                channel.getUrl(),
                channel.getName()
        );

        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, videoPlayerFragment);
        transaction.addToBackStack("video_player");
        transaction.commit();
    }

    // Helper method to format played time for display
    private String formatPlayedTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) return "";

        try {
            // If it's a small number (0-100), treat as percentage
            if (timeString.matches("\\d+")) {
                int value = Integer.parseInt(timeString);
                if (value >= 0 && value <= 100) {
                    return value + "%";
                } else {
                    // If it's larger than 100, treat as seconds
                    return formatSeconds(value);
                }
            }

            // If it contains colon, format HH:MM:SS
            if (timeString.contains(":")) {
                String[] parts = timeString.split(":");
                if (parts.length == 3) {
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1]);
                    int seconds = Integer.parseInt(parts[2]);

                    if (hours > 0) {
                        return String.format("%dh %dm", hours, minutes);
                    } else if (minutes > 0) {
                        return String.format("%dm %ds", minutes, seconds);
                    } else if (seconds > 0) {
                        return String.format("%ds", seconds);
                    } else {
                        return "Just started";
                    }
                }
            }

            // Try to parse as seconds if it's just a number
            if (timeString.matches("\\d+")) {
                int seconds = Integer.parseInt(timeString);
                return formatSeconds(seconds);
            }

        } catch (Exception e) {
            Log.e("TimeFormat", "Error formatting time: " + timeString, e);
        }

        return timeString;
    }

    // Helper method to format seconds to readable time
    private String formatSeconds(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    // Calculate progress percentage based on content type and time string
    private int calculateProgressPercentage(String contentType, String timeString) {
        if (timeString == null || timeString.isEmpty()) return 0;

        try {
            // If it's a small number (0-100), treat as percentage directly
            if (timeString.matches("\\d+")) {
                int value = Integer.parseInt(timeString);
                if (value >= 0 && value <= 100) {
                    return value;
                }
            }

            // Parse time string to seconds
            int watchedSeconds = parseTimeStringToSeconds(timeString);
            if (watchedSeconds == -1) {
                // Could not parse as time, check if it's a percentage
                if (timeString.matches("\\d+")) {
                    int value = Integer.parseInt(timeString);
                    // If value is > 100, assume it's seconds
                    if (value > 100) {
                        watchedSeconds = value;
                    } else {
                        return value; // It's a percentage
                    }
                } else {
                    return 0;
                }
            }

            // Get total duration based on content type
            int totalDuration = getTotalDurationForContentType(contentType);

            // Calculate percentage
            if (totalDuration > 0 && watchedSeconds > 0) {
                int progress = (int) ((watchedSeconds * 100.0) / totalDuration);
                return Math.min(Math.max(progress, 0), 100);
            }

        } catch (Exception e) {
            Log.e("ProgressCalc", "Error calculating progress for: " + timeString, e);
        }

        return 0;
    }

    // Parse time string to seconds (handles HH:MM:SS and plain seconds)
    private int parseTimeStringToSeconds(String timeString) {
        if (timeString == null || timeString.isEmpty()) return -1;

        try {
            // Check if it's in HH:MM:SS format
            if (timeString.contains(":")) {
                String[] parts = timeString.split(":");
                if (parts.length == 3) {
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1]);
                    int seconds = Integer.parseInt(parts[2]);
                    return hours * 3600 + minutes * 60 + seconds;
                } else if (parts.length == 2) {
                    // MM:SS format
                    int minutes = Integer.parseInt(parts[0]);
                    int seconds = Integer.parseInt(parts[1]);
                    return minutes * 60 + seconds;
                }
            }

            // Check if it's just a number (seconds)
            if (timeString.matches("\\d+")) {
                return Integer.parseInt(timeString);
            }

        } catch (Exception e) {
            Log.e("TimeParse", "Error parsing time string: " + timeString, e);
        }

        return -1; // Could not parse
    }

    // Get total duration based on content type
    private int getTotalDurationForContentType(String contentType) {
        if (contentType == null) return DEFAULT_MOVIE_DURATION;

        switch (contentType) {
            case "Movies":
                return DEFAULT_MOVIE_DURATION; // 2 hours
            case "TVShows":
                return DEFAULT_TVSHOW_DURATION; // 45 minutes
            case "Channels":
                return DEFAULT_CHANNEL_DURATION; // 1 hour
            default:
                return DEFAULT_MOVIE_DURATION; // Default
        }
    }

    @Override
    public int getItemCount() {
        return childItemList == null ? 0 : childItemList.size();
    }

    // Method to update data
    public void updateData(List<ChildItemAllFragment> newList) {
        childItemList.clear();
        childItemList.addAll(newList);
        notifyDataSetChanged();
    }

    // Method to clear all data
    public void clearData() {
        childItemList.clear();
        notifyDataSetChanged();
    }

    // ViewHolder for Continue Watching items
    static class ContinueWatchingViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        TextView posterTitle;
        TextView playedTimeText;
        TextView progressText; // Optional: to show percentage text
        ProgressBar progressBar;
        ImageView resumeIcon;

        public ContinueWatchingViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.posterImage);
            posterTitle = itemView.findViewById(R.id.posterTitle);
            playedTimeText = itemView.findViewById(R.id.playedTimeText);
            progressBar = itemView.findViewById(R.id.progressBar);
            resumeIcon = itemView.findViewById(R.id.resumeIcon);


        }
    }

    // ViewHolder for Channel items
    static class ChannelViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        TextView posterTitle;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.posterImage);
            posterTitle = itemView.findViewById(R.id.posterTitle);
        }
    }

    // ViewHolder for Default items
    static class ChildViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        TextView posterTitle;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.posterImage);
            posterTitle = itemView.findViewById(R.id.posterTitle);
        }
    }
}