package pt.aodispor.android;

import android.graphics.Typeface;

import java.text.SimpleDateFormat;

/**
 * Defines constants and globally used variables
 */
public class AppDefinitions {
    /* NOTE related to anything that uses [api SearchQueryResult.java]
     * Number of results (professionals) per page can be accessed in the api JSON
     * and should not therefore be defined as a constant
     */

    //region DEBUG/DEVELOPMENT ONLY

    /**
     * set true to skip login related dialogs
     * <br>the phone number used is equal to the test phone number
     * even if te user inputs otherwise. (used for DEVELOPMENT AND DEBUG ONLY!)
     */
    public static final boolean SKIP_LOGIN = true;

    public static final String testPhoneNumber = "+351" + "911793861";
    public static final String testPassword = "835358";

    /**always show amplify - for debug purposes*/
    static final boolean FORCE_AMPLIFY = false;

    public static String aoDisporApiBaseAddress = "https://api.aodispor.pt";

    //endregion

    static final public String TIMEDATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    static final public SimpleDateFormat TIMAEDATE_FORMATER =
            new SimpleDateFormat(AppDefinitions.TIMEDATE_FORMAT);

    /**
     * phone number that sends password via SMS
     */
    public static final String[] PASSWORD_SMS_PHONES = new String[]{
            "+351911793861",     //TODO remove my phone later (used for testing)
            "+320335320002",
            "+447903571480"
    };

    /*
     * Number of milliseconds needed for a error to occur in requests
    public static final int TIMEOUT = 20000;*/

    public static final int QUERY_MIN_LENGTH = 5;
    public static final int QUERY_MAX_LENGTH = 64;

    /**
     * [CardFragment.java] - nextSet loading
     * will try to load the nextSet
     * when having this number of cards or less left in currentSet
     * <br>(should always be higher than 2!)
     */
    public static final int MIN_NUMBER_OFCARDS_2LOAD = 5;

    public static final int DISCARD_ANIMATION_MILLISECONDS = 300;
    public static final int RESTORE_ANIMATION_MILLISECONDS = 300;

    //region USER RELATED

    /**
     * used to block certain features when user has not loggedin with the sms code
     * deve estar a FALSE em release builds
     */
    public static boolean smsLoginDone = false;
    /**
     * postal code to be sent to 'tokens' database
     */
    public static int postal_code;

    //endregion

    //region PERMISSIONS
    //used to identify permissions (possibly composite) without using arrays and/or Manifest.permission...
    public static final int PERMISSION_NOT_REQUESTED = 20;
    public static final int PERMISSIONS_REQUEST_READ_SMS = 2;
    public static final int PERMISSIONS_REQUEST_PHONENUMBER = 3;
    public static final int PERMISSIONS_REQUEST_INTERNET = 4;
    public static final int PERMISSIONS_REQUEST_GPS = 5;
    //endregion
}
