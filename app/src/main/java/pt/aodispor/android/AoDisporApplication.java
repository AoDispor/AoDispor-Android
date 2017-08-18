package pt.aodispor.android;

import android.app.Application;
import android.support.annotation.NonNull;

import com.github.stkent.amplify.tracking.Amplify;

import static pt.aodispor.android.AppDefinitions.FORCE_AMPLIFY;

public class AoDisporApplication extends Application {

    private static AoDisporApplication singleton;

    static public AoDisporApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;

        Amplify.initSharedInstance(this)
                .setFeedbackEmailAddress(getString(R.string.feedback_email))
                .setAlwaysShow(FORCE_AMPLIFY)
                .applyAllDefaultRules();
    }

    @NonNull
    static public String getStringResource(int id) {
        return getInstance().getResources().getString(id);
    }
}
