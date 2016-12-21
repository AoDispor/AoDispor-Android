package pt.aodispor.aodispor_android.view.tests;

import android.os.Build;
import android.text.Html;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

//import testrobo.testrobolectric.BuildConfig;
//import testrobo.testrobolectric.base.CustomTestRule;

import java.util.ArrayList;
import java.util.ListIterator;

import pt.aodispor.aodispor_android.API.Professional;
import pt.aodispor.aodispor_android.API.SearchQueryResult;
import pt.aodispor.aodispor_android.BuildConfig;
import pt.aodispor.aodispor_android.view.base.CardFragmentTestClass;
import pt.aodispor.aodispor_android.view.base.EmptyActivity;
import pt.aodispor.aodispor_android.view.base.ProfessionalTestClass;

import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
//@RunWith(AndroidJUnit4.class)
@Config(
        constants = BuildConfig.class,
        manifest = "src/main/AndroidManifest.xml",
        sdk = Build.VERSION_CODES.LOLLIPOP
)
public class CardFragmentTest{

    // Used to set all the mock dependencies
    //@Rule
    //public final CustomTestRule mRule = new CustomTestRule(); TODO not used so far

    int iterator;

    @Before
    public void initTest() {
    }

    /**
     *  No caso de os testes falharem com o erro "Verify", é devido a uma limitação da versão do Java que usado
     *  Para resolver basta editar as Run Configurations do test, no campo VM Options e adicionar a flag -noverify
     */

    @Test
    public void testSwipeCardOrder1() {
        CardFragmentTestClass fragment = new CardFragmentTestClass();
        SupportFragmentTestUtil.startFragment(fragment, EmptyActivity.class);

        ArrayList<Professional> test_data_set = new ArrayList<Professional>(128);
        for(int i = 0; i<128 ; ++i) test_data_set.add(ProfessionalTestClass.testProfessional(""+Integer.toString(i),""+Integer.toString(i)));
        fragment.setTestData(test_data_set,CardFragmentTestClass.Test.forward);
        //SearchQueryResult cardSet;
        //    cardSet = fragment.getCurrentSet();

        ShadowLog.stream = System.out;
        ShadowLog.i("","\n- - - - - - - - - - - - - - - - - - - - - - - - -\n");
        ShadowLog.i("testSwipeCardOrder1","Verify display order of professional cards over a segment of one single set (forward iteration only)");

        int i=0;
        for(; i <20 ; ++i) {
            fragment.unblockAccess();
             String location = Html.fromHtml(test_data_set.get(i).location).toString();
           String title= Html.fromHtml(test_data_set.get(i).title).toString();
            Assert.assertEquals( (location+title) , fragment.getCurrentShownCardProfessionalLocationPlusProfession());
            fragment.discardTopCard();
        }
        ShadowLog.i("testSwipeCardOrder1","Verify display order of professional cards of one entire single set (forward iteration only)");
        for(; i <64 ; ++i) {
            fragment.unblockAccess();
            String location = Html.fromHtml(test_data_set.get(i).location).toString();
            String title= Html.fromHtml(test_data_set.get(i).title).toString();
            Assert.assertEquals( (location+title) , fragment.getCurrentShownCardProfessionalLocationPlusProfession());
            fragment.discardTopCard();
        }
        ShadowLog.i("testSwipeCardOrder1","Verify display order of professional cards of the next set (forward iteration only)");
        for(; i <85 ; ++i) {
            fragment.unblockAccess();
            String location = Html.fromHtml(test_data_set.get(i).location).toString();
            String title= Html.fromHtml(test_data_set.get(i).title).toString();
            Assert.assertEquals( (location+title) , fragment.getCurrentShownCardProfessionalLocationPlusProfession());
            fragment.discardTopCard();
        }
    }

