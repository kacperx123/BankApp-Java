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
public class WithdrawController {

    AccountRepository accountRepository;
    UserAccountSession userAccountSession;

    AccountService accountService;

    @Autowired
    public WithdrawController(AccountRepository accountRepository, UserAccountSession userAccountSession, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.userAccountSession = userAccountSession;
        this.accountService = accountService;
    }

    @GetMapping("/showWithdrawForm")
    public ModelAndView showDepositForm() {
        ModelAndView modelAndView = new ModelAndView("withdrawForm");
        modelAndView.addObject("userAccountNr", userAccountSession.getCurrentAccountNr());
        return modelAndView;
    }

    @Transactional
    @PostMapping("/withdrawBalance")
    public ModelAndView depositBalance(@RequestParam BigDecimal balance) {
        ModelAndView modelAndView = new ModelAndView("withdrawForm");

        Account account = accountRepository.findByAccountNr(userAccountSession.getCurrentAccountNr());

        if (balance.compareTo(BigDecimal.valueOf(0)) < 0 || balance.compareTo(BigDecimal.valueOf(99999999)) > 0) {
            modelAndView.addObject("error", "the input value is incorrect! (0 - 99kk)");
            return modelAndView;
        }
        BigDecimal currentBalance = account.getBalance();

        if (account.getBalance().subtract(balance).compareTo(BigDecimal.valueOf(0)) < 0) {
            modelAndView.addObject("error", "your account does not hold that much money");
            return modelAndView;
        }

        account.setBalance(currentBalance.subtract(balance));
        accountRepository.save(account);
        modelAndView.addObject("success", "Your withdrawal is successful");


        return modelAndView;
    }
}
