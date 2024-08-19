package com.cos.security1.config.oauth;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.oauth.provider.FacebookUserInfo;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service // 구글 로그인 이후 후처리 클래스
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    @Lazy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    // # 함수 종료시 @AuthenticationPrincipal 를 사용할 수 있다.
    @Override // 구글로 부터 받은 userRequest 데이터에 대한 후처리 되는 함수
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // userRequest : org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest@45f3a990
        System.out.println("getClientRegistration : "+ userRequest.getClientRegistration()); // registrationId(google)로 어떤 OAuth를 이용해 로그인 했는지 확인가능
        System.out.println("getAccessToken : "+ userRequest.getAccessToken());
        System.out.println("getClientRegistration : "+ userRequest.getClientRegistration().getClientId());

        // userRequest 를 통해 OAuth2User 객체에 정보를 집어넣어주는 방식
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 구글로그인 버튼 클릭 -> 구글 로그인 창 오픈 -> 로그인을 완료 -> code를 리턴(OAuth-Client 라이브러리) -> Access Token 요청
        // 여기까지가 userRequest 정보 -> 이후 회원 프로필 받아야함, 이때 사용되는 함수가 loadUser 함수 -> loadUser 호출하여 구글로 부터 회원프로필 받아오기
        System.out.println("getAttributes : "+ oauth2User.getAttributes());

        // 회원가입을 강제로 진행
        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo =  new GoogleUserInfo(oauth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo =  new FacebookUserInfo(oauth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청");
            // naver는 response 로 정보를 받아오기 때문에
            oAuth2UserInfo =  new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
        }
        else{
            System.out.println("우리는 구글과 페이스북, 네이버만 지원하고있습니다.");
        }

        String provider = oAuth2UserInfo.getProvider(); // 회사명
        String providerId = oAuth2UserInfo.getProviderId(); // 고유 아이디 116099776214391544647
        String username = provider+"_"+providerId; // 이름 충돌 방지(google_116099776214391544647)
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String providerEmail = oAuth2UserInfo.getEmail(); // 이메일
        String role = "USER";

        User userEntity = userRepository.findByUsername(username);
        if(userEntity==null){
            System.out.println("소셜 로그인이 최초입니다.");
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(providerEmail)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }else{
            System.out.println("소셜 로그인을 한 기록이 있습니다.");
        }

        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }
}
