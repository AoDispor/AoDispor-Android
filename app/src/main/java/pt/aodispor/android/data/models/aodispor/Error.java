package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lamelas on 01/02/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Error extends AODISPOR_JSON_WEBAPI {
    @JsonProperty("message")
    public String message;
    @JsonProperty("status_code")
    public Integer status_code;
}
