package com.example.inspire_jpa.features.openai.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Builder
@Getter 
@ToString 
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor 
@AllArgsConstructor 
public class RecommandResponseDTO {
    private String weather;
    private String location;
    private List<RestaurantDTO> restaurants;

    
    @Builder
    @Getter 
    @ToString 
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor 
    @AllArgsConstructor 
    public static class RestaurantDTO{
        private String name;
        private String catergory;
        private String reason;
    }
}
