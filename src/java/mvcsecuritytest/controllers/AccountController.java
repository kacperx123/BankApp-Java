package mvcsecuritytest.controllers;

import mvcsecuritytest.entity.Account;
import mvcsecuritytest.entity.UserEntity;
import mvcsecuritytest.repository.AccountRepository;
import mvcsecuritytest.repository.UserRepository;
import mvcsecuritytest.service.AccountService;
import mvcsecuritytest.session.UserAccountSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
public class AccountController {

    AccountRepository accountRepository;
    UserRepository userRepository;
    UserAccountSession userAccountSession;
    AccountService accountService;

    @Autowired
    public AccountController(AccountRepository accountRepository, UserRepository userRepository, UserAccountSession userAccountSession, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.userAccountSession = userAccountSession;
        this.accountService = accountService;
    }

    @GetMapping("/")
    public ModelAndView showUserAccounts(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("list-accounts");
        String username = principal.getName();
        List<Account> accounts = accountRepository.findByUser_Username(username);
        if (accounts.isEmpty()) {
            modelAndView.addObject("error", "No accounts have been found!");
            return modelAndView;
        }
        modelAndView.addObject("accounts", accounts);
        return modelAndView;
    }

    @PostMapping("/loginAccount")
    public ModelAndView loginToAccount(@RequestParam String pin,
                                       @RequestParam String accountNr,
                                       Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        String username = principal.getName();
        Optional<Account> accountOpt = accountRepository.findByAccountNrAndUser_Username(accountNr, username);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getPin().equals(pin)) {
                modelAndView.addObject("account", account);
                userAccountSession.setCurrentAccountNr(account.getAccountNr());
                modelAndView.setViewName("redirect:/showAccountDetails");
                return modelAndView;
            } else {

                modelAndView.addObject("error", "Invalid PIN. Please try again.");
                return modelAndView;
            }
        } else {
            modelAndView.addObject("error", "Account not found or access denied.");
            return modelAndView;
        }

    }

    @GetMapping("/showRegisterAccount")
    public ModelAndView showRegisterAccount() {
        ModelAndView modelAndView = new ModelAndView("/registerAccount");
        return modelAndView;
    }

    @Transactional
    @PostMapping("/registerTheAccount")
    public ModelAndView RegisterAccount(Principal principal,
                                        @RequestParam String pin) {
        ModelAndView modelAndView = new ModelAndView("/registerAccount");
        if (pin == null || !Pattern.matches("\\d{4}", pin)) {
            modelAndView.addObject("error", "PIN must be 4 digits long.");
            return modelAndView;
        }


        String username = principal.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        String accountNr = accountService.generateUniqueAccountNumber();

        BigDecimal balance = new BigDecimal(0);
        String currency = "EUR";

        Account account = new Account(accountNr, pin, balance, currency, user);
        List<Account> accounts = user.getAccounts();
        accounts.add(account);
        user.setAccounts(accounts);

        userRepository.save(user);
        modelAndView.addObject("success", "Your account have been registered!");

        return modelAndView;
    }


}



