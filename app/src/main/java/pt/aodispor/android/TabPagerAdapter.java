package pt.aodispor.android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pt.aodispor.android.profile.Profile;
import pt.aodispor.android.profile.TabbedUserArea;
import pt.aodispor.android.request.UserRequestFragment;

/**
 * Custom FragmentPagerAdapter class to control any sort of fragment class and to select the text
 * to be shown in the tabs.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {
    /** Number of pages this adapter supports. */
    private final int NUMBER_OF_PAGES = 2;
    //TODO mudar se necessário private ProfileFragment profileFragment;
    private Profile userProfileFragment;
    private CardFragment cardFragment;
    private UserRequestFragment requestFormFragment;

    /**
     * The TabPagerAdapter constructor.
     * @param fm the fragment adapter.
     */
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * This method returns the fragment corresponding with the position in the tab page.
     * @param position the number of the position of the tab. Starts in 0.
     * @return the fragment corresponding with the number of the tab page.
     */
    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch (position){
            case 0:
                f = new Profile();
                //f = ProfileFragment.newInstance();
                //TODO mudar se necessário profileFragment = (ProfileFragment) f;
                //profileFragment = (ProfileFragment) f;
                userProfileFragment = (Profile) f;
                break;
            case 1:
                f = CardFragment.newInstance();
                cardFragment = (CardFragment) f;
                break;
            case 2:
                f = requestFormFragment.newInstance();
                requestFormFragment = (UserRequestFragment) f;
                break;
        }
        return f;
    }

    /* TODO mudar se necessário
    public ProfileFragment getProfileFragment() {
        return profileFragment;
    }
    */

    public Profile getUserProfileFragment() {
        return userProfileFragment;
    }

    public CardFragment getCardFragment() {
        return cardFragment;
    }

    /**
     * This method returns the number of the pages this TabPagerAdapter has.
     * @return the number of pages.
     */
    @Override
    public int getCount() {
        return NUMBER_OF_PAGES;
    }

    /**
     * This method returns the title of a tab page in this TabPagerAdapter.
     * @param position the number of the position of the tab. Starts in 0.
     * @return the title of the corresponding tab.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Perfil";
            case 1:
                return "Encontrar";
            case 2:
                return "Pedido";
        }
        return null;
    }
}
