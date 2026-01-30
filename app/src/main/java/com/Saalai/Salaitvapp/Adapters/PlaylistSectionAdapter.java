package com.Saalai.Salaitvapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Saalai.Salaitvapp.Models.PlaylistSection;
import com.Saalai.Salaitvapp.R;
import java.util.List;

public class PlaylistSectionAdapter extends RecyclerView.Adapter<PlaylistSectionAdapter.SectionViewHolder> {

    private List<PlaylistSection> sectionList;
    private ArtistAdapter.OnArtistClickListener onArtistClickListener;

    public PlaylistSectionAdapter(List<PlaylistSection> sectionList, ArtistAdapter.OnArtistClickListener listener) {
        this.sectionList = sectionList;
        this.onArtistClickListener = listener;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        PlaylistSection section = sectionList.get(position);
        holder.bind(section);
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public void updateData(List<PlaylistSection> newSectionList) {
        this.sectionList = newSectionList;
        notifyDataSetChanged();
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionTitle;
        private RecyclerView artistsRecyclerView;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
            artistsRecyclerView = itemView.findViewById(R.id.artistsRecyclerView);
        }

        public void bind(PlaylistSection section) {
            sectionTitle.setText(section.getSectionName());

            ArtistAdapter artistAdapter = new ArtistAdapter(section.getArtists(), onArtistClickListener);
            artistsRecyclerView.setLayoutManager(new LinearLayoutManager(
                    itemView.getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
            ));
            artistsRecyclerView.setAdapter(artistAdapter);
        }
    }
}