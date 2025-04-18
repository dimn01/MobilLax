package MobilLax.Repository;

import MobilLax.Model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JPA Repository, UserAccount(Entity) <= Repository(JPA) => MySQL
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    // 이메일로 사용자 찾기
    Optional<UserAccount> findByEmail(String email);
    // 이메일 중복 확인
    boolean existsByEmail(String email);
}
