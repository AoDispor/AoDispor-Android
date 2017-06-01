package pt.aodispor.android.features.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import pt.aodispor.android.data.models.aodispor.GCMServerInstance;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    private static final String URL = "http://notificacoes.aodispor.pt/store_gcm_token.php";

    public RegistrationIntentService() {
        super(TAG);
        Log.i(TAG, "Constructing object");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);

            sendRegistrationToServer(token);
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            e.printStackTrace();
        }
    }
    private void sendRegistrationToServer(String token) {
        HttpRequestTask request = HttpRequestTask.POST(String.class,URL);
        // new HttpRequestTask(String.class, this, URL);
        //request.setMethod(HttpRequestTask.POST_REQUEST);

        GCMServerInstance gcm_server_instance = new GCMServerInstance();
        gcm_server_instance.gcm_token = token;
        gcm_server_instance.postal_code = AppDefinitions.postal_code;

        request.setJSONBody(gcm_server_instance);
        request.execute();
    }

   /* @Override
    public void onHttpRequestSuccessful(ApiJSON answer, int type) {

    }

    @Override
    public void onHttpRequestFailed(ApiJSON errorData, int type) {

    }*/
}
