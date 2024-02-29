package mvcsecuritytest.controllers;

import mvcsecuritytest.entity.Account;
import mvcsecuritytest.entity.UserData;
import mvcsecuritytest.entity.UserEntity;
import mvcsecuritytest.repository.AccountRepository;
import mvcsecuritytest.repository.UserDataRepository;
import mvcsecuritytest.repository.UserRepository;
import mvcsecuritytest.session.UserAccountSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class AccountDetailsController {

    UserAccountSession userAccountSession;
    UserRepository userRepository;
    UserDataRepository userDataRepository;
    AccountRepository accountRepository;

    @Autowired
    public AccountDetailsController(UserAccountSession userAccountSession, UserRepository userRepository, UserDataRepository userDataRepository, AccountRepository accountRepository) {
        this.userAccountSession = userAccountSession;
        this.userRepository = userRepository;
        this.userDataRepository = userDataRepository;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/showAccountDetails")
    public ModelAndView showAccountDetails(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("accountDetails");
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        UserData userData = userDataRepository.findByUsername(username);
        Account account = accountRepository.findByAccountNr(userAccountSession.getCurrentAccountNr());
        modelAndView.addObject("accountNr", account.getAccountNr());
        modelAndView.addObject("username", user.getUsername());
        modelAndView.addObject("balance", account.getBalance());
        modelAndView.addObject("currency", account.getCurrency());
        modelAndView.addObject("firstname", userData.getFirstname());
        modelAndView.addObject("lastname", userData.getLastname());
        modelAndView.addObject("email", userData.getEmail());
        return modelAndView;
    }
}
