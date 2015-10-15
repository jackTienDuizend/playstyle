package examplejack.com.playstyle.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import examplejack.com.playstyle.ui.FriendsFragment;
import examplejack.com.playstyle.ui.InboxFragment;

/**
 * Created by jack on 23-8-2015.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Inbox", "Friends" };
    private Context context;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return InboxFragment.newInstance(position);
            case 1:
                return FriendsFragment.newInstance(position);
        default:
           break;
        }
        return null;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}