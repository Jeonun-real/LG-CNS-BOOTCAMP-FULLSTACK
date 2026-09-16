package com.example.inspire_jpa.features.openapi.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder 
@Getter 
@ToString
@JsonIgnoreProperties (ignoreUnknown = true) 
@NoArgsConstructor
@AllArgsConstructor
public class ForecastResponseDTO {
    
    @JsonProperty("category")
    private String category;

    @JsonProperty("fcstValue")
    private String fcstValue;

    private String categoryName;
    
}
