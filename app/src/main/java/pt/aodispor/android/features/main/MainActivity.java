package pt.aodispor.android.features.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.stkent.amplify.prompt.DefaultLayoutPromptView;
import com.github.stkent.amplify.tracking.Amplify;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Arrays;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.features.cardstack.CardFragment;
import pt.aodispor.android.features.cardstack.CardStack;
import pt.aodispor.android.features.cardstack.GeoLocation;
import pt.aodispor.android.features.login.Advanceable;

/**
 * This class serves as the main activity for the application which extends AppCompatActivity.
 * <p>
 * This is where the application initializes its lifecycle. This activity has a custom ViewPager
 * object that holds the pages of a tabbed view and has also a TabPagerAdapter that controls
 * the page switching for the tabbed pages.
 * </p>
 */
public class MainActivity extends AppCompatActivity
        implements Advanceable, NavigationView.OnNavigationItemSelectedListener {
    private MyViewPager mViewPager;
    private SearchView searchView;
    // private ImageView profileView, stackView;

    /**
     * This method is called when the main activity is created.
     *
     * @param savedInstanceState object with saved states of previously created activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO arranjar uma maneira de centralizar esta cena do token
        HttpRequestTask.setToken(getResources().getString(R.string.ao_dispor_api_key));

        if (savedInstanceState == null) {
            DefaultLayoutPromptView promptView = (DefaultLayoutPromptView) findViewById(R.id.prompt_view);
            Amplify.getSharedInstance().promptIfReady(promptView);
        }

        installFonts();

        startPagerAndMainContent();

        TextView titleView = (TextView) findViewById(R.id.app_title);
        titleView.setTypeface(AppDefinitions.dancingScriptRegular);
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchView searchView = (SearchView) findViewById(R.id.searchView);
                searchView.setQuery("", false);
                searchView.clearFocus();

                CardFragment cardFragment = ((TabPagerAdapter) mViewPager.getAdapter()).getCardFragment();
                cardFragment.setSearchQuery("");
                cardFragment.prepareNewStack();
                //FIXME isto precisa de ter uma maneira para limpar a string pesquisada
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setNavState(navigationView);
    }

    private void setNavState(NavigationView navigationView) {
        //hide specific options
        final int[] logged_features_only = new int[]{R.id.nav_profile, R.id.nav_requests};
        final int[] not_logged_features_only = new int[]{R.id.nav_login};
        final int[] features_to_hide = AppDefinitions.smsLoginDone ? not_logged_features_only : logged_features_only;

        Menu menu = navigationView.getMenu();

        for (int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++) {
            MenuItem menuItem = menu.getItem(menuItemIndex);
            if (Arrays.binarySearch(features_to_hide, menuItem.getItemId()) >= 0) {
                menuItem.setVisible(false);
            }
        }

        //had hamburger menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_content);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//LOCK_MODE_LOCKED_CLOSED

        Button drawer_btn = (Button) findViewById(R.id.menu_button);
        drawer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_content);
                //avoid freezing the card before opening drawer
                ((TabPagerAdapter) mViewPager.getAdapter()).getCardFragment().BLOCK_INTERACTIONS();
                drawer.openDrawer(GravityCompat.START);
                //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_content);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                ((TabPagerAdapter) mViewPager.getAdapter()).getCardFragment().UNBLOCK_INTERACTIONS();
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_content);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        };
        drawer.addDrawerListener(toggle);
    }

    /**
     * Returns this activity's custom ViewPager.
     *
     * @return the custom ViewPager.
     */
    public MyViewPager getViewPager() {
        return mViewPager;
    }

    public void installFonts() {
        AppDefinitions.dancingScriptRegular = Typeface.createFromAsset(getAssets(), "fonts/dancing-script-ot/DancingScript-Regular.otf");
        AppDefinitions.yanoneKaffeesatzBold = Typeface.createFromAsset(getAssets(), "fonts/Yanone-Kaffeesatz/YanoneKaffeesatz-Bold.otf");
        AppDefinitions.yanoneKaffeesatzLight = Typeface.createFromAsset(getAssets(), "fonts/Yanone-Kaffeesatz/YanoneKaffeesatz-Light.otf");
        AppDefinitions.yanoneKaffeesatzRegular = Typeface.createFromAsset(getAssets(), "fonts/Yanone-Kaffeesatz/YanoneKaffeesatz-Regular.otf");
        AppDefinitions.yanoneKaffeesatzThin = Typeface.createFromAsset(getAssets(), "fonts/Yanone-Kaffeesatz/YanoneKaffeesatz-Thin.otf");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            launchSearch(query);
        }
    }

    /**
     * Creates header components ;
     * <br>Initialize and configure ImageLoader ;
     * <br>Creates pager, loads cardFragment & ProfileFragment ;
     */
    private void startPagerAndMainContent() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabPagerAdapter mSectionsPagerAdapter;
        mSectionsPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        //android.support.v4.view.ViewPager cannot be cast to pt.aodispor.android.MyViewPager
        mViewPager = (MyViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        searchView = (SearchView) findViewById(R.id.searchView);
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    closeSearchView();
                }
            }
        });
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                /**
                 * Get all the words in the query to get rid of spaces and then
                 * build another String with all the words in order separated by
                 * only one space in between each of them.
                 */
                String[] words = query.split("\\s+");
                String newQuery = "";
                for (int i = 0; i < words.length; i++) {
                    newQuery += words[i];
                    if (i < words.length - 1) {
                        newQuery += " ";
                    }
                }
                if (newQuery.length() >= 5) {
                    CardFragment cardFrag = null;
                    for (Object frag : getSupportFragmentManager().getFragments()) {
                        if (frag instanceof CardFragment) {
                            cardFrag = (CardFragment) frag;
                        }
                    }
                    if (cardFrag != null) {
                        cardFrag.setSearchQuery(query);
                        cardFrag.prepareNewStack();
                    }
                    mViewPager.setCurrentItem(1, true);
                    closeSearchView();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.search_bar_toast), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        if (!AppDefinitions.smsLoginDone) {
            //profileView.setVisibility(View.INVISIBLE);
            //stackView.setVisibility(View.INVISIBLE);
            mViewPager.setSwipeEnabled(false); // impedir o swipe se o utilizador não estiver loggado
            mViewPager.setEnabled(false);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        advance(requestCode, permissions, grantResults);
    }

    /**
     * Proceeds to next dialog (or ends) the login process.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void advance(int requestCode, String[] permissions, int[] grantResults) {
        //Realizado dependendo do tipo de permissao
        switch (requestCode) {
            case AppDefinitions.PERMISSIONS_REQUEST_GPS:
                CardFragment cardFragment = ((TabPagerAdapter) mViewPager.getAdapter()).getCardFragment();
                ///cardFragment.updateGeoLocation();
                GeoLocation.getInstance().updateLatLon(this);
                cardFragment.prepareNewSearchQuery(false);
                break;
            default:
                if (android.os.Build.VERSION.SDK_INT >= 23)
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //endregion


    @Override
    public void onBackPressed() {
        /**
         * Close the search view if its already open.
         *
         if(!searchView.isIconified()) {
         closeSearchView();
         }*/
        searchView.setIconified(true);
        if (mViewPager.getCurrentItem() == 1) {
            CardFragment cardFragment = ((TabPagerAdapter) mViewPager.getAdapter()).getCardFragment();
            cardFragment.restorePreviousCard();
        } else {
            super.onBackPressed();
        }
    }

    private void launchSearch(String query) {
        if (query.length() < AppDefinitions.QUERY_MIN_LENGTH) {
            Toast.makeText(this, R.string.query_too_short, Toast.LENGTH_SHORT).show();
            return;
        }
        CardFragment cardFrag = null;
        for (Object frag : getSupportFragmentManager().getFragments())
            if (frag instanceof CardFragment) cardFrag = (CardFragment) frag;
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQuery(query, false);
        cardFrag.setSearchQuery(query);
        cardFrag.prepareNewStack();
        mViewPager.setCurrentItem(1, true);
        searchView.clearFocus();
    }

    private void closeSearchView() {
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            mViewPager.setCurrentItem(1);
        } else if (id == R.id.nav_profile) {
            mViewPager.setCurrentItem(0);
        } else if (id == R.id.nav_requests) {
            mViewPager.setCurrentItem(2);
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_login) {
            //CardStack.stopCardStackActivities();//TODO not sure this s best solution ... =/
            Intent showSplitActivityActivity = new Intent(MainActivity.this, SplitActivity.class);
            showSplitActivityActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(showSplitActivityActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_content);
        drawer.closeDrawer(GravityCompat.START);
        return true;
        //java.lang.ClassCastException: android.support.design.widget.CoordinatorLayout cannot be cast to android.support.v4.widget.DrawerLayout
    }
}
