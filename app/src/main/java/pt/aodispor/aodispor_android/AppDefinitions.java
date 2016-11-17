package pt.aodispor.aodispor_android;

import android.graphics.Typeface;

public class AppDefinitions {
    /* NOTE related to anything that uses [API SearchQueryResult.java]
     * Number of results (professionals) per page can be accessed in the API JSON
     * and should not therefore be defined as a constant
     */

    /**[CardFragment.java] - http request timeout */
    public static Typeface dancingScriptRegular;

    public static Typeface yanoneKaffeesatzBold;
    public static Typeface yanoneKaffeesatzLight;
    public static Typeface yanoneKaffeesatzRegular;
    public static Typeface yanoneKaffeesatzThin;

    public static final int MILISECONDS_TO_TIMEOUT_ON_QUERY = 5000;//TODO might be used 4 all http request - left 2 decide
    public static final int TIMEOUT = 5000;
    /**[CardFragment.java] - nextSet loading
     * will try to load the nextSet
     * when having this number of cards or less left in currentSet
     * (should always be higher than 2) */
    public static final int MIN_NUMBER_OFCARDS_2LOAD = 5;
}
