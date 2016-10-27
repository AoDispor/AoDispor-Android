package com.example.pedrobarbosa.tabapplication.API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Professional {
    @JsonProperty("full_name") private String full_name;
    @JsonProperty("title") private String title;
    @JsonProperty("description") private String description;
    @JsonProperty("rate") private String rate;
    @JsonProperty("currency") private String currency;
    @JsonProperty("type") private String type;
    @JsonProperty("string_id") private String string_id;
    @JsonProperty("location") private String location;
    @JsonProperty("avatar_url") private String avatar_url;
    @JsonProperty("phone") private String phone;

    public String getFullName   (){return new String(full_name);}
    public String getTitle		(){return new String(title);}
    public String getDescription(){return new String(description);}
    public String getRate		(){return new String(rate);}
    public String getCurrency	(){return new String(currency);}
    public String getType		(){return new String(type);}
    public String getString_id	(){return new String(string_id);}
    public String getLocation	(){return new String(location);}
    public String getAvatar_url	(){return new String(avatar_url);}
    public String getPhone		(){return new String(phone);}
}
