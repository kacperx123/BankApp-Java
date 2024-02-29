package mvcsecuritytest.controllers;

import mvcsecuritytest.entity.Account;
import mvcsecuritytest.repository.AccountRepository;
import mvcsecuritytest.service.AccountService;
import mvcsecuritytest.session.UserAccountSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;

@Controller
public class DepositController {

    AccountRepository accountRepository;
    UserAccountSession userAccountSession;

    AccountService accountService;

    @Autowired
    public DepositController(AccountRepository accountRepository, UserAccountSession userAccountSession, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.userAccountSession = userAccountSession;
        this.accountService = accountService;
    }

    @GetMapping("/showDepositForm")
    public ModelAndView showDepositForm() {
        ModelAndView modelAndView = new ModelAndView("depositForm");
        modelAndView.addObject("userAccountNr", userAccountSession.getCurrentAccountNr());
        return modelAndView;
    }

    @Transactional
    @PostMapping("/depositBalance")
    public ModelAndView depositBalance(@RequestParam BigDecimal balance) {
        ModelAndView modelAndView = new ModelAndView("depositForm");

        Account account = accountRepository.findByAccountNr(userAccountSession.getCurrentAccountNr());

        if (balance.compareTo(BigDecimal.valueOf(0)) < 0 || balance.compareTo(BigDecimal.valueOf(999999999)) > 0) {
            modelAndView.addObject("error", "the input value is incorrect!");
            return modelAndView;
        }
        BigDecimal currentBalance = account.getBalance();

        if (account.getBalance().add(balance).compareTo(BigDecimal.valueOf(999999999)) > 0) {
            modelAndView.addObject("error", "your account cant hold that much money");
            return modelAndView;
        }

        account.setBalance(currentBalance.add(balance));

        modelAndView.addObject("success", "Your deposit is successful");


        return modelAndView;
    }
}
