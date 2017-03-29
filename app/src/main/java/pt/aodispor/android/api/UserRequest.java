package pt.aodispor.android.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//TODO ver se algum deles já funciona como pretendido por default
import java.util.Date;
//import java.sql.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest extends BasicCardFields {
    //@JsonProperty("data_expiracao") public Date data_expiracao;//TODO pode ser necessario fazer 1 deserializer custom
    @JsonProperty("data_expiracao") public String data_expiracao;
}
