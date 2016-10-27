package pt.aodispor.aodispor_android.API;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.*;

import java.util.List;

public class Pagination {
        @JsonProperty("total") private int total;
        @JsonProperty("count") private int count;
        @JsonProperty("per_page") private int per_page;
        @JsonProperty("current_page") private int current_page; //starting index is 1
        @JsonProperty("total_pages") private int total_pages;

        @JsonProperty("links")
        @JsonDeserialize(using = ItemListDeserializer.class)
        private List<Links> links;

        public static class ItemListDeserializer extends OptionalArrayDeserializer<Links> {
                protected ItemListDeserializer() {
                        super(Links.class);
                }
        }

        public int getTotal(){return total;}
        public int getCount(){return count;}
        public int getPerPage(){return per_page;}
        public int getCurrentPage(){return current_page;}
        public int getPages(){return total_pages;}

        public Links getLinks(){return links.size()>0?links.get(0):null;}//only makes sense if clone
}
