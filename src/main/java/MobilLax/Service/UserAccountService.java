package MobilLax.Service;

import MobilLax.Model.UserAccount;
import MobilLax.Repository.UserAccountRepositoryInterface;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserAccountService {

    private final UserAccountRepositoryInterface userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepositoryInterface userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(String name, String email, String password) {
        if (userAccountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이 이메일은 이미 사용 중입니다.");
        }

        if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        UserAccount userAccount = new UserAccount();
        userAccount.setName(name);
        userAccount.setEmail(email);
        userAccount.setPassword(encodedPassword);
        userAccount.setRole("USER");

        userAccountRepository.save(userAccount);
    }

    public void authenticateUser(String email, String password) {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("아이디가 잘못되었습니다."));

        if (!passwordEncoder.matches(password, userAccount.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 잘못되었습니다.");
        }
    }
}
