package pt.aodispor.android;

import android.content.Intent;
import android.view.View;
import android.support.v4.app.Fragment;

public class ImageOnClickListener implements View.OnClickListener {
    private String _name;
    private String _profession;
    private String _location;
    private String _description;
    private String _price;
    private String _currency;
    private String _type;
    private String _avatar_url;

    private Fragment _frag;

    public ImageOnClickListener(String name, String profession, String location, String description, String price, String currency, String type, String avatar_url, Fragment frag) {
        this._name = name;
        this._profession = profession;
        this._location = location;
        this._description = description;
        this._price = price;
        this._currency = currency;
        this._type = type;
        this._avatar_url = avatar_url;

        this._frag = frag;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this._frag.getActivity(), ProfessionalProfileActivity.class);

        intent.putExtra("name",this._name);
        intent.putExtra("profession",this._profession);
        intent.putExtra("location",this._location);
        intent.putExtra("description",this._description);
        intent.putExtra("price",this._price);
        intent.putExtra("currency",this._currency);
        intent.putExtra("type",this._type);
        intent.putExtra("avatar_url",this._avatar_url);

        _frag.startActivity(intent);
    }
}
