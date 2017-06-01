package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//TODO ver se algum deles já funciona como pretendido por default
import java.util.Date;

import pt.aodispor.android.AppDefinitions;
//import java.sql.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest extends BasicCardFields {
    //@JsonProperty("data_expiracao") public Date data_expiracao;//TODO pode ser necessario fazer 1 deserializer custom
    //@JsonDeserialize(using = MyDateDeserializer.class)
    @JsonProperty("data_expiracao") public String data_expiracao;

    public Date getExpirationDate(){
        Date ret = null;
        try {
            ret = AppDefinitions.TIMAEDATE_FORMATER.parse(data_expiracao);
        } catch (Exception e){}
        return ret;
    }

    /*
    //DID NOT TEST, MAY WORK OR NOT
    public class MyDateDeserializer extends JsonDeserializer<Date> {

        private static final long serialVersionUID = 1L;


        /* (non-Javadoc)
         * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
         /
        @Override
        public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

            Class<? extends BasicCardFields> clazz = Professional.class;;
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            ObjectNode obj = (ObjectNode) mapper.readTree(jp);

            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat(AppDefinitions.TIMEDATE_FORMAT);
            Date ret=null;
            try{
                ret=simpleDateFormat.parse(obj.toString());
            } catch (Exception e){}

            return ret;
        }
    }*/
}
