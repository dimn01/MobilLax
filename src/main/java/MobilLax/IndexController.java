package MobilLax;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/register")
@CrossOrigin(origins = "*") // Allow CORS
public class IndexController {

  @PostMapping
  public ResponseEntity<Response> register(@RequestBody User user) {
    if (user.getUsername().isEmpty() || user.getEmail().isEmpty() ||
            user.getPassword().isEmpty() || user.getConfirmPassword().isEmpty()) {
      return ResponseEntity.badRequest().body(new Response("Please fill in all fields.", false));
    }

    if (!user.getPassword().equals(user.getConfirmPassword())) {
      return ResponseEntity.badRequest().body(new Response("Passwords do not match.", false));
    }
    System.out.println(user);
    return ResponseEntity.ok(new Response("Registration successful!", true));
  }

  static class User {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String toString() {
      return username + " " + email + " " + password;
    }
  }

  static class Response {
    private String message;
    private boolean success;

    public Response(String message, boolean success) {
      this.message = message;
      this.success = success;
    }

    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
  }
}
