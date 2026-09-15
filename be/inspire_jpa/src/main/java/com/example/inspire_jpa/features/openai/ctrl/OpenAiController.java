package com.example.inspire_jpa.features.openai.ctrl;

import com.example.inspire_jpa.features.openai.domain.QuizResponseDTO;
import com.example.inspire_jpa.features.openai.domain.RecommandResponseDTO;
import com.example.inspire_jpa.features.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/openai")
@RequiredArgsConstructor
public class OpenAiController {
    
    private final OpenAiService openAiService;


    @PostMapping("/recommand")
    public ResponseEntity<?> recommand(   @RequestParam("weather") String weather,
                                          @RequestParam("location") String location) {
        System.out.println(">>>> openai controller recomomand");
        System.out.println("debug >>>> openai copntroller recommand parmas: "+ weather);
        System.out.println("debug >>>> openai copntroller recommand parmas: "+ location);
        
        return ResponseEntity.status(HttpStatus.OK)
            .body(openAiService.recommend(weather,location));
    }
    
    @PostMapping("/quiz")
    public ResponseEntity<QuizResponseDTO> quiz(@RequestParam("subject") String subject) {
        System.out.println(">>>> openai controller quiz");
        System.out.println(">>>> openai copntroller quiz parmas: "+ subject);
        return ResponseEntity.status(HttpStatus.OK)
            .body(openAiService.quiz(subject));
    }

}
