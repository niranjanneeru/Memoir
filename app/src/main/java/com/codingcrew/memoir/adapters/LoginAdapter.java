package com.codingcrew.memoir.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.codingcrew.memoir.fragments.authFragments.LoginTabFragment;
import com.codingcrew.memoir.fragments.authFragments.SignUpTabFragment;

public class LoginAdapter extends FragmentPagerAdapter {


    private Context context;
    private int totalTabs;

    public LoginAdapter(@NonNull FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LoginTabFragment();
            case 1:
                return new SignUpTabFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
