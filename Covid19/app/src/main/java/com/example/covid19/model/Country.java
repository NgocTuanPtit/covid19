package com.example.covid19.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class Country implements Serializable {
    private String updateDay;
    private String countryName;
    private String countryCode;
    private String countryFlag;
    private String continent;
    private int cases;
    private int recovered;
    private int active;
    private int critical;
    private int deaths;
}
