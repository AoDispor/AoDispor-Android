package pt.aodispor.android;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.github.stkent.amplify.tracking.Amplify;

import io.fabric.sdk.android.Fabric;

public class SplitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Answers(), new Crashlytics());

        final Class<? extends Activity> activityClass;
        LoginDataPreferences preferences = new LoginDataPreferences(getApplicationContext());

        Log.d("PREFERENCES", preferences.get().telephone());
        Log.d("PREFERENCES", preferences.get().password());

        if (preferences.get().hasValidPair()) {
            // Guardar username e password no AppDefinitions para a API poder usar
            // FIXME devia ser qualquer coisa mais segura...
            AppDefinitions.phoneNumber = preferences.get().telephone();
            AppDefinitions.userPassword = preferences.get().password();
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
