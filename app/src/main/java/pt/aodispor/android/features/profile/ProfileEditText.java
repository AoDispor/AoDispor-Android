package pt.aodispor.android.features.profile;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import android.os.Handler;

/**
 * <p>schedules updates after text has been edited</p>
 * <p>
 * If the user starts editing an update is schedule to run after a certain time.
 * This is to prevent losing changes in case the user takes a lot of time editing
 * </p>
 * <p>
 * If the user finishes editing (closes keyboard) the update is schedule to run almost immidiatly after.
 * </p>
 */
public class ProfileEditText extends EditText {

    /*note
        can be generalized for other fragments
        remove static Runnable, rename it,
        and create a function to assign the Runnable to
         all textviews of the given fragment
      */

    static final int WAIT_SHORT = 1000;
    static final int WAIT_LONG = 15000;
    static final Handler updateHandler = new Handler();

    private enum UPDATE_SCHEDULED {
        no, _short, _long;

        public boolean isScheduled() {
            return this != no;
        }
    }

    ;
    static UPDATE_SCHEDULED updateScheduled = UPDATE_SCHEDULED.no;
    static Runnable update = new Runnable() {
        @Override
        public void run() {
            Log.d("EditText", "update after a " + updateScheduled.toString() + " delay");
            updateScheduled = UPDATE_SCHEDULED.no;
            updateUserProfile.run();
        }
    };
    static Runnable updateUserProfile;

    public static void setUpdateFragment(Runnable updateUserProfile) {
        ProfileEditText.updateUserProfile = updateUserProfile;
    }

    public ProfileEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ProfileEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileEditText(Context context) {
        super(context);
    }


    static public void scheduleHandler() {
        scheduleHandler(false);
    }

    static private void scheduleHandler(boolean _long) {
        if (updateScheduled.isScheduled() && _long) return;
        synchronized (updateHandler) {
            updateHandler.removeCallbacksAndMessages(null);//remove all
            updateScheduled = _long ? UPDATE_SCHEDULED._long : UPDATE_SCHEDULED._short;
            int WAIT = _long ? WAIT_LONG : WAIT_SHORT;
            updateHandler.postDelayed(update, WAIT);
            Log.d("EditText", "scheduled " + updateScheduled.toString());
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (hasFocus() && lengthAfter != lengthBefore) {
            Log.d("EditText", "1 tried schedule long");
            scheduleHandler(true);
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (!focused) {
            Log.d("EditText", "2 tried schedule short");
            scheduleHandler(false);
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void onEditorAction(int actionCode) {
        if (
                actionCode == EditorInfo.IME_ACTION_DONE
                        || actionCode == EditorInfo.IME_ACTION_PREVIOUS
                        || actionCode == EditorInfo.IME_ACTION_NEXT
                        || actionCode == EditorInfo.IME_ACTION_SEND
                ) {
            Log.d("EditText", "3 tried schedule short");
            scheduleHandler(false);
        }
        super.onEditorAction(actionCode);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (
                keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP
                ) {
            Log.d("EditText", "4 tried schedule short");
            scheduleHandler(false);
        }
        return super.onKeyPreIme(keyCode, event);
    }

}
