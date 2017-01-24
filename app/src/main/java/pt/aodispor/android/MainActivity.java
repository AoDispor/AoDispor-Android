package pt.aodispor.android;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.Register;

import static pt.aodispor.android.AppDefinitions.PASSWORD_SMS_PHONES;

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

        installFonts();

        TextView titleView = (TextView) findViewById(R.id.app_title);
        titleView.setTypeface(AppDefinitions.dancingScriptRegular);

        if (!AppDefinitions.SKIP_LOGIN) {
            Permission.requestPermission(this, AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER);
        } else {
            AppDefinitions.phoneNumber = AppDefinitions.testPhoneNumber;
            AppDefinitions.userPassword = AppDefinitions.testPassword;
            startPagerAndMainContent();
        }
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
            CardFragment cardFrag = null;
            for(Object frag : getSupportFragmentManager().getFragments())
                if(frag instanceof CardFragment) cardFrag = (CardFragment) frag;
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
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabPagerAdapter mSectionsPagerAdapter;
        mSectionsPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        mViewPager = (MyViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        profileView = ((ImageView) findViewById(R.id.profile_icon));
        stackView = ((ImageView) findViewById(R.id.stack_icon));
        stackView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));
        profileView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(positionOffset != 0) {
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
                CardFragment cardFrag = null;
                for(Object frag : getSupportFragmentManager().getFragments())
                    if(frag instanceof CardFragment) cardFrag = (CardFragment) frag;
                cardFrag.setSearchQuery(query);
                cardFrag.setupNewStack();
                mViewPager.setCurrentItem(1, true);
                searchView.clearFocus();
                return true;
            }
        });
    }


    //region LOGIN DIALOGS & HANDLERS

    /**
     * shows phone number request dialog for login
     */
    private void loginDialog(final String phoneNumber) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.phonenumber_request);
        //dialog.setTitle("LOGIN");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button loginButton = (Button) dialog.findViewById(R.id.button);
        if (phoneNumber != null) {
            EditText phoneEditText = (EditText) dialog.findViewById(R.id.phonebox);
            phoneEditText.setText(phoneNumber);
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String aux = ((EditText) dialog.findViewById(R.id.phonebox)).getText().toString();
                if (!Utility.validPhoneNumber(aux)) {
                    dialog.dismiss();
                    loginDialog(phoneNumber);
                    return;
                }
                AppDefinitions.phoneNumber = "+" +
                        ((EditText) dialog.findViewById(R.id.phonebox_country)).getText().toString() + " " +
                        aux.substring(0, 3) + " " +
                        aux.substring(3, 6) + " " +
                        aux.substring(6, 9);
                dialog.dismiss();

                HttpRequestTask request_register = new HttpRequestTask(
                        String.class, null, REGISTER_URL);
                request_register.setMethod(HttpRequestTask.POST_REQUEST);
                request_register.setJSONBody(new Register(AppDefinitions.phoneNumber));
                request_register.execute();

                Permission.requestPermission(MainActivity.this, AppDefinitions.PERMISSIONS_REQUEST_READ_SMS);
            }
        });

        dialog.show();
    }

    /**
     * shows validate SMS dialog
     */
    private void validationDialog(String received_sms) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.sms_validation);
        //dialog.setTitle("LOGIN");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button loginButton = (Button) dialog.findViewById(R.id.button);
        if (received_sms != null) {
            Log.e("X2",received_sms);
            try {
                EditText phoneEditText = (EditText) dialog.findViewById(R.id.password_box);
                phoneEditText.setText(Utility.parseSMS(received_sms));
            } catch (Exception e) {
            }
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDefinitions.userPassword = ((EditText) dialog.findViewById(R.id.password_box)).getText().toString();
                dialog.dismiss();
                startPagerAndMainContent();
            }
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        advance(requestCode,permissions,grantResults);
    }

    /**
     * Proceeds to next dialog (or ends) the login process.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void advance(int requestCode, String[] permissions, int[] grantResults)
    {
        //Realizado sempre independentemente do tipo de permissao
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission Granted.
            Toast.makeText(MainActivity.this, getResources().getString(R.string.permisson_accepted), Toast.LENGTH_SHORT).show();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // Permission Denied
            Toast.makeText(MainActivity.this, getResources().getString(R.string.permisson_denied), Toast.LENGTH_SHORT).show();
        }

        //Realizado dependendo do tipo de permissao
        switch (requestCode) {
            case AppDefinitions.PERMISSIONS_REQUEST_READ_SMS:
                String rec_password = null;
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) //find token on inbox sms
                    try {
                        for(int i = 0; i<PASSWORD_SMS_PHONES.length; ++i) {
                            rec_password = Utility.getLastMessageBody(getApplicationContext(), PASSWORD_SMS_PHONES[i]);
                            if (rec_password!=null) break;
                        }
                    } catch (Exception e) {
                    }
                validationDialog(rec_password);
                break;
            case AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER:
                String phoneNumber = null;
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    phoneNumber = Utility.getPhoneNumber(getApplicationContext());
                loginDialog(phoneNumber);
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //endregion


    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 1) {
            CardFragment cardFragment = ((TabPagerAdapter) mViewPager.getAdapter()).getCardFragment();
            cardFragment.restorePreviousCard();
        } else {
            super.onBackPressed();
        }
    }

}
