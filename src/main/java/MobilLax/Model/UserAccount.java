package MobilLax.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")  // 실제 테이블 이름
public class UserAccount {
    @Id
    private String id; // 사용자 ID (이메일 또는 사용자명)

    private String password; // 암호화된 비밀번호

    private String role;  // 사용자 역할 (ROLE_USER 등)
}
