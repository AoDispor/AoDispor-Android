package pt.aodispor.android.features.shared;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.HashMap;

import pt.aodispor.android.R;

public class EnumSpinner<X extends ISpinnerEnum>{

    String[] spinnerDisplayStrings;
    HashMap<String,X> spinnerMap;
    ArrayAdapter<String> adapter;
    Spinner spinner;


    public EnumSpinner(Context context, Spinner spinner, X[] values) {
        this.spinner = spinner;

        String[] spinnerDisplayStrings = new String[values.length];
        HashMap<String,X> spinnerMap = new HashMap<String, X>();

        for (int i = 0; i < values.length; i++)
        {
            spinnerMap.put(values[i].getDisplayString() , values[i]);
            spinnerDisplayStrings[i] = values[i].getDisplayString();
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(context, R.layout.currency_spinner_layout,spinnerDisplayStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public X getSelectedItem(){
        return spinnerMap.get(spinner.getSelectedItem());
    }

}
