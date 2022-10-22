package com.example.myapplication.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.adapters.SongAdapter;
import com.example.myapplication.models.Song;
import com.example.myapplication.services.MyService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;

    RecyclerView mRecyclerView;
    SongAdapter mSongAdapter;
    List<Song> mListSong;

    TextView txtHello;
    ImageView imgSort;
    EditText edtSearch;
    Button btPlay, btShuffle;

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
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initView();

        mRecyclerView = view.findViewById(R.id.rcv_list_song);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mListSong = MyService.mData;
        mSongAdapter = new SongAdapter(mListSong, new SongAdapter.IOnItemClickListener() {
            @Override
            public void onItemClickListener(Song song) {
                startMusic(song);
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
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);

        handleTextTitle();

        btPlay.setOnClickListener(view1 -> {
            startMusic(mListSong.get(0));
        });

        btShuffle.setOnClickListener(view1 -> {
            int randomPosition = (int) (Math.random() * mListSong.size());
            startMusic(mListSong.get(randomPosition));
        });

        imgSort.setOnClickListener(view1 -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), imgSort);
            popupMenu.getMenuInflater().inflate(R.menu.menu_sort, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.sort_title:
                        sortByTitle(mListSong);
                        MyService.mData = mListSong;
                        mSongAdapter.setData(mListSong);
                        return true;

                    case R.id.sort_artist:
                        sortByArtist(mListSong);
                        MyService.mData = mListSong;
                        mSongAdapter.setData(mListSong);
                        return true;

                    case R.id.sort_added_time:
                        sortByAddedTime(mListSong);
                        MyService.mData = mListSong;
                        mSongAdapter.setData(mListSong);
                        return true;

                    default:
                        return false;
                }
            });
            popupMenu.show();
        });

        return view;
    }


    private void initView() {
        txtHello = view.findViewById(R.id.txt_hello);
        imgSort = view.findViewById(R.id.image_sort);
        edtSearch = view.findViewById(R.id.edt_search);
        btPlay = view.findViewById(R.id.button_play);
        btShuffle = view.findViewById(R.id.button_shuffle);
    }

    private void handleTextTitle() {
        DateFormat dateFormat = new SimpleDateFormat("HH");
        String date = dateFormat.format(Calendar.getInstance().getTime());
        int hour = Integer.parseInt(date);
        if (5 <= hour && hour < 12) {
            txtHello.setText("Good morning");
        }

        if (hour >= 12 && hour < 19) {
            txtHello.setText("Good afternoon");
        }

        if (hour >= 19 && hour < 22) {
            txtHello.setText("Good evening");
        } else {
            txtHello.setText("Good night");
        }
    }

    private void startMusic(Song song) {
        if (((MainActivity) requireActivity()).isServiceConnected) {
            MyService.mSong = song;
            ((MainActivity) requireActivity()).onClickStopService();
            ((MainActivity) requireActivity()).onClickStartService(MyService.mSong);
        } else {
            MyService.mSong = song;
            ((MainActivity) requireActivity()).onClickStartService(MyService.mSong);
        }
    }

    private void sortByTitle(List<Song> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(Song::getName));
        }
    }

    private void sortByArtist(List<Song> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(Song::getSinger));
        }
    }

    private void sortByAddedTime(List<Song> list) {
        Collections.sort(list, (song, t1) -> t1.getAddedDate().compareTo(song.getAddedDate()));
    }

}