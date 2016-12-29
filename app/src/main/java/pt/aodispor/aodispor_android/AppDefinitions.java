package pt.aodispor.aodispor_android;

import android.graphics.Typeface;

/**
 * Defines constans and globally used variables
 */
public class AppDefinitions {
    /* NOTE related to anything that uses [API SearchQueryResult.java]
     * Number of results (professionals) per page can be accessed in the API JSON
     * and should not therefore be defined as a constant
     */

    /**
     * set true to skip login related dialogs
     */
    public static final boolean SKIP_LOGIN = false;

    /**
     * used in AoDispor logo/title
     */
    public static Typeface dancingScriptRegular;
    public static Typeface yanoneKaffeesatzBold;
    public static Typeface yanoneKaffeesatzLight;
    public static Typeface yanoneKaffeesatzRegular;
    public static Typeface yanoneKaffeesatzThin;

    //public static final int MILISECONDS_TO_TIMEOUT_ON_QUERY = 5000;//TODO might be used 4 all http request - left 2 decide
    public static final int TIMEOUT = 5000;

    /**
     * [CardFragment.java] - nextSet loading
     * will try to load the nextSet
     * when having this number of cards or less left in currentSet
     * (should always be higher than 2)
     */
    public static final int MIN_NUMBER_OFCARDS_2LOAD = 5;

    static final int DISCARD_ANIMATION_MILLISECONDS = 300;
    static final int RESTORE_ANIMATION_MILLISECONDS = 300;

    /**
     * Indicates if the user is registered as a professional
     */
    public static boolean loggedInAsProfessional = false;

    /**
     * postal code to be sent to 'tokens' database
     */
    public static int postal_code;

    //PERMISSIONS
    //used to identify permissions (possibly composite) without using arrays and/or Manifest.permission...
    public static final int PERMISSION_NOT_REQUESTED = 20;
    public static final int PERMISSIONS_REQUEST_READ_SMS = 2;
    public static final int PERMISSIONS_REQUEST_PHONENUMBER = 3;
    public static final int PERMISSIONS_REQUEST_INTERNET = 4;

}
