package pt.aodispor.android.notifications;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "GcmListenerService";

    /*@Override
    public void onMessageReceived(String from, Bundle data) {
        //String message = data.getString("message");
        Log.d(TAG, "From: " + from);
       // Log.d(TAG, "Message: " + message);

        // ...
    }*/
}
