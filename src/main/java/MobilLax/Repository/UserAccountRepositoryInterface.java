package MobilLax.Repository;

import MobilLax.Model.UserAccount;
import java.util.Optional;

public interface UserAccountRepositoryInterface {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
    UserAccount save(UserAccount user);
}
