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

    @Before
    public void initTest() {
    }

    /**
     *  No caso de os testes falharem com o erro "Verify", é devido a uma limitação da versão do Java que usado
     *  Para resolver basta editar as Run Configurations do test, no campo VM Options e adicionar a flag -noverify
     */

    @Test
    public void testSwipeCardOrder1() throws Exception{
        CardFragmentTestClass fragment = new CardFragmentTestClass();
        SupportFragmentTestUtil.startFragment(fragment, EmptyActivity.class);

        ArrayList<Professional> test_data_set = new ArrayList<Professional>(128);
        for(int i = 0; i<128 ; ++i) test_data_set.add(ProfessionalTestClass.testProfessional(""+Integer.toString(i),""+Integer.toString(i)));
        fragment.setTestData(test_data_set);
        //SearchQueryResult cardSet;
        //    cardSet = fragment.getCurrentSet();

        ShadowLog.stream = System.out;
        ShadowLog.i("testSwipeCardOrder1","Verify display order of professional cards over a segment of one single set (forward iteration only)");
        int i=0;
        for(; i <20 ; ++i) {
             String location = Html.fromHtml(test_data_set.get(i).location).toString();
           String title= Html.fromHtml(test_data_set.get(i).title).toString();
            Assert.assertEquals( (location+title) , fragment.getCurrentShownCardProfessionalLocationPlusProfession());
            fragment.discardTopCard();
        }
        ShadowLog.i("testSwipeCardOrder1","Verify display order of professional cards of one entire single set (forward iteration only)");
        for(; i <64 ; ++i) {
            String location = Html.fromHtml(test_data_set.get(i).location).toString();
            String title= Html.fromHtml(test_data_set.get(i).title).toString();
            Assert.assertEquals( (location+title) , fragment.getCurrentShownCardProfessionalLocationPlusProfession());
            fragment.discardTopCard();
        }
        ShadowLog.i("testSwipeCardOrder1","Verify display order of professional cards of the next set (forward iteration only)");
        for(; i <85 ; ++i) {
            String location = Html.fromHtml(test_data_set.get(i).location).toString();
            String title= Html.fromHtml(test_data_set.get(i).title).toString();
            Assert.assertEquals( (location+title) , fragment.getCurrentShownCardProfessionalLocationPlusProfession());
            fragment.discardTopCard();
        }
    }


    /*@Test
    need some data that is not updated for testing purposes
    public void testDownloadDataFromApiJSON() {
        final CardFragment fragment = new CardFragment();
        SupportFragmentTestUtil.startFragment(fragment, MainActivity.class);

        ApiJSON data = new SearchQueryResult();

        ResponseEntity entity = Mockito.mock(ResponseEntity.class);
        Mockito.when(entity.getBody()).thenReturn(data);
        Mockito.when(
                mRule.getMockRestTemplate().exchange(
                        any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(String[].class))
        ).thenReturn(entity);

        //fragment.fetchDataFromJsonApi();
        //assertEquals(data, fragment.mDownloadedData);
    }*/
}