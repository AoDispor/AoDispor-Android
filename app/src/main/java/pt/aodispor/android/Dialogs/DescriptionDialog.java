package pt.aodispor.android.Dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import pt.aodispor.android.ProfileFragment;
import pt.aodispor.android.R;

public class DescriptionDialog extends DialogFragment{
    public static final int DIALOG_FRAGMENT = 1;

    public DescriptionDialog() {

    }

    public static DescriptionDialog newInstance(ProfileFragment pf){
        DescriptionDialog dd = new DescriptionDialog();
        Bundle args = new Bundle();
        dd.setTargetFragment(pf, DIALOG_FRAGMENT);
        dd.setArguments(args);
        return dd;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.description_edit, container);

        // Set Window and Keyboard Settings
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
