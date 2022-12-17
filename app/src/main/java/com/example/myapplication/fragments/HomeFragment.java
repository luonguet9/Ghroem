package com.example.myapplication.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.activities.PlaylistDetailActivity;
import com.example.myapplication.activities.SplashActivity;
import com.example.myapplication.adapters.SongAdapter;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.dialogs.AddToPlaylistDialog;
import com.example.myapplication.models.Song;
import com.example.myapplication.services.MyService;
import com.example.myapplication.utilities.itemtouch.ItemTouchHelperListener;
import com.example.myapplication.utilities.itemtouch.RecyclerViewItemTouchHelper;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    LinearLayout layoutRoot;
    NestedScrollView mNestedScrollView;

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

        mListSong = SplashActivity.mSongList;
        mSongAdapter = new SongAdapter(mListSong, new SongAdapter.IOnItemClickListener() {
            @Override
            public void onItemClickListener(Song song) {
                startMusic(song);
            }

            @Override
            public void onMoreButtonClickListener(Song song, View view) {
                ImageView imgMore = view.findViewById(R.id.image_more);
                PopupMenu popupMenu = new PopupMenu(getContext(), imgMore);
                popupMenu.getMenuInflater().inflate(R.menu.menu_more_song, popupMenu.getMenu());
                MenuItem menuItemDelete = popupMenu.getMenu().findItem(R.id.action_delete_from_playlist);
                MenuItem menuItemLove = popupMenu.getMenu().findItem(R.id.action_love);
                MenuItem menuItemUnLove = popupMenu.getMenu().findItem(R.id.action_un_love);

                for (Song temp : AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(1)) {
                    if (temp.getUrl().equals(song.getUrl())) {
                        song.isLove = true;
                        break;
                    }
                }

                if (song.isLove) {
                    menuItemLove.setVisible(false);
                    menuItemUnLove.setVisible(true);
                } else {
                    menuItemLove.setVisible(true);
                    menuItemUnLove.setVisible(false);
                }

                menuItemDelete.setVisible(false);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.action_love:
                            handleActionLove(song);
                            return true;

                        case R.id.action_un_love:
                            handleActionUnLove(song);
                            return true;

                        case R.id.action_add_to_playlist:
                            AddToPlaylistDialog addToPlaylistDialog = new AddToPlaylistDialog(song);
                            addToPlaylistDialog.show(getActivity().getSupportFragmentManager(), "");
                            return true;

                        default:
                            return false;
                    }
                });
                popupMenu.show();
            }
        });

        mRecyclerView.setAdapter(mSongAdapter);
        //ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);

        handleSwipeItemSong();

        handleTextTitle();

        btPlay.setOnClickListener(view1 -> {
            if (mListSong != null && mListSong.size() > 0) {
                startMusic(mListSong.get(0));
            }
        });

        btShuffle.setOnClickListener(view1 -> {
            if (mListSong != null && mListSong.size() > 0) {
                int randomPosition = (int) (Math.random() * mListSong.size());
                startMusic(mListSong.get(randomPosition));
            }
        });

        imgSort.setOnClickListener(view1 -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), imgSort);
            popupMenu.getMenuInflater().inflate(R.menu.menu_sort, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.sort_title:
                        sortByTitle(mListSong);
                        //MyService.mData = mListSong;
                        //mSongAdapter.setData(mListSong);
                        loadDataWithAnimation();
                        return true;

                    case R.id.sort_artist:
                        sortByArtist(mListSong);
                        //MyService.mData = mListSong;
                        //mSongAdapter.setData(mListSong);
                        loadDataWithAnimation();
                        return true;

                    case R.id.sort_added_time:
                        sortByAddedTime(mListSong);
                        //MyService.mData = mListSong;
                        //mSongAdapter.setData(mListSong);
                        loadDataWithAnimation();
                        return true;

                    default:
                        return false;
                }
            });
            popupMenu.show();
        });

        edtSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                searchSong();
            }
            return false;
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchSong();
            }
        });

        return view;
    }


    private void initView() {
        txtHello = view.findViewById(R.id.txt_hello);
        imgSort = view.findViewById(R.id.image_sort);
        edtSearch = view.findViewById(R.id.edt_search);
        btPlay = view.findViewById(R.id.button_play);
        btShuffle = view.findViewById(R.id.button_shuffle);
        layoutRoot = view.findViewById(R.id.layout_root);
        mNestedScrollView = view.findViewById(R.id.layout_fragment);
    }

