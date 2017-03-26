package pt.aodispor.android.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchQueryResult extends ApiJSON {
    /**
     * list of professionals
     * */
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
