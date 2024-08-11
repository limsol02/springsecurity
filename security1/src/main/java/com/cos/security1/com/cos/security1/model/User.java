package com.cos.security1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    // DateTime 타입으로 자동 날짜 저장
    @CreationTimestamp
    private Timestamp createDate;
}
