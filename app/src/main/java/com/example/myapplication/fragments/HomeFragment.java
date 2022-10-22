package com.example.myapplication.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.adapters.SongAdapter;
import com.example.myapplication.models.Song;
import com.example.myapplication.services.MyService;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView mRecyclerView;
    SongAdapter mSongAdapter;
    List<Song> mListSong;
    com.example.myapplication.services.MyService mService;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = view.findViewById(R.id.rcv_list_song);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mListSong = MyService.mData;
        mSongAdapter = new SongAdapter(mListSong, new SongAdapter.IOnItemClickListener() {
            @Override
            public void onItemClickListener(Song song, int position) {
                startMusic(song, position);
            }

            @Override
            public void onMoreButtonClickListener(Song song, View view) {
                ImageView imgMore = view.findViewById(R.id.image_more);
                PopupMenu popupMenu = new PopupMenu(getContext(), imgMore);
                popupMenu.getMenuInflater().inflate(R.menu.menu_more_button, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_love:
                                //TODO
                                return true;

                            case R.id.action_add_to_playlist:
                                //TODO
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        mRecyclerView.setAdapter(mSongAdapter);

        return view;
    }

    private void startMusic(Song song, int position) {
        if (((MainActivity) requireActivity()).isServiceConnected) {
            MyService.mSong = song;
            MyService.position = position;
            ((MainActivity) requireActivity()).onClickStopService();
            ((MainActivity) requireActivity()).onClickStartService(MyService.mSong);
        } else {
            MyService.mSong = song;
            MyService.position = position;
            ((MainActivity) requireActivity()).onClickStartService(MyService.mSong);
        }
    }

}