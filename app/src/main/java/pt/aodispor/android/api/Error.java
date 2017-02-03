package pt.aodispor.android.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Created by lamelas on 01/02/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Error extends ApiJSON {
    @JsonProperty("message")
    public String message;
    @JsonProperty("status_code")
    public Integer status_code;
}
