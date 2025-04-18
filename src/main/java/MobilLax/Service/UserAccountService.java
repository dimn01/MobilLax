package MobilLax.Service;

import MobilLax.Model.UserAccount;
import MobilLax.Repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Business Logic, Spring Security <= UserAccountService => UserDetailsService
@Service
public class UserAccountService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자에서 필요한 의존성 주입
    public UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 사용자가 입력한 이메일로 사용자를 조회하고, 찾지 못하면 예외 발생
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 반환 시 UserDetails 객체로 반환 (Spring Security에서 사용)
        return User.builder()
                .username(userAccount.getEmail())
                .password(userAccount.getPassword())
                .roles(userAccount.getRole())
                .build();
    }

    // 회원 가입 처리 - 비밀번호 암호화 후 저장
    @Transactional
    public boolean registerUser(String email, String password, String role) {
        if (userAccountRepository.existsByEmail(email)) {
            return false; // 이미 존재하는 이메일일 경우 false 반환
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 새로운 UserAccount 객체 생성 및 저장
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(email);
        userAccount.setPassword(encodedPassword);
        userAccount.setRole(role);  // 'USER' 또는 'ADMIN'

        userAccountRepository.save(userAccount); // DB에 저장

        return true; // 회원 가입 성공
    }
}
