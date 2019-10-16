package mk.com.barambe.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import mk.com.barambe.fragment.CategoryFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final List<String> data;

    public SectionsPagerAdapter(FragmentManager fm, List<String> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return CategoryFragment.newInstance(data.get(position));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return data.get(position);
    }

}
