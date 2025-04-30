package MobilLax.Mock;

import MobilLax.Model.UserAccount;
import MobilLax.Repository.UserAccountRepositoryInterface;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class MockUserDetailsFetcherService implements UserDetailsService {

    private final UserAccountRepositoryInterface userRepo;

    public MockUserDetailsFetcherService(UserAccountRepositoryInterface userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserAccount user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
