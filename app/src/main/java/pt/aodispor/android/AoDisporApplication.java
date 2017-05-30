package pt.aodispor.android;

import android.app.Application;

import com.github.stkent.amplify.tracking.Amplify;

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
                .applyAllDefaultRules();
    }
}
