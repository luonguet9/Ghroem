package com.example.myapplication.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.PlaylistDetailActivity;
import com.example.myapplication.adapters.PlaylistNameAdapter;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;

import java.util.List;

public class AddToPlaylistDialog extends AppCompatDialogFragment {

    List<Playlist> mData;
    Song mSong;
    RecyclerView mRecyclerView;
    PlaylistNameAdapter mPlaylistNameAdapter;
    RelativeLayout layoutCreateNewPlaylist;

    public AddToPlaylistDialog(Song song) {
        mSong = song;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_add_to_playlist, container, false);

        mData = AppDatabase.getInstance(view.getContext()).playlistDao().getListPlaylist();
        mPlaylistNameAdapter = new PlaylistNameAdapter(mData, playlist -> {
            handleAddSongToPlaylist(mSong, playlist);
        });

        mRecyclerView = view.findViewById(R.id.rcv_list_playlist_name);
        mRecyclerView.setAdapter(mPlaylistNameAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        layoutCreateNewPlaylist = view.findViewById(R.id.layout_create_new_playlist);
        layoutCreateNewPlaylist.setOnClickListener(view1 -> {
            createNewPlayList();
        });
        return view;

    }

    private void handleAddSongToPlaylist(Song song, Playlist playlist) {
        for (Song temp : AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(playlist.getId())) {
            if (temp.getUrl().equals(song.getUrl())) {
                Toast.makeText(getContext(), R.string.song_exists, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Song newSong = song.copySong();
        newSong.playlistId = playlist.getId();
        PlaylistDetailActivity.viewModel.insertSong(newSong);
        Toast.makeText(getContext(), "Added to " + playlist.getName() +" playlist", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private void createNewPlayList() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_new_playlist);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        EditText edtNamePlaylist = dialog.findViewById(R.id.edt_name_playlist);
        Button btCancel = dialog.findViewById(R.id.button_cancel);
        Button btCreate = dialog.findViewById(R.id.button_create);

        btCancel.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        btCreate.setOnClickListener(view1 -> {
            String name = edtNamePlaylist.getText().toString();
            if (name.isEmpty()) {
                edtNamePlaylist.setError(getString(R.string.please_give_your_playlist_a_name));
            } else {
                if (AppDatabase.getInstance(getContext()).playlistDao().getPlaylistByName(name) != null) {
                    Toast.makeText(getContext(), R.string.playlist_exist, Toast.LENGTH_SHORT).show();
                } else {
                    Playlist playlist = new Playlist(name);
                    AppDatabase.getInstance(getContext()).playlistDao().insertPlaylist(playlist);
                    //loadData
                    mData = AppDatabase.getInstance(getContext()).playlistDao().getListPlaylist();
                    mPlaylistNameAdapter.setData(mData);
                    dialog.dismiss();
                }
            }

        });

        dialog.show();
    }
}
