package pt.aodispor.aodispor_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

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

    /**
     * This method is called when the main activity is created.
     * @param savedInstanceState object with saved states of previously created activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabPagerAdapter mSectionsPagerAdapter;
        mSectionsPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        mViewPager = (MyViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    /**
     * Returns this activity's custom ViewPager.
     * @return the custom ViewPager.
     */
    public MyViewPager getViewPager(){
        return mViewPager;
    }
}
