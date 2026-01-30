package com.Saalai.Salaitvapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.Saalai.Salaitvapp.Models.ArtistCategory;
import com.Saalai.Salaitvapp.Models.AudioModel;
import com.Saalai.Salaitvapp.R;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ArtistCategory> artistList;
    private OnArtistClickListener onArtistClickListener;

    public interface OnArtistClickListener {
        void onArtistClick(String artistName, List<AudioModel> songs, String artistImageUrl);
    }

    public ArtistAdapter(List<ArtistCategory> artistList, OnArtistClickListener listener) {
        this.artistList = artistList;
        this.onArtistClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return artistList.get(position).getAdapterType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == 1) {
            View type1View = inflater.inflate(R.layout.item_artist_type1, parent, false);
            return new Type1ViewHolder(type1View);
        } else if (viewType == 2) {
            View type2View = inflater.inflate(R.layout.item_artist_type2, parent, false);
            return new Type2ViewHolder(type2View);
        } else {
            View type3View = inflater.inflate(R.layout.item_artist_type3, parent, false);
            return new Type3ViewHolder(type3View);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ArtistCategory artist = artistList.get(position);

        int viewType = holder.getItemViewType();
        if (viewType == 1) {
            ((Type1ViewHolder) holder).bind(artist);
        } else if (viewType == 2) {
            ((Type2ViewHolder) holder).bind(artist);
        } else {
            ((Type3ViewHolder) holder).bind(artist);
        }
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public void updateData(List<ArtistCategory> newArtistList) {
        this.artistList = newArtistList;
        notifyDataSetChanged();
    }

    // Type 1: Square with radius
    class Type1ViewHolder extends RecyclerView.ViewHolder {
        private TextView artistName;
        private TextView songCount;
        private CardView cardView;
        private ImageView artistImage;

        public Type1ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistName = itemView.findViewById(R.id.artistName);
            songCount = itemView.findViewById(R.id.songCount);
            cardView = itemView.findViewById(R.id.artistCard);
            artistImage = itemView.findViewById(R.id.artistImage);
        }

        public void bind(ArtistCategory artist) {
            artistName.setText(artist.getArtistName());
            songCount.setText(artist.getSongs().size() + " songs");

            Glide.with(itemView.getContext())
                    .load(artist.getArtistImageUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                    .into(artistImage);

            cardView.setOnClickListener(v -> {
                if (onArtistClickListener != null) {
                    onArtistClickListener.onArtistClick(
                            artist.getArtistName(),
                            artist.getSongs(),
                            artist.getArtistImageUrl()
                    );
                }
            });
        }
    }

    // Type 2: Round
    class Type2ViewHolder extends RecyclerView.ViewHolder {
        private TextView artistName;
        private TextView songCount;
        private CardView cardView;
        private ImageView artistImage;

        public Type2ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistName = itemView.findViewById(R.id.artistName);
            songCount = itemView.findViewById(R.id.songCount);
            cardView = itemView.findViewById(R.id.artistCard);
            artistImage = itemView.findViewById(R.id.artistImage);
        }

        public void bind(ArtistCategory artist) {
            artistName.setText(artist.getArtistName());
            songCount.setText(artist.getSongs().size() + " songs");

            Glide.with(itemView.getContext())
                    .load(artist.getArtistImageUrl())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(artistImage);

            cardView.setOnClickListener(v -> {
                if (onArtistClickListener != null) {
                    onArtistClickListener.onArtistClick(
                            artist.getArtistName(),
                            artist.getSongs(),
                            artist.getArtistImageUrl()
                    );
                }
            });
        }
    }

    // Type 3: Different layout
    class Type3ViewHolder extends RecyclerView.ViewHolder {
        private TextView artistName;
        private TextView songCount;
        private CardView cardView;
        private ImageView artistImage;

        public Type3ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistName = itemView.findViewById(R.id.artistName);
            songCount = itemView.findViewById(R.id.songCount);
            cardView = itemView.findViewById(R.id.artistCard);
            artistImage = itemView.findViewById(R.id.artistImage);
        }

        public void bind(ArtistCategory artist) {
            artistName.setText(artist.getArtistName());
            songCount.setText(artist.getSongs().size() + " songs");

            Glide.with(itemView.getContext())
                    .load(artist.getArtistImageUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                    .into(artistImage);

            cardView.setOnClickListener(v -> {
                if (onArtistClickListener != null) {
                    onArtistClickListener.onArtistClick(
                            artist.getArtistName(),
                            artist.getSongs(),
                            artist.getArtistImageUrl()
                    );
                }
            });
        }
    }
}