package pt.aodispor.android.API;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CPPQueryResult extends ApiJSON {
    @JsonProperty("data") public LocationCPP data;
}
