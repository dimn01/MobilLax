package MobilLax.Mock;

import MobilLax.Model.UserAccount;
import MobilLax.Repository.UserAccountRepositoryInterface;
import MobilLax.Service.UserAccountService;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class MockUserAccountService extends UserAccountService {

    public MockUserAccountService(UserAccountRepositoryInterface userAccountRepository,
                                  PasswordEncoder passwordEncoder) {
        super(userAccountRepository, passwordEncoder);
    }

}
