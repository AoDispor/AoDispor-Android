package pt.aodispor.android.profile;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public abstract class ListItem {
    private boolean updated;
    protected Notification notification;
    protected Context context;

    public ListItem(Context c) {
        updated = false;
        context = c;
    }

    public void setNotification(Notification n) {
        notification = n;
    }

    public View getView() {
        return new View(context);
    }

    public boolean isUpdated() {
        return updated;
    }

    public void notifyUpdate() {
        if(notification != null) {
            notification.notify(this);
        }
    }

    public abstract boolean onStart();

    public abstract boolean onUpdate();

}
