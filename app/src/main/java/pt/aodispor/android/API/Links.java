package pt.aodispor.android.API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Links {
    @JsonProperty("first") String first;
    @JsonProperty("last") String last;
    @JsonProperty("previous") String prev;
    @JsonProperty("next") String next;

    public String getFirst(){return first;}
    public String getLast(){return last;}
    public String getPrevious(){return prev;}
    public String getNext(){return next;}
}
