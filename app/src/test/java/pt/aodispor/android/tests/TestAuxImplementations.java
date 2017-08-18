package pt.aodispor.android.tests;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.junit.Assert;
import org.junit.Test;
import org.robolectric.shadows.ShadowLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.base.MockDateUtils;
import pt.aodispor.android.data.models.aodispor.Professional;
import pt.aodispor.android.data.models.aodispor.SearchQueryResult;
import pt.aodispor.android.data.models.aodispor.UserRequest;
import pt.aodispor.android.utils.DateUtils;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class TestAuxImplementations {

    @Test
    public void testDeserializer() {

        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(AppDefinitions.TIMEDATE_FORMAT);

        Date someDate1 = null;
        Date someDate2 = null;
        try {
            someDate1 = simpleDateFormat.parse("2017-04-10 20:35:55");
            someDate2 = simpleDateFormat.parse("2017-04-12 20:35:56");
        } catch (Exception ignored) {
        }
        Period period = new Period(someDate1.getTime(), someDate2.getTime(), PeriodType.standard());
        Assert.assertTrue(period.getHours() == 0);
        Assert.assertTrue(period.getDays() == 2);

        String data =
                "{" +
                        "\"data\":" +
                        "[" +
                        "{" +
                        "\"full_name\":\"dood1 req\"," +
                        "\"data_expiracao\":\"2017-04-12 20:35:56\"" +
                        "}," +
                        "{" +
                        "\"full_name\":\"dood3 prof\"" +
                        "}" +
                        "]" +
                        "}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            SearchQueryResult sqr = mapper.readValue(data, SearchQueryResult.class);
            assertTrue(sqr.data.get(0).getClass() == UserRequest.class);
            assertTrue(sqr.data.get(0).getClass() != Professional.class);
            assertTrue(sqr.data.get(1).getClass() == Professional.class);
            assertTrue(sqr.data.get(1).getClass() != UserRequest.class);

            period = new Period(someDate1.getTime(), ((UserRequest) sqr.data.get(0)).getExpirationDate().getTime(), PeriodType.standard());
            Assert.assertTrue(period.getHours() == 0);
            Assert.assertTrue(period.getDays() == 2);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDate() {

        long timenow1 =
                new Date().getTime()
                        + TimeZone.getTimeZone(DateUtils.ADServerTimeZoneName).getRawOffset();//sets to AoDispor TimeZone
        long timenow2 = System.currentTimeMillis() + TimeZone.getTimeZone(DateUtils.ADServerTimeZoneName).getRawOffset();
        long timenow3 = org.joda.time.DateTimeUtils.currentTimeMillis() + TimeZone.getTimeZone(DateUtils.ADServerTimeZoneName).getRawOffset();

        long timenowUsed = DateUtils.getServerTime();

        long timenow_m5 = new Date().getTime()
                + TimeZone.getTimeZone("GMT-5:00").getRawOffset();

        Assert.assertTrue(Math.abs(timenowUsed - timenow1) < 1000);
        Assert.assertTrue(Math.abs(timenowUsed - timenow2) < 1000);
        Assert.assertTrue(Math.abs(timenowUsed - timenow3) < 1000);
        Assert.assertTrue(Math.abs(timenowUsed - timenow_m5) > 1000);

        long twosdaysfromnow =
                timenowUsed +
                        2 * 24 * 3600 * 1000;

        long randinthefuture =
                timenowUsed +
                        Math.abs(new Random().nextLong())
                                % (15 * 24 * 3600 * 1000) + //max 15 days+ here
                        24 * 3600 * 1000 //+1day guaranteed
                ;

        /*MockContext a = Mockito.mock(MockContext.class);
        Mockito.when(a.getString(any(Integer.class))).thenReturn("D/");
        DateUtils.setPeriodSuffixes(a);*/

        MockDateUtils.setMockupPeriodSuffixes(new String[]{
                "D/", "Ds/"
        });

        ShadowLog.stream = System.out;
        ShadowLog.i("DateUtils.timeDifference", "\n" +
                "verify if the printed results are within expectations\n" +
                "now               : " + new DateTime(timenowUsed).toString() + "\n" +
                "after tomorrow    : " + new DateTime(twosdaysfromnow).toString() + "\n" +
                "time until then   : " + DateUtils.timeDifference(timenowUsed, twosdaysfromnow) + "\n" +
                "sometime in future: " + new DateTime(randinthefuture).toString() + "\n" +
                "time until then   : " + DateUtils.timeDifference(timenowUsed, randinthefuture)
        );

    }

}
