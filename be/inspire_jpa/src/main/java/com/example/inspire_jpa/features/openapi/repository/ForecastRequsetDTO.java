package com.example.inspire_jpa.features.openapi.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder 
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ForecastRequsetDTO {
    private String beach_num;
    private String base_date;
    private String base_time;

}
