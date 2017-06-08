package pt.aodispor.android.features.shared;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.HashMap;

import pt.aodispor.android.R;

public class EnumSpinner<X extends ISpinnerEnum> {

    //String[] spinnerDisplayStrings;
    //private ArrayAdapter<String> adapter;
    private HashMap<String, X> spinnerMap;
    private Spinner spinner;


    public EnumSpinner(Context context, Spinner spinner, X[] values) {
        this.spinner = spinner;

        String[] spinnerDisplayStrings = new String[values.length];
        spinnerMap = new HashMap<>();

        for (int i = 0; i < values.length; i++) {
            spinnerMap.put(values[i].getDisplayString(), values[i]);
            spinnerDisplayStrings[i] = values[i].getDisplayString();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.currency_spinner_layout, spinnerDisplayStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public X getSelectedItem() {
        //noinspection SuspiciousMethodCalls
        return spinnerMap.get(spinner.getSelectedItem());
    }

}
