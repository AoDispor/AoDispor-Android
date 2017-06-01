package pt.aodispor.android.api;


import android.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import pt.aodispor.android.data.local.UserData;

//TODO IMPLEMENTAR E FAZER HTTP REQUEST TASK GENERICO
public class AoDisporHttpsRequestsBuilder {
    private static AoDisporHttpsRequestsBuilder ourInstance = new AoDisporHttpsRequestsBuilder();

    /*public static AoDisporHttpsRequestsBuilder getInstance() {
        return ourInstance;
    }*/

    private AoDisporHttpsRequestsBuilder() {
    }

    public enum REQUEST{get_card_set,get_profile_data,get_profile_image,update_profile_info,update_profile_img};

    static String token = null;
    private static final String serverTimeZone = "UTC";

    public static void setToken(String _token) {
        if (token == null) token = _token;
        else throw new RuntimeException("Tried to set token multiple times");
    }

    private String getLocalDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.UK);
        //TODO next line might not be needed anymore, just define Locale in definitions... maybe?
        dateFormat.setTimeZone(TimeZone.getTimeZone(serverTimeZone));
        return dateFormat.format(new Date());
    }

    private void add_API_Auth(HttpHeaders httpHeaders) {
        final String date = getLocalDate();
        MediaType[] mediaTypes = {MediaType.APPLICATION_JSON};
        httpHeaders.setAccept(Arrays.asList(mediaTypes));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("API-Authorization", token + date);
    }

    private void add_User_Auth(HttpHeaders httpHeaders){
        UserData.UserAuthentication auth = UserData.getInstance().getUserLoginAuth();
        String encoded_header_value = "Basic "
                + Base64.encodeToString((auth.phone_number + ":" + auth.validation_code).getBytes(), Base64.DEFAULT);
        httpHeaders.add("Authorization", "Basic " + encoded_header_value);
    }



}
