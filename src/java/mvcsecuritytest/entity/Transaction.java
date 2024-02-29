package mvcsecuritytest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction_history")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_account", referencedColumnName = "account_nr")
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account", referencedColumnName = "account_nr")
    private Account toAccount;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    public Transaction(Account fromAccount, Account toAccount, BigDecimal amount, String currency, LocalDateTime date) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
    }
}
