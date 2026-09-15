package com.example.inspire_jpa.features.openai.domain;

import java.util.List;

import com.example.inspire_jpa.features.openai.domain.RecommandResponseDTO.RestaurantDTO;
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
public class QuizResponseDTO {

    private List<Quiz> quizs;

    @Builder
    @Getter 
    @ToString 
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor 
    @AllArgsConstructor 
    public static class Quiz{
        private String question;
        private List<String> options;
        private String answer;
        private String desc;
    }
}
