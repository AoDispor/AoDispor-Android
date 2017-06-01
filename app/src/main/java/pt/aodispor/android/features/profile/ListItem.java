package pt.aodispor.android.features.profile;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public abstract class ListItem {
    //protected Notification notification;
    protected Context context;

    public ListItem(Context c) {
        context = c;
    }

    /*public void setNotification(Notification n) {
        notification = n;
    }*/

    public View getView() {
        return new View(context);
    }

    public abstract boolean onStart();

    public abstract boolean onUpdate();

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
