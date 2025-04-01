package MobilLax.demo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class IndexController {

    @PostMapping
    public Response register(@RequestBody User user) {
      // 간단한 유효성 검사
      if (user.getUsername().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
        return new Response("모든 필드를 입력해주세요.", false);
      }

      // 회원가입 로직 (DB 저장 로직은 생략)
      return new Response("회원가입이 완료되었습니다!", true);
    }

    class User {
      private String username;
      private String email;
      private String password;

      // Getters and Setters
      public String getUsername() {
        return username;
      }

      public void setUsername(String username) {
        this.username = username;
      }

      public String getEmail() {
        return email;
      }

      public void setEmail(String email) {
        this.email = email;
      }

      public String getPassword() {
        return password;
      }

      public void setPassword(String password) {
        this.password = password;
      }
    }

    class Response {
      private String message;
      private boolean success;

      public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
      }

      public String getMessage() {
        return message;
      }

      public boolean isSuccess() {
        return success;
      }
    }
}
