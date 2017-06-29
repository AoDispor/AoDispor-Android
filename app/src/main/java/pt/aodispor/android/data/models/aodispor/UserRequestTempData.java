package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRequestTempData extends AODISPOR_JSON_WEBAPI{
    @JsonProperty("uuid") public String uuid;

    @JsonProperty("titulo") public String titulo;
    @JsonProperty("descricao") public String descricao;

    @JsonProperty("codigo_postal") public String codigo_postal;
    @JsonProperty("codigo_postal_localizacao") public String codigo_postal_localizacao;
    @JsonProperty("data_criacao") public String data_criacao;
    @JsonProperty("data_expiracao") public String data_expiracao;
}
