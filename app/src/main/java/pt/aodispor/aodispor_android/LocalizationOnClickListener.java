package pt.aodispor.aodispor_android;

import android.app.Activity;

import android.view.View;
import android.widget.TextView;

public class LocalizationOnClickListener implements View.OnClickListener {
    private Activity _activity;
    private TextView _location;

    LocalizationOnClickListener(Activity activity, TextView location) {
        this._activity = activity;
        this._location = location;
    }

    @Override
    public void onClick(View view) {
        final LocalizationDialog dialog = new LocalizationDialog(_activity);
        dialog.setLocation(_location);
        dialog.show();
    }
}
