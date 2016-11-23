package pt.aodispor.aodispor_android.Dialogs;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import pt.aodispor.aodispor_android.ProfileFragment;
import pt.aodispor.aodispor_android.R;

/**
 * Custom Dialog Fragment for editing price information.
 */
public class PriceDialog extends DialogFragment {
    public static final int DIALOG_FRAGMENT = 1;
    private int buttonChosen;
    private Button[] buttons;
    private EditText priceView;
    private ProfileFragment.PriceType priceType;
    private int rate;
    private Switch priceSwitch;

    public PriceDialog() {

    }

    public static PriceDialog newInstance(ProfileFragment pf, int r, boolean f, int pt){
        PriceDialog pd = new PriceDialog();
        Bundle args = new Bundle();
        args.putInt("rate", r);
        args.putBoolean("final", f);
        args.putInt("type", pt);
        pd.setTargetFragment(pf, DIALOG_FRAGMENT);
        pd.setArguments(args);
        return pd;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.price_edit, container);

        // Set Window and Keyboard Settings
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Set variables
        rate = getArguments().getInt("rate");
        priceType = ProfileFragment.PriceType.values()[getArguments().getInt("type")];

        // Get Views
        priceView = (EditText) root.findViewById(R.id.price_input);
        priceView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        priceView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 1 && editable.toString().equals("0")){
                    editable.clear();
                }
            }
        });
        Button byHour = (Button) root.findViewById(R.id.type1);
        Button byDay = (Button) root.findViewById(R.id.type2);
        Button byService = (Button) root.findViewById(R.id.type3);
        priceSwitch = (Switch) root.findViewById(R.id.priceSwitch);

        // Radio Buttons
        buttons = new Button[]{ byHour, byDay, byService };
        for (Button button : buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < buttons.length; j++) {
                        if (buttons[j] == view) {
                            buttonChosen = j;
                        }
                    }
                    priceType = ProfileFragment.PriceType.values()[buttonChosen];
                    LayerDrawable drawable = (LayerDrawable) view.getBackground();
                    GradientDrawable shapeDrawable = (GradientDrawable) drawable.getDrawable(1);
                    shapeDrawable.setColor(ContextCompat.getColor(getContext(), R.color.aoDispor2));
                    for (int i = 0; i < buttons.length; i++) {
                        if (i != buttonChosen) {
                            LayerDrawable d = (LayerDrawable) buttons[i].getBackground();
                            GradientDrawable s = (GradientDrawable) d.getDrawable(1);
                            s.setColor(ContextCompat.getColor(getContext(), R.color.white));
                        }
                    }
                }
            });
        }
        updateViews();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        ProfileFragment pf = (ProfileFragment) getTargetFragment();
        int nRate = rate;
        if (priceView.getText().length() != 0) {
            nRate = Integer.parseInt(priceView.getText().toString());
        }
        boolean nFinal = priceSwitch.isChecked();
        ProfileFragment.PriceType nPriceType = ProfileFragment.PriceType.values()[buttonChosen];
        pf.onPriceDialogCallBack(nRate,nFinal,nPriceType);
    }

    private void updateViews() {
        // Price Edit Text
        priceView.setText("");
        priceView.append(rate + "");

        // Price Final Switch
        priceSwitch.setChecked(getArguments().getBoolean("final"));

        // Radio Buttons
        buttonChosen = priceType.ordinal();
        for (int i = 0; i < buttons.length; i++) {
            if (i != buttonChosen) {
                LayerDrawable d = (LayerDrawable) buttons[i].getBackground();
                GradientDrawable s = (GradientDrawable) d.getDrawable(1);
                s.setColor(ContextCompat.getColor(getContext(), R.color.white));
            }
        }
        LayerDrawable d = (LayerDrawable) buttons[buttonChosen].getBackground();
        GradientDrawable s = (GradientDrawable) d.getDrawable(1);
        s.setColor(ContextCompat.getColor(getContext(), R.color.aoDispor2));
    }

}
