package MobilLax.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Spring Security Rule Setting
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    // 생성자를 통해 UserDetailsService 주입
    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login", "/register").permitAll()  // 로그인, 회원가입 페이지는 모두 접근 가능
                                .requestMatchers("/admin/**").hasRole("ADMIN") // /admin/** 경로는 ADMIN 권한만 가능
                                .requestMatchers("/user/**").hasRole("USER")  // /user/** 경로는 USER 권한만 가능
                                .anyRequest().authenticated()  // 나머지 요청은 인증된 사용자만 접근 가능
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login") // 로그인 페이지 경로
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout") // 로그아웃 경로
                                .permitAll()
                );
        return http.build();
    }
}
