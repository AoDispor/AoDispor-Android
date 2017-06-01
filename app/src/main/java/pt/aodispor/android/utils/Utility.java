package pt.aodispor.android.utils;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.aodispor.android.data.models.aodispor.Professional;

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
        return stream.toByteArray();
    }

    /**
     * Used to obtain the last message received from a specified sender.
     * [INCOMPLETE!]
     *
     * @param ctx    Obtained with getApplicationContext()
     * @param sender Message sender
     * @return { body , date }
     */
    @Nullable
    @Deprecated
    public static String[] getLastMessage(Context ctx, String sender[]) {
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[]{"_id", "address", "body", "date"};
        ContentResolver cr = ctx.getContentResolver();
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);

        long latestDate = 0;
        String[] ret = null;

        if (c.getCount() == 0)
            return null;
        c.moveToFirst();
        while (!c.isLast()) {
            boolean isSender = false;
            for (String s : sender) {
                if (c.getString(1).equals(s)) {
                    isSender = true;
                    break;
                }
            }
            if (isSender) {
                if (latestDate < 0) {//TODO finish if needed... the usage of this method is not a priority
                    ret = new String[]{c.getString(2), c.getString(3)};
                }
            }
            c.moveToNext();
        }
        c.close();
        return ret;
    }

    /**
     * @param ctx Obtained using getApplicationContext()
     * @return Returns the phone number, empty string if unavailable.
     */
    @SuppressLint("HardwareIds")
    public static String getPhoneNumber(Context ctx) {
        TelephonyManager tMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    public static boolean isProfessionalRegistered(Professional info) {
        return !(info.full_name == null || info.full_name.equals("")) &&
                !(info.avatar_url == null || info.avatar_url.equals("")) &&
                !(info.title == null || info.title.equals("")) &&
                !(info.currency == null || info.currency.equals("")) &&
                !(info.type == null || info.type.equals("")) &&
                !(info.phone == null || info.phone.equals("")) &&
                !(info.rate == null || info.rate.equals("")) &&
                !(info.location == null || info.location.equals(""));
    }

    public static boolean isPostalCodeSet(Professional p) {
        return !(p.cp4 == null || p.title.equals(""));
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


    public static boolean validPhoneNumber(String phoneNumber) {
        //has 9 digits
        if (phoneNumber.length() < 9) return false;
        //possibly some other types of validation
        //...
        return true;
    }

    /**
     * gets password token form received sms
     */
    public static String parseSMS(String sms) {
        Pattern pattern = Pattern.compile("\\d{6}");
        Matcher matcher = pattern.matcher(sms);
        matcher.find();
        return matcher.group();
    }

    /**
     * Utility method to print the professional profile data.
     *
     * @param p The professional object to print.
     */
    public static void printProfessional(Professional p) {
        Log.v("debug", "name:" + p.full_name);
        Log.v("debug", "title:" + p.title);
        Log.v("debug", "location:" + p.location);
        Log.v("debug", "rate:" + p.rate);
        Log.v("debug", "type:" + p.type);
        Log.v("debug", "description:" + p.description);
    }


    public interface IViewModifier {
        public void apply(View v);
    }

    static public void apply2AllChildrenBFS(View v, IViewModifier mod) {
        List<View> unvisited = new ArrayList<View>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            if (!(child instanceof ViewGroup)) {
                mod.apply(child);
                continue;
            }
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) unvisited.add(group.getChildAt(i));
        }
    }

}
