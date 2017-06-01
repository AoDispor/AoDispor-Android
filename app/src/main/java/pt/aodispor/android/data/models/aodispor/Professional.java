package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Professional extends BasicCardFields implements Cloneable {

    @JsonProperty("type") public String type;

    @JsonProperty("avatar_url") public String avatar_url;


    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Object clone() throws CloneNotSupportedException {
        Professional professional = new Professional();
        professional.type = this.type;
        professional.avatar_url = this.avatar_url;
        professional.cp3 = this.cp3;
        professional.full_name = this.full_name;
        professional.location = this.location;
        professional.cp4 = this.cp4;
        professional.currency = this.currency;
        professional.description = this.description;
        professional.phone = this.phone;
        professional.rate = this.rate;
        professional.string_id = this.string_id;
        professional.distance = this.distance;
        professional.title = this.title;
        return professional;
    }
}
