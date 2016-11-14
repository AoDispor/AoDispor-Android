package pt.aodispor.aodispor_android.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import pt.aodispor.aodispor_android.ProfileFragment;
import pt.aodispor.aodispor_android.R;

public class PriceDialog extends AlertDialog{
    private int buttonChosen;
    private Button[] buttons;
    private ProfileFragment profileFragment;
    private Button byHour,byDay,byService;
    private TextView priceView;
    private ProfileFragment.PriceType priceType;
    private Switch priceSwitch;

    public PriceDialog(Context context, ProfileFragment pf) {
        super(context);
        profileFragment = pf;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price_edit);

        // Set Window and Keyboard Settings
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // Get Views
        priceView = (TextView) findViewById(R.id.price_input);
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
        buttonChosen = 0;
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
}
