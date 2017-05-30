package pt.aodispor.android.profile;

import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.HashMap;

import pt.aodispor.android.EnumSpinner;
import pt.aodispor.android.R;
import pt.aodispor.android.professional.CurrencyType;
import pt.aodispor.android.professional.PaymentType;

/**
 * Custom Dialog Fragment for editing price information.
 */
public class NewPriceDialog extends DialogFragment {
    private PriceDialogListener listener;
    private int buttonChosen;
    private Button[] buttons;
    private EditText priceView;
    private PaymentType priceType;
    private int rate;
    private Switch priceSwitch;
    //private Spinner currencySpinner;
    private EnumSpinner<CurrencyType> currencySpinner;

    public static NewPriceDialog newInstance(int r, boolean f, PaymentType pt, CurrencyType c) {
        NewPriceDialog pd = new NewPriceDialog();
        Bundle args = new Bundle();
        args.putInt("rate", r);
        args.putBoolean("final", f);
        args.putInt("type", pt.ordinal());
        args.putString("currency", c.getAPICode());
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
        priceType = PaymentType.values()[getArguments().getInt("type")];
        // TODO currency spinner

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
                if (editable.length() == 1 && editable.toString().equals("0")) {
                    editable.clear();
                }
            }
        });
        Button byHour = (Button) root.findViewById(R.id.type1);
        Button byDay = (Button) root.findViewById(R.id.type2);
        Button byService = (Button) root.findViewById(R.id.type3);
        priceSwitch = (Switch) root.findViewById(R.id.priceSwitch);
        //currencySpinner = (Spinner) root.findViewById(R.id.currency_spinner);

        // Radio Buttons
        buttons = new Button[]{byHour, byDay, byService};
        for (Button button : buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < buttons.length; j++) {
                        if (buttons[j] == view) {
                            buttonChosen = j;
                        }
                    }
                    priceType = PaymentType.values()[buttonChosen];
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

        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.allowed_currencies, R.layout.currency_spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);
        currencySpinner.setAdapter(spa);*/
        currencySpinner = new EnumSpinner<CurrencyType>(root.getContext(),
                (Spinner) root.findViewById(R.id.currency_spinner),
                CurrencyType.values());

        updateViews();
        return root;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            int newRate = 0;
            try {
                newRate = Integer.parseInt(priceView.getText().toString());
            } catch (Exception e) {
            }
            boolean isFinal = priceSwitch.isChecked();
            PaymentType newPriceType = PaymentType.values()[buttonChosen];
            CurrencyType newCurrency = (CurrencyType) currencySpinner.getSelectedItem();
            listener.onPriceChanged(newRate, isFinal, newPriceType, newCurrency);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViews();
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

    public void setListener(PriceDialogListener l) {
        listener = l;
    }

    public interface PriceDialogListener {
        void onPriceChanged(int rate, boolean isFinal, PaymentType type, CurrencyType currency);
    }

}
