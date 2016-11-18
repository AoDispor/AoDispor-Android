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
    private static final String token = "4bsHGsYeva6eud8VsLiKEVVQYQEgmfCafwtuNrhuFYFcPjxWnT";

    private Class answerType;
    private String url;
    private String[] urlVariables;
    private HttpRequest postExecute;
    private boolean timeout = false;
    private HttpEntity<?> entityReq;
    private HttpMethod method;
    private ApiJSON body;
    private HttpHeaders httpHeaders;
    private RestTemplate template;
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
            try {
                ApiJSON response;
                switch (method) {
                    case POST:
                        entityReq = new HttpEntity<>(body,httpHeaders);
                        String s = template.postForObject(url, entityReq, String.class);
                        ObjectMapper om = new ObjectMapper();
                        JsonNode root = om.readTree(s);
                        response = (ApiJSON) om.readValue(root.get("data") + "", answerType);
                        break;
                    default:
                        entityReq = new HttpEntity<>(httpHeaders);
                        response = (ApiJSON) template.exchange(url, HttpMethod.GET, entityReq, answerType, urlVariables).getBody();
                        break;
                }
                return response;
            }catch (RestClientException re) {
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

    private void prepareHeaders(){
        final String date = getLocalDate();
        MediaType[] mediaTypes = { MediaType.APPLICATION_JSON };
        httpHeaders.setAccept(Arrays.asList(mediaTypes));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("API-Authorization", token + date);
    }

    public void setType(int t){
        type = t;
    }

    public void setMethod(int m) {
        switch (m){
            case POST_REQUEST:
                method = HttpMethod.POST;
                break;
            case GET_REQUEST:
                method = HttpMethod.GET;
                break;
        }
    }

    public int getType(){
        return type;
    }

    public void setJSONBody(ApiJSON b){
        body = b;
    }

    public void addAPIAuthentication(String phone, String password){
        String encode = Base64.encodeToString((phone + ":" + password).getBytes(), Base64.DEFAULT);
        httpHeaders.set("Authorization", "Basic " + encode);
    }

}