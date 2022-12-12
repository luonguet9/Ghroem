package com.example.myapplication.adapters;

import android.content.res.Resources;
import android.net.Uri;
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

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {

    List<Playlist> mData;
    IOnItemClickListener iOnItemClickListener;

    public PlaylistAdapter(List<Playlist> mData, IOnItemClickListener iOnItemClickListener) {
        this.mData = mData;
        this.iOnItemClickListener = iOnItemClickListener;
    }

    public void setData(List<Playlist> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public interface IOnItemClickListener {
        void onItemClickListener(Playlist playlist);

        void onMoreButtonClickListener(Playlist playlist, ImageView imageView);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Playlist playlist = mData.get(position);

        if (playlist == null) {
            return;
        }

        List<Song> songList = AppDatabase.getInstance(holder.itemView.getContext()).playlistDao().getAllSongsInPlaylist(playlist.getId());


        if (position == 0) {
            holder.imgPlaylist.setImageResource(R.drawable.favorite_playlist);
        } else {
            if (songList.size() > 0) {
                for (Song song : songList) {
                    Uri artworkUri = song.getAlbumUri();
                    if (artworkUri != null) {
                        holder.imgPlaylist.setImageURI(artworkUri);
                        break;
                    }
                }
            }
            else {
                //Set default
                holder.imgPlaylist.setImageResource(R.drawable.playlist_cover);
            }
        }


        holder.txtNamePlaylist.setText(playlist.getName());
        if (playlist.getId() == 1) {
            holder.txtNamePlaylist.setText(holder.itemView.getContext().getString(R.string.favorite));
        }


        if (songList.size() == 0 || songList.size() == 1) {
            holder.txtNumbersOfSongs.setText(songList.size() + " " + holder.itemView.getContext().getString(R.string.song));
        } else {
            holder.txtNumbersOfSongs.setText(songList.size() + " " + holder.itemView.getContext().getString(R.string.songs));
        }

        holder.imgMore.setOnClickListener(view -> {
            iOnItemClickListener.onMoreButtonClickListener(playlist, holder.imgMore);
        });

        holder.itemView.setOnClickListener(view -> {
            iOnItemClickListener.onItemClickListener(playlist);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPlaylist, imgMore;
        TextView txtNamePlaylist, txtNumbersOfSongs;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPlaylist = itemView.findViewById(R.id.image_playlist);
            txtNamePlaylist = itemView.findViewById(R.id.txt_name_playlist);
            txtNumbersOfSongs = itemView.findViewById(R.id.txt_number_of_songs);
            imgMore = itemView.findViewById(R.id.image_more);

        }
    }
}
