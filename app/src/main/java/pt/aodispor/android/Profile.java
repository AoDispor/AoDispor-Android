package pt.aodispor.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class Profile extends ListItem {

    public Profile(Context c) {
        super(c);
    }

    @Override
    public View getView() {
        View root = LayoutInflater.from(context).inflate(R.layout.profile_field, null);
        return root;
    }
}
