package pt.aodispor.android.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CardDeserializer extends JsonDeserializer<BasicCardFields> {

    private static final long serialVersionUID = 1L;

    // the registry of unique field names to Class types
   // static private Map<String, Class<? extends BasicCardFields>> registry;

    public CardDeserializer(Class<BasicCardFields> clazz) {
        super();
    //    registry = new HashMap<String, Class<? extends BasicCardFields>>();
    }

    public void register(String uniqueProperty, Class<? extends BasicCardFields> clazz) {
      //  registry.put(uniqueProperty, clazz);
    }

    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public BasicCardFields deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        //registry.put("data_expiracao",UserRequest.class);
        //registry.put("data_expiracao",UserRequest.class);

        Class<? extends BasicCardFields> clazz = Professional.class;;
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode obj = (ObjectNode) mapper.readTree(jp);
        Iterator<Map.Entry<String, JsonNode>> elementsIterator = obj.fields();

        //while (elementsIterator.hasNext()) {
          //  Map.Entry<String, JsonNode> element = elementsIterator.next();
           // String name = element.getKey();
           // if (registry.containsKey(name)) {
           //     clazz = registry.get(name);
           //     break;
           // }
        //}
        while (elementsIterator.hasNext())
            if (elementsIterator.next().getKey().equals("data_expiracao"))
                clazz = UserRequest.class;
        /*if (clazz == null) {
            throw ctxt.mappingException("No registered unique properties found for polymorphic deserialization");
        }*/

        return mapper.treeToValue(obj, clazz);
    }

    public static Class<? extends BasicCardFields> findClass(
            JsonNode obj//JsonParser jp
    )throws IOException
    {
        Class<? extends BasicCardFields> clazz = Professional.class;;
        //ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        //ObjectNode obj = (ObjectNode) mapper.readTree(jp);
        Iterator<Map.Entry<String, JsonNode>> elementsIterator = obj.fields();

        while (elementsIterator.hasNext())
            if (elementsIterator.next().getKey().equals("data_expiracao"))
                clazz = UserRequest.class;

        return  clazz;
    }

}