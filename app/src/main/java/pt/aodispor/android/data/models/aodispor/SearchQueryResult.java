package pt.aodispor.android.data.models.aodispor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.aodispor.android.data.models.aodispor.parsing.CardDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchQueryResult extends ApiJSON {
    /**
     * list of professionals
     */
    @JsonDeserialize(using = CardListDeserializer.class)
    @JsonProperty("data")
    public List<BasicCardFields> data;
    @JsonProperty("meta")
    public Meta meta;

    public static class CardListDeserializer extends JsonDeserializer<List<BasicCardFields>> {

        @Override
        public List<BasicCardFields> deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            ObjectCodec oc = jp.getCodec();
            JsonNode node = oc.readTree(jp);
            ArrayList<BasicCardFields> list = new ArrayList<>();
            if (node.isArray()) {
                for (JsonNode elementNode : node) {
                    list.add(oc.treeToValue(elementNode, CardDeserializer.findClass(elementNode)));
                    //TODO may use mapper later here
                }
            } else {
                list.add(oc.treeToValue(node, CardDeserializer.findClass(node)));
                //TODO may use mapper later here
            }
            return list;
        }

    }


}
