package com.example.pedrobarbosa.tabapplication.API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {
    @JsonProperty("pagination") public Pagination pagination;
}
