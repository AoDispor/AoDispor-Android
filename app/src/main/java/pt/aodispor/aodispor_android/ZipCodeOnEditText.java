package pt.aodispor.aodispor_android;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import pt.aodispor.aodispor_android.API.ApiJSON;
import pt.aodispor.aodispor_android.API.CPPQueryResult;
import pt.aodispor.aodispor_android.API.HttpRequest;
import pt.aodispor.aodispor_android.API.HttpRequestTask;

public class ZipCodeOnEditText implements TextWatcher, HttpRequest {
    private TextView _location;

    private EditText _zip1;
    private EditText _zip2;

    private boolean lastDigit;
    private boolean locationSet;

    public ZipCodeOnEditText(TextView location, EditText zip1, EditText zip2) {
        this._location = location;
        this._zip1 = zip1;
        this._zip2 = zip2;
        this.lastDigit = false;
        this.locationSet = false;
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
            _location.setVisibility(TextView.GONE);
            _location.getRootView().findViewById(R.id.location_progress_bar).setVisibility(ProgressBar.VISIBLE);
            lastDigit = true;
            String url = "https://api.aodispor.pt/location/{cp4}/{cp3}";
            HttpRequestTask request = new HttpRequestTask(CPPQueryResult.class, this, url, zip1, zip2);
            request.setType(HttpRequest.GET_LOCATION);
            request.execute();
            Log.v("debug", "Request sent");

        } else {
            lastDigit = false;
            locationSet = false;
        }
    }

    public boolean isLocationSet(){
        return locationSet;
    }

    public String getCp4(){
        return _zip1.getText().toString();
    }

    public String getCp3(){
        return _zip2.getText().toString();
    }

    @Override
    public void onHttpRequestCompleted(ApiJSON answer, int type) {
        Log.v("debug", "Request completed");
        if(lastDigit) {
            if(type == HttpRequest.GET_LOCATION){
                CPPQueryResult result = (CPPQueryResult) answer;
                if (result != null && result.data != null) {
                    _location.setText(result.data.getLocalidade());
                    _location.setVisibility(TextView.VISIBLE);
                    _location.getRootView().findViewById(R.id.location_progress_bar).setVisibility(ProgressBar.GONE);
                    locationSet = true;
                }
            }
        }
    }

    @Override
    public void onHttpRequestFailed() {

    }
}