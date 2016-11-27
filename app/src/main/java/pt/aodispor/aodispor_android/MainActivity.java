package pt.aodispor.aodispor_android;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
    private static final boolean SKIP_LOGIN=false;

    /**
     * This method is called when the main activity is created.
     * @param savedInstanceState object with saved states of previously created activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        installFonts();
        if(!SKIP_LOGIN) loginDialog();
        else            startPagerAndMainContent();
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

    private void loginDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.phonenumber_request);
        dialog.setTitle("LOGIN");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button loginButton= (Button) dialog.findViewById(R.id.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                validationDialog();
            }
        });

        dialog.show();

    }

    private void validationDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.sms_validation);
        dialog.setTitle("LOGIN");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button loginButton= (Button) dialog.findViewById(R.id.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startPagerAndMainContent();
            }
        });

        dialog.show();
    }

    private void startPagerAndMainContent()
    {
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

}
