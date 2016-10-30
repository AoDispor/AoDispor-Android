/*package pt.aodispor.aodispor_android;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.test.ApplicationTestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import pt.aodispor.aodispor_android.API.SearchQueryResult;

import static org.junit.Assert.*;


@RunWith(RobolectricGradleTestRunner.class)
//@RunWith(RobolectricTestRunner.class)
public class CardFragmentTTest extends ApplicationTestCase<Application> {

    public CardFragmentTTest() {
        super(Application.class);
    }

    MainActivity mainActivity;
    CardFragment mainActivityFragment;

    @Before
    public void setUp() {
        mainActivity = Robolectric.setupActivity(MainActivity.class);
        mainActivityFragment = new CardFragment();
        startFragment(mainActivityFragment);
    }

    @Test
    public void testMainActivity() {
        Assert.assertNotNull(mainActivity);
    }

    @Test
    public void test1() {
        //assertThat(mainActivityFragment.calculateYoungCows(10)).isEqualTo(2);
        //assertThat(mainActivityFragment.calculateYoungCows(99)).isEqualTo(3);
        mainActivityFragment.searchQuery = "arqueologo";
        //cf.rootView = (RelativeLayout) (new LayoutInflater()).inflate(R.layout.card_zone, container, false);
        mainActivityFragment.setupNewStack();
//        wait(1000);
        while(mainActivityFragment.getCurrentSet()==null);
        SearchQueryResult queryResult = mainActivityFragment.getCurrentSet();
        for(int i = 0; i < queryResult.meta.pagination.getTotal();++i)
        {
            //while (cf.blockAccess) wait(200);
            queryResult = mainActivityFragment.getCurrentSet();
            assertEquals(mainActivityFragment.getCurrentShownCardProfessionalName(),queryResult.data.get(i));
            mainActivityFragment.discardTopCard();
        }
    }

    private void startFragment( Fragment fragment ) {
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null );
        fragmentTransaction.commit();
    }
}*/