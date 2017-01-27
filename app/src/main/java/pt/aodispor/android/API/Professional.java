package pt.aodispor.android.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Professional extends ApiJSON {
    @JsonProperty("full_name") public String full_name;
    @JsonProperty("title") public String title;
    @JsonProperty("description") public String description;
    @JsonProperty("rate") public String rate;
    @JsonProperty("currency") public String currency;
    @JsonProperty("type") public String type;
    @JsonProperty("string_id") public String string_id;
    @JsonProperty("location") public String location;
    @JsonProperty("avatar_url") public String avatar_url;
    @JsonProperty("phone") public String phone;
    @JsonProperty("cp4") public String cp4;
    @JsonProperty("cp3") public String cp3;
}
