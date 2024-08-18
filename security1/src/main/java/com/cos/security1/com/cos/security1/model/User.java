package com.cos.security1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;
@Entity // 이 클래스가 데이터베이스 테이블과 매핑되는 JPA 엔티티임을 나타냄
@Data // Getter & Setter
public class User {
    @Id // primary key 식별
    @GeneratedValue(strategy = GenerationType.IDENTITY) // primary key 의 값을 자동으로 생성(AUTO_INCREMENT)
    // 필드 나열
    private int id;
    private String username;
    private String password;
    private String email;
    private String role; // ROLE_USER, ROLE_ADMIN
    private String provider; // google
    private String providerId; // 구글의 아이디
    // DateTime 타입으로 자동 날짜 저장
    @CreationTimestamp
    private Timestamp createDate;
    @Builder // 구글 로그인 회원가입 용
    public User(String username, String password, String email, String role, String provider, String providerId, Timestamp createDate) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.createDate = createDate;
    }

    public User() {

    }

    /*
     PrincipalOauth2UserService => super.loadUser(userRequest)
     username: google_116099776214391544647
     password: 암호화 된 getinthere
     email: ghdwjdgh89@gmail.com
     role: USER
     provider: google
     providerId: 116099776214391544647
    * */
}
