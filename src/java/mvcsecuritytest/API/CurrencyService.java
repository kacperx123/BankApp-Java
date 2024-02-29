package mvcsecuritytest.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class CurrencyService {

    private final Map<String, Double> rates = new HashMap<>();
    private final RestTemplate restTemplate;

    private String accessKey = "";

    @Autowired
    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        refreshRates();
    }

    public Set<String> getCurrencies() {
        Set<String> currencies = rates.keySet();
        return currencies;
    }

    public Map<String, Double> getCurrentRates() {
        return rates;
    }

    @Scheduled(fixedRate = 3600000) // Refresh every hour
    public void refreshRates() {
        String url = "http://api.exchangeratesapi.io/v1/latest?access_key=" + accessKey;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response != null && response.containsKey("rates")) {
            Map<String, Number> apiRates = (Map<String, Number>) response.get("rates");
            rates.clear();
            apiRates.forEach((key, value) -> rates.put(key, value.doubleValue()));
        }
    }


    public BigDecimal convertCurrency(String fromCurrency, String toCurrency, BigDecimal amount) {
        Double fromRate = rates.get(fromCurrency);
        Double base = rates.get("EUR");
        Double toRate = rates.get(toCurrency);

        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Currency conversion rate not found for " + fromCurrency + " or " + toCurrency);
        }

        BigDecimal amountInBase = amount.multiply(BigDecimal.valueOf(base)).setScale(2, RoundingMode.HALF_UP); // converting to base (Euro)
        return amountInBase.multiply(BigDecimal.valueOf(toRate)).setScale(2, RoundingMode.HALF_UP);
    }
}

