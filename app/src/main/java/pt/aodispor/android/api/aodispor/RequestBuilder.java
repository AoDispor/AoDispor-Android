package pt.aodispor.android.api.aodispor;

import android.graphics.Bitmap;
import android.util.Base64;

import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.data.models.aodispor.AODISPOR_JSON_WEBAPI;
import pt.aodispor.android.data.models.aodispor.CPPQueryResult;
import pt.aodispor.android.data.models.aodispor.Professional;
import pt.aodispor.android.data.models.aodispor.Register;
import pt.aodispor.android.data.models.aodispor.SearchQueryResult;
import pt.aodispor.android.features.cardstack.GeoLocation;
import pt.aodispor.android.utils.Utility;

//TODO IMPLEMENTAR E FAZER HTTP REQUEST TASK GENERICO
public class RequestBuilder {

    //private static AoDisporHttpsRequestsBuilder ourInstance = new AoDisporHttpsRequestsBuilder();

    private RequestBuilder() {
    }

    private static final BasicRequestInfo queryProfilesURL = new BasicRequestInfo(
            "https://api.aodispor.pt/profiles/?query={query}&lat={lat}&lon={lon}", false
    );
    private static final BasicRequestInfo queryProfilesURLempty = new BasicRequestInfo(
            "https://api.aodispor.pt/profiles/?query=&lat={lat}&lon={lon}", false
    );
    private static final BasicRequestInfo REGISTER_URL = new BasicRequestInfo(
            "https://api.aodispor.pt/users/register", false
    );
    /**
     * for login validation ???
     */
    private static final BasicRequestInfo MYSELF_URL = new BasicRequestInfo(
            "https://api.aodispor.pt/users/me", false, SearchQueryResult.class
    );
    private static final BasicRequestInfo URL_MY_PROFILE = new BasicRequestInfo(
            "https://api.aodispor.pt/profiles/me", true
    );
    private static final BasicRequestInfo URL_UPLOAD_IMAGE = new BasicRequestInfo(
            "https://api.aodispor.pt/users/me/profile/avatar", true
    );
    private static final BasicRequestInfo URL_LOCATION = new BasicRequestInfo(
            "https://api.aodispor.pt/location/{cp4}/{cp3}", false
    );

    public static HttpRequestTask<AODISPOR_JSON_WEBAPI> buildCardStackRequest(final String searchQuery, final GeoLocation geoLocation) {
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request;
        if (searchQuery == null || searchQuery.equals("")) {
            request = HttpRequestTask.GET(SearchQueryResult.class,
                    queryProfilesURLempty.URL, geoLocation.getLatitude(), geoLocation.getLongitude());
        } else {
            request = HttpRequestTask.GET(SearchQueryResult.class,
                    queryProfilesURL.URL, searchQuery, geoLocation.getLatitude(), geoLocation.getLongitude());
        }
        queryProfilesURL.setHeaders(request);
        return request;
    }

    public static HttpRequestTask<String> buildSmsRequest(String phoneNumber) {
        HttpRequestTask<String> request = HttpRequestTask.POST(String.class, REGISTER_URL.URL);
        REGISTER_URL.setHeaders(request);
        request.setJSONBody(new Register(phoneNumber));
        return request;
    }

    public static HttpRequestTask<AODISPOR_JSON_WEBAPI> buildUpdateUserProfileInfosRequest(Professional professional) {
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = HttpRequestTask.POST(SearchQueryResult.class, URL_MY_PROFILE.URL);
        URL_MY_PROFILE.setHeaders(request);
        if (professional != null)
            request.setJSONBody(professional);
        return request;
    }

    public static HttpRequestTask<AODISPOR_JSON_WEBAPI> buildGetUserProfileRequest() {
        return buildUpdateUserProfileInfosRequest(null);
    }

    public static HttpRequestTask<AODISPOR_JSON_WEBAPI> buildUpdateUserProfilePhotoRequest(Bitmap image) {
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = HttpRequestTask.POST(SearchQueryResult.class, URL_UPLOAD_IMAGE.URL);
        URL_UPLOAD_IMAGE.setHeaders(request);
        request.setBitmapBody(Utility.convertBitmapToBinary(image));
        return request;
    }

    public static HttpRequestTask<AODISPOR_JSON_WEBAPI> buildLocationRequest(String cp_prefix, String cp_suffix) {
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = HttpRequestTask.GET(CPPQueryResult.class, URL_LOCATION.URL, cp_prefix, cp_suffix);
        URL_LOCATION.setHeaders(request);
        return request;
    }

    public static HttpRequestTask<AODISPOR_JSON_WEBAPI> buildValidationRequest(String phone_number, String validation_code) {
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = MYSELF_URL.buildSimpleRequest();
        String encoded_header_value = "Basic "
                + Base64.encodeToString((phone_number + ":" + validation_code).getBytes(), Base64.DEFAULT);
        request.addHeader("Authorization", encoded_header_value);
        return request;
    }

}
