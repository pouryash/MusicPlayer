package com.example.ps.music;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by poorya on 4/4/2018.
 */

public class pager extends FragmentStatePagerAdapter {

    int tabCount;

    public pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                onlineMusicFragment tb1 = new onlineMusicFragment();
                return tb1;
            case 1:
                LocalMusicFragment tb2 = new LocalMusicFragment();
                return tb2;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
