package pt.aodispor.android;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.HashMap;

import pt.aodispor.android.professional.CurrencyType;

public class EnumSpinner<X extends ISpinnerEnum>{

    CurrencyType[] currencyTypes = CurrencyType.values();
    String[] spinnerDisplayStrings = new String[currencyTypes.length];
    HashMap<String,X> spinnerMap = new HashMap<String, X>();
    ArrayAdapter<String> adapter;
    Spinner spinner;


    public EnumSpinner(Context context, Spinner spinner, X[] values) {
        this.spinner = spinner;

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
