package pt.aodispor.aodispor_android.API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterResult extends ApiJSON {
    @JsonProperty("data") public UserBasicData data;
}
