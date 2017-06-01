package pt.aodispor.android.data.models.aodispor.parsing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import pt.aodispor.android.data.models.aodispor.BasicCardFields;
import pt.aodispor.android.data.models.aodispor.Professional;
import pt.aodispor.android.data.models.aodispor.UserRequest;

public class CardDeserializer extends JsonDeserializer<BasicCardFields> {

    private static final long serialVersionUID = 1L;

    public CardDeserializer(Class<BasicCardFields> clazz) {
        super();
    }

    @Override
    public BasicCardFields deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        //registry.put("data_expiracao",UserRequest.class);
        //registry.put("data_expiracao",UserRequest.class);

        Class<? extends BasicCardFields> clazz = Professional.class;
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode obj = mapper.readTree(jp);
        Iterator<Map.Entry<String, JsonNode>> elementsIterator = obj.fields();

        while (elementsIterator.hasNext())
            if (elementsIterator.next().getKey().equals("data_expiracao"))
                clazz = UserRequest.class;

        return mapper.treeToValue(obj, clazz);
    }

    public static Class<? extends BasicCardFields> findClass(
            JsonNode obj
    ) throws IOException {
        Class<? extends BasicCardFields> clazz = Professional.class;
        Iterator<Map.Entry<String, JsonNode>> elementsIterator = obj.fields();

        while (elementsIterator.hasNext())
            if (elementsIterator.next().getKey().equals("data_expiracao"))
                clazz = UserRequest.class;

        return clazz;
    }

}