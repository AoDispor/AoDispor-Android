package pt.aodispor.android.features.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pt.aodispor.android.features.cardstack.CardFragment;
import pt.aodispor.android.features.profile.ProfileFragment;
import pt.aodispor.android.features.userrequest.UserRequestFragment;

/**
 * Custom FragmentPagerAdapter class to control any sort of fragment class and to select the text
 * to be shown in the tabs.
 */
class TabPagerAdapter extends FragmentPagerAdapter {
    /**
     * Number of pages this adapter supports.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int NUMBER_OF_PAGES = 4;

    static final int cardStackItem = 1;
    static final int AboutItem = 0;
    static final int ProfileItem = 2;
    static final int RequestsItem = 3;

    //TODO mudar se necessário private ProfileFragment profileFragment;
    private ProfileFragment userProfileFragment;
    private AboutAoDisporFragment aboutAoDisporFragment;
    private CardFragment cardFragment;
    private UserRequestFragment requestFormFragment;

    /**
     * The TabPagerAdapter constructor.
     *
     * @param fm the fragment adapter.
     */
    public TabPagerAdapter(FragmentManager fm/*, int numb_pages*/) {
        super(fm);
        //NUMBER_OF_PAGES = numb_pages;
    }

    /**
     * This method returns the fragment corresponding with the position in the tab page.
     *
     * @param position the number of the position of the tab. Starts in 0.
     * @return the fragment corresponding with the number of the tab page.
     */
    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch (position) {
            case 1:
                f = CardFragment.newInstance();
                cardFragment = (CardFragment) f;
                break;
            case 0:
                f = new AboutAoDisporFragment();
                aboutAoDisporFragment = (AboutAoDisporFragment) f;
                break;
            case 2:
                f = new ProfileFragment();
                //f = ProfileFragment.newInstance();
                //TODO mudar se necessário profileFragment = (ProfileFragment) f;
                //profileFragment = (ProfileFragment) f;
                userProfileFragment = (ProfileFragment) f;
                break;
            case 3:
                f = new UserRequestFragment();
                requestFormFragment = (UserRequestFragment) f;
                break;
        }
        return f;
    }

    CardFragment getCardFragment() {
        return cardFragment;
    }

    /**
     * This method returns the number of the pages this TabPagerAdapter has.
     *
     * @return the number of pages.
     */
    @Override
    public int getCount() {
        return NUMBER_OF_PAGES;
    }

    /**
     * This method returns the title of a tab page in this TabPagerAdapter.
     *
     * @param position the number of the position of the tab. Starts in 0.
     * @return the title of the corresponding tab.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1:
                return "Encontrar";
            case 0:
                return "Sobre";
            case 2:
                return "Perfil";
            case 3:
                return "Pedido";
        }
        return null;
    }
}
