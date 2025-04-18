package MobilLax.Service;

import MobilLax.Model.User;
import MobilLax.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return false; // 이미 이메일이 존재함
        }
        userRepository.save(user);
        return true;
    }
}