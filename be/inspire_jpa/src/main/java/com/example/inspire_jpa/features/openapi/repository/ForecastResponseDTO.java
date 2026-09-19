package com.example.inspire_jpa.features.openapi.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter 
@Builder 
@Getter 
@ToString
@JsonIgnoreProperties (ignoreUnknown = true) 
@NoArgsConstructor
@AllArgsConstructor
public class ForecastResponseDTO {

    @JsonProperty("baseDate")
    private String baseDate;

    @JsonProperty("baseTime")
    private String baseTime;

    @JsonProperty("fcstDate")
    private String fcstDate;

    @JsonProperty("fcstTime")
    private String fcstTime;
    
    @JsonProperty("category")
    private String category;

    @JsonProperty("fcstValue")
    private String fcstValue;

    private String categoryName;
    
}
