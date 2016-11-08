package pt.aodispor.aodispor_android;

import android.content.res.Resources;

public abstract class Utility {

    /**
     * Auxiliary method to convert density independent pixels to actual pixels on the screen
     * depending on the systems metrics.
     * @param dp the number of density independent pixels.
     * @return the number of actual pixels on the screen.
     */
    public static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
