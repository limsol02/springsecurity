package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 기본적으로 CRUD 함수를 JpaRepository 가 들고있음
// @Repository 라는 어노테이션이 없어도 IoC 가능.
// 이유? JpaRepository를 상속했기에
public interface UserRepository extends JpaRepository<User, Integer> {
    // findBy 규칙 -> Username 문법 JPA Query Method
    // select * from user where username=?
    public User findByUsername(String username);
    // select * from user where email=?
    //public User findByEmail(String email);
}
