package mvcsecuritytest.repository;

import mvcsecuritytest.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Account findByAccountNr(String accountNr);

    List<Account> findByUser_Username(String username);

    Optional<Account> findByAccountNrAndUser_Username(String accountNr, String username);

    boolean existsByAccountNr(String accountNr);
}
