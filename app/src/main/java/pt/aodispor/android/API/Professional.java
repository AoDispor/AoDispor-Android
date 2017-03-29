package pt.aodispor.android.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Professional extends BasicCardFields {





    @JsonProperty("rate") public String rate;
    @JsonProperty("currency") public String currency;
    @JsonProperty("type") public String type;

    @JsonProperty("avatar_url") public String avatar_url;

}
