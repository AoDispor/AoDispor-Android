package pt.aodispor.aodispor_android;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by JOSE PEREIRA on 12-11-2016.
 */

public class LocalizationDialog extends AlertDialog {
    private TextView _location;

    protected LocalizationDialog(Context context) {
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

        TextView location = (TextView) findViewById(R.id.localizacao);

        EditText.OnEditorActionListener listener = new ZipCodeOnEditText(location,_location,zip1,zip2);

        zip1.setOnEditorActionListener(listener);
        zip2.setOnEditorActionListener(listener);
    }

    public void setLocation(TextView location) {
        this._location = location;
    }
}
