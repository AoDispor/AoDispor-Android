package pt.aodispor.android.profile;

import android.content.Context;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import android.os.Handler;

public class ProfileEditText extends EditText {

    //do not update immidiatly, user might change other infos
    //if starts editing immidiatly another, delay per a longer period
    //if concludes reset to short

    static final int WAIT_SHORT = 2000;
    static final int WAIT_LONG = 30000;
    static final Handler updateHandler = new Handler();
    private enum UPDATE_SCHEDULED{no , _short, _long ;
    public boolean isScheduled(){return this!=no;}
    };
    static UPDATE_SCHEDULED updateScheduled = UPDATE_SCHEDULED.no;
    static Runnable update = new Runnable() {
        @Override
        public void run() {
            updateUserProfile.run();
            updateScheduled =  UPDATE_SCHEDULED.no;
        }
    };
    static Runnable updateUserProfile;

    public static void setUpdateFragment(Runnable updateUserProfile) {
        ProfileEditText.updateUserProfile = updateUserProfile;
    }

    public ProfileEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ProfileEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProfileEditText(Context context) {
        super(context);
        init();
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP
                ) {
            dispatchKeyEvent(event);
            Log.d("UPDATING", "1");
            runHandler(false);
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    static public void runHandler() {
        runHandler(false);
    }

    static private void runHandler(boolean _long) {
        synchronized (updateHandler) {
            updateHandler.removeCallbacksAndMessages(null);//remove all
            updateScheduled =  _long? UPDATE_SCHEDULED._long: UPDATE_SCHEDULED._short;
            int WAIT = _long ? WAIT_LONG : WAIT_SHORT;
            updateHandler.postDelayed(updateUserProfile, WAIT);
        }
    }

    private void init() {

        this.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d("UPDATING", "2");
                    runHandler(false);
                } else if (updateScheduled.isScheduled()) {
                    Log.w("LONG","1");
                    runHandler(true);
                }
            }
        });

        this.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (
                        actionId == EditorInfo.IME_ACTION_DONE ||
                                actionId == EditorInfo.IME_ACTION_PREVIOUS
                        ) {
                    Log.d("UPDATING", "3");
                    runHandler(false);
                    return false;
                }
                if(updateScheduled==UPDATE_SCHEDULED._short) {
                    Log.w("LONG","2");
                    runHandler(true);
                }
                return true;
            }
        });
    }
}
