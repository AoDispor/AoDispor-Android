package pt.aodispor.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.utils.L;

import pt.aodispor.android.R;
import pt.aodispor.android.api.ApiJSON;
import pt.aodispor.android.api.CPPQueryResult;
import pt.aodispor.android.api.HttpRequest;
import pt.aodispor.android.api.HttpRequestTask;

public class LocationDialog extends DialogFragment implements HttpRequest{
    LocationDialogListener listener;
    EditText postalCodePrefix, postalCodeSuffix;
    TextView locationName;
    ProgressBar progressBar;
    LocationDialog locationDialog = this;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View root = inflater.inflate(R.layout.localization_reg, null);
        postalCodePrefix = (EditText) root.findViewById(R.id.zip1);
        postalCodeSuffix = (EditText) root.findViewById(R.id.zip2);
        locationName = (TextView) root.findViewById(R.id.locationName);
        progressBar = (ProgressBar) root.findViewById(R.id.location_progress_bar);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(postalCodeSuffix.length() == 3 && postalCodePrefix.length() == 4) {
                    String prefix = postalCodePrefix.getText().toString();
                    String suffix = postalCodeSuffix.getText().toString();
                    locationName.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    String url = "https://api.aodispor.pt/location/{cp4}/{cp3}";
                    HttpRequestTask request = new HttpRequestTask(CPPQueryResult.class, locationDialog, url, prefix, suffix);
                    request.setType(HttpRequest.GET_LOCATION);
                    //request.execute();
                } else {
                    progressBar.setVisibility(View.GONE);
                    locationName.setVisibility(View.VISIBLE);
                    if (postalCodePrefix.length() == 4) {
                        postalCodeSuffix.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        postalCodePrefix.addTextChangedListener(textWatcher);
        postalCodeSuffix.addTextChangedListener(textWatcher);
        builder.setView(root);
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(listener != null) {
            listener.onDismiss();
        }
    }

    public void setListener(LocationDialogListener l) {
        listener = l;
    }

    @Override
    public void onHttpRequestCompleted(ApiJSON answer, int type) {
        if(type == HttpRequest.GET_LOCATION){
            CPPQueryResult result = (CPPQueryResult) answer;
            if (result != null && result.data != null) {
                progressBar.setVisibility(View.GONE);
                locationName.setText(result.data.getLocalidade());
                locationName.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onHttpRequestFailed() {

    }

    public interface LocationDialogListener {
        void onDismiss();
    }

}
