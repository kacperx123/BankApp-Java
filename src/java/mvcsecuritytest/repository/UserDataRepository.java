package mvcsecuritytest.repository;

import mvcsecuritytest.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, String> {
    UserData findByUsername(String username);

    boolean existsByEmail(String email);
}
