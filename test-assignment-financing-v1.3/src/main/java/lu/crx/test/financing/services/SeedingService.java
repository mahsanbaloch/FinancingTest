package lu.crx.test.financing.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import lu.crx.test.financing.entities.Creditor;
import lu.crx.test.financing.entities.Debtor;
import lu.crx.test.financing.entities.Invoice;
import lu.crx.test.financing.entities.Purchaser;
import lu.crx.test.financing.entities.PurchaserFinancingSettings;
import lu.crx.test.financing.utils.InvoiceStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SeedingService {

    private final EntityManager entityManager;

    private Creditor creditor1;

    private Debtor debtor1;
    private Debtor debtor2;
    private Debtor debtor3;

    private Purchaser purchaser1;
    private Purchaser purchaser2;

    public SeedingService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void seedMasterData() {
        log.info("Seeding master data");

        // creditors
        creditor1 = Creditor.builder()
                .name("Coffee Beans LLC")
                .maxFinancingRateInBps(BigDecimal.valueOf(4))
                .build();
        entityManager.persist(creditor1);


        // debtors
        debtor1 = Debtor.builder()
                .name("Chocolate Factory")
                .build();
        entityManager.persist(debtor1);

        debtor2 = Debtor.builder()
                .name("Sweets Inc")
                .build();
        entityManager.persist(debtor2);

        debtor3 = Debtor.builder()
                .name("ChocoLoco")
                .build();
        entityManager.persist(debtor3);

        // purchasers
        purchaser1 = Purchaser.builder()
                .name("RichBank")
                .minimumFinancingTermInDays(10)
                .purchaserFinancingSetting(PurchaserFinancingSettings.builder()
                        .creditor(creditor1)
                        .annualRateInBps(BigDecimal.valueOf(50))
                        .build())
                .build();
        entityManager.persist(purchaser1);

        purchaser2 = Purchaser.builder()
                .name("FatBank")
                .minimumFinancingTermInDays(12)
                .purchaserFinancingSetting(PurchaserFinancingSettings.builder()
                        .creditor(creditor1)
                        .annualRateInBps(BigDecimal.valueOf(40))
                        .build())
                .build();
        entityManager.persist(purchaser2);

    }

    @Transactional
    public void seedInvoices() {
        log.info("Seeding the invoices");

        entityManager.persist(Invoice.builder()
                .creditor(creditor1)
                .debtor(debtor1)
                .valueInCents(10000)
                .maturityDate(LocalDate.now().plusDays(30))
                .status(InvoiceStatus.READY.name())
                .build());


    }

}
