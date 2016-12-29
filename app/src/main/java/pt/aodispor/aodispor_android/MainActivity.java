package pt.aodispor.aodispor_android;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * This class serves as the main activity for the application which extends AppCompatActivity.
 * <p>
 *     This is where the application initializes its lifecycle. This activity has a custom ViewPager
 *     object that holds the pages of a tabbed view and has also a TabPagerAdapter that controls
 *     the page switching for the tabbed pages.
 * </p>
 */
public class MainActivity extends AppCompatActivity {
    private MyViewPager mViewPager;

    int mLastPage = 0;

    /**
     * This method is called when the main activity is created.
     * @param savedInstanceState object with saved states of previously created activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        installFonts();

        // Set title font
        TextView titleView = (TextView) findViewById(R.id.app_title);
        titleView.setTypeface(AppDefinitions.dancingScriptRegular);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabPagerAdapter mSectionsPagerAdapter;
        mSectionsPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        mViewPager = (MyViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch(mLastPage) {
                    case 0:
                        ((ImageView) findViewById(R.id.profile_icon)).setImageResource(R.mipmap.ic_account_circle_black_48dp);
                        break;
                    case 1:
                        ((ImageView) findViewById(R.id.stack_icon)).setImageResource(R.mipmap.ic_library_books_black_48dp);
                        break;
                }
                switch(mViewPager.getCurrentItem()) {
                    case 0:
                        ((ImageView) findViewById(R.id.profile_icon)).setImageResource(R.mipmap.ic_account_circle_white_48dp);
                        break;
                    case 1:
                        ((ImageView) findViewById(R.id.stack_icon)).setImageResource(R.mipmap.ic_library_books_white_48dp);
                        break;
                }
                mLastPage = mViewPager.getCurrentItem();
            }

            @Override
            public void onPageSelected(int position) {
                switch(mLastPage) {
                    case 0:
                        ((ImageView) findViewById(R.id.profile_icon)).setImageResource(R.mipmap.ic_account_circle_black_48dp);
                        break;
                    case 1:
                        ((ImageView) findViewById(R.id.stack_icon)).setImageResource(R.mipmap.ic_library_books_black_48dp);
                        break;
                }
                switch(position) {
                    case 0:
                        ((ImageView) findViewById(R.id.profile_icon)).setImageResource(R.mipmap.ic_account_circle_white_48dp);
                        break;
                    case 1:
                        ((ImageView) findViewById(R.id.stack_icon)).setImageResource(R.mipmap.ic_library_books_white_48dp);
                        break;
                }
                mLastPage = mViewPager.getCurrentItem();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                CardFragment cardFrag = (CardFragment) getSupportFragmentManager().getFragments().get(0);
                cardFrag.setSearchQuery(query);
                cardFrag.setupNewStack();
                return true;
            }
        });
    }

    /**
     * Returns this activity's custom ViewPager.
     * @return the custom ViewPager.
     */
    public MyViewPager getViewPager(){
        return mViewPager;
    }

    public void installFonts() {
        AppDefinitions.dancingScriptRegular = Typeface.createFromAsset(getAssets(),"fonts/dancing-script-ot/DancingScript-Regular.otf");
        AppDefinitions.yanoneKaffeesatzBold = Typeface.createFromAsset(getAssets(),"fonts/Yanone-Kaffeesatz/YanoneKaffeesatz-Bold.otf");
        AppDefinitions.yanoneKaffeesatzLight = Typeface.createFromAsset(getAssets(),"fonts/Yanone-Kaffeesatz/YanoneKaffeesatz-Light.otf");
        AppDefinitions.yanoneKaffeesatzRegular = Typeface.createFromAsset(getAssets(),"fonts/Yanone-Kaffeesatz/YanoneKaffeesatz-Regular.otf");
        AppDefinitions.yanoneKaffeesatzThin = Typeface.createFromAsset(getAssets(),"fonts/Yanone-Kaffeesatz/YanoneKaffeesatz-Thin.otf");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            CardFragment cardFrag = (CardFragment) getSupportFragmentManager().getFragments().get(0);
            cardFrag.setSearchQuery(query);
            cardFrag.setupNewStack();
        }
    }

    public void changeFrag(View view) {
        String viewID = getResources().getResourceEntryName(view.getId());
        switch(viewID) {
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

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 1) {
            CardFragment cardFragment = ((TabPagerAdapter)mViewPager.getAdapter()).getCardFragment();
            cardFragment.restorePreviousCard();
        } else {
            super.onBackPressed();
        }
    }
}
