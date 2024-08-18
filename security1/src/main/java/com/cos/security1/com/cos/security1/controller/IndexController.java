package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // view를 리턴
public class IndexController {
    @Autowired
    private UserRepository userRepository; // JPA 인터페이스
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);


    /*
    ★ 스프링 시큐리티 로그인과 구글 로그인(OAuth)
    - 스프링 시큐리티 : 내부에는 시큐리티 세션이 있는데, 이 안에 들어올 수 있는 객체 타입은 오로지 Authentication 타입만 가능
        1) Authentication 안에 들어가는 타입은 UserDetails 타입 & OAuth2User 타입
        2) UserDetails : 일반 로그인 시 타입 생성
        3) OAuth2User : OAuth 로그인 시 타입 생성
        4) 이때 둘다 한번에 처리를 해야되는 상황이 당연히 생기는데, 이걸 처리하기 위해서 한 클래스를 userDetails 랑 OAuth2User 상속받아서
            Authentication 안에 이 클래스를 넣어서 처리하면 그만!
    * */
    @GetMapping("/test/login")
    //  @AuthenticationPrincipal UserDetails userDetails 으로 하는데 PrincipalDetails 에서 userDetails 를 implement 햇기떄문에 PrincipalDetails 로도 받을수있음
    public @ResponseBody String loginTest(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails){ //DI(의존성 주입)
        System.out.println("/test/login==================");
        // System.out.println("authentication 01 : "+authentication.getPrincipal());
        // authentication.getPrincipal() : 리턴타입이 객체임
        // authentication : com.cos.security1.config.auth.PrincipalDetails@48db0500

        /*
        1. PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            방법으로 getUser
        2. @AuthenticationPrincipal PrincipalDetails userDetails 방법으로 getUser 받아오기
        * */
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication 02 : "+principalDetails.getUser());
        System.out.println("userDetails : "+userDetails.getUser());
        return "세션정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    // 위에 테 스트로그인은 구글 로그인시  PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); 에서 error 발생
    public @ResponseBody String loginTest(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth){ //DI(의존성 주입)
        System.out.println("/test/oauth/login==================");
        // 구글 로그인은 PrincipalDetails 이 아닌 OAuth2User로 받아야 정상적으로 돌아감
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // principalDetails.getUser() = userDetails.getUser() = oAuth2User.getAttributes()
        // oAuth2User.getAttributes() = super.loadUser()에서 호출햇던 getAttributes())
        // 이것도 마찬가지로 타입만 OAuth2User 라면 객체 생성을 하던 어노테이션을 이용하던 결과는 동일하다
        System.out.println("authentication 01 : "+oAuth2User.getAttributes());
        System.out.println("oauth"+oAuth.getAttributes());
        return "OAUTH 세션정보 확인하기";
    }


    @GetMapping({"", "/"})
    // 머스테치 : 기본폴더 src/main/resources/ 가 기본경로로 잡히고
    // 뷰 리졸버 설정 : templates (prifix) & .mustache(sufixc) ==> pom에 등록시 자동 & 생략가능
    public String index(){
        logger.info("index 컨트롤러 호출됨");
        logger.debug("index 페이지를 반환합니다.");
        logger.error("index 페이지에서 오류가 발생했습니다.");
        return "index";
    } // src/main/resources/template/index.mustache 로 기본적으로 잡힘 ==> html로 수정필요(WebMvcConfig)

    /*
    # PrincipalDetails에 UserDetails & OAuth2User 을 둘다 상속받았기 때문에
        @AuthenticationPrincipal PrincipalDetails principalDetails 하나로
        구글 로그인과 일반 로그인을 처리할 수 있다.

   # PrincipalOauth2UserService (구글 로그인 후 후처리 함수 클래스)
        를 통해 정보가 없을경우 강제로 회원가입 시켜놓는 로직도 추가

    * */
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails : "+principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }
    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }

    // /user 라고 하면 스프링 시큐리티가 먼저 낚아채감
    // securityConfig 파일 생성 이후 내 로그인 페이지가 뜸

    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }
    @PostMapping("/join")
    public String join(User user){
        System.out.println(user);
        //user.setRole(user.getUsername().toUpperCase());
        user.setRole("User");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user); // 회원가입 완료 & but 비밀번호: 1234
        // 시큐리티로 로그인 불가(패스워드가 암호화가 되어있지 않기 때문에)
        // 리턴을 loginForm으로 이동
        return "redirect:/loginForm";
    }

    @PreAuthorize("hasAuthority('ADMIN')") // 특정 메소드에 원하는 권한 설정
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

}