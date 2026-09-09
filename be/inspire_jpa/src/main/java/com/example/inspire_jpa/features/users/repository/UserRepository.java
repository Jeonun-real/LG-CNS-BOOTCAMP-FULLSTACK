package com.example.inspire_jpa.features.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inspire_jpa.features.users.domain.entity.UserEntity;

@Repository 
public interface UserRepository extends JpaRepository<UserEntity, String>{
    
    // 사용자 정의 메서드 추가 
    // findByPropertyName, findByPropertyNameAndPropertyName
    // findByEmailAndPassword()  

    public Optional<UserEntity> findByEmailAndPassword(String email, String password) ; 

}




