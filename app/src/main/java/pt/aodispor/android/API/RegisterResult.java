package pt.aodispor.android.API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterResult extends ApiJSON {
    @JsonProperty("data") public UserBasicData data;
}
