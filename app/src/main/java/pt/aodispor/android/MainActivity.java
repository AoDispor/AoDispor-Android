package pt.aodispor.android;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.stkent.amplify.prompt.DefaultLayoutPromptView;
import com.github.stkent.amplify.tracking.Amplify;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.Register;

import static pt.aodispor.android.AppDefinitions.PASSWORD_SMS_PHONES;

import pt.aodispor.android.Advanceable;

/**
 * This class serves as the main activity for the application which extends AppCompatActivity.
 * <p>
 * This is where the application initializes its lifecycle. This activity has a custom ViewPager
 * object that holds the pages of a tabbed view and has also a TabPagerAdapter that controls
 * the page switching for the tabbed pages.
 * </p>
 */
public class MainActivity extends AppCompatActivity implements Advanceable {
    private MyViewPager mViewPager;
    private SearchView searchView;
    private ImageView profileView, stackView;

    int mLastPage = 0;

    private final String REGISTER_URL = "https://api.aodispor.pt/users/register";

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
                cardFragment.setupNewStack(cardFragment.prepareNewStack());
                //FIXME isto precisa de ter uma maneira para limpar a string pesquisada
            }
        });
    }

    /*protected void onPostCreate (Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }*/

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

    public void changeFrag(View view) {
        String viewID = getResources().getResourceEntryName(view.getId());
        switch (viewID) {
            case "profile_icon":
                mViewPager.setCurrentItem(0);
                break;
            case "stack_icon":
                mViewPager.setCurrentItem(1);
                break;
            case "fav_icon":
                mViewPager.setCurrentItem(2);
                break;
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

        mViewPager = (MyViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1); //TODO Mostrar o perfil se a pessoa se tiver registado

        profileView = ((ImageView) findViewById(R.id.profile_icon));
        stackView = ((ImageView) findViewById(R.id.stack_icon));

        stackView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));
        profileView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black));

        if(AppDefinitions.smsLoginDone) {
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (positionOffset != 0) {
                        profileView.setColorFilter(ColorUtils.blendARGB(ContextCompat.getColor(getApplicationContext(), R.color.white), ContextCompat.getColor(getApplicationContext(), R.color.black), positionOffset));
                        stackView.setColorFilter(ColorUtils.blendARGB(ContextCompat.getColor(getApplicationContext(), R.color.black), ContextCompat.getColor(getApplicationContext(), R.color.white), positionOffset));
                    }
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        searchView = (SearchView) findViewById(R.id.searchView);
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
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
                    if(i < words.length - 1) {
                        newQuery += " ";
                    }
                }
                if(newQuery.length() >= 5) {
                    CardFragment cardFrag = null;
                    for(Object frag : getSupportFragmentManager().getFragments()) {
                        if (frag instanceof CardFragment) {
                            cardFrag = (CardFragment) frag;
                        }
                    }
                    if(cardFrag != null) {
                        cardFrag.setSearchQuery(query);
                        cardFrag.setupNewStack(cardFrag.prepareNewStack());
                    }
                    mViewPager.setCurrentItem(1, true);
                    closeSearchView();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.search_bar_toast), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        if(!AppDefinitions.smsLoginDone) {
            profileView.setVisibility(View.INVISIBLE);
            stackView.setVisibility(View.INVISIBLE);
            mViewPager.setSwipeEnabled(false); // impedir o swipe se o utilizador não estiver loggado
            mViewPager.setEnabled(false);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
                CardFragment cardFragment =  ((TabPagerAdapter) mViewPager.getAdapter()).getCardFragment();
                cardFragment.updateGeoLocation();
                cardFragment.setupNewStack(cardFragment.prepareNewSearchQuery(false));
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

    private void launchSearch(String query)
    {
        if(query.length()<AppDefinitions.QUERY_MIN_LENGTH)
        {
            Toast.makeText(this, R.string.query_too_short, Toast.LENGTH_SHORT).show();
            return;
        }
        CardFragment cardFrag = null;
        for(Object frag : getSupportFragmentManager().getFragments())
            if(frag instanceof CardFragment) cardFrag = (CardFragment) frag;
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQuery(query, false);
        cardFrag.setSearchQuery(query);
        cardFrag.setupNewStack(cardFrag.prepareNewStack());
        mViewPager.setCurrentItem(1, true);
        searchView.clearFocus();
    }

    private void closeSearchView() {
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);
    }

}
