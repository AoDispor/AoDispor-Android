package pt.aodispor.aodispor_android.API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchQueryResult extends ApiJSON {
    @JsonDeserialize(using = ProfessionalListDeserializer.class)
    @JsonProperty("data")
    public List<Professional> data;
    @JsonProperty("meta") public Meta meta;

    public static class ProfessionalListDeserializer extends OptionalArrayDeserializer<Professional> {
        protected ProfessionalListDeserializer() {
            super(Professional.class);
        }
    }
}
