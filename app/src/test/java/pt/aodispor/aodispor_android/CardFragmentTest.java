package pt.aodispor.aodispor_android;


import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.junit.Assert;
import org.junit.Test;

import pt.aodispor.aodispor_android.API.SearchQueryResult;


import static android.R.attr.fragment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
/*public class CardStackUnitTest {

    @Test
    public void shown_cards_order_isCorrect() throws Exception {
        MainActivity mm = new MainActivity();
        CardFragment cf = new CardFragment();
        //mm.addContentView(cf.rootView,null);

        cf.searchQuery = "arqueologo";
        //cf.rootView = (RelativeLayout) (new LayoutInflater()).inflate(R.layout.card_zone, container, false);
        cf.setupNewStack();
//        wait(1000);
        //while(cf.getCurrentSet()==null);
        SearchQueryResult queryResult = cf.getCurrentSet();
        for(int i = 0; i < queryResult.meta.pagination.getTotal();++i)
        {
            //while (cf.blockAccess) wait(200);
            queryResult = cf.getCurrentSet();
            assertEquals(cf.getCurrentShownCardProfessionalName(),queryResult.data.get(i));
            cf.discardTopCard();
        }
    }
}*/

/*import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=21)
public class CardFragmentTest extends ApplicationTestCase<Application> {

    public CardFragmentTest() {
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
        //while(cf.getCurrentSet()==null);
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

import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

/*import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;*/

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Robolectric;
//import org.robolectric.RobolectricGradleTestRunner;
//import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

//import static org.robolectric.*;
//import burrows.apps.example.template.R;
//import burrows.apps.example.template.test.TestBase;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.instanceOf;
//import static org.hamcrest.Matchers.is;
import static org.robolectric.util.FragmentTestUtil.startFragment;
//import static org.robolectric.util.SupportFragmentTestUtil.startFragment;
//import static org.robolectric.util.SupportFragmentTestUtil.startVisibleFragment;


/*@RunWith(RobolectricGradleTestRunner.class)
//@RunWith(RobolectricTestRunner.class)
@SuppressWarnings({"ConstantConditions", "ResourceType"})
public class CardFragmentTest {

    CardFragment fragment;

    @Before
    public void setUp() {
        fragment = new CardFragment();
        startFragment(fragment, FragmentActivity.class);
    }

    // --------------------------------------------
    // Testing the Fragment itself
    // --------------------------------------------

    @Test
    public void test_startFragmentView() {
        //assertThat(mainActivityFragment.calculateYoungCows(10)).isEqualTo(2);
        //assertThat(mainActivityFragment.calculateYoungCows(99)).isEqualTo(3);
        fragment.searchQuery = "arqueologo";
        //cf.rootView = (RelativeLayout) (new LayoutInflater()).inflate(R.layout.card_zone, container, false);
        fragment.setupNewStack();
//        wait(1000);
        //while(cf.getCurrentSet()==null);
        SearchQueryResult queryResult = fragment.getCurrentSet();
        for(int i = 0; i < queryResult.meta.pagination.getTotal();++i)
        {
            //while (cf.blockAccess) wait(200);
            queryResult = fragment.getCurrentSet();
            assertEquals(fragment.getCurrentShownCardProfessionalName(),queryResult.data.get(i));
            fragment.discardTopCard();
        }
    }

}*/

/*@RunWith(RobolectricGradleTestRunner.class)
//@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,constants = BuildConfig.class,sdk = 21)
public class CardFragmentTest {

    @Test
    public void test1() throws Exception {
        //android.support.v4.app.Fragment
        CardFragmentT fragment = new CardFragmentT();
        fragment.searchQuery = "arqueologo";
        startFragment( fragment, FragmentActivity.class);
        //cf.rootView = (RelativeLayout) (new LayoutInflater()).inflate(R.layout.card_zone, container, false);
        fragment.setupNewStack();
//        wait(1000);
        while(fragment.getCurrentSet()==null);
        SearchQueryResult queryResult = fragment.getCurrentSet();
        assertTrue(queryResult!=null);
        assertTrue(queryResult.data!=null);
        //assertTrue(queryResult.data!=null);
//        for(int i = 0; i < queryResult.meta.pagination.getPages();++i)
        {
            //while (cf.blockAccess) wait(200);
           // queryResult = fragment.getCurrentSet();
           // assertEquals(fragment.getCurrentShownCardProfessionalName(),queryResult.data.get(i));
           // fragment.discardTopCard();
        }
    }
}*/