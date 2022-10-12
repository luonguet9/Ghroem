package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
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

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSong;
        TextView txtNameSong, txtSingleSong;
        ImageView imgMore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSong = itemView.findViewById(R.id.image_song);
            txtNameSong = itemView.findViewById(R.id.text_name_song);
            txtSingleSong = itemView.findViewById(R.id.text_singer_song);
            imgMore = itemView.findViewById(R.id.image_more);
        }
    }
}
