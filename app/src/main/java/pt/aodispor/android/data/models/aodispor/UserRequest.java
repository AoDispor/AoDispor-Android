package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//TODO ver se algum deles já funciona como pretendido por default
import java.util.Date;

import pt.aodispor.android.AppDefinitions;
//import java.sql.Date;

/**
 * data used in the card stack cards
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest extends BasicCardFields implements Cloneable {
    //@JsonProperty("data_expiracao") public Date data_expiracao;//TODO pode ser necessario fazer 1 deserializer custom
    //@JsonDeserialize(using = MyDateDeserializer.class)
    @JsonProperty("data_expiracao")
    public String data_expiracao;

    public Date getExpirationDate() {
        Date ret = null;
        try {
            ret = AppDefinitions.TIMAEDATE_FORMATER.parse(data_expiracao);
        } catch (Exception ignored) {
        }
        return ret;
    }


    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Object clone() throws CloneNotSupportedException {
        UserRequest ur = new UserRequest();
        ur.data_expiracao = data_expiracao;
        super.copyFields(ur);
        return ur;
    }

}
