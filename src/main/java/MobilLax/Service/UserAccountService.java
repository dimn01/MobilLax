package MobilLax.Service;

import MobilLax.Model.UserAccount;
import MobilLax.Repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원 가입 처리 - 비밀번호 암호화 후 저장
    @Transactional
    public boolean registerUser(String name, String email, String password) {
        if (userAccountRepository.existsByEmail(email)) {
            return false; // 이미 존재하는 이메일일 경우 false 반환
        }

        String encodedPassword = passwordEncoder.encode(password);

        UserAccount userAccount = new UserAccount();
        userAccount.setName(name);  // 이름 처리 추가
        userAccount.setEmail(email);
        userAccount.setPassword(encodedPassword);
        userAccount.setRole("ROLE_USER");  // 기본적으로 'ROLE_USER' 설정

        userAccountRepository.save(userAccount); // DB에 저장

        return true; // 회원 가입 성공
    }

    // 로그인 처리 - 이메일과 비밀번호 검증
    public boolean authenticateUser(String email, String password) {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("아이디 또는 비밀번호가 잘못되었습니다."));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(password, userAccount.getPassword())) {
            return false; // 비밀번호가 맞지 않으면 false 반환
        }

        return true; // 로그인 성공
    }
}
