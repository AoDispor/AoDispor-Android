package pt.aodispor.aodispor_android;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import pt.aodispor.aodispor_android.API.CPPQueryResult;
import pt.aodispor.aodispor_android.API.HttpRequestTask;
import pt.aodispor.aodispor_android.API.SearchQueryResult;

public class ZipCodeOnEditText implements TextWatcher {
    private TextView _location;
    private TextView _location_reg;

    private EditText _zip1;
    private EditText _zip2;

    public ZipCodeOnEditText(TextView location, TextView location_reg, EditText zip1, EditText zip2) {
        this._location = location;
        this._location_reg = location_reg;
        this._zip1 = zip1;
        this._zip2 = zip2;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(charSequence.length() == 4)
            _zip2.requestFocus();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String zip1 = _zip1.getText().toString();
        String zip2 = _zip2.getText().toString();

        if(zip1.length() == 4 && zip2.length() == 3) {
            String url = "https://api.aodispor.pt/location/{cp4}/{cp3}";

            Log.d("FCC1",zip1);
            Log.d("FCC2",zip2);

            HttpRequestTask request = new HttpRequestTask(CPPQueryResult.class, null, url, zip1, zip2);

            CPPQueryResult result;
            try {
                Log.d("CPPQuery","Executing GET REQUEST");
                Log.d("STRING QUERY", url + zip1 + zip2);
                result = (CPPQueryResult) request.execute().get(AppDefinitions.MILISECONDS_TO_TIMEOUT_ON_QUERY, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                Log.d("L330:EXCP", e.toString());
                return;
            }
            if (result!=null && result.data!=null) {
                _location.setText(result.data.getLocalidade());
                _location_reg.setText(result.data.getLocalidade());
            }
        }
        else {
            Log.d("L","Postal code yet to fill");
        }
    }
}