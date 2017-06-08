package pt.aodispor.android.features.main;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import pt.aodispor.android.AppDefinitions;

/**
 * This class extends the functionality of a ViewPager to add an enable status flag that can be changed.
 * This allows control of the ViewPager swipe functionality.
 */
public class MyViewPager extends ViewPager {
//    private boolean enabled;

    /**
     * The MyViewPager constructor. This is used by the application when creating a myViewPager.
     * @param context the context of this ViewPager.
     * @param attrs the attribute set of this ViewPager.
     */
    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        //this.enabled = true;
    }

    /**
     * This method is called when there is an onTouchEvent in this ViewPager.
     * @param event the event triggered.
     * @return whether the event is propagated or not.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*if (AppDefinitions.smsLoginDone && this.enabled) {
            return super.onTouchEvent(event);
        }*/
        return false;
    }

    /**
     * This method is called when MyViewPager intercepts a touch event.
     * @param event the event intercepted.
     * @return whether the event is propagated or not.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
     /*   if (AppDefinitions.smsLoginDone && this.enabled) {
            return super.onInterceptTouchEvent(event);
        }*/
        return false;
    }

    /**
     * This method sets the enable status flag to true or false.
     * @param enabled the boolean value for the status flag.
     */
  /*  public void setSwipeEnabled(boolean enabled) {
        this.enabled = enabled;
    }*/
}
