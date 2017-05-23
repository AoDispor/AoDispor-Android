package pt.aodispor.android.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.aodispor.android.MyViewPager;
import pt.aodispor.android.R;
import pt.aodispor.android.TabPagerAdapter;

public class TabbedUserArea extends Fragment {
    private final static int PROFILE = 0;
    private final static int SETTINGS = 1;
    private MyViewPager viewPager;

    /**
     * Factory method to create a new instance of ProfileFragment class. This is needed because of how
     * a ViewPager handles the creation of a Fragment.
     *
     * @return the ProfileFragment object created.
     */
    public static TabbedUserArea newInstance() {
        return new TabbedUserArea();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.tabbed_profile, container, false);
        viewPager = (MyViewPager) profileView.findViewById(R.id.tabbedProfileViewPager);
        viewPager.setAdapter(new TabbedProfilePagerAdapter(getActivity().getSupportFragmentManager()));
        viewPager.setCurrentItem(PROFILE);
        viewPager.setSwipeEnabled(false);
        return profileView;
    }

    private class TabbedProfilePagerAdapter extends TabPagerAdapter {

        public TabbedProfilePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment f = null;
            switch (i) {
                case PROFILE:
                    f = UserAreaFragment.newInstance();
                    break;
                case SETTINGS:
                    f = SettingsFragment.newInstance();
                    break;
            }
            return f;
        }
    }

}
