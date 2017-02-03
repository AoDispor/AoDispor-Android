package pt.aodispor.android;

import android.graphics.Typeface;

/**
 * Defines constans and globally used variables
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

    public static final String testPhoneNumber = "+351912488434";
    public static final String testPassword = "123456";//"??????"

    //endregion


    //region Typefaces

    /**
     * used in AoDispor logo/title
     */
    public static Typeface dancingScriptRegular;
    public static Typeface yanoneKaffeesatzBold;
    public static Typeface yanoneKaffeesatzLight;
    public static Typeface yanoneKaffeesatzRegular;
    public static Typeface yanoneKaffeesatzThin;
    //endregion

    /**
     * phone number that sends password via SMS
     */
    public static final String[] PASSWORD_SMS_PHONES= new String[]{
            "+320335320002",
            "+447903571480"
    };

    /**
     * Number of milliseconds needed for a timeout to occur in requests
     */
    public static final int TIMEOUT = 20000;

    /**
     * [CardFragment.java] - nextSet loading
     * will try to load the nextSet
     * when having this number of cards or less left in currentSet
     * <br>(should always be higher than 2!)
     */
    public static final int MIN_NUMBER_OFCARDS_2LOAD = 5;

    static final int DISCARD_ANIMATION_MILLISECONDS = 300;
    static final int RESTORE_ANIMATION_MILLISECONDS = 300;

    //region USER RELATED DATA

    /**
     * Phone Number used on login
     */
    public static String phoneNumber = "";

    /**
     * Password used to login. Professional profile editing is blocked without the correct password.
     * */
    public static String userPassword = "";

    /*
     * Indicates if the user is registered as a professional
    public static boolean loggedInAsProfessional = false;
    */

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
