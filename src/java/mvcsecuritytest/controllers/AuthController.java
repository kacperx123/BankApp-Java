package mvcsecuritytest.controllers;

import mvcsecuritytest.entity.Authority;
import mvcsecuritytest.entity.UserData;
import mvcsecuritytest.entity.UserEntity;
import mvcsecuritytest.entity.embedded.AuthorityId;
import mvcsecuritytest.repository.AuthorityRepository;
import mvcsecuritytest.repository.UserDataRepository;
import mvcsecuritytest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashSet;
import java.util.Set;


@Controller
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private UserDataRepository userDataRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, AuthorityRepository authorityRepository, UserDataRepository userDataRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.userDataRepository = userDataRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/showMyLoginPage")
    public String showLoginPage() {
        return "fancy-login";
    }

    @PostMapping("/authenticateTheUser")
    public ModelAndView authenticateUser(@RequestParam("username") String username,
                                         @RequestParam("password") String password) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new ModelAndView("redirect:/list-accounts"); // Redirect after successful login
        } catch (AuthenticationException e) {
            ModelAndView modelAndView = new ModelAndView("fancy-login");
            modelAndView.addObject("error", "Invalid username or password");
            return modelAndView; // Return to login page with error message
        }
    }

    @GetMapping("/showMyRegisterPage")
    public String showRegisterPage() {
        return "register";
    }

    @Transactional
    @PostMapping("/registerTheUser")
    public ModelAndView registerUser(@RequestParam("username") String username,
                                     @RequestParam("password") String password,
                                     @RequestParam("firstname") String firstname,
                                     @RequestParam("lastname") String lastname,
                                     @RequestParam("email") String email) {
        try {
            if (userRepository.existsByUsername(username)) {
                ModelAndView modelAndView = new ModelAndView("register");
                modelAndView.addObject("error", "username already exists");
                return modelAndView;
            }

            if (userDataRepository.existsByEmail(email)) {
                ModelAndView modelAndView = new ModelAndView("register");
                modelAndView.addObject("error", "email already exists");
                return modelAndView;
            }

            if (password.length() > 30 || password.length() < 3
                    || firstname.length() > 30 || firstname.length() < 3
                    || lastname.length() > 30 || lastname.length() < 3
                    || email.length() > 30 || email.length() < 3
                    || username.length() > 30 || username.length() < 3) {
                ModelAndView modelAndView = new ModelAndView("register");
                modelAndView.addObject("error", "Minimum number of characters in each field is 3 and maximum is 30!");
                return modelAndView;
            }

            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(true);

            AuthorityId authorityId = new AuthorityId(username, "ROLE_USER");


            Authority authority = new Authority(authorityId, user);

            Set<Authority> authoritySet = new LinkedHashSet<>();
            authoritySet.add(authority);
            user.setAuthorities(authoritySet);

            UserData userData = new UserData(username, firstname, lastname, email, user);

            user.setUserData(userData);

            userRepository.save(user);

            return new ModelAndView("redirect:/fancy-login");
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("error", "An error occurred during registration. Please try again.");
            return modelAndView;
        }
    }
}





