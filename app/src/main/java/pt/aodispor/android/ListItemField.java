package pt.aodispor.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class ListItemField extends ListItem {
    private String fieldName;

    public ListItemField(Context c, String fn) {
        super(c);
        fieldName = fn;
    }

    @Override
    public View getView() {
        return LayoutInflater.from(context).inflate(R.layout.profile_field, null);
    }
}
