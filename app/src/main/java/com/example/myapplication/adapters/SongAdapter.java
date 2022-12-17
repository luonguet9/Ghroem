package com.example.myapplication.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.PlaylistDetailActivity;
import com.example.myapplication.dialogs.AddSongsDialog;
import com.example.myapplication.models.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    List<Song> mData;
    IOnItemClickListener iOnItemClickListener;

    public SongAdapter(List<Song> mData, IOnItemClickListener iOnItemClickListener) {
        this.mData = mData;
        this.iOnItemClickListener = iOnItemClickListener;
    }

    public void setData(List<Song> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public interface IOnItemClickListener {
        void onItemClickListener(Song song);
        void onMoreButtonClickListener(Song song, View view);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Song song = mData.get(position);

        if (song == null) {
            return;
        }

        //holder.imgSong.setImageResource(song.getImageResource());
        Uri artworkUri = song.getAlbumUri();
        if (artworkUri != null) {
            holder.imgSong.setImageURI(artworkUri);
        } else {
            holder.imgSong.setImageResource(R.drawable.spotify_blue);
        }

        holder.txtNameSong.setText(song.getName());
        holder.txtSingleSong.setText(song.getSinger());

        holder.imgMore.setOnClickListener(view -> {
            iOnItemClickListener.onMoreButtonClickListener(song, holder.itemView);
        });

        holder.itemView.setOnClickListener(view -> {
            iOnItemClickListener.onItemClickListener(song);
        });
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSong;
        TextView txtNameSong, txtSingleSong;
        ImageView imgMore;

        public ConstraintLayout layoutForeground;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSong = itemView.findViewById(R.id.image_song);
            txtNameSong = itemView.findViewById(R.id.text_name_song);
            txtSingleSong = itemView.findViewById(R.id.text_singer_song);
            imgMore = itemView.findViewById(R.id.image_more);
            layoutForeground = itemView.findViewById(R.id.layout_foreground);
        }
    }

    public void removeSong(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void undoRemove(Song song, int position) {
        mData.add(position, song);
        notifyItemInserted(position);
    }

}
