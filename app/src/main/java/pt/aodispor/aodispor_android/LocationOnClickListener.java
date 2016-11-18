package pt.aodispor.aodispor_android;

import android.app.Activity;

import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

public class LocationOnClickListener implements View.OnClickListener {
    private Activity _activity;
    private ProfileFragment fragment;
    private TextView _location;

    LocationOnClickListener(Activity activity, ProfileFragment f, TextView location) {
        this._activity = activity;
        this._location = location;
        this.fragment = f;
    }

    @Override
    public void onClick(View view) {
        final LocationDialog dialog = new LocationDialog(_activity);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                fragment.onLocationDialogCallBack(dialog.getLocation(), dialog.getCp4(), dialog.getCp3(), dialog.isLocationSet());
            }
        });
        dialog.setLocation(_location);
        dialog.show();
    }
}
