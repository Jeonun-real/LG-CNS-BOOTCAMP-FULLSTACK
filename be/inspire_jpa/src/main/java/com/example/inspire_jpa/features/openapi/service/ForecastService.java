package com.example.inspire_jpa.features.openapi.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.inspire_jpa.features.openapi.repository.ForecastRequsetDTO;
import com.example.inspire_jpa.features.openapi.repository.ForecastResponseDTO;
import com.example.inspire_jpa.features.openapi.util.CategoryCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;


@Service 
@RequiredArgsConstructor 
public class ForecastService {

    private final ObjectMapper objectMapper;

    @Value("${openapi.serviceKey}")
    private String key;

    @Value("${openapi.callBackUrl}")
    private String endPoint;

    @Value("${openapi.datatype}") 
    private String type;

    public List<ForecastResponseDTO> connection(ForecastRequsetDTO request){
        System.out.println(">>> fsct service");
        String requestUrl = UriComponentsBuilder
                        .fromUriString(endPoint)
                        .queryParam("serviceKey", key) // servicekey → serviceKey
                        .queryParam("beach_num", request.getBeach_num())
                        .queryParam("base_date", request.getBase_date())
                        .queryParam("base_time", request.getBase_time())
                        .queryParam("dataType", type)
                        .build(true)                   // 이미 Encoding된 키의 재인코딩 방지
                        .toUriString();

        System.out.println(">>>> fcst service requestUri :" + requestUrl);

        HttpURLConnection http = null;
        InputStream stream = null;
        String  result = null;
        try{
            URL url = new URL(requestUrl);
            http = (HttpURLConnection) url.openConnection();
            int status = http.getResponseCode();
            System.out.println(">>> fcst service connection stratus : " + status);

            if(status == 200){
                return readString(http.getInputStream());
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<ForecastResponseDTO> readString(InputStream stream) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
        String input = null;

        StringBuilder result = new StringBuilder();
        while((input = br.readLine())!=null){
            result.append(input);
        }

        System.out.println("fcst service reade String");
        System.out.println(result.toString());
        br.close();

        return parseJson(result.toString());
    }


    public List<ForecastResponseDTO> parseJson(String result) throws Exception{
        JsonNode node = objectMapper.readTree(result);
        JsonNode item = node.findValue("item");

        List<ForecastResponseDTO> list = null;
        if(item.isArray()){
            list = Arrays.asList(objectMapper.treeToValue(item,ForecastResponseDTO[].class));
        }
        list.stream()
                .forEach(System.out::println);

        // STREAM과 MAP - to list로 
        // CATRGORYNAME에 값을 할당
        list = list.stream()
                    .map(dto->{
                        dto.setCategoryName(CategoryCode.valueOf(dto.getCategory()).getName());
                        String value = CategoryCode.getCodeValue(dto.getCategory(), dto.getFcstValue());
                        String unit = CategoryCode.valueOf(dto.getCategory()).getUnit();
                        dto.setFcstValue(value+unit);

                        return dto;
                    })
                    .toList();

        System.out.println();
        list.stream()
                .forEach(System.out::println);
                
        return list;
    }

}
