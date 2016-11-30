package pt.aodispor.aodispor_android;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


//import pt.aodispor.aodispor_android.Permissions.Permission;

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

    /**for debug purposes, skips initial login if set to true. should be false in releases*/
    private static final boolean SKIP_LOGIN = false;

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

        if(!SKIP_LOGIN) {
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
                //validationDialog();
            }
        });

        dialog.show();
    }

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

    /** Creates pager and load cardFragment and ProfileFragment */
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

}
