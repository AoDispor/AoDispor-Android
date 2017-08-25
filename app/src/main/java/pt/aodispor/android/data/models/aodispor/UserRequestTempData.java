package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import pt.aodispor.android.AppDefinitions;

/**
 * data received by the the owner of the request
 */
public class UserRequestTempData extends AODISPOR_JSON_WEBAPI implements Cloneable {
    @JsonProperty("uuid")
    public String uuid;

    @JsonProperty("titulo")
    public String titulo;
    @JsonProperty("descricao")
    public String descricao;

    @JsonProperty("codigo_postal")
    public String codigo_postal;
    @JsonProperty("codigo_postal_localizacao")
    public String codigo_postal_localizacao;
    @JsonProperty("data_criacao")
    public String data_criacao;
    @JsonProperty("data_expiracao")
    public String data_expiracao;


    @JsonCreator
    private UserRequestTempData(@JsonProperty("uuid") String uuid,
                                @JsonProperty("titulo") String titulo,
                                @JsonProperty("descricao") String descricao,
                                @JsonProperty("codigo_postal") String codigo_postal,
                                @JsonProperty("codigo_postal_localizacao") String codigo_postal_localizacao,
                                @JsonProperty("data_criacao") String data_criacao,
                                @JsonProperty("data_expiracao") String data_expiracao) {
        this.uuid = uuid;
        this.titulo = titulo;
        this.descricao = descricao;
        this.codigo_postal = codigo_postal;
        this.codigo_postal_localizacao = codigo_postal_localizacao;
        this.data_criacao = data_criacao;
        this.data_expiracao = data_expiracao;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new UserRequestTempData(
                this.uuid
                , this.titulo
                , this.descricao
                , this.codigo_postal
                , this.codigo_postal_localizacao
                , this.data_criacao
                , this.data_expiracao
        );
    }

    public Date getExpirationDate() {
        Date ret = null;
        try {
            ret = AppDefinitions.TIMAEDATE_FORMATER.parse(data_expiracao);
        } catch (Exception ignored) {
        }
        return ret;
    }
}
