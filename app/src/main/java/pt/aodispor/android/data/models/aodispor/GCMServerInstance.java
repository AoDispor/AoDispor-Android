package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class GCMServerInstance extends AODISPOR_JSON_WEBAPI {
    @JsonProperty("gcm_token") public String gcm_token;
    @JsonProperty("postal_code") public int postal_code;
}
