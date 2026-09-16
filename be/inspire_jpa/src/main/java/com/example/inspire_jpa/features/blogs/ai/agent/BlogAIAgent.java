package com.example.inspire_jpa.features.blogs.ai.agent;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.inspire_jpa.features.blogs.domain.dto.BlogRequestDTO;
import com.example.inspire_jpa.features.blogs.domain.dto.BlogResponseDTO;
import com.example.inspire_jpa.features.blogs.domain.entity.BlogEntity;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class BlogAIAgent {

    private final ChatClient blogchatclient;
    
    public String generate(Map<String, Object> map){
        System.out.println(">>>> blog AI agent generate");


        //SEO(search engine optmization)
        String systemPrompt = """
                당신은 seo를 고려한 블로그 작성 에이전트 입니다.
                <절차>
                1. searchBlogkeyword 도구로 기존 글을 검색한다.
                2. 검색 결과를 참고하여 중복되지 않는 본문을 생성한다.
                3. 생성된 본문만 반환한다.
                4. 나츠메소세키의 문제로 작성한다.
                </절차>

                <규칙>
                - 본문은 300자 이내로 작성한다.
                - saveBlog 도구를 절대 호출하지 않는다.
                - 데이터베이스에 게시글을 저장하지 않는다.
                - 생성된 본문 문자열만 반환한다.
                </규칙>
                """;
        String userPrompt = """
                %s %s를 기반으로 글 작성해줘
                """.formatted((String)(map.get("category")),
                              (String)(map.get("keyword")));

        String result = blogchatclient.prompt()
                            .system(systemPrompt)
                            .user(userPrompt)
                            .call()
                            .content();

        System.out.println(">>>> blog agent generate result");
        System.out.println(result);                    
        return result;
        
    }

    public BlogResponseDTO insert(BlogRequestDTO request) {
        System.out.println("debug >>>> blog AIagent insert "); 
        
        //SEO(search engine optmization)
        String systemPrompt = """
            당신은 블로그 게시글의 중복 여부를 확인하고 저장하는 발행 에이전트입니다.
            아래 절차를 순서대로 반드시 지키세요.

            <절차>
            1. searchBlogkeyword호출하여 같은 category에서 title 또는 content가
            유사한 게시글이 존재하는지 확인한다.
            2. 검색 결과가 1건 이상이면 saveBlog를 호출하지 않는다.
            3. 중복 게시글의 제목과 함께 중복임을 보고하고 작업을 종료한다.
            4. 검색 결과가 0건일 때만 saveBlog를 호출한다.
            5. 저장된 blogId를 포함하여 처리 결과를 보고한다.
            </절차>

            <규칙>
            - category, title, content, email은 사용자가 전달한 값을 그대로 사용한다.
            - 입력값을 임의로 생성하거나 수정하지 않는다.
            - searchBlogKeyword와 saveBlog는 각각 최대 한 번만 호출한다.
            - 중복 게시글이 존재하면 어떤 경우에도 saveBlog를 호출하지 않는다.
            - 마크다운과 백틱을 사용하지 않는다.
            </규칙>
        """;
        String userPrompt = """
            다음 게시글 정보를 확인하고 등록 절차를 수행하세요.

            <게시글>
                <category>%s</category>
                <title>%s</title>
                <content>%s</content>
                <email>%s</email>
            </게시글>

            같은 카테고리에서 제목 또는 내용이 유사한 기존 게시글이 있는지 먼저 검색하세요.
            중복 게시글이 없을 때만 전달받은 정보를 변경하지 않고 저장하세요.                
        """.formatted(request.getCategory(), request.getTitle(), request.getContent(), request.getEmail());

        BlogResponseDTO result = blogchatclient.prompt()
                            .system(systemPrompt)
                            .user(userPrompt)
                            .call()
                            .entity(BlogResponseDTO.class);

        System.out.println(">>>> blog agent generate result");
        System.out.println(result);                    
        return result;
    }

}
