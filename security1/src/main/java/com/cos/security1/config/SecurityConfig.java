package com.cos.security1.config;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // prePostEnabled 어노테이션 활성화 & securedEnabled 어노테이션 활성화
public class SecurityConfig {
    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    // 비밀번호 암호화
    // Bean : 해당 메서드의 리턴되는 오브젝트를 IoC해준다.
    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    /*
    # CSRF? (= Cross-Site Request Forgery)
    1. 이미 인증된 세션을 이용하여 악의적인 요청을 서버에 보냄
    2. 서버에 의도하지 않은 작업을 수행할 수 있게 하여 사용자 데이터의 무단변경, 정보 유출 등의 문제를 일으킨다.

    # CSRF 보호 목적
    1. CSRF 토큰 : 웹 애플리케이션은 각 요청에 대하여 고유한 CSRF 토큰을 생성하여 사용한다.
        이 토큰은 사용자 세션과 연결되어 있으며 서버는 요청에 포함된 CSRF 토큰이 유효한지 검증
        이때, 유효하지 않거나 누락된 토큰이 포함된 요청은 거부

    2. 쿠키와 헤더 : CSRF 토큰은 일반적으로 HTML 폼에 숨겨진 필드로 포함되거나 HTTP 헤더를 통해 전송
        이를 통해 서버는 요청이 사용자가 의도한 것인지 확인 가능

    # oauth2
    - 소셜 네트워크 로그인 단계?
    1. 코드받기(인증) 2. 엑세스 토큰 받기(권한 생성) 3. 생성된 권한을 통해 사용자 프로필 정보를 가져오기 4. 그 정보를 토대로 회원가입을 자동으로 진행
    이때, 이메일, 전화번호, 이름, 아이디 => 구글이 제공하는 기본 정보를 넘어서 주소나 등급 같은 추가적인 정보가 필요할 경우 추가적인 회원가입 절차가 필요함

    - 구글 로그인 처리를 하고 완료된 뒤의 후처리가 필요하다.
    Tip) 코드를 받는게 아니다 (액세스 토큰 + 사용자 프로필 정보 를 받아온다.)
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/**").authenticated() // 인증된 사용자만 접근 가능(인증만 된다면 들어갈 수 있는 주소)
                        .requestMatchers("/manager/**").hasAnyAuthority("MANAGER", "ADMIN") // 'MANAGER' 또는 'ADMIN' 역할 가진 사용자만 접근 가능
                        .requestMatchers("/admin/**").hasAuthority("ADMIN") // 'ADMIN' 역할을 가진 사용자만 접근가능
                        .anyRequest().permitAll() // 나머지 요청은 모두 허용
                )
                .formLogin(form -> form
                        .loginPage("/loginForm") // 로그인 페이지 URL
                        // .usernameParameter("username2") : 만약 내 html에서 name값을 이렇게 준다면 여기서 param값 설정을 해줘야 PrincipalDetailsService의 username에서 인식할수있음
                        .loginProcessingUrl("/login") // login 주소가 호출이 되면 시큐리티가 자동으로 낚아채서 대신 로그인을 진행해준다
                        .defaultSuccessUrl("/") // 로그인 성공 후 리다이렉트 URL 메인페이지 호출
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/loginForm") // 구글 로그인이 완료된 뒤의 후처리가 필요 => 로그인하면 구글은 기본적으로
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(principalOauth2UserService) // 여전히 이 방식 사용 가능
                        )
                );
        return http.build();
    }
}
