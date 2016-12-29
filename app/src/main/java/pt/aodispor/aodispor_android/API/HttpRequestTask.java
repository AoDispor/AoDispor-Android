package pt.aodispor.aodispor_android.API;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import pt.aodispor.aodispor_android.AppDefinitions;

public class HttpRequestTask extends AsyncTask<Void, Void, ApiJSON> {
    public static final int GET_REQUEST = 0;
    public static final int POST_REQUEST = 1;
    public static final int PUT_REQUEST = 2;
    private static final String token = "4bsHGsYeva6eud8VsLiKEVVQYQEgmfCafwtuNrhuFYFcPjxWnT";

    /**
     * tells the API how to deserialize the response
     */
    private Class answerType;
    private String url;
    private String[] urlVariables;
    private boolean timeout = false;
    private HttpEntity<?> entityReq;
    private HttpMethod method;
    private ApiJSON body;
    private byte[] bitmapBody;
    private HttpHeaders httpHeaders;
    private RestTemplate template;

    /**
     * handler that executes after an answer is received.
     * <br>using the handler allows the application to run without waiting for a timeout or an answer.
     * <br>can be set to null
     */
    private HttpRequest postExecute;
    /**
     * passed to postExecute method to identify what to do after a specific request
     */
    private int type;

    public HttpRequestTask(Class a, HttpRequest p, String u) {
        answerType = a;
        postExecute = p;
        url = u;
        urlVariables = new String[]{};
        method = HttpMethod.GET;
        httpHeaders = new HttpHeaders();
    }

    public HttpRequestTask(Class a, HttpRequest p, String u, String... uv) {
        answerType = a;
        postExecute = p;
        url = u;
        urlVariables = uv;
        method = HttpMethod.GET;
        httpHeaders = new HttpHeaders();
    }

    @Override
    protected ApiJSON doInBackground(Void... params) {
        try {
            prepareHeaders();
            SimpleClientHttpRequestFactory cf = new SimpleClientHttpRequestFactory();
            cf.setConnectTimeout(AppDefinitions.TIMEOUT);
            cf.setReadTimeout(AppDefinitions.TIMEOUT);
            template = new RestTemplate(cf);
            Object answer;//temp before assigning response: may not return ApiJSON
            try {
                ApiJSON response = null;
                switch (method) {
                    case POST:
                        entityReq = new HttpEntity<>(body, httpHeaders);
                        answer = template.postForObject(url, entityReq, answerType);
                        if (ApiJSON.class.isAssignableFrom(answerType))
                            response = (ApiJSON) answer;
                        break;
                    case PUT:
                        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                        entityReq = new HttpEntity<>(bitmapBody, httpHeaders);
                        answer = template.exchange(url, HttpMethod.PUT, entityReq, String.class, urlVariables).getBody();
                        if (ApiJSON.class.isAssignableFrom(answerType))
                            response = (ApiJSON) answer;
                        break;
                    default:
                        entityReq = new HttpEntity<>(httpHeaders);
                        answer = template.exchange(url, HttpMethod.GET, entityReq, answerType, urlVariables).getBody();
                        if (ApiJSON.class.isAssignableFrom(answerType))
                            response = (ApiJSON) answer;
                        break;
                }
                return response;
            } catch (RestClientException re) {
                timeout = true;
                re.printStackTrace();
            }
        } catch (Exception e) {
            timeout = true;
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ApiJSON data) {
        if (postExecute == null)
            return;
        if (timeout) {
            postExecute.onHttpRequestFailed();
        } else {
            if (data == null)
                return;
            postExecute.onHttpRequestCompleted(data, type);
        }
    }

    private String getLocalDate() {
        Date cDate = new Date();
        return new SimpleDateFormat("yyyyMMdd").format(cDate);
    }

    private void prepareHeaders() {
        final String date = getLocalDate();
        MediaType[] mediaTypes = {MediaType.APPLICATION_JSON};
        httpHeaders.setAccept(Arrays.asList(mediaTypes));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("API-Authorization", token + date);
    }

    /**
     * setting type allows HttpRequestHandler to implement different executions for different requests
     */
    public void setType(int t) {
        type = t;
    }

    public void setMethod(int m) {
        switch (m) {
            case POST_REQUEST:
                method = HttpMethod.POST;
                break;
            case GET_REQUEST:
                method = HttpMethod.GET;
                break;
            case PUT_REQUEST:
                method = HttpMethod.PUT;
        }
    }

    public int getType() {
        return type;
    }

    public void setJSONBody(ApiJSON b) {
        body = b;
    }

    public void setBitmapBody(byte[] b) {
        bitmapBody = b;
    }

    public void addAPIAuthentication(String phone, String password) {
        String encode = Base64.encodeToString((phone + ":" + password).getBytes(), Base64.DEFAULT);
        httpHeaders.set("Authorization", "Basic " + encode);
    }

}