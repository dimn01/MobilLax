package MobilLax.Config;

import MobilLax.Service.UserAccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserAccountService userAccountService;

    public SecurityConfig(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/home", "/login", "/signup").permitAll()  // 로그인과 회원가입은 모두 접근 가능
                .anyRequest().authenticated()  // 그 외 모든 요청은 인증 필요
                .and()
                .formLogin()
                .loginPage("/login")  // 로그인 페이지
                .loginProcessingUrl("/login")  // 로그인 요청 URL
                .defaultSuccessUrl("/home", true)  // 로그인 성공 시 리다이렉트될 페이지
                .failureUrl("/login?error")  // 로그인 실패 시 리다이렉트될 페이지
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")  // 로그아웃 URL
                .logoutSuccessUrl("/login?logout")  // 로그아웃 후 리다이렉트 URL
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userAccountService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 비밀번호 암호화를 위한 암호화기
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
