package pt.aodispor.aodispor_android;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


public class LocationDialog extends AlertDialog {
    private TextView _location;
    private ZipCodeOnEditText listener;

    protected LocationDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.localization_reg);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        EditText zip1 = (EditText) findViewById(R.id.zip1);
        EditText zip2 = (EditText) findViewById(R.id.zip2);

        _location = (TextView) findViewById(R.id.localizacao);

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
