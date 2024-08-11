package com.cos.security1.controller;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @GetMapping({"", "/"})
    // 머스테치 : 기본폴더 src/main/resources/ 가 기본경로로 잡히고
    // 뷰 리졸버 설정 : templates (prifix) & .mustache(sufixc) ==> pom에 등록시 자동 & 생략가능
    public String index(){
        logger.info("index 컨트롤러 호출됨");
        logger.debug("index 페이지를 반환합니다.");
        logger.error("index 페이지에서 오류가 발생했습니다.");
        return "index";
    } // src/main/resources/template/index.mustache 로 기본적으로 잡힘 ==> html로 수정필요(WebMvcConfig)


    @GetMapping("/user")
    public @ResponseBody String user(){
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
        user.setRole(user.getUsername().toUpperCase());
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