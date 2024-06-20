package in.softment.mindmotivation.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import in.softment.mindmotivation.Model.AlbumModel;
import in.softment.mindmotivation.R;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private List<AlbumModel> albumList;
    private Context context;

    public AlbumAdapter(Context context, List<AlbumModel> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AlbumModel album = albumList.get(position);
        holder.albumName.setText(album.getName());
        holder.albumTracks.setText(album.getTracks() + " Tracks");
    }

    @Override
    public int getItemCount() {

        return albumList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView albumName;
        public TextView albumTracks;

        public ViewHolder(View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.title); // Replace with your TextView ID
            albumTracks = itemView.findViewById(R.id.tracks); // Replace with your TextView ID
        }
    }
}
