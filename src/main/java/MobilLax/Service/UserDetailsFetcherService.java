package MobilLax.Service;

import MobilLax.Model.UserAccount;
import MobilLax.Repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// UserDetails process Logic, Spring Security <= UserDetailsFetcherService => UserDetailsService
@Service
public class UserDetailsFetcherService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public UserDetailsFetcherService(UserAccountRepository userAccountRepository) {
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
