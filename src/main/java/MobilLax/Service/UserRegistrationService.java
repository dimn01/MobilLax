package MobilLax.Service;

import MobilLax.Model.UserAccount;
import MobilLax.Repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Business(register) Logic, Spring Security <=> UserRegistrationService
@Service
public class UserRegistrationService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원 가입 처리 - 비밀번호 암호화 후 저장
    @Transactional
    public boolean registerUser(String email, String password, String role) {
        if (userAccountRepository.existsByEmail(email)) {
            return false; // 이미 존재하는 이메일일 경우 false 반환
        }

        String encodedPassword = passwordEncoder.encode(password);

        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(email);
        userAccount.setPassword(encodedPassword);
        userAccount.setRole(role);

        userAccountRepository.save(userAccount); // DB에 저장

        return true; // 회원 가입 성공
    }
}