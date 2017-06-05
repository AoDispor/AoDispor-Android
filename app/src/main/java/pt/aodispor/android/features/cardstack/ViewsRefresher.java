package pt.aodispor.android.features.cardstack;

import android.os.Handler;
import android.util.Log;

public class ViewsRefresher {

    private final static int delayBetweenLayoutUpdates = 1000;
    private Handler handler;
    private Boolean running_stuff = false;
    private CardStackContainer cardStackContainer;
    private final Object lock = new Object(); //just in case

    public ViewsRefresher(CardStackContainer cardStackContainer) {
        this.cardStackContainer = cardStackContainer;
        this.running_stuff = false;
    }

    public void startTask() {
        synchronized (lock) {
            if (running_stuff) return;
            running_stuff = Boolean.TRUE;
            if (handler == null) handler = new Handler();
            handler.postDelayed(runnable, delayBetweenLayoutUpdates);
            //Log.d("ViewsRefresher", "START");
        }
    }

    public void stopTask() {
        synchronized (lock) {
            if (!running_stuff) return;
            handler.removeCallbacksAndMessages(null);
            running_stuff = Boolean.FALSE;
            //Log.d("ViewsRefresher", "STOP");
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (cardStackContainer.cardStack.updateCardViews()) {
                handler.postDelayed(this, delayBetweenLayoutUpdates);
                //Log.d("ViewsRefresher", "REPEAT");
            } else synchronized (lock) {
                running_stuff = Boolean.FALSE;
                //Log.d("ViewsRefresher", "SLEEP");
            }
        }
    };

}
