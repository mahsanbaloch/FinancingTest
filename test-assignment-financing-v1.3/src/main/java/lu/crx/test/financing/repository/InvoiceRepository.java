package lu.crx.test.financing.repository;

import lu.crx.test.financing.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStatus(String status);
}
