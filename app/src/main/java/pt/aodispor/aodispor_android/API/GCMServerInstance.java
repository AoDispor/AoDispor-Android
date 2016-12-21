package pt.aodispor.aodispor_android.API;

import com.fasterxml.jackson.annotation.JsonProperty;



public class GCMServerInstance  extends ApiJSON{
    @JsonProperty("gcm_token") public String gcm_token;
    @JsonProperty("postal_code") public int postal_code;
}
