package MobilLax.Service;

import MobilLax.Model.UserAccount;
import MobilLax.Repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // UserDetailsService 구현 (로그인 시 사용)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(userAccount.getId())
                .password(userAccount.getPassword())
                .roles(userAccount.getRole())  // 기본적으로 ROLE_USER
                .build();
    }

    // 회원가입 처리
    public void register(UserAccount userAccount) {
        userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));  // 비밀번호 암호화
        userAccount.setRole("ROLE_USER");  // 기본 ROLE_USER
        userAccountRepository.save(userAccount);
    }
}
