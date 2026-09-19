package com.example.inspire_jpa.features.openapi.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inspire_jpa.features.openapi.repository.ForecastRequsetDTO;
import com.example.inspire_jpa.features.openapi.service.ForecastService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/forecast")
@RequiredArgsConstructor 
public class ForecastController {

    private final ForecastService forecastService;

    @PostMapping("/fcst")
    public ResponseEntity<?> postMethodName(@RequestBody ForecastRequsetDTO request) {
        
        System.out.println(">>> forcast controller fcst");
        System.out.println(">>> forcast controller fcst params : " + request);

        return ResponseEntity.status(HttpStatus.OK)
                    .body(forecastService.connection(request));
    }
    
    
}
