package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/*
# SecurityConfug 에서 loginProcessingUrl("/login");
    => /login 요청이 오면 자동으로 UserDetailsService 타입으로 Ioc 돠어있는 loadUserByUsername 함수가 실행된다
* */
@Service // IoC로 자동등록
public class PrincipalDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    /*
    # Security Session 안에는 Authentication 타입만 들어갈 수 있고 Authentication 타입에는 UserDetails
    이 중에 아래 리턴값은 Authentication 에 들어가고 이게 Security Session 안으로 매핑

    * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new PrincipalDetails(user);
    }
}
