package pt.aodispor.android.profile;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class ListItem {
    protected Context context;
    protected FragmentActivity activity;

    public ListItem(Context c) {
        context = c;
    }

    public View getView() {
        return new View(context);
    }

}
