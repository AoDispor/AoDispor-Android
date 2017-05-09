package pt.aodispor.android;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import tools.fastlane.screengrab.Screengrab;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SplitActivityTest {

    @Rule
    public ActivityTestRule<SplitActivity> mActivityTestRule = new ActivityTestRule<>(SplitActivity.class);

    @Test
    public void splitActivityTest() {
        Screengrab.screenshot("name_of_screenshot_here");

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.next_button), withText("Seguinte"),
                        withParent(withId(R.id.layoutContainer)),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.skip_text2), withText("Não me inscrever agora."), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.search_button), withContentDescription("Search"),
                        withParent(allOf(withId(R.id.search_bar),
                                withParent(withId(R.id.searchView)))),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction searchAutoComplete = onView(
                allOf(withId(R.id.search_src_text),
                        withParent(allOf(withId(R.id.search_plate),
                                withParent(withId(R.id.search_edit_frame)))),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText("fundador"), closeSoftKeyboard());

        ViewInteraction searchAutoComplete5 = onView(
                allOf(withId(R.id.search_src_text), withText("fundador"),
                        withParent(allOf(withId(R.id.search_plate),
                                withParent(withId(R.id.search_edit_frame)))),
                        isDisplayed()));
        searchAutoComplete5.perform(pressImeActionButton());

    }

}
