package pt.aodispor.aodispor_android.API;

import android.os.AsyncTask;
import android.util.Log;

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

public class HttpRequestTask extends AsyncTask<Void, Void, ApiJSON> {

    private Class answerType;
    private String url;
    private String[] urlVariables;
    boolean useDefaultHeader = true;
    private OnHttpRequestCompleted postExecute;
    private boolean timeout = false;

    private String getLocalDate()
    {
        Date cDate = new Date();
        return new SimpleDateFormat("yyyyMMdd").format(cDate);
    }

    public HttpRequestTask(Class answerType, OnHttpRequestCompleted postExecute, String url) {
        this.answerType = answerType;
        this.postExecute = postExecute;
        this.url = url;
        this.urlVariables = new String[]{};
    }
    public HttpRequestTask(Class answerType, OnHttpRequestCompleted postExecute, String url, String... urlVariables) {
        this.answerType = answerType;
        this.postExecute = postExecute;
        this.url = url;
        this.urlVariables = urlVariables;
    }

    //these might need to add some form of validation later (could be done outside class)
    public void setURL(String url)
    {
        this.url = url;
    }

    public void setUrlVariables(String... variables)
    {
        this.urlVariables = variables;
    }

    @Override
    protected ApiJSON doInBackground(Void... params) {
        try {
            HttpEntity<?> entityReq;
            if(useDefaultHeader) {
                final String token = "4bsHGsYeva6eud8VsLiKEVVQYQEgmfCafwtuNrhuFYFcPjxWnT";
                final String date = getLocalDate();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("API-Authorization", token + date);
                entityReq = new HttpEntity<>(headers);
            }else {
                entityReq = new HttpEntity<>(null);
            }
            SimpleClientHttpRequestFactory cf = new SimpleClientHttpRequestFactory();
            cf.setConnectTimeout(3000);
            cf.setReadTimeout(3000);
            RestTemplate template = new RestTemplate(cf);
            try {
                ApiJSON r = (ApiJSON) template.exchange(url, HttpMethod.GET, entityReq, answerType, urlVariables).getBody();
                return r;
            }catch (RestClientException re) {
                timeout = true;
                re.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ApiJSON data) {
        if (postExecute == null)
            return;
        if(timeout){
            postExecute.onHttpRequestFailed();
        }else {
            if (data == null)
                return;
            postExecute.onHttpRequestCompleted(data);
        }
    }
}