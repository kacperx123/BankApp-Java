package mvcsecuritytest.controllers;

import mvcsecuritytest.API.CurrencyService;
import mvcsecuritytest.entity.Account;
import mvcsecuritytest.repository.AccountRepository;
import mvcsecuritytest.service.AccountService;
import mvcsecuritytest.session.UserAccountSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Set;

@Controller
public class ConvertCurrencyController {

    AccountRepository accountRepository;
    UserAccountSession userAccountSession;
    CurrencyService currencyService;
    AccountService accountService;

    public ConvertCurrencyController(AccountRepository accountRepository, UserAccountSession userAccountSession, CurrencyService currencyService, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.userAccountSession = userAccountSession;
        this.currencyService = currencyService;
        this.accountService = accountService;
    }

    @GetMapping("/showConvertCurrencyForm")
    public ModelAndView showConvertCurrency() {
        ModelAndView modelAndView = new ModelAndView("convertCurrencyForm");
        Set<String> currencies = currencyService.getCurrencies();
        modelAndView.addObject("availableCurrencies", currencies);
        modelAndView.addObject("currencyRates", currencyService.getCurrentRates());
        return modelAndView;
    }

    @Transactional
    @PostMapping("/convertCurrency")
    public ModelAndView convertCurrency(@RequestParam String currency) {
        ModelAndView modelAndView = new ModelAndView("convertCurrencyForm");
        String accountNr = userAccountSession.getCurrentAccountNr();
        Account account = accountRepository.findByAccountNr(accountNr);

        BigDecimal amount = currencyService.convertCurrency(account.getCurrency(), currency, account.getBalance());

        account.setBalance(amount);
        account.setCurrency(currency);

        accountRepository.save(account);
        modelAndView.addObject("success", "you have converted your currency!");

        return modelAndView;
    }


}
