package com.henry.android.tourguideapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Henry on 2017/8/15.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private static final int SCHOOL = 0, FOOD = 1, ATTRATION = 2, SHOP = 3, TOTAL = 4;
    private Context mContext;

    public FragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == SCHOOL) {
            return new InSchoolFragment();
        } else if (position == FOOD) {
            return new FoodFragment();
        } else if (position == ATTRATION) {
            return new AttractionFragment();
        } else {
            return new ShoppingFragment();
        }
    }

    @Override
    public int getCount() {
        return TOTAL;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == SCHOOL) {
            return mContext.getString(R.string.category_school);
        } else if (position == FOOD) {
            return mContext.getString(R.string.category_food);
        } else if (position == ATTRATION) {
            return mContext.getString(R.string.category_attraction);
        } else {
            return mContext.getString(R.string.category_shopping);
        }
    }

}
