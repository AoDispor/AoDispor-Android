package pt.aodispor.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabbedProfile extends Fragment {
    private final static int PROFILE = 0;
    private final static int SETTINGS = 1;
    private MyViewPager viewPager;

    /**
     * Factory method to create a new instance of ProfileFragment class. This is needed because of how
     * a ViewPager handles the creation of a Fragment.
     *
     * @return the ProfileFragment object created.
     */
    public static TabbedProfile newInstance() {
        return new TabbedProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.tabbed_profile, container, false);
        viewPager = (MyViewPager) profileView.findViewById(R.id.tabbedProfileViewPager);
        viewPager.setAdapter(new TabbedProfilePagerAdapter(getActivity().getSupportFragmentManager()));
        viewPager.setCurrentItem(PROFILE);
        return profileView;
    }

    private class TabbedProfilePagerAdapter extends TabPagerAdapter{

        public TabbedProfilePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment f = null;
            switch (i) {
                case 0:
                    f = NewProfileFragment.newInstance();
                    break;
                case 1:
                    f = SettingsFragment.newInstance();
                    break;
            }
            return f;
        }
    }

}
