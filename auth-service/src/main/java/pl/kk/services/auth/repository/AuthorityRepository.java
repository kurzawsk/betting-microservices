package pl.kk.services.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kk.services.auth.domain.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, String> {

}
