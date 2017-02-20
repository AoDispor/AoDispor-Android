package pt.aodispor.android;

import android.app.Application;

import com.github.stkent.amplify.tracking.Amplify;

/**
 * Created by lamelas on 19/02/17.
 */

public class AoDisporApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Amplify.initSharedInstance(this)
                .setFeedbackEmailAddress(getString(R.string.feedback_email))
                .applyAllDefaultRules();
    }
}
