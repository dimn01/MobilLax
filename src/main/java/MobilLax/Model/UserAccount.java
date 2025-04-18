package MobilLax.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// JPA Entity, MySQL users Table Mapping
@Getter
@Setter
@Entity
@Table(name = "users")  // 테이블 이름 설정
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가 전략
    private Long id;    // 사용자 고유번호
    
    @Column(nullable = false, unique = true)  // 이메일은 유니크하며 null을 허용하지 않음
    private String email;  // 이메일로 사용자를 식별

    @Column(nullable = false)  // 이름은 null을 허용하지 않음
    private String name;  // 사용자 이름

    @Column(nullable = false)  // 비밀번호는 null을 허용하지 않음
    private String password;  // 암호화된 비밀번호

    @Column(nullable = false)  // 권한은 null을 허용하지 않음
    private String role;  // 사용자 권한 (ROLE_USER, ROLE_ADMIN 등)

    @Column(nullable = false, updatable = false)  // 생성일은 null을 허용하지 않음, 수정되지 않음
    private LocalDateTime createdAt;  // 계정 생성 시기

    // 엔티티가 저장되기 전에 생성일을 자동으로 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
