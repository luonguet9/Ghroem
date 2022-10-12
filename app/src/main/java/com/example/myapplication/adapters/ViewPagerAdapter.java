package com.example.myapplication.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.fragments.HomeFragment;
import com.example.myapplication.fragments.LibraryFragment;
import com.example.myapplication.fragments.SettingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new LibraryFragment();

            case 2:
                return new SettingFragment();

            default:
                return new HomeFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
