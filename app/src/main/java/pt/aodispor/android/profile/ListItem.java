package pt.aodispor.android.profile;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public abstract class ListItem {
    protected Notification notification;
    protected Context context;

    public ListItem(Context c) {
        context = c;
    }

    public void setNotification(Notification n) {
        notification = n;
    }

    public View getView() {
        return new View(context);
    }

    public void notifyUpdate() {
        if(notification != null) {
            notification.notify(this, true, "");
        }
    }

    public abstract boolean onStart();

    public abstract boolean onUpdate();

}
