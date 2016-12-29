package pt.aodispor.aodispor_android;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * This class serves as the main activity for the application which extends AppCompatActivity.
 * <p>
 * This is where the application initializes its lifecycle. This activity has a custom ViewPager
 * object that holds the pages of a tabbed view and has also a TabPagerAdapter that controls
 * the page switching for the tabbed pages.
 * </p>
 */
public class MainActivity extends AppCompatActivity {
    private MyViewPager mViewPager;

    int mLastPage = 0;

    /**
     * This method is called when the main activity is created.
     *
     * @param savedInstanceState object with saved states of previously created activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        installFonts();

        if(!AppDefinitions.SKIP_LOGIN) {
            Permission.requestPermission(this, AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER);
        }
        else startPagerAndMainContent();
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
            CardFragment cardFrag = (CardFragment) getSupportFragmentManager().getFragments().get(0);
            android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) findViewById(R.id.searchView);
            searchView.setQuery(query, false);
            cardFrag.setSearchQuery(query);
            cardFrag.setupNewStack();
            mViewPager.setCurrentItem(1, true);
            searchView.clearFocus();
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
                switch (mLastPage) {
                    case 0:
                        ((ImageView) findViewById(R.id.profile_icon)).setImageResource(R.mipmap.ic_account_circle_black_48dp);
                        break;
                    case 1:
                        ((ImageView) findViewById(R.id.stack_icon)).setImageResource(R.mipmap.ic_library_books_black_48dp);
                        break;
                }
                switch (mViewPager.getCurrentItem()) {
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
                switch (mLastPage) {
                    case 0:
                        ((ImageView) findViewById(R.id.profile_icon)).setImageResource(R.mipmap.ic_account_circle_black_48dp);
                        break;
                    case 1:
                        ((ImageView) findViewById(R.id.stack_icon)).setImageResource(R.mipmap.ic_library_books_black_48dp);
                        break;
                }
                switch (position) {
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

        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
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
                mViewPager.setCurrentItem(1, true);
                searchView.clearFocus();
                return true;
            }
        });
    }


    //region LOGIN DIALOGS & HANDLERS

    /** shows phone number request dialog for login */
    private void loginDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.phonenumber_request);
        dialog.setTitle("LOGIN");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button loginButton = (Button) dialog.findViewById(R.id.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Permission.requestPermission(MainActivity.this,AppDefinitions.PERMISSIONS_REQUEST_READ_SMS);
            }
        });

        dialog.show();
    }

    /** shows validate SMS dialog */
    private void validationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.sms_validation);
        dialog.setTitle("LOGIN");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button loginButton = (Button) dialog.findViewById(R.id.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startPagerAndMainContent();
            }
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //Realizado sempre independentemente do tipo de permissao
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission Granted.
            Toast.makeText(MainActivity.this, getResources().getString(R.string.permisson_accepted), Toast.LENGTH_SHORT).show();
        } else  if (grantResults[0] == PackageManager.PERMISSION_DENIED){
            // Permission Denied
            Toast.makeText(MainActivity.this, getResources().getString(R.string.permisson_denied), Toast.LENGTH_SHORT).show();
        }

        //Realizado dependendo do tipo de permissao
        switch (requestCode) {
            case AppDefinitions.PERMISSIONS_REQUEST_READ_SMS:
                validationDialog();
                break;
            case AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER:
                loginDialog();
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //endregion

}
