package mvcsecuritytest.service;

import mvcsecuritytest.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class AccountService {

    AccountRepository accountRepository;

    public final Map<String, BigDecimal> rates = new HashMap<>();

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    private void initializeRates() {
        // Assuming EUR as base currency
        rates.put("USD", new BigDecimal("1.08"));
        rates.put("EUR", new BigDecimal("1"));
        rates.put("GBP", new BigDecimal("0.85"));
        rates.put("JPY", new BigDecimal("160.71"));
        rates.put("CHF", new BigDecimal("0.94"));
        rates.put("PLN", new BigDecimal("4.32"));
    }

    public String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 11; i++) {
                int digit = random.nextInt(10);
                sb.append(digit);
            }
            accountNumber = sb.toString();
        } while (accountRepository.existsByAccountNr(accountNumber));

        return accountNumber;
    }

    public BigDecimal convertCurrency(String fromCurrency, String toCurrency, BigDecimal amount) {
        initializeRates();
        BigDecimal fromRate = rates.get(fromCurrency);
        BigDecimal toRate = rates.get(toCurrency);

        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Currency conversion rate not found for " + fromCurrency + " or " + toCurrency);
        }

        BigDecimal amountInBase = amount.divide(fromRate, 4, RoundingMode.HALF_UP);
        return amountInBase.multiply(toRate).setScale(2, RoundingMode.HALF_UP);
    }


}