//    private void handleSwipeItem() {
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return true;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                if (viewHolder instanceof SongAdapter.MyViewHolder) {
//                    int position = viewHolder.getAdapterPosition();
//                    Song song = mListSong.get(position);
//                    String nameSong = song.getName();
//                    mSongAdapter.removeSong(position);
//                    //removeSong(position);
//                    //handleActionLove(song);
//
//                    Snackbar snackbar = Snackbar.make(layoutRoot, nameSong + " removed", Snackbar.LENGTH_LONG);
//                    snackbar.setAction("Undo", view -> mSongAdapter.undoRemove(song, position));
//                    snackbar.setActionTextColor(Color.BLUE);
//                    snackbar.show();
//                }
//            }
//
//            @Override
//            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
//                if (viewHolder != null) {
//                    View foreGroundView = ((SongAdapter.MyViewHolder) viewHolder).layoutForeground;
//                    getDefaultUIUtil().onSelected(foreGroundView);
//                }
//            }
//
//            @Override
//            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                assert viewHolder != null;
//                View foreGroundView = ((SongAdapter.MyViewHolder) viewHolder).layoutForeground;
//                getDefaultUIUtil().onDrawOver(c, recyclerView, foreGroundView, dX, dY, actionState, isCurrentlyActive);
//            }
//
//            @Override
//            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
//                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
//
//                View foreGroundView = ((SongAdapter.MyViewHolder) viewHolder).layoutForeground;
//                getDefaultUIUtil().onDraw(c, recyclerView, foreGroundView, dX, dY, actionState, isCurrentlyActive);
//
//
//            }
//
//            @Override
//            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//
//                View foreGroundView = ((SongAdapter.MyViewHolder) viewHolder).layoutForeground;
//                getDefaultUIUtil().clearView(foreGroundView);
//            }
//        }).attachToRecyclerView(mRecyclerView);
//    }

    private void handleSwipeItemSong() {
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, viewHolder -> {
            if (viewHolder instanceof SongAdapter.MyViewHolder) {
                int position = viewHolder.getAdapterPosition();
                Song song = mListSong.get(position);
                String nameSong = song.getName();
                mSongAdapter.removeSong(position);
                //removeSong(position);
                //handleActionLove(song);

                Snackbar snackbar = Snackbar.make(layoutRoot, getString(R.string.removed) + " " + nameSong, Snackbar.LENGTH_LONG);
                snackbar.setAction(getString(R.string.undo), view -> {
                    mSongAdapter.undoRemove(song, position);
                    if (position == mListSong.size() - 1) {
                        mRecyclerView.scrollToPosition(position);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        });

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerView);
    }


    private void loadDataWithAnimation() {
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_up_to_down);
        mRecyclerView.setLayoutAnimation(layoutAnimationController);
        mSongAdapter.setData(mListSong);
        mRecyclerView.setAdapter(mSongAdapter);
    }

    private void handleTextTitle() {
        DateFormat dateFormat = new SimpleDateFormat("HH");
        String date = dateFormat.format(Calendar.getInstance().getTime());
        int hour = Integer.parseInt(date);
        if (5 <= hour && hour < 12) {
            txtHello.setText(R.string.good_morning);
        } else {
            if (hour >= 12 && hour < 19) {
                txtHello.setText(R.string.good_afternoon);
            } else {
                if (hour >= 19 && hour < 22) {
                    txtHello.setText(R.string.good_evening);
                } else {
                    txtHello.setText(R.string.good_night);
                }
            }
        }


    }

    private void startMusic(Song song) {
        if (((MainActivity) requireActivity()).isServiceConnected) {
            MyService.mSong = song;
            MyService.mData = SplashActivity.mSongList;
            ((MainActivity) requireActivity()).onClickStopService();
            ((MainActivity) requireActivity()).onClickStartService(MyService.mSong);
        } else {
            MyService.mSong = song;
            MyService.mData = SplashActivity.mSongList;
            ((MainActivity) requireActivity()).onClickStartService(MyService.mSong);
        }
    }

    private void removeSong(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.delete_song)
                .setMessage(R.string.sure_to_delete)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    mSongAdapter.removeSong(position);
                    Toast.makeText(getContext(), "Delete successfully!", Toast.LENGTH_SHORT).show();
                })
                .show();
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

    private void handleActionLove(Song song) {
        for (Song temp : AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(1)) {
            if (temp.getUrl().equals(song.getUrl())) {
                Toast.makeText(getContext(), R.string.song_exists, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        song.isLove = true;
        Song newSong = song.copySong();
        newSong.isLove = true;
        newSong.playlistId = 1;
        PlaylistDetailActivity.viewModel.insertSong(newSong);
        Toast.makeText(getContext(), R.string.added_song_to_favorite_playlist, Toast.LENGTH_SHORT).show();
    }

    private void handleActionUnLove(Song song) {
        song.isLove = false;
        for (Song temp : AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(1)) {
            if (temp.getUrl().equals(song.getUrl())) {
                PlaylistDetailActivity.viewModel.deleteSong(temp);
                Toast.makeText(getContext(), R.string.remove_song_to_favorite_playlist, Toast.LENGTH_SHORT).show();
            }

            if (MyService.mSong != null && MyService.mSong.getUrl().equals(song.getUrl())) {
                MyService.mSong.isLove = false;
            }
        }

    }

    private void searchSong() {
        String keyword = edtSearch.getText().toString().trim().toLowerCase();
        //mListSong = AppDatabase.getInstance(getContext()).playlistDao().searchSong(keyword);
        List<Song> list = new ArrayList<>();
        for (Song song : mListSong) {
            if (song.getName().toLowerCase().contains(keyword)) {
                list.add(song);
            }
        }


        mSongAdapter.setData(list);

    }


}