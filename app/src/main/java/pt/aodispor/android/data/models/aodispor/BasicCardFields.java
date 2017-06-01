package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonProperty;

//hmmm... ainda não sei se a versão do fasterxml que estamos a usar juntamente com o spring suporta  @JsonSubTypes para poder receber objectos de tipos diferentes.

/** has common variables to Professional and UserRequest Cards */
/*@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "request", value = UserRequest.class),
        @JsonSubTypes.Type(name = "professional", value = Professional.class)
})*/
//@JsonDeserialize(using = CardDeserializer.class)
public abstract class BasicCardFields extends ApiJSON {
    @JsonProperty("full_name") public String full_name;
    @JsonProperty("title") public String title;
    @JsonProperty("description") public String description;
    @JsonProperty("phone") public String phone;
    @JsonProperty("location") public String location;
    @JsonProperty("string_id") public String string_id;

    @JsonProperty("cp4") public String cp4;
    @JsonProperty("cp3") public String cp3;

    @JsonProperty("rate") public String rate;
    @JsonProperty("currency") public String currency;

    @JsonProperty("distance") public String distance;
}