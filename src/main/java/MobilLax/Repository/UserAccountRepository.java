package MobilLax.Repository;

import MobilLax.Model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    boolean existsById(String id);  // 이메일(또는 사용자 ID) 중복 체크
}
