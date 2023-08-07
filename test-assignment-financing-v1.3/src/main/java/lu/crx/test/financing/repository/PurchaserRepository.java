package lu.crx.test.financing.repository;

import lu.crx.test.financing.entities.Purchaser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaserRepository extends JpaRepository<Purchaser, Long> {

    List<Purchaser> findByMinimumFinancingTermInDaysLessThanEqual(long financingTerm);
}
