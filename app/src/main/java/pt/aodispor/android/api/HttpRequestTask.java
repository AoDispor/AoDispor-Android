package pt.aodispor.android.api;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;

import pt.aodispor.android.data.models.aodispor.AODISPOR_JSON_WEBAPI;

//TODO not suited for all purposes but more flexible than before
//using a generic type instead of Object type might not have been the best decision
//hopefully will bring more awareness to the developer of the types expected but will require a bit of extra work
public class HttpRequestTask<Z> extends AsyncTask<Void, Void, Z> {

    public interface IOnHttpRequestCompleted<A> {
        void exec(A answer);
    }

    private ArrayList<IOnHttpRequestCompleted<Z>> onEnd;
    private ArrayList<IOnHttpRequestCompleted<Z>> onSuccess;
    private ArrayList<IOnHttpRequestCompleted<Z>> onFail;

    private void addHandlers(ArrayList<IOnHttpRequestCompleted<Z>> list, IOnHttpRequestCompleted<Z>[] array) {
        list.addAll(Arrays.asList(array));
        //for (IOnHttpRequestCompleted handler : array) list.add(handler);
    }

    @SafeVarargs
    public final void addOnEndHandlers(IOnHttpRequestCompleted<Z>... handlers) {
        if (onEnd == null) {
            onEnd = new ArrayList<>();
        }
        addHandlers(onEnd, handlers);
    }


    @SafeVarargs
    public final void addOnSuccessHandlers(IOnHttpRequestCompleted<Z>... handlers) {
        if (onSuccess == null) {
            onSuccess = new ArrayList<>();
        }
        addHandlers(onSuccess, handlers);
    }

    @SafeVarargs
    public final void addOnFailHandlers(IOnHttpRequestCompleted<Z>... handlers) {
        if (onFail == null) {
            onFail = new ArrayList<>();
        }
        addHandlers(onFail, handlers);
    }

    /**
     * tells the api how to deserialize the response
     */
    private Class answerType;
    private String url;
    private String[] urlVariables;
    //error can't be detected with handler (would need to use the systems time to find out error occurrences)
    private boolean error = false;

    public boolean gotError() {
        return error;
    }

    private HttpMethod method;
    //private AODISPOR_JSON_WEBAPI body;
    private Object body;
    private byte[] bitmapBody;
    private HttpHeaders httpHeaders;

    //in milliseconds
    static final int DEFAULT_READ_TIMEOUT = 20000;
    static final int DEFAULT_CONNECTION_TIMEOUT = 20000;
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    public void setTimeouts(int read, int connection) {
        readTimeout = read;
        connectionTimeout = connection;
    }

    private Class<Z> error_class;

    public void setErrorClass(Class<Z> error) {
        error_class = error;
    }


    private HttpRequestTask() {
    }

    /**
     * Creates a request task. The request must then be started to with execute.
     * <br>A GET request is used by default, use setMethod to change this.
     * <br>The Body is empty by default. Use setJSONBody to define the body.
     * <br>Verify if addAPIAuthentication is needed
     * <br>Use setType if 'executor' (httpRequest) can receive different answers and/or implements different behaviours
     *
     * @param answer answer type expected, must e deserializable
     * @param url    request destination
     */
    static public <A> HttpRequestTask<A> GET(Class answer, String url) {
        HttpRequestTask<A> request = new HttpRequestTask<>();
        request.answerType = answer;
        request.url = url;
        request.urlVariables = new String[]{};
        request.method = HttpMethod.GET;
        request.httpHeaders = new HttpHeaders();
        return request;
    }

    /**
     * Creates a request task. The request must then be started to with execute.
     * <br>A GET request is used by default, use setMethod to change this.
     * <br>The Body is empty by default. Use setJSONBody to define the body.
     * <br>Verify if addAPIAuthentication is needed
     * <br>Use setType if 'executor' (httpRequest) can receive different answers and/or implements different behaviours
     *
     * @param answer answer type expected, must e deserializable
     * @param url    request destination
     * @param uv     url variables
     */
    static public <A> HttpRequestTask<A> GET(Class answer, String url, String... uv) {
        HttpRequestTask<A> request = HttpRequestTask.GET(answer, url);
        request.urlVariables = uv;
        return request;
    }

