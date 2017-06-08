package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterResult extends AODISPOR_JSON_WEBAPI {
    @JsonProperty("data") public UserBasicData data;
}
