package pt.aodispor.android.utils;

import android.support.annotation.VisibleForTesting;

import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Locale;
import java.util.TimeZone;

import pt.aodispor.android.AoDisporApplication;
import pt.aodispor.android.R;

public class DateUtils {

    @VisibleForTesting
    protected DateUtils() {
    }

    public static final String ADServerTimeZoneName = "GMT";
    public static final Locale defaultLocale = Locale.US;

    private static void setPeriodSuffixes() {
        DateUtils.periodSuffixes = new String[]{
                " " + AoDisporApplication.getStringResource(R.string.day),
                " " + AoDisporApplication.getStringResource(R.string.days)};
    }

    @VisibleForTesting
    protected static String[] periodSuffixes;

    /**
     * get the current <b>server</b> date as the milliseconds since Java epoch
     *
     * @return a long that represents the time of the current server date
     */
    public static long getServerTime() {
        /*note
        these should return the same value
                        System.currentTimeMillis() -> fastest
                        org.joda.time.DateTimeUtils.currentTimeMillis()
                        new Date().getTime()
        */
        return System.currentTimeMillis()
                + TimeZone.getTimeZone(DateUtils.ADServerTimeZoneName).getRawOffset();
    }

    /**
     * returns a string with the difference between 2 dates given as milliseconds since the Java epoch.
     * the string contains the difference in days, hours, minutes and seconds.
     *
     * @param time1 usually a time prior to time2
     * @param time2 usually a time after time1
     * @return a text with the period between the 2 times
     */
    static public String timeDifference(long time1, long time2) {
        if (periodSuffixes == null) setPeriodSuffixes();
        return new PeriodFormatterBuilder()
                .printZeroNever()
                .appendDays()
                .appendSuffix(periodSuffixes[0], periodSuffixes[1])
                .appendSeparator(", ")
                .minimumPrintedDigits(2).printZeroAlways()
                .appendHours()
                .appendSeparator(":")
                .appendMinutes()
                .appendSeparator(":")
                .appendSeconds()
                .toFormatter()
                .print(new Period(time1, time2,
                        PeriodType.standard()
                                .withWeeksRemoved()
                                .withMonthsRemoved()
                                .withYearsRemoved()
                ));
    }
}
