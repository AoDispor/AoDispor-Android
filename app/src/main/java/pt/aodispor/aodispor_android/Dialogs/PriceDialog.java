package pt.aodispor.aodispor_android.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import pt.aodispor.aodispor_android.ProfileFragment;
import pt.aodispor.aodispor_android.R;

public class PriceDialog extends DialogFragment {
    private int buttonChosen;
    private Button[] buttons;
    private ProfileFragment profileFragment;
    private Button byHour,byDay,byService;
    private EditText priceView;
    private ProfileFragment.PriceType priceType;
    private int rate;
    private Switch priceSwitch;

    public PriceDialog() {

    }

    public static PriceDialog newInstance(int r, boolean f, int pt){
        PriceDialog pd = new PriceDialog();

        Bundle args = new Bundle();
        args.putInt("rate",r);
        args.putBoolean("final",f);
        args.putInt("type",pt);

        pd.setArguments(args);
        return pd;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.price_edit,container);

        // Set Window and Keyboard Settings
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Set variables
        rate = getArguments().getInt("rate");
        priceType = ProfileFragment.PriceType.values()[getArguments().getInt("type")];

        // Get Views
        priceView = (EditText) root.findViewById(R.id.price_input);
        byHour = (Button) root.findViewById(R.id.type1);
        byDay = (Button) root.findViewById(R.id.type2);
        byService = (Button) root.findViewById(R.id.type3);
        priceSwitch = (Switch) root.findViewById(R.id.priceSwitch);

        // Price Edit Text
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                for (int j = start; j < end; j++) {
                    if (source.charAt(j) == '.') {
                        return "";
                    }
                }
                return null;
            }
        };
        priceView.setFilters(new InputFilter[] { filter });
        priceView.append(rate+"");

        // Price Final Switch
        priceSwitch.setChecked(getArguments().getBoolean("final"));

        // Buttons
        buttons = new Button[]{ byHour, byDay, byService };
        buttonChosen = priceType.ordinal();
        LayerDrawable d = (LayerDrawable) buttons[buttonChosen].getBackground();
        GradientDrawable s = (GradientDrawable) d.getDrawable(1);
        s.setColor(ContextCompat.getColor(getContext(),R.color.aoDispor2));
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < buttons.length; j++){
                        if(buttons[j] == view){
                            buttonChosen = j;
                        }
                    }
                    priceType = ProfileFragment.PriceType.values()[buttonChosen];
                    LayerDrawable drawable = (LayerDrawable) view.getBackground();
                    GradientDrawable shapeDrawable = (GradientDrawable) drawable.getDrawable(1);
                    shapeDrawable.setColor(ContextCompat.getColor(getContext(),R.color.aoDispor2));
                    for (int i = 0; i < buttons.length; i++){
                        if(i != buttonChosen){
                            LayerDrawable d = (LayerDrawable) buttons[i].getBackground();
                            GradientDrawable s = (GradientDrawable) d.getDrawable(1);
                            s.setColor(ContextCompat.getColor(getContext(),R.color.white));
                        }
                    }
                }
            });
        }
        getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(priceView.getText().length() != 0){
                    profileFragment.setPrice(Integer.parseInt(priceView.getText().toString()), priceSwitch.isChecked(), priceType);
                }
            }
        });

        return root;
    }







    /*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price_edit);

        // Set variables
        rate = profileFragment.getPriceRate();
        priceType = profileFragment.getPriceType();

        // Set Window and Keyboard Settings
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // Get Views
        priceView = (EditText) findViewById(R.id.price_input);

        //priceView.setText(rate,EditText.BufferType.EDITABLE);
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                for (int j = start; j < end; j++) {
                    if (source.charAt(j) == '.') {
                        return "";
                    }
                }
                return null;
            }
        };
        priceView.setFilters(new InputFilter[] { filter });
        byHour = (Button) findViewById(R.id.type1);
        byDay = (Button) findViewById(R.id.type2);
        byService = (Button) findViewById(R.id.type3);
        priceSwitch = (Switch) findViewById(R.id.priceSwitch);

        // Buttons
        buttons = new Button[]{ byHour, byDay, byService };
        buttonChosen = priceType.ordinal();
        LayerDrawable d = (LayerDrawable) buttons[buttonChosen].getBackground();
        GradientDrawable s = (GradientDrawable) d.getDrawable(1);
        s.setColor(ContextCompat.getColor(getContext(),R.color.aoDispor2));
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < buttons.length; j++){
                        if(buttons[j] == view){
                            buttonChosen = j;
                        }
                    }
                    priceType = ProfileFragment.PriceType.values()[buttonChosen];
                    LayerDrawable drawable = (LayerDrawable) view.getBackground();
                    GradientDrawable shapeDrawable = (GradientDrawable) drawable.getDrawable(1);
                    shapeDrawable.setColor(ContextCompat.getColor(getContext(),R.color.aoDispor2));
                    for (int i = 0; i < buttons.length; i++){
                        if(i != buttonChosen){
                            LayerDrawable d = (LayerDrawable) buttons[i].getBackground();
                            GradientDrawable s = (GradientDrawable) d.getDrawable(1);
                            s.setColor(ContextCompat.getColor(getContext(),R.color.white));
                        }
                    }
                }
            });
        }
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(priceView.getText().length() != 0){
                    profileFragment.setPrice(Integer.parseInt(priceView.getText().toString()), priceSwitch.isChecked(), priceType);
                }
            }
        });
    }
    */

}
