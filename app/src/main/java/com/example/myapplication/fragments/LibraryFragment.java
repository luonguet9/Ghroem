package com.example.myapplication.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.activities.PlaylistDetailActivity;
import com.example.myapplication.adapters.PlaylistAdapter;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;
    RecyclerView mRecyclerView;
    PlaylistAdapter mPlaylistAdapter;
    List<Playlist> mData = new ArrayList<>();

    ImageView imgAdd;

    public LibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_library, container, false);

        initView();

        mData = AppDatabase.getInstance(getContext()).playlistDao().getListPlaylist();

        mPlaylistAdapter = new PlaylistAdapter(mData, new PlaylistAdapter.IOnItemClickListener() {
            @Override
            public void onItemClickListener(Playlist playlist) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("playlist_object", playlist);
                bundle.putBoolean("isServiceConnected", ((MainActivity) requireActivity()).isServiceConnected);

                Intent intent = new Intent(getActivity(), PlaylistDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                //Toast.makeText(getContext(), "" + playlist.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMoreButtonClickListener(Playlist playlist, ImageView imageView) {
                PopupMenu popupMenu = new PopupMenu(getContext(), imageView);
                popupMenu.getMenuInflater().inflate(R.menu.menu_more_playlist, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.action_rename) {
                        //TODO rename playlist
                        renamePlaylist(playlist);
                        return true;
                    }
                    if (menuItem.getItemId() == R.id.action_delete) {
                        deletePlaylist(playlist);
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            }
        });

        mRecyclerView.setAdapter(mPlaylistAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);

        initFavoritePlaylist();

        imgAdd.setOnClickListener(view1 -> {
            createNewPlayList();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlaylistAdapter != null) {
            loadData();
        }
    }

    private void initView() {
        mRecyclerView = view.findViewById(R.id.rcv_list_playlist);
        imgAdd = view.findViewById(R.id.image_add_playlist);
    }

    private void loadData() {
        mData = AppDatabase.getInstance(getContext()).playlistDao().getListPlaylist();
        mPlaylistAdapter.setData(mData);
    }

    private void initFavoritePlaylist() {
        if (mData.size() == 0) {
            Playlist playlist = new Playlist(getString(R.string.favorite));
            AppDatabase.getInstance(getContext()).playlistDao().insertPlaylist(playlist);
            loadData();
        }
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
                    loadData();
                    dialog.dismiss();
                }
            }

        });

        dialog.show();

//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        View view1 = getLayoutInflater().inflate(R.layout.dialog_name_playlist, null);
//        EditText edtNamePlaylist = view1.findViewById(R.id.edt_name_playlist);
//        edtNamePlaylist.setHint("Type your playlist name...");
//
//        builder.setView(view1)
//                .setTitle("Create new playlist")
//                .setNegativeButton("Cancel", null)
//                .setPositiveButton("Create", (dialogInterface, i) -> {
//                    String name = edtNamePlaylist.getText().toString().trim();
//                    if (name.isEmpty()) {
//                        edtNamePlaylist.requestFocus();
//                        edtNamePlaylist.setError("Give your playlist a name!");
//                        Toast.makeText(getContext(), "Please give your playlist a name!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        if (AppDatabase.getInstance(getContext()).playlistDao().getPlaylistByName(name) != null) {
//                            Toast.makeText(getContext(), "This playlist has exist!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Playlist playlist = new Playlist(name);
//                            AppDatabase.getInstance(getContext()).playlistDao().insertPlaylist(playlist);
//                            loadData();
//                        }
//                    }
//                })
//                .show();
    }

    private void renamePlaylist(Playlist playlist) {

        if (playlist == mData.get(0)) {
            Toast.makeText(getContext(), R.string.cant_rename_favorite_playlist, Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rename_playlist);

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
        Button btSave = dialog.findViewById(R.id.button_save);

        btCancel.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        btSave.setOnClickListener(view1 -> {
            String name = edtNamePlaylist.getText().toString().trim();
            if (name.isEmpty()) {
                edtNamePlaylist.requestFocus();
                edtNamePlaylist.setError(String.valueOf(R.string.give_your_playlist_a_name));
                Toast.makeText(getContext(), R.string.please_give_your_playlist_a_name, Toast.LENGTH_SHORT).show();
            } else {
                if (AppDatabase.getInstance(getContext()).playlistDao().getPlaylistByName(name) != null) {
                    Toast.makeText(getContext(), R.string.playlist_exist, Toast.LENGTH_SHORT).show();
                } else {
                    playlist.setName(name);
                    AppDatabase.getInstance(getContext()).playlistDao().updatePlaylist(playlist);
                    loadData();
                    dialog.dismiss();
                }
            }

        });

        dialog.show();

//        if (playlist == mData.get(0)) {
//            Toast.makeText(getContext(), "Can't rename Favorite Playlist", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        View view1 = getLayoutInflater().inflate(R.layout.dialog_name_playlist, null);
//        EditText edtNamePlaylist = view1.findViewById(R.id.edt_name_playlist);
//        edtNamePlaylist.setHint("Type your new playlist name...");
//
//        builder.setView(view1)
//                .setTitle("Rename playlist")
//                .setNegativeButton("Cancel", null)
//                .setPositiveButton("Rename", (dialogInterface, i) -> {
//                    String name = edtNamePlaylist.getText().toString().trim();
//                    if (name.isEmpty()) {
//                        edtNamePlaylist.requestFocus();
//                        edtNamePlaylist.setError("Give your playlist a name!");
//                        Toast.makeText(getContext(), "Please give your playlist a name!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        if (AppDatabase.getInstance(getContext()).playlistDao().getPlaylistByName(name) != null) {
//                            Toast.makeText(getContext(), "This playlist has exist!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            playlist.setName(name);
//                            AppDatabase.getInstance(getContext()).playlistDao().updatePlaylist(playlist);
//                            loadData();
//                        }
//                    }
//
//                })
//                .show();
    }

    private void deletePlaylist(Playlist playlist) {
        if (playlist == mData.get(0)) {
            Toast.makeText(getContext(), R.string.cant_delete_favorite_playlist, Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_playlist);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        Button btCancel = dialog.findViewById(R.id.button_cancel);
        Button btDelete = dialog.findViewById(R.id.button_delete);

        btCancel.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        btDelete.setOnClickListener(view1 -> {
            // delete all songs in playlist
            for (Song song : AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(playlist.getId())) {
                AppDatabase.getInstance(getContext()).playlistDao().deleteSong(song);
            }
            // delete playlist
            AppDatabase.getInstance(getContext()).playlistDao().deletePlaylist(playlist);
            loadData();
            dialog.dismiss();

        });

        dialog.show();


//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//
//        builder.setTitle("Delete " + playlist.getName() + " playlist")
//                .setMessage("Are you sure to delete?")
//                .setNegativeButton("No", null)
//                .setPositiveButton("Yes", (dialogInterface, i) ->  {
//                    AppDatabase.getInstance(getContext()).playlistDao().deletePlaylist(playlist);
//                    loadData();
//                })
//                .show();
    }
}