package pt.aodispor.android;

import android.content.Context;
import android.view.View;

public class ListItem {
    protected Context context;

    public ListItem(Context c) {
        context = c;
    }

    public View getView() {
        return new View(context);
    }

}
