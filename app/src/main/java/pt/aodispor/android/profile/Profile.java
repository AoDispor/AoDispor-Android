package pt.aodispor.android.profile;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import pt.aodispor.android.R;
import pt.aodispor.android.api.ApiJSON;
import pt.aodispor.android.api.HttpRequest;
import pt.aodispor.android.dialogs.LocationDialog;

public class Profile extends ListItem implements LocationDialog.LocationDialogListener, HttpRequest {
    private final String LOCATION_TAG = "location";
    private static final String PRICE_DIALOG_TAG = "price-dialog";
    private Profile thisObject;
    private FragmentActivity activity;
    private EditText nameEdit, professionEdit, locationEdit, priceEdit, descriptionEdit;
    private View root;

    public Profile(Context c, FragmentActivity a) {
        super(c);
        thisObject = this;
        activity = a;
        root = LayoutInflater.from(context).inflate(R.layout.profile, null);
        nameEdit = (EditText) root.findViewById(R.id.nameEdit);
        professionEdit = (EditText) root.findViewById(R.id.professionEdit);
        locationEdit = (EditText) root.findViewById(R.id.locationEdit);
        priceEdit = (EditText) root.findViewById(R.id.priceEdit);
        descriptionEdit = (EditText) root.findViewById(R.id.descriptionEdit);

        locationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationDialog dialog = new LocationDialog();
                dialog.setListener(thisObject);
                dialog.show(activity.getSupportFragmentManager(), LOCATION_TAG);
            }
        });
    }

    @Override
    public View getView() {
        return root;
    }

    public void setName(String n) {
        nameEdit.setText(n);
    }

    public void setProfession(String p) {
        professionEdit.setText(p);
    }

    public void setLocation(String l) {
        locationEdit.setText(l);
    }

    public void setPrice(String p) {
        priceEdit.setText(p);
    }

    public void setDescription(String d) {
        descriptionEdit.setText(d);
    }

    public String getName() {
        return nameEdit.getText().toString();
    }

    public String getProfession() {
        return professionEdit.getText().toString();
    }

    public String getLocation() {
        return locationEdit.getText().toString();
    }

    public String getPrice() {
        return priceEdit.getText().toString();
    }

    public String getDescription() {
        return descriptionEdit.getText().toString();
    }



    @Override
    public void onHttpRequestCompleted(ApiJSON answer, int type) {

    }

    @Override
    public void onHttpRequestFailed(ApiJSON errorData) {

    }

    @Override
    public void onDismiss(boolean set, String locationName, String prefix, String suffix) {

    }
}
