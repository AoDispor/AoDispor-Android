package pt.aodispor.android.api;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import pt.aodispor.android.AppDefinitions;

public class HttpRequestTask extends AsyncTask<Void, Void, ApiJSON> {
    public static final int GET_REQUEST = 0;
    public static final int POST_REQUEST = 1;
    public static final int PUT_REQUEST = 2;
    private static final String token = "4bsHGsYeva6eud8VsLiKEVVQYQEgmfCafwtuNrhuFYFcPjxWnT";
    //TODO next line might not be needed anymore, just define Locale in definitions... maybe?
    private static final String serverTimeZone = "UTC";

    /**
     * tells the api how to deserialize the response
     */
    private Class answerType;
    private String url;
    private String[] urlVariables;
    //error can't be detected with handler (would need to use the systems time to find out error occurrences)
    //private boolean error = false;
    private boolean error = false;
    public boolean gotError(){return error;}
    private HttpEntity<?> entityReq;
    private HttpMethod method;
    private ApiJSON body;
    private byte[] bitmapBody;
    private HttpHeaders httpHeaders;
    private RestTemplate template;

    /**
     * handler that executes after an answer is received.
     * <br>using the handler allows the application to run without waiting for a error or an answer.
     * <br>can be set to null
     */
    private HttpRequest postExecute;
    /**
     * passed to postExecute method to identify what to do after a specific request
     */
    private int type;

    /**
     * Creates a request task. The request must then be started to with execute.
     * <br>A GET request is used by default, use setMethod to change this.
     * <br>The Body is empty by default. Use setJSONBody to define the body.
     * <br>Verify if addAPIAuthentication is needed
     * <br>Use setType if 'executor' (httpRequest) can receive different answers and/or implements different behaviours
     *
     * @param answer   answer type expected, must e deserializable
     * @param executor the instance that implements the HttpRequest. Only needed to run request on background (can be null).
     * @param url      request destination
     */
    public HttpRequestTask(Class answer, HttpRequest executor, String url) {
        answerType = answer;
        postExecute = executor;
        this.url = url;
        urlVariables = new String[]{};
        method = HttpMethod.GET;
        httpHeaders = new HttpHeaders();
    }

    /**
     * Creates a request task. The request must then be started to with execute.
     * <br>A GET request is used by default, use setMethod to change this.
     * <br>The Body is empty by default. Use setJSONBody to define the body.
     * <br>Verify if addAPIAuthentication is needed
     * <br>Use setType if 'executor' (httpRequest) can receive different answers and/or implements different behaviours
     *
     * @param answer   answer type expected, must e deserializable
     * @param executor the instance that implements the HttpRequest. Only needed to run request on background (can be null).
     * @param url      request destination
     * @param uv       url variables
     */
    public HttpRequestTask(Class answer, HttpRequest executor, String url, String... uv) {
        answerType = answer;
        postExecute = executor;
        this.url = url;
        urlVariables = uv;
        method = HttpMethod.GET;
        httpHeaders = new HttpHeaders();
    }

    @Override
    protected ApiJSON doInBackground(Void... params) {
        Object answer;//temp before assigning response: may not return ApiJSON
        try {
            prepareHeaders();
            SimpleClientHttpRequestFactory cf = new SimpleClientHttpRequestFactory();
            cf.setConnectTimeout(AppDefinitions.TIMEOUT);
            cf.setReadTimeout(AppDefinitions.TIMEOUT);
            template = new RestTemplate(cf);
            if(answerType == null)
                answerType = String.class;
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
                        answer = template.exchange(url, HttpMethod.PUT, entityReq, answerType, urlVariables).getBody();
                        if (ApiJSON.class.isAssignableFrom(answerType))
                            response = (ApiJSON) answer;
                        break;
                    default:
                        entityReq = new HttpEntity<>(httpHeaders);
                        response = (ApiJSON) template.exchange(url, HttpMethod.GET, entityReq, answerType, urlVariables).getBody();
                        //if (answerType != null && ApiJSON.class.isAssignableFrom(answerType))
                            //response = (ApiJSON) answer;
                        break;
                }
                return response;
            } catch (HttpStatusCodeException e) {
                error = true;

                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(e.getResponseBodyAsString(), Error.class);
            } catch (RestClientException re) {
                error = true;
                re.printStackTrace();
            }
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ApiJSON data) {
        if (postExecute == null)
            return;
        if (/*error || */error) {
            if (data == null)
                return;
            postExecute.onHttpRequestFailed(data);
        } else {
            if (data == null)
                return;
            postExecute.onHttpRequestCompleted(data, type);
        }
    }

    private String getLocalDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.UK);
        //TODO next line might not be needed anymore, just define Locale in definitions... maybe?
        dateFormat.setTimeZone(TimeZone.getTimeZone(serverTimeZone));
        return dateFormat.format(new Date());
    }

    private void prepareHeaders() {
        final String date = getLocalDate();
        MediaType[] mediaTypes = {MediaType.APPLICATION_JSON};
        httpHeaders.setAccept(Arrays.asList(mediaTypes));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("api-Authorization", token + date);
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

    /**
     * Use this method before executing request if it is a Registered used relate operation that needs credentials
     */
    public void addAPIAuthentication(String phone, String password) {
        String encode = Base64.encodeToString((phone + ":" + password).getBytes(), Base64.DEFAULT);
        httpHeaders.set("Authorization", "Basic " + encode);
    }

}