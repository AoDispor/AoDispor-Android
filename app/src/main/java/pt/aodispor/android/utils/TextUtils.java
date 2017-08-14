package pt.aodispor.android.utils;


import android.app.Fragment;
import android.content.Context;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import pt.aodispor.android.R;

import static android.R.attr.fragment;

public class TextUtils {

    private TextUtils() {
    }

    /**
     * prints the difference between 2 dates
     * in days, hours, minutes and seconds
     * or null if time2 older than time1
     * @param time1 usually, a time prior to time2
     * @param time2 usually a time after time1
     * @param context current context
     * @return a string with the date difference if time1<=time2. if not then returns null
     */
    static public String timeDifference(long time1, long time2, Context context){

        Period p = new Period(time1, time2, PeriodType.standard());

        /*note it seems Days are not supported =( ...
        must do calculations manually*/
        long difference = time2 - time1;
        long days = difference / (24 * 60 * 60 * 1000);

        String daysString = "";
        if (days > 0) daysString = days + " ";
        daysString += context.getString(days == 1 ? R.string.day : R.string.days) + " ";

        return daysString
                + p.getHours()
                + ":" + p.getMinutes()
                + ":" + p.getSeconds() + " "
                + context.getString(R.string.left_to_expire);
    }

}
