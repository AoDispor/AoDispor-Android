package pt.aodispor.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class Profile extends ListItem {
    private EditText nameEdit, professionEdit, locationEdit, priceEdit, descriptionEdit;
    private String name, profession, location, price, description;

    public Profile(Context c) {
        super(c);
    }

    @Override
    public View getView() {
        View root = LayoutInflater.from(context).inflate(R.layout.profile, null);

        nameEdit = (EditText) root.findViewById(R.id.nameEdit);
        professionEdit = (EditText) root.findViewById(R.id.professionEdit);
        locationEdit = (EditText) root.findViewById(R.id.locationEdit);
        priceEdit = (EditText) root.findViewById(R.id.priceEdit);
        descriptionEdit = (EditText) root.findViewById(R.id.descriptionEdit);

        nameEdit.setText(name);
        professionEdit.setText(profession);
        locationEdit.setText(location);
        priceEdit.setText(price);
        descriptionEdit.setText(description);

        return root;
    }

    public void setName(String n) {
        name = n;
    }

    public void setProfession(String p) {
        profession = p;
    }

    public void setLocation(String l) {
        location = l;
    }

    public void setPrice(String p) {
        price = p;
    }

    public void setDescription(String d) {
        description = d;
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

}
