package com.secreto.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Aashish Tomar on 8/25/2017.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] titles;
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    public MyFragmentPagerAdapter(FragmentManager manager, ArrayList<Fragment> fragmentArrayList, String[] titles) {
        super(manager);
        this.titles = new String[titles.length];
        System.arraycopy(titles, 0, this.titles, 0, titles.length);
        this.fragmentArrayList = fragmentArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
