package pt.aodispor.android.profile;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;
import pt.aodispor.android.dialogs.LocationDialog;
import pt.aodispor.android.dialogs.NewPriceDialog;

public class Profile extends ListItem implements LocationDialog.LocationDialogListener, NewPriceDialog.PriceDialogListener {
    private final String LOCATION_TAG = "location";
    private static final String PRICE_DIALOG_TAG = "price-dialog";
    private Profile thisObject;
    private FragmentActivity activity;
    private TextView nameView, professionView, locationView, priceView, descriptionView;
    private EditText nameEdit, professionEdit, locationEdit, priceEdit, descriptionEdit;
    private View root;
    private int rate;
    private boolean isFinal;
    private String currency;
    private NewPriceDialog.PriceType type;

    public Profile(Context c, FragmentActivity a) {
        super(c);
        thisObject = this;
        activity = a;
        root = LayoutInflater.from(context).inflate(R.layout.profile, null);

        // Get Text Views
        nameView = (TextView) root.findViewById(R.id.name);
        professionView = (TextView) root.findViewById(R.id.profession);
        priceView = (TextView) root.findViewById(R.id.price);
        locationView = (TextView) root.findViewById(R.id.location);
        descriptionView = (TextView) root.findViewById(R.id.description);

        // Get Edit Text Views
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

        priceEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewPriceDialog dialog = NewPriceDialog.newInstance(rate, isFinal, type, currency);
                dialog.setListener(thisObject);
                dialog.show(activity.getSupportFragmentManager(), PRICE_DIALOG_TAG);
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

    public void setPrice(int p, boolean f, NewPriceDialog.PriceType t, String c) {
        rate = p;
        isFinal = f;
        type = t;
        currency = c;
        priceEdit.setText(rate + " " + currency);
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
    public void onDismiss(boolean set, String locationName, String prefix, String suffix) {

    }

    @Override
    public void onPriceChanged(int rate, boolean isFinal, NewPriceDialog.PriceType type, String currency) {
        setPrice(rate, isFinal, type, currency);
    }

    private void setFonts() {
        // Text Views
        nameView.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        professionView.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        priceView.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        locationView.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        descriptionView.setTypeface(AppDefinitions.yanoneKaffeesatzBold);

        // Edit Views
        nameEdit.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        professionEdit.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        locationEdit.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        priceEdit.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        descriptionEdit.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
    }
}
