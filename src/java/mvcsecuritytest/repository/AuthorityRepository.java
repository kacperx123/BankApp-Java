package mvcsecuritytest.repository;

import mvcsecuritytest.entity.Authority;
import mvcsecuritytest.entity.embedded.AuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorityRepository extends JpaRepository<Authority, AuthorityId> {

}
