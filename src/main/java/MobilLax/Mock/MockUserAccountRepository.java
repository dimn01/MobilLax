package MobilLax.Mock;

import MobilLax.Model.UserAccount;
import MobilLax.Repository.UserAccountRepositoryInterface;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@Profile("dev")
public class MockUserAccountRepository implements UserAccountRepositoryInterface {

    private final Map<String, UserAccount> db = new HashMap<>();

    public MockUserAccountRepository() {
        UserAccount user = new UserAccount();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("테스트유저");
        user.setPassword("$2a$10$WDylNdyupllNM6u1Yde0k.6N/DYEUdM5w6IdtMTTG8qpoybZ5V8/e");
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        db.put(user.getEmail(), user);
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return Optional.ofNullable(db.get(email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return db.containsKey(email);
    }

    @Override
    public UserAccount save(UserAccount user) {
        if (user.getId() == null) user.setId((long) (db.size() + 1));
        if (user.getCreatedAt() == null) user.setCreatedAt(LocalDateTime.now());
        db.put(user.getEmail(), user);
        return user;
    }
}