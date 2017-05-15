package pt.aodispor.android.api;

import android.os.AsyncTask;
import android.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import pt.aodispor.android.AppDefinitions;

//TODO 2b generic later
public class HttpRequestTask extends AsyncTask<Void, Void, ApiJSON> {
    public static final int GET_REQUEST = 0;
    public static final int POST_REQUEST = 1;
    public static final int PUT_REQUEST = 2;
    private static String token = null;

    /*public interface HttpRequest2<X> extends HttpRequest<X>{
        void onHttpRequestComplete();
    }*/

    /**
     * indicates that the task is using a new implementation that
     * works as replacement for the new implementation to come later on
     */
    private boolean usingTransitionVersion = false;


    public interface IOnHttpRequestCompleted {
        void exec(ApiJSON answer);//TODO 2b generic later
    }

    //private Delegator el;
    private ArrayList<IOnHttpRequestCompleted> onEnd;
    private ArrayList<IOnHttpRequestCompleted> onSuccess;
    private ArrayList<IOnHttpRequestCompleted> onFail;

    private void addHandlers(ArrayList<IOnHttpRequestCompleted> list, IOnHttpRequestCompleted[] array) {
        usingTransitionVersion = true;
        list.addAll(Arrays.asList(array));
        //for (IOnHttpRequestCompleted handler : array) list.add(handler);
    }

    public void addOnEndHandlers(IOnHttpRequestCompleted... handlers) {
        if (onEnd == null) {
            onEnd = new ArrayList<IOnHttpRequestCompleted>();
        }
        addHandlers(onEnd, handlers);
    }

    public void addOnSuccessHandlers(IOnHttpRequestCompleted... handlers) {
        if (onSuccess == null) {
            onSuccess = new ArrayList<IOnHttpRequestCompleted>();
        }
        addHandlers(onSuccess, handlers);
    }

    public void addOnFailHandlers(IOnHttpRequestCompleted... handlers) {
        if (onFail == null) {
            onFail = new ArrayList<IOnHttpRequestCompleted>();
        }
        addHandlers(onFail, handlers);
    }


    public static void setToken(String token) {
        if (HttpRequestTask.token == null) HttpRequestTask.token = token;
    }

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

    public boolean gotError() {
        return error;
    }

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
     * passed to postExecute method to identify nuances or process certain data
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

    private HttpRequestTask(){}
    static public HttpRequestTask POST(Class answer, HttpRequest executor, String url, String... uv){
        HttpRequestTask request = new HttpRequestTask();
        request.method = HttpMethod.POST;
        request.answerType = answer;
        request.postExecute = executor;
        request.url = url;
        request.urlVariables = uv;
        request.httpHeaders = new HttpHeaders();
        return request;
    }

    static public HttpRequestTask PUT(Class answer, HttpRequest executor, String url, String... uv){
        HttpRequestTask request = new HttpRequestTask();
        request.method = HttpMethod.PUT;
        request.answerType = answer;
        request.postExecute = executor;
        request.url = url;
        request.urlVariables = uv;
        request.httpHeaders = new HttpHeaders();
        return request;
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
            if (answerType == null)
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
        if (usingTransitionVersion) {
            if (error) {
                if (onFail != null)
                    for (IOnHttpRequestCompleted handler : onFail) handler.exec(data);
            } else {
                if (onSuccess != null)
                    for (IOnHttpRequestCompleted handler : onSuccess) handler.exec(data);
            }
            if (onEnd != null)
                for (IOnHttpRequestCompleted handler : onEnd) handler.exec(data);
            return;
        }
        //IF IS USING OLDER VERSION see code below

        if (postExecute == null)
            return;
        if (/*error || */error) {
            //if (data == null)
            //    return;
            postExecute.onHttpRequestFailed(data, type);
        } else {
            if (data == null)
                return;
            postExecute.onHttpRequestSuccessful(data, type);
        }
        /*if(postExecute.getClass()==HttpRequest2.class){
            ((HttpRequest2)postExecute).onHttpRequestComplete();
        }*/
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
        httpHeaders.set("API-Authorization", token + date);
    }

    /**
     * TODO refactor to extra or some other name
     */
    public void setType(int t) {
        type = t;
    }

    /*public void setMethod(int m) {
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
    }*/

    /**
     * TODO refactor to extra or some other name
     */
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