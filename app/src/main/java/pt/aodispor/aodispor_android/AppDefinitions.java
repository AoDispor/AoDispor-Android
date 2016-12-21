package pt.aodispor.aodispor_android;

import android.graphics.Typeface;

public class AppDefinitions {
    /* NOTE related to anything that uses [API SearchQueryResult.java]
     * Number of results (professionals) per page can be accessed in the API JSON
     * and should not therefore be defined as a constant
     */

    public static Typeface dancingScriptRegular;

    public static Typeface yanoneKaffeesatzBold;
    public static Typeface yanoneKaffeesatzLight;
    public static Typeface yanoneKaffeesatzRegular;
    public static Typeface yanoneKaffeesatzThin;

    /**[CardFragment.java] - http request timeout */
    //public static final int MILLISECONDS_TO_TIMEOUT_ON_QUERY = 5000;
    public static final int TIMEOUT = 5000;
    /**[CardFragment.java] - nextSet loading
     * will try to load the nextSet
     * when having this number of cards or less left in currentSet
     * (should always be higher than 2) */
    public static final int MIN_NUMBER_OFCARDS_2LOAD = 5;

    /**Indicates if the user is registered as a professional*/
    public static boolean loggedInAsProfessional = false;


    //PERMISSIONS
    public static final int PERMISSION_NOT_REQUESTED = 20;
    //public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final int PERMISSIONS_REQUEST_READ_SMS = 2;
    public static final int PERMISSIONS_REQUEST_PHONENUMBER = 3;
    public static final int PERMISSIONS_REQUEST_INTERNET = 4; // Normal Permission

    public static int postal_code;

}
