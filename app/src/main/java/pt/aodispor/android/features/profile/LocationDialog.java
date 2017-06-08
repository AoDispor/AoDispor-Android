package pt.aodispor.android.features.profile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import pt.aodispor.android.R;
import pt.aodispor.android.api.aodispor.RequestBuilder;
import pt.aodispor.android.data.models.aodispor.AODISPOR_JSON_WEBAPI;
import pt.aodispor.android.data.models.aodispor.CPPQueryResult;
import pt.aodispor.android.api.HttpRequestTask;

public class LocationDialog extends DialogFragment {
    private LocationDialogListener listener;
    private EditText postalCodePrefix, postalCodeSuffix;
    private TextView locationName;
    private ProgressBar progressBar;
    private boolean set;
    private LocationDialog locationDialog = this;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        set = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View root = inflater.inflate(R.layout.localization_reg, null);
        postalCodePrefix = (EditText) root.findViewById(R.id.zip1);
        postalCodeSuffix = (EditText) root.findViewById(R.id.zip2);
        locationName = (TextView) root.findViewById(R.id.locationName);
        progressBar = (ProgressBar) root.findViewById(R.id.location_progress_bar);
        postalCodePrefix.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (postalCodePrefix.length() == 4) {
                    postalCodeSuffix.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        postalCodeSuffix.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (postalCodeSuffix.length() == 3) {
                    String prefix = postalCodePrefix.getText().toString();
                    String suffix = postalCodeSuffix.getText().toString();
                    locationName.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    HttpRequestTask<AODISPOR_JSON_WEBAPI> request = RequestBuilder.buildLocationRequest(prefix,suffix);
                    request.addOnSuccessHandlers(onRequestSuccess);
                    request.addOnFailHandlers(onRequestFailed);
                    request.execute();
                } else {
                    progressBar.setVisibility(View.GONE);
                    locationName.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        builder.setView(root);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        return dialog;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onDismiss(set, locationName.getText().toString(), postalCodePrefix.getText().toString(), postalCodeSuffix.getText().toString());
        }
    }

    public void setListener(LocationDialogListener l) {
        listener = l;
    }


    HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> onRequestSuccess =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI answer) {

                    if (postalCodeSuffix.length() == 3 && postalCodePrefix.length() == 4) {
                        CPPQueryResult result = (CPPQueryResult) answer;
                        if (result != null && result.data != null) {
                            progressBar.setVisibility(View.GONE);
                            locationName.setText(result.data.getLocalidade());
                            locationName.setVisibility(View.VISIBLE);
                            set = true;
                        }
                    }
                }
            };

    HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> onRequestFailed =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI answer) {
                    set = false;
                    locationName.setText(R.string.empty);
                    progressBar.setVisibility(View.GONE);
                    locationName.setVisibility(View.VISIBLE);
                }
            };

    public interface LocationDialogListener {
        void onDismiss(boolean set, String locationName, String prefix, String suffix);
    }

}
