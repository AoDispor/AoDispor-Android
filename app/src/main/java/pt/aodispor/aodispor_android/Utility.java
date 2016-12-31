package pt.aodispor.aodispor_android;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.ByteArrayOutputStream;

import pt.aodispor.aodispor_android.API.Professional;

/**
 * Contains many methods which implement a more generic functionality
 */
public abstract class Utility {

    /**
     * Auxiliary method to convert density independent pixels to actual pixels on the screen
     * depending on the systems metrics.
     *
     * @param dp the number of density independent pixels.
     * @return the number of actual pixels on the screen.
     */
    public static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Converts an image into a byte array after converting to JPEG.
     * <br>(not a lossless conversion)
     *
     * @param image bitimage to be converted
     * @return compressed image as a byte array
     */
    public static byte[] convertBitmapToBinary(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    /**
     * Used to obtain the last message received from a specified sender.
     *
     * @param ctx    Obtained with getApplicationContext()
     * @param sender Message sender
     */
    @Nullable
    public static String getLastMessageBody(Context ctx, String sender) {
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[]{"_id", "address", "body"};
        ContentResolver cr = ctx.getContentResolver();
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);
        if (c.getCount() == 0)
            return null;
        c.moveToFirst();
        while (!c.isLast()) {
            if (c.getString(1).equals(sender)) {
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
    public static String getPhoneNumber(Context ctx) {
        TelephonyManager tMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    public static boolean isProfessionalRegistered(Professional info) {

        if (info.full_name == null || info.full_name.equals("")) return false;
        if (info.avatar_url == null || info.avatar_url.equals("")) return false;
        if (info.title == null || info.title.equals("")) return false;
        if (info.currency == null || info.currency.equals("")) return false;
        if (info.type == null || info.type.equals("")) return false;
        if (info.phone == null || info.phone.equals("")) return false;
        if (info.rate == null || info.rate.equals("")) return false;
        if (info.location == null || info.location.equals("")) return false;

        return true;
    }

    public static boolean isPostalCodeSet(Professional p) {
        if (p.cp4 == null || p.title.equals("")) return false;

        return true;
    }


    /**
     * Google Play services
     */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Verifies if the current device supports google play services.
     * <br>(will not send any notification to te device user)
     *
     * @return true if supported
     */
    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);//this.getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("ProfileFragment", "This device is not supported.");
                activity.finish();//this.getActivity().finish();
            }
            return false;
        }
        return true;
    }


    public static boolean validPhoneNumber(String phoneNumber)
    {
        //has 9 digits
        if(phoneNumber.length() < 9) return false;
        //possibly some other types of validation
        //...
        return true;
    }
}
