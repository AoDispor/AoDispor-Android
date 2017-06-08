package pt.aodispor.android.api.aodispor;

import android.util.Base64;
import android.util.Log;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.data.local.UserData;
import pt.aodispor.android.data.models.aodispor.AODISPOR_JSON_WEBAPI;

public class BasicRequestInfo {

    private static String token = null;
    private static final String serverTimeZone = "UTC";

    String URL;
    private boolean needsUserAuth;
    private Class answerType;
    //TODO maybe define timeouts later (connection, read, write)

    BasicRequestInfo(String URL, boolean needsUserLogin) {
        this.URL = URL;
        this.needsUserAuth = needsUserLogin;
        answerType = String.class;
    }

    BasicRequestInfo(String URL, boolean needsUserLogin, Class answer_type) {
        this.URL = URL;
        this.needsUserAuth = needsUserLogin;
        this.answerType = answer_type;
    }

    HttpRequestTask setHeaders(HttpRequestTask _request) {
        HttpHeaders headers = new HttpHeaders();
        if (needsUserAuth) add_User_Auth(headers);
        _request.setHeader(headers);
        add_API_Auth(headers);
        return _request;
    }

    HttpRequestTask<AODISPOR_JSON_WEBAPI> buildSimpleRequest() {
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = HttpRequestTask.GET(answerType, URL);
        this.setHeaders(request);
        return request;
    }

    public static void setToken(String _token) {
        if (token == null) token = _token;
    }

    static private String getLocalDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.UK);
        //TODO next line might not be needed anymore, just define Locale in definitions... maybe?
        dateFormat.setTimeZone(TimeZone.getTimeZone(serverTimeZone));
        return dateFormat.format(new Date());
    }

    static private void add_API_Auth(HttpHeaders httpHeaders) {
        final String date = getLocalDate();
        MediaType[] mediaTypes = {MediaType.APPLICATION_JSON};
        httpHeaders.setAccept(Arrays.asList(mediaTypes));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("API-Authorization", token + date);
    }

    static private void add_User_Auth(HttpHeaders httpHeaders) {
        UserData.UserAuthentication auth = UserData.getInstance().getUserLoginAuth();
        String encoded_header_value = "Basic "
                + Base64.encodeToString((auth.phone_number + ":" + auth.validation_code).getBytes(), Base64.DEFAULT);
        httpHeaders.add("Authorization", encoded_header_value);
    }

}
