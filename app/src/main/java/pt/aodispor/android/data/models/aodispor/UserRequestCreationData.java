package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRequestCreationData  extends AODISPOR_JSON_WEBAPI{

    @JsonProperty("titulo") public String titulo;
    @JsonProperty("descricao") public String descricao;
    @JsonProperty("codigo_postal") public String codigo_postal;

    public UserRequestCreationData(String titulo, String descricao, String codigo_postal) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.codigo_postal = codigo_postal;
    }
}
