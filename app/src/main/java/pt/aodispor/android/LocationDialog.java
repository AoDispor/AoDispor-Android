package pt.aodispor.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

@Deprecated
public class LocationDialog extends AlertDialog {
    private TextView _location;
    private String location;
    private ZipCodeOnEditText listener;
    private LocationDialog thisDialog;

    protected LocationDialog(Context context, String l) {
        super(context);
        thisDialog = this;
        location = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localization_reg);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        EditText zip1 = (EditText) findViewById(R.id.zip1);
        zip1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if(listener.isLocationSet()){
                        thisDialog.dismiss();
                        return false;
                    }else {
                        return true;
                    }
                }
                return false;
            }
        });
        EditText zip2 = (EditText) findViewById(R.id.zip2);
        zip2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if(listener.isLocationSet()){
                        thisDialog.dismiss();
                        return false;
                    }else {
                        return true;
                    }
                }
                return false;
            }
        });

        _location = (TextView) findViewById(R.id.locationName);
        _location.setText(location);

        listener = new ZipCodeOnEditText(_location,zip1,zip2);

        zip1.addTextChangedListener(listener);
        zip2.addTextChangedListener(listener);
    }



    public void setLocation(TextView location) {
        this._location = location;
    }

    public boolean isLocationSet(){
        return listener.isLocationSet();
    }

    public String getCp4(){
        return listener.getCp4();
    }

    public String getCp3(){
        return listener.getCp3();
    }

    public String getLocation() {
        return _location.getText().toString();
    }
}
