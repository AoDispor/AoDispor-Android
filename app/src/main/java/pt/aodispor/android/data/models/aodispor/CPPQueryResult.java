package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CPPQueryResult extends AODISPOR_JSON_WEBAPI {
    @JsonProperty("data") public LocationCPP data;
}
