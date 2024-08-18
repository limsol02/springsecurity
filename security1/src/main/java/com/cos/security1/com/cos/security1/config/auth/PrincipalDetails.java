package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/*
# PrincipalDetails ??
1) 만든 목적 : 시큐리티 세션 안에 들어갈 수 있는 Authentication 타입에 OAuth2User & UserDetails 타입을 넣어주기위해
    기본적으로 회원가입을 하면 User라는 정보는 OAuth2User & UserDetails 타입이 아니기에
    User 객체를 하나하나 변환해서 타입을 변환해주는 작업을 수행

2) UserDetails 와 OAuth2User 는 PrincipalDetails 로 묶어버리면 구글이든 일반 로그인이든 처리가능

3) 하나의 PrincipalDetails 를 통해서 User 객체 정보를 둘다 넣을 수 있다.

시큐리티가 /login 을 낚아채서 로그인을 진행시킨다.
이때, 로그인 진행이 완료가 되면 Security Session 을 만들어준다.(Security ContextHolder 가 키값 , 안에 세션정보 저장)

# 들어가는 오브젝트 타입? Authentication 타입 객체
    이 Authentication 안에는 User 정보가 있어야한다.
    User 객체타입 = UserDetails 타입 객체

# Security Session 안에는 Authentication 타입만 들어갈 수 있고 Authentication 타입에는 UserDetails 타입 객체만 저장된다.

#꺼내는 방법?
    UserDetails(PrincipalDetails)
*/
@Data
public class PrincipalDetails implements UserDetails,OAuth2User {

    private User user; // 현재 로그인한 사용자의 정보를 담고있음 "컴포지션(composition)" 관계
    private Map<String,Object> attributes;
    // 일반 로그인 사용 생성자
    public PrincipalDetails(User user){
        this.user = user;
    }
    // OAuth 로그인 사용 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes){
        this.user = user;
        this.attributes = attributes;
    }
    // 해당 유저의 권한을 리턴하는곳! String 타입이 아니라서 타입을 맞춰줘야한다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 사용자의 계정이 만료되었는지 여부를 반영
        //  true : 항상 만료되지 않은 상태로 세팅
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정이 잠기지 않았는지 여부
        // true : 항상 잠기지않은 상태로 설정
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 사용자의 비밀번호가 만료되지 않았는지 여부를 반환
        // true : 항상 비밀번호가 만료되지 않은 상태로 설정
        return true;
    }

    @Override
    public boolean isEnabled(){
        // 현재 사이트에서 1년동안 로그인을 하지않으면 휴면계정으로 하게 될 경우 여기서 설정
        // 사용자가 활성화된 상태인지 여부를 반환
        // 항상 true를 반환하여 사용자가 활성화된 상태로 설정
        return true;
    }

    // OAuth Override 메소드 2개
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        // attributes.get("sub") == 구글 프라이머리키 아이디
        return null;
    }
}