    static public <A> HttpRequestTask<A> DELETE(Class answer, String url, String... uv) {
        HttpRequestTask<A> request = new HttpRequestTask<A>();
        request.method = HttpMethod.DELETE;
        request.urlVariables = uv;
        return request;
    }

    static public <A> HttpRequestTask<A> POST(Class answer, String url, String... uv) {
        HttpRequestTask<A> request = new HttpRequestTask<A>();
        request.method = HttpMethod.POST;
        request.answerType = answer;
        request.url = url;
        request.urlVariables = uv;
        request.httpHeaders = new HttpHeaders();
        return request;
    }

    static public <A> HttpRequestTask<A> PUT(Class answer, String url, String... uv) {
        HttpRequestTask<A> request = new HttpRequestTask<A>();
        request.method = HttpMethod.PUT;
        request.answerType = answer;
        request.url = url;
        request.urlVariables = uv;
        request.httpHeaders = new HttpHeaders();
        return request;
    }

    @Override
    protected Z doInBackground(Void... params) {

        Object answer;//temp before assigning response: may not return ApiJSON
        try {
            SimpleClientHttpRequestFactory cf = new SimpleClientHttpRequestFactory();
            cf.setConnectTimeout(connectionTimeout);
            cf.setReadTimeout(readTimeout);
            RestTemplate template = new RestTemplate(cf);
            if (answerType == null)
                answerType = String.class;
            try {
                //ApiJSON response = null;
                switch (method) {
                    case POST:
                        HttpEntity<?> entityReq = new HttpEntity<>(body, httpHeaders);
                        answer = template.postForObject(url, entityReq, answerType);
                        //if (ApiJSON.class.isAssignableFrom(answerType))
                        //    response = (ApiJSON) answer;
                        break;
                    case PUT:
                        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                        entityReq = new HttpEntity<>(bitmapBody, httpHeaders);
                        answer = template.exchange(url, HttpMethod.PUT, entityReq, answerType, urlVariables).getBody();
                        //if (ApiJSON.class.isAssignableFrom(answerType))
                        //    response = (ApiJSON) answer;
                        break;
                    case DELETE:
                        entityReq = new HttpEntity<>(httpHeaders);
                        answer = template.exchange(url, HttpMethod.DELETE, entityReq, answerType, urlVariables).getBody();
                        break;
                    default:
                        entityReq = new HttpEntity<>(httpHeaders);

                        //Log.d("ZZ",template.exchange(url, HttpMethod.GET, entityReq, String.class, urlVariables).getBody().toString()+"_");

                        answer = template.exchange(url, HttpMethod.GET, entityReq, answerType, urlVariables).getBody();
                        //response = (ApiJSON) template.exchange(url, HttpMethod.GET, entityReq, answerType, urlVariables).getBody();
                        //if (answerType != null && ApiJSON.class.isAssignableFrom(answerType))
                        //response = (ApiJSON) answer;
                        break;
                }

                return (Z) answer;
            } catch (HttpStatusCodeException e) {
                error = true;
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(e.getResponseBodyAsString(), error_class);
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
    protected void onPostExecute(Z data) {
        if (error) {
            if (onFail != null)
                for (IOnHttpRequestCompleted<Z> handler : onFail) handler.exec(data);
        } else {
            if (onSuccess != null)
                for (IOnHttpRequestCompleted<Z> handler : onSuccess) handler.exec(data);
        }
        if (onEnd != null)
            for (IOnHttpRequestCompleted<Z> handler : onEnd) handler.exec(data);
    }

    public void setJSONBody(Object b) {
        body = b;
    }

    public void setBitmapBody(byte[] b) {
        bitmapBody = b;
    }

    public void setHeader(HttpHeaders headers) {
        this.httpHeaders = headers;
    }

    public void addHeader(String name, String value) {
        this.httpHeaders.add(name, value);
    }

}