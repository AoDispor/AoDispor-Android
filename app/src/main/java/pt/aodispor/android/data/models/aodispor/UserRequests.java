package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

import pt.aodispor.android.data.models.shared.parsing.OptionalArrayDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequests  extends AODISPOR_JSON_WEBAPI{

        @JsonDeserialize(using = RequestsListDeserializer.class)
        @JsonProperty("data")
        public List<UserRequestTempData> data;

    public static class RequestsListDeserializer extends OptionalArrayDeserializer<UserRequestTempData> {
        protected RequestsListDeserializer() {
            super(UserRequestTempData.class);
        }
    }
}
