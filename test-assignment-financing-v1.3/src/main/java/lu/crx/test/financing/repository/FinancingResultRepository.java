package lu.crx.test.financing.repository;

import lu.crx.test.financing.entities.FinancingResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancingResultRepository extends JpaRepository<FinancingResult, Long> {
}