    @Test
    public void testSwipeCardOrder2(){

        CardFragmentTestClass fragment = new CardFragmentTestClass();
        SupportFragmentTestUtil.startFragment(fragment, EmptyActivity.class);

        ArrayList<Professional> test_data_set = new ArrayList<Professional>(128);
        for(int i = 0; i<128 ; ++i) test_data_set.add(ProfessionalTestClass.testProfessional(""+Integer.toString(i),""+Integer.toString(i)));
        fragment.setTestData(test_data_set,CardFragmentTestClass.Test.backward);
        //SearchQueryResult cardSet;
        //    cardSet = fragment.getCurrentSet();

        ShadowLog.stream = System.out;
        ShadowLog.i("","\n- - - - - - - - - - - - - - - - - - - - - - - - -\n");

        int i=85;

        ShadowLog.i("testSwipeCardOrder1","Verify display order of professional cards over a segment of one single set (backward iteration only)");
        for(; i >64 ; --i) {
            fragment.unblockAccess();
            String location = Html.fromHtml(test_data_set.get(i).location).toString();
            String title= Html.fromHtml(test_data_set.get(i).title).toString();
            Assert.assertEquals( (location+title) , fragment.getCurrentShownCardProfessionalLocationPlusProfession());
            fragment.restorePreviousCard();
        }

        ShadowLog.i("testSwipeCardOrder1","Verify display order of professional cards over previous set (backward iteration only)");
        for(; i >20 ; --i) {
            fragment.unblockAccess();
            String location = Html.fromHtml(test_data_set.get(i).location).toString();
            String title= Html.fromHtml(test_data_set.get(i).title).toString();
            Assert.assertEquals( (location+title) , fragment.getCurrentShownCardProfessionalLocationPlusProfession());
            fragment.restorePreviousCard();
        }
    }

    @Test
    public void testSwipeCardOrder3() {

        CardFragmentTestClass fragment = new CardFragmentTestClass();
        SupportFragmentTestUtil.startFragment(fragment, EmptyActivity.class);

        ArrayList<Professional> test_data_set = new ArrayList<Professional>(128);
        for (int i = 0; i < 128; ++i)
            test_data_set.add(ProfessionalTestClass.testProfessional("" + Integer.toString(i), "" + Integer.toString(i)));
        fragment.setTestData(test_data_set, CardFragmentTestClass.Test.mix);
        //SearchQueryResult cardSet;
        //    cardSet = fragment.getCurrentSet();

        ShadowLog.stream = System.out;
        ShadowLog.i("", "\n- - - - - - - - - - - - - - - - - - - - - - - - -\n");

        iterator=50;

        ShadowLog.i("testSwipeCardOrder1", "forward and backward iterations inside same set");
        assert1ForwardIteration(fragment,test_data_set);
        assert1ForwardIteration(fragment,test_data_set);
        assert1BackwardIteration(fragment,test_data_set);
        assert1ForwardIteration(fragment,test_data_set);
        assert1ForwardIteration(fragment,test_data_set);
        assert1ForwardIteration(fragment,test_data_set);
        assert1BackwardIteration(fragment,test_data_set);
        assert1BackwardIteration(fragment,test_data_set);

        ShadowLog.i("testSwipeCardOrder1", "forward and backward iterations alternating sets");
        //iterator=52
        for(int i= 0; i<30;++i) assert1ForwardIteration(fragment,test_data_set);
        for(int i= 0; i<30;++i) assert1BackwardIteration(fragment,test_data_set);
        for(int i= 0; i<30;++i) assert1ForwardIteration(fragment,test_data_set);
        for(int i= 0; i<30;++i) assert1BackwardIteration(fragment,test_data_set);

    }

    void assert1BackwardIteration(CardFragmentTestClass fragment, ArrayList<Professional> test_data_set)
    {
        iterator--;
        fragment.restorePreviousCard();
        fragment.unblockAccess();
        String location = Html.fromHtml(test_data_set.get(iterator).location).toString();
        String title= Html.fromHtml(test_data_set.get(iterator).title).toString();
        Assert.assertEquals( (location+title) , fragment.getCurrentShownCardProfessionalLocationPlusProfession());
    }

    void assert1ForwardIteration(CardFragmentTestClass fragment, ArrayList<Professional> test_data_set)
    {
        iterator++;
        fragment.discardTopCard();
        fragment.unblockAccess();
        String location = Html.fromHtml(test_data_set.get(iterator).location).toString();
        String title= Html.fromHtml(test_data_set.get(iterator).title).toString();
        Assert.assertEquals( (location+title) , fragment.getCurrentShownCardProfessionalLocationPlusProfession());
    }

}