package com.example.inspire_jpa.features.comments.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.inspire_jpa.features.blogs.domain.entity.BlogEntity;
import com.example.inspire_jpa.features.blogs.repository.BlogRepository;
import com.example.inspire_jpa.features.comments.domain.dto.CommentRequestDTO;
import com.example.inspire_jpa.features.comments.domain.dto.CommentResponseDTO;
import com.example.inspire_jpa.features.comments.domain.entity.CommentEntity;
import com.example.inspire_jpa.features.comments.repository.CommentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional 
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;

    public CommentResponseDTO insert(CommentRequestDTO request){
        System.out.println(">>>>comment insert service");
        System.out.println(">>>>service insert commentmapper" + request);

        BlogEntity blog = blogRepository.findById(request.getBlogId())
            .orElseThrow(()->new RuntimeException(request.getBlogId()+"blog not found"));
        
        CommentEntity entity = request.toEntity(blog);
        CommentEntity result = commentRepository.save(entity);

        return CommentResponseDTO.fromEntity(result);
        

    }

    public int delete(Integer commentId){
        System.out.println(">>>>comment delete service");
        System.out.println(">>>>service delete param: " + commentId);


        commentRepository.deleteById(commentId);
        return 1;

    }

    /*
    jpa의 업데이트 주의사항
    - update는 dml
    - dml : transaction(commit, rollback)
    - Dirty Checking(변경감지) -> @Transactional 필수
    findById() - 영속성관리로 진입
    setXXXXX() - 필드 수정(DirtyChecking) not commit
    현재상태와 스냅샷 상태를 비교하고 다름을 확인하면 update 실행 - commit 
    */

    @Transactional
    public int update(Map<String,Object> map){
        System.out.println(">>>>comment update service");
        CommentEntity entity = commentRepository
            .findById((Integer)(map.get("id")))
            .orElseThrow(()->new RuntimeException((Integer)(map.get("id"))+"comment not found"));

        entity.updateComment((String)map.get("comment"));
        
        return 1;

    }
    
}
