package pt.aodispor.android.view.tests;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.Delegator;
import pt.aodispor.android.Utility;
import pt.aodispor.android.api.Professional;
import pt.aodispor.android.api.SearchQueryResult;
import pt.aodispor.android.api.UserRequest;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class TestAuxImplementations {

    @Test
    public void testMyDelegator() throws IllegalAccessException, InstantiationException {

        Delegator testme = new Delegator(String.class, int.class);

        //try {
        //    testme.addMethod(true, TestMe.class /*Class.forName("TestMe")*/, "privateStaticMethod");
        //}catch (Exception e){
       //     e.printStackTrace();
        //}
        //testme.addMethod(true, TestAuxImplementations.class, "protectedStaticMethod");
        //testme.addMethod(false, TestAuxImplementations.class, "publicStaticMethod");
        testme.addMethod(true, this, "privateMethod");
        testme.addMethod(true, this, "protectedMethod");
        testme.addMethod(false, this, "publicMethod");
        testme.execute("some text..." , 4145);
    }

    static private void privateStaticMethod(String text, int value) {
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + text + value);
    }

    static protected void protectedStaticMethod(String text, int value) {
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + text + value);
    }

    static public void publicStaticMethod(String text, int value) {
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + text + value);
    }

    private void privateMethod(String text, int value) {
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + text + value);
    }

    protected void protectedMethod(String text, int value) {
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + text + value);
    }

    public void publicMethod(String text, int value) {
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + text + value);
    }

    @Test
    public void testDeserializer(){

        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(AppDefinitions.TIMEDATE_FORMAT);

        Date someDate1 = null;
        Date someDate2 = null;
        try{
            someDate1 = simpleDateFormat.parse("2017-04-10 20:35:55");
            someDate2 = simpleDateFormat.parse("2017-04-12 20:35:56");
        }catch (Exception e){}
        Period period = new Period(someDate1.getTime(),someDate2.getTime(), PeriodType.standard());
        Assert.assertTrue( period.getHours() == 0);
        Assert.assertTrue( period.getDays() == 2);

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
            assertTrue(sqr.data.get(0).getClass()== UserRequest.class);
            assertTrue(sqr.data.get(0).getClass()!= Professional.class);
            assertTrue(sqr.data.get(1).getClass()== Professional.class);
            assertTrue(sqr.data.get(1).getClass()!= UserRequest.class);

            period = new Period(someDate1.getTime(),((UserRequest)sqr.data.get(0)).getExpirationDate().getTime(), PeriodType.standard());
            Assert.assertTrue( period.getHours() == 0);
            Assert.assertTrue( period.getDays() == 2);
        } catch (Exception e) {e.printStackTrace(); fail();}
    }

}
