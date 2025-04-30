package MobilLax.Security;

import MobilLax.Model.UserAccount;
import MobilLax.Repository.UserAccountRepositoryInterface;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class UserDetailsFetcherService implements UserDetailsService {

    private final UserAccountRepositoryInterface userAccountRepository;

    public UserDetailsFetcherService(UserAccountRepositoryInterface userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return User.builder()
                .username(userAccount.getEmail())
                .password(userAccount.getPassword())
                .roles(userAccount.getRole())
                .build();
    }
}