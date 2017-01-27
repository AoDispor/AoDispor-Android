package pt.aodispor.android.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CPPQueryResult extends ApiJSON {
    @JsonProperty("data") public LocationCPP data;
}
