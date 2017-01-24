package pt.aodispor.android.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by JOSE PEREIRA on 13-11-2016.
 */
public class LocationCPP {
    @JsonProperty("cp4") private String cp4;
    @JsonProperty("cp3") private String cp3;
    @JsonProperty("localidade") private String localidade;
    @JsonProperty("latitude") private String latitude;
    @JsonProperty("longitude") private String longitude;

    public String getLocalidade() {
        return localidade;
    }

    public String getCp4() {
        return cp4;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCp3() {
        return cp3;
    }
}
