package mvcsecuritytest.controllers;

import mvcsecuritytest.entity.Account;
import mvcsecuritytest.entity.Transaction;
import mvcsecuritytest.repository.AccountRepository;
import mvcsecuritytest.repository.TransactionRepository;
import mvcsecuritytest.repository.UserRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class TransferController {

    AccountRepository accountRepository;
    UserRepository userRepository;
    TransactionRepository transactionRepository;
    UserAccountSession userAccountSession;

    AccountService accountService;

    @Autowired
    public TransferController(AccountRepository accountRepository, UserRepository userRepository, UserAccountSession userAccountSession, AccountService accountService, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.userAccountSession = userAccountSession;
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/showTransferForm")
    public ModelAndView showTransferForm() {
        ModelAndView modelAndView = new ModelAndView("transferForm");
        String userAccountNr = userAccountSession.getCurrentAccountNr();
        modelAndView.addObject("userAccountNr", userAccountNr);

        Account account = accountRepository.findByAccountNr(userAccountNr);
        List<Transaction> fromTransactions = transactionRepository.findByFromAccount(account);
        List<Transaction> toTransactions = transactionRepository.findByToAccount(account);

        List<Transaction> accountTransactions = new ArrayList<>();
        accountTransactions.addAll(fromTransactions);
        accountTransactions.addAll(toTransactions);

        accountTransactions.sort(Comparator.comparing(Transaction::getDate));

        if (accountTransactions.isEmpty()) {
            modelAndView.addObject("notification", "This account has no transactions");
        } else {
            modelAndView.addObject("transactions", accountTransactions);
        }
        return modelAndView;
    }

    @Transactional
    @PostMapping("/transferBalance")
    public ModelAndView transferBalance(@RequestParam String accountNr,
                                        @RequestParam BigDecimal balance) {
        ModelAndView modelAndView = new ModelAndView("transferForm");
        String userAccountNr = userAccountSession.getCurrentAccountNr();

        if (userAccountNr.equals(accountNr)) {
            modelAndView.addObject("error", "Cannot transfer to the same account.");
            return modelAndView;
        }

        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            modelAndView.addObject("error", "Transfer amount must be positive.");
            return modelAndView;
        }

        Account userAccount = accountRepository.findByAccountNr(userAccountNr); // current user

        if (userAccount == null || userAccount.getBalance().compareTo(balance) < 0) {
            modelAndView.addObject("error", "You don't have enough money!");
            return modelAndView;
        }

        Account account = accountRepository.findByAccountNr(accountNr);

        if (account == null) {
            modelAndView.addObject("error", "Account with this account number does not exist");
            return modelAndView;
        }
        BigDecimal amount = accountService.convertCurrency(userAccount.getCurrency(), account.getCurrency(), balance);

        if (amount.compareTo(BigDecimal.valueOf(999999999)) > 0) {
            modelAndView.addObject("error", "Capacity exceeded on the target account");
            return modelAndView;
        }

        account.setBalance(account.getBalance().add(amount));
        userAccount.setBalance(userAccount.getBalance().subtract(balance));

        accountRepository.save(account);
        accountRepository.save(userAccount);


        Transaction from = new Transaction(userAccount, account, balance, userAccount.getCurrency(), LocalDateTime.now());
        transactionRepository.save(from);

        modelAndView.addObject("success", "Transfer is successful");
        return modelAndView;
    }

}
