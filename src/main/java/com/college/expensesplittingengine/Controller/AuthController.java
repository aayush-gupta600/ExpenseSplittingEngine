package com.college.expensesplittingengine.Controller;
import com.college.expensesplittingengine.Auth.Imple.SignupRequest;
import com.college.expensesplittingengine.Auth.Imple.UserDetailsImple;
import com.college.expensesplittingengine.Auth.JwtUtils;
import com.college.expensesplittingengine.Auth.LoginRequest;
import com.college.expensesplittingengine.Auth.LoginResponse;
import com.college.expensesplittingengine.Auth.MessageResponse;
import com.college.expensesplittingengine.Models.User;
import com.college.expensesplittingengine.Repo.UserRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {

        if (userRepo.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username already exists"));
        }
        if (userRepo.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Email already exists"));
        }
        User user = new User();

        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        userRepo.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        UserDetailsImple userDetails =
                (UserDetailsImple) authentication.getPrincipal();

        ResponseCookie cookie = jwtUtils.generateJwtCookie(userDetails);

        LoginResponse response =
                new LoginResponse(userDetails.getUsername(), null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {

        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Logged out successfully"));
    }
}