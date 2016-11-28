package pt.aodispor.aodispor_android;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

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

    /**
     * Used to obtain the last message received from a specified sender.
     * @param ctx Obtained with getApplicationContext()
     * @param sender Message sender
     */
    public static String getLastMessageBody(Context ctx, String sender)
    {
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[] { "_id", "address", "body"};
        ContentResolver cr = ctx.getContentResolver();
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);
        if (c.getCount() == 0)
            return null;
        c.moveToFirst();
        while (!c.isLast())
        {
            if (c.getString(1).equals(sender))
            {
                String temp = c.getString(2);
                c.close();
                return temp;
            }
            c.moveToNext();
        }
        return null;
    }

    /**
     * @param ctx Obtained using getApplicationContext()
     * @return Returns the phone number, empty string if unavailable.
     */
    public String getPhoneNumber(Context ctx)
    {
        TelephonyManager tMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }
}
