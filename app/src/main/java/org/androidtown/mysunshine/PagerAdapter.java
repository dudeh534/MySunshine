package org.androidtown.mysunshine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ChaeYoungDo on 2015-08-16.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private final String[] TITLES = {"서울", "인천", "포항", "대구", "가평"};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public CharSequence getPageTitle(int position){
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        return ViewPagerFragment.newInstance(position);
    }

    public String returnTitle(int i){
        return TITLES[i];
    }

}
