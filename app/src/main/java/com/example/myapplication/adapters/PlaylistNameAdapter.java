package com.example.myapplication.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;

import java.util.List;

public class PlaylistNameAdapter extends RecyclerView.Adapter<PlaylistNameAdapter.MyViewHolder> {

    List<Playlist> mData;
    IOnItemClickListener iOnItemClickListener;

    public PlaylistNameAdapter(List<Playlist> mData, IOnItemClickListener iOnItemClickListener) {
        this.mData = mData;
        this.iOnItemClickListener = iOnItemClickListener;
    }

    public void setData(List<Playlist> list) {
        mData = list;
        notifyDataSetChanged();
    }

    public interface IOnItemClickListener {
        void onItemClickListener(Playlist playlist);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_name, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Playlist playlist = mData.get(position);

        if (playlist == null) {
            return;
        }

        holder.txtPlaylistName.setText(playlist.getName());
        if (playlist.getName().equals("Favorite")) {
            holder.imgPlaylist.setImageResource(R.drawable.favorite_playlist);
        } else {
            if (AppDatabase.getInstance(holder.itemView.getContext()).playlistDao().getAllSongsInPlaylist(playlist.getId()).size() > 0) {
                Log.v("imgPlaylist", mData.size() + "");
                for (Song song : AppDatabase.getInstance(holder.itemView.getContext()).playlistDao().getAllSongsInPlaylist(playlist.getId())) {
                    Uri artworkUri = song.getAlbumUri();
                    if (artworkUri != null) {
                        holder.imgPlaylist.setImageURI(artworkUri);
                        break;
                    }
                }
            } else {
                holder.imgPlaylist.setImageResource(R.drawable.playlist_cover);
            }
        }

        holder.itemView.setOnClickListener(view -> {
            iOnItemClickListener.onItemClickListener(playlist);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPlaylist;
        TextView txtPlaylistName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPlaylist = itemView.findViewById(R.id.image_playlist);
            txtPlaylistName = itemView.findViewById(R.id.text_playlist_name);
        }
    }
}
