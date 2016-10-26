package com.example.pedrobarbosa.tabapplication.API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchQueryResult extends ApiJSON{
    @JsonProperty("data") public List<Professional> data;
    @JsonProperty("meta") public Meta meta;
}
