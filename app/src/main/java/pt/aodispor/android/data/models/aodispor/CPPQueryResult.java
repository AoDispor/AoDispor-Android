package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CPPQueryResult extends ApiJSON {
    @JsonProperty("data") public LocationCPP data;
}
