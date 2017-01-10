package pt.aodispor.android.API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserBasicData{
    @JsonProperty("uuid") private String uuid;
    @JsonProperty("postal_code") private String postal_code;
    @JsonProperty("telephone") private String telephone;
}
