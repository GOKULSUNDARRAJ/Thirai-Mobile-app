package com.Saalai.Salaitvapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Saalai.Salaitvapp.Activity.MainActivity;
import com.Saalai.Salaitvapp.ClickInterface.TabNavigationListener;
import com.Saalai.Salaitvapp.Fragments.MoreLastesMovieFragment;
import com.Saalai.Salaitvapp.Fragments.MoreLatestTvShowListFragment;
import com.Saalai.Salaitvapp.Fragments.RadioFragment;
import com.Saalai.Salaitvapp.Models.ParentItemAllFragment;
import com.Saalai.Salaitvapp.R;

import java.util.List;

public class ParentAdapterAllFragment extends RecyclerView.Adapter<ParentAdapterAllFragment.ParentViewHolder> {

    private List<ParentItemAllFragment> parentItemList;
    private TabNavigationListener tabNavigationListener;
    private AppCompatActivity activity;
    private boolean isAccountBlocked = false;
    private String blockedMessage = "";

    public ParentAdapterAllFragment(List<ParentItemAllFragment> parentItemList, TabNavigationListener listener, AppCompatActivity activity) {
        this.parentItemList = parentItemList;
        this.tabNavigationListener = listener;
        this.activity = activity;
    }

    // Add this method to update account blocked status
    public void setAccountBlocked(boolean isBlocked, String message) {
        this.isAccountBlocked = isBlocked;
        this.blockedMessage = message;
        notifyDataSetChanged(); // Refresh to pass to child adapters
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent_all_fragment, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        ParentItemAllFragment parentItem = parentItemList.get(position);
        holder.sectionTitle.setText(parentItem.getSectionTitle());

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.childRecyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        holder.childRecyclerView.setLayoutManager(layoutManager);

        // Create child adapter and set account blocked status
        ChildAdapterAllFragment childAdapter = new ChildAdapterAllFragment(parentItem.getChildItemList());
        childAdapter.setAccountBlocked(isAccountBlocked, blockedMessage); // Pass the blocked status to child adapter only
        holder.childRecyclerView.setAdapter(childAdapter);

        // Show "See All" button only if there are items
        if (parentItem.getChildItemList() != null && !parentItem.getChildItemList().isEmpty()) {
            holder.sellall.setVisibility(View.VISIBLE);
            holder.sellall.setText("See All");
        } else {
            holder.sellall.setVisibility(View.GONE);
        }

        String type = parentItem.getType();

        if (type.contains("ContinueWatching")){
            holder.sellall.setVisibility(View.GONE);
        }else {
            holder.sellall.setVisibility(View.VISIBLE);
        }

        // Updated sellall click listener
        holder.sellall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NO account blocked check here - let "See All" work normally
                String type = parentItem.getType();

                switch (type) {
                    case "Channels":
                        if (tabNavigationListener != null) {
                            tabNavigationListener.navigateToTab(1);
                        }
                        break;

                    case "Movies":
                        MoreLastesMovieFragment movieFragment = new MoreLastesMovieFragment();
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_container, movieFragment)
                                .addToBackStack("more_movies")
                                .commit();
                        break;

                    case "TVShows":
                        MoreLatestTvShowListFragment tvShowFragment = new MoreLatestTvShowListFragment();
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_container, tvShowFragment)
                                .addToBackStack("more_tv_shows")
                                .commit();
                        break;

                    case "Radio":
                        navigateToRadio();
                        break;

                    case "ContinueWatching":

                        break;

                    default:
                        Toast.makeText(activity, "Feature coming soon!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return parentItemList.size();
    }

    // Helper method to navigate to Radio
    private void navigateToRadio() {
        // NO account blocked check here - let navigation work normally

        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            if (mainActivity.getBottomNavItems() != null && !mainActivity.getBottomNavItems().isEmpty()) {
                int radioPosition = -1;
                for (int i = 0; i < mainActivity.getBottomNavItems().size(); i++) {
                    String itemName = mainActivity.getBottomNavItems().get(i).getBottommenuName();
                    if (itemName != null && itemName.equalsIgnoreCase("Radio")) {
                        radioPosition = i;
                        break;
                    }
                }

                if (radioPosition != -1) {
                    mainActivity.selectTab(radioPosition);
                } else {
                    loadRadioFragmentDirectly();
                }
            } else {
                loadRadioFragmentDirectly();
            }
        } else {
            loadRadioFragmentDirectly();
        }
    }

    // Fallback method to load Radio fragment directly
    private void loadRadioFragmentDirectly() {
        RadioFragment radioFragment = new RadioFragment();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, radioFragment)
                .addToBackStack("radio")
                .commit();
    }



    // Add a public method to update the activity reference if needed
    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    // Add method to update the data
    public void updateData(List<ParentItemAllFragment> newList) {
        parentItemList.clear();
        parentItemList.addAll(newList);
        notifyDataSetChanged();
    }

    // Add method to clear all data
    public void clearData() {
        parentItemList.clear();
        notifyDataSetChanged();
    }

    static class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle, sellall;
        RecyclerView childRecyclerView;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
            sellall = itemView.findViewById(R.id.sellall);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
        }
    }
}