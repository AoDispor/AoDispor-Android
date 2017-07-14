package pt.aodispor.android.features.main;


import android.app.Activity;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.Tapper;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;
import pt.aodispor.android.api.aodispor.BasicRequestInfo;
import pt.aodispor.android.data.local.UserData;
import pt.aodispor.android.utils.Permission;
import tools.fastlane.screengrab.Screengrab;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasToString;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    //@Rule
    //public ActivityTestRule<SplitActivity> mActivityTestRule = new ActivityTestRule<>(SplitActivity.class);

    class CustomTestRule<T> extends ActivityTestRule {

        CustomTestRule(Class<T> activityClass) {
            super(activityClass);
        }

        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            UserData.getInstance().setUserLoginAuth(AppDefinitions.testPhoneNumber, AppDefinitions.testPassword);
            AppDefinitions.smsLoginDone = true;
            Permission.enabled = false;
        }
    }

    @Rule
    public CustomTestRule<MainActivity> mActivityTestRule = new CustomTestRule<>(MainActivity.class);
    Activity activity;

    @Before
    public void init() {
        activity = mActivityTestRule.getActivity();
        BasicRequestInfo.setToken(activity.getResources().getString(R.string.ao_dispor_api_key));
        Fabric.with(activity, new Answers(), new Crashlytics());
    }

    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    public static ViewAction customClick() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isEnabled(); // no constraints, they are checked above
            }

            @Override
            public String getDescription() {
                return "";
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.performClick();
            }
        };
    }

    void nav2(int nav_textstring_id){
        onView(anyOf(withId(R.id.menu_button))).perform(customClick());
        onView(
                allOf(withId(R.id.design_menu_item_text), withText(
                        activity.getResources().getString(nav_textstring_id)
                ), isDisplayed()))
                .perform(click());
    }

    void search(String query){
        search(query,null);
    }

    void search(String query,String screenshotname){
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.search_button), withContentDescription("Pesquisar"),
                        withParent(allOf(withId(R.id.search_bar),
                                withParent(withId(R.id.searchView)))),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction searchAutoComplete = onView(
                allOf(withId(R.id.search_src_text),
                        withParent(allOf(withId(R.id.search_plate),
                                withParent(withId(R.id.search_edit_frame)))),
                        isDisplayed()));
        searchAutoComplete.perform(click());

        ViewInteraction searchAutoComplete2 = onView(
                allOf(withId(R.id.search_src_text),
                        withParent(allOf(withId(R.id.search_plate),
                                withParent(withId(R.id.search_edit_frame)))),
                        isDisplayed()));
        searchAutoComplete2.perform(replaceText(query), closeSoftKeyboard());

        if(screenshotname!=null)Screengrab.screenshot(screenshotname);

        ViewInteraction searchAutoComplete3 = onView(
                allOf(withId(R.id.search_src_text), withText(query),
                        withParent(allOf(withId(R.id.search_plate),
                                withParent(withId(R.id.search_edit_frame)))),
                        isDisplayed()));
        searchAutoComplete3.perform(pressImeActionButton());
    }

    @Test
    public void MainActivityTest() {
        onView(isRoot()).perform(waitFor(3000));

        //Reload initial fragment
        onView(anyOf(withId(R.id.app_title))).perform(customClick());
        Screengrab.screenshot("default_cardstack");

        //navigate stuff
        nav2(R.string.nav_profile);
        Screengrab.screenshot("about");

        nav2(R.string.nav_aboutus);
        Screengrab.screenshot("about");

        //search stuff
        search("fundador","searchfundador1");
        Screengrab.screenshot("searchfundador2");
    }

}
