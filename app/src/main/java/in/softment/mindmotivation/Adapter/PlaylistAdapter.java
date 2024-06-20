package in.softment.mindmotivation.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.softment.mindmotivation.Model.MusicModel;
import in.softment.mindmotivation.Model.UserModel;
import in.softment.mindmotivation.PlayListActivity;
import in.softment.mindmotivation.R;
import in.softment.mindmotivation.Util.Services;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<MusicModel> musicList;
    private LayoutInflater inflater;
    private OnItemClickListener listener;
    private PlayListActivity playListActivity;

    public interface OnItemClickListener {
        void onItemClicked(int position);
        void onFavClicked(MusicModel musicModel);
    }

    public PlaylistAdapter(Context context, List<MusicModel> musicList, OnItemClickListener listener) {
        this.playListActivity = (PlayListActivity) context;
        this.inflater = LayoutInflater.from(context);
        this.musicList = musicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.music_view, parent, false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        MusicModel current = musicList.get(position);
        holder.bind(current, listener,playListActivity);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView musicName;
        public TextView musicDuration;
        public ImageView favImage;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            musicName = itemView.findViewById(R.id.textViewTitle);
            musicDuration = itemView.findViewById(R.id.time);
            favImage = itemView.findViewById(R.id.favImage);
        }

        public void bind(final MusicModel music, final OnItemClickListener listener, PlayListActivity playListActivity) {
            musicName.setText(music.getTitle().trim());
            musicDuration.setText(Services.convertSecondsToMinAndSec(music.getDuration())+" min"); // Implement getDurationString in your MusicModel
            if (music.getThumbnail() != null && !music.getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(music.getThumbnail())
                        .placeholder(R.drawable.placeholder)
                        .into(imageView);
            }

            playListActivity.checkIsFavorite(music.getId(), UserModel.data.getUid(), new PlayListActivity.OnCheckFavoriteListener() {
                @Override
                public void onCheckFavoriteCompleted(boolean isFavorite) {
                   if (isFavorite) {
                       favImage.setImageResource(R.drawable.love2);
                   }
                   else {
                       favImage.setImageResource(R.drawable.love);
                   }
                }
            });

            itemView.setOnClickListener(v -> listener.onItemClicked(getAdapterPosition()));
            favImage.setOnClickListener(v -> listener.onFavClicked(music));
        }
    }
}
