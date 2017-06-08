package pt.aodispor.android.features.main;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import io.fabric.sdk.android.Fabric;
import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;
import pt.aodispor.android.api.aodispor.BasicRequestInfo;
import pt.aodispor.android.data.local.UserData;
import pt.aodispor.android.features.login.OnBoardingActivity;

public class SplitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BasicRequestInfo.setToken(getResources().getString(R.string.ao_dispor_api_key));

        Fabric.with(this, new Answers(), new Crashlytics());

        final Class<? extends Activity> activityClass;
        LoginDataPreferences preferences = new LoginDataPreferences(getApplicationContext());

        Log.d("PREFERENCES", preferences.get().telephone());
        Log.d("PREFERENCES", preferences.get().password());

        if (preferences.get().hasValidPair()) {
            // Guardar username e password no AppDefinitions para a API poder usar
            // FIXME devia ser qualquer coisa mais segura...
            UserData.getInstance().setUserLoginAuth(
                    preferences.get().telephone()
                    , preferences.get().password());
            AppDefinitions.smsLoginDone = true;
            activityClass = MainActivity.class;
        } else {
            activityClass = OnBoardingActivity.class;
        }

        Intent newActivity = new Intent(SplitActivity.this, activityClass);
        newActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newActivity);

        finish();
    }
}
