package pl.kk.services.mdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kk.services.mdm.model.domain.Bookmaker;

public interface BookmakerRepository extends JpaRepository<Bookmaker, Long> {

}
