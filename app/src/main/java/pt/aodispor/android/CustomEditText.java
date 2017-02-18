package pt.aodispor.android;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class CustomEditText extends AppCompatEditText {
    OnBackPressedListener onBackPressedListener;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(onBackPressedListener != null) {
                onBackPressedListener.onBackPressed();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        onBackPressedListener = listener;
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }
}
