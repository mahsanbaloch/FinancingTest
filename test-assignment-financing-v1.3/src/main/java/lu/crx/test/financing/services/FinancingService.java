package lu.crx.test.financing.services;

import lombok.extern.slf4j.Slf4j;
import lu.crx.test.financing.entities.Creditor;
import lu.crx.test.financing.entities.FinancingResult;
import lu.crx.test.financing.entities.Invoice;
import lu.crx.test.financing.entities.Purchaser;
import lu.crx.test.financing.model.PurchaserModel;
import lu.crx.test.financing.repository.FinancingResultRepository;
import lu.crx.test.financing.repository.InvoiceRepository;
import lu.crx.test.financing.repository.PurchaserRepository;
import lu.crx.test.financing.utils.InvoiceStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

@Slf4j
@Service
public class FinancingService {

    private final InvoiceRepository invoiceRepository;
    private final PurchaserRepository purchaserRepository;
    private final FinancingResultRepository financingResultRepository;

    private static final int BPS_SCALE = 3;

    public FinancingService(InvoiceRepository invoiceRepository,
                            PurchaserRepository purchaserRepository,
                            FinancingResultRepository financingResultRepository) {
        this.invoiceRepository = invoiceRepository;
        this.purchaserRepository = purchaserRepository;
        this.financingResultRepository = financingResultRepository;
    }

    @Transactional
    public void finance() {
        log.info("Financing started");
        runFinancingAlgorithm();
        log.info("Financing completed");
    }

    public void runFinancingAlgorithm() {
        List<Invoice> nonFinancedInvoices =
                invoiceRepository.findByStatus(InvoiceStatus.READY.name());
        nonFinancedInvoices.forEach(this::processInvoice);
    }

    @Transactional
    protected void processInvoice(Invoice invoice) {
        log.info("Processing Invoice: ", invoice.getId());
        try {
            updateInvoiceStatus(invoice, InvoiceStatus.PROCESSING.name());
            Creditor creditor = invoice.getCreditor();
            int financingTerm = calculateFinancingTerm(invoice.getMaturityDate());

            PurchaserModel bestPurchaser = getPurchasersEligibleForFinancing(creditor, financingTerm);
            if (bestPurchaser != null) {
                FinancingResult financingResult =
                        calculateAndPersistFinancingResults(invoice, bestPurchaser, creditor);

                saveFinancingResult(financingResult);
                updateInvoiceStatus(invoice, InvoiceStatus.PAID.name());
            } else {
                updateInvoiceStatus(invoice, InvoiceStatus.READY.name());
            }
            log.info("Invoice Processed Successfully: {}", invoice.getId());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Invoice Processing Failed: {}", invoice.getId());
            updateInvoiceStatus(invoice, InvoiceStatus.FAILED.name());
        }

        log.info("Invoice Status: {}", invoice.getStatus());
    }

    private void saveFinancingResult(FinancingResult financingResult) {
        financingResultRepository.save(financingResult);
    }

    private void updateInvoiceStatus(Invoice invoice, String invoiceStatus) {
        invoice.setStatus(invoiceStatus);
        invoiceRepository.save(invoice);
    }

    PurchaserModel getPurchasersEligibleForFinancing(Creditor creditor, int financingTerm) {

        List<Purchaser> validPurchaserBasedOnFinancingTerm =
                purchaserRepository.findByMinimumFinancingTermInDaysLessThanEqual(financingTerm);

        Set<PurchaserModel> validPurchasersBasedOnCreditor =
                getValidPurchasersWhereCreditorExist(creditor, validPurchaserBasedOnFinancingTerm);


        PriorityQueue<PurchaserModel> validPurchasersWithAFR =
                getPurchaserModelsBasedOnAFR(financingTerm, validPurchasersBasedOnCreditor);

        return validPurchasersWithAFR.peek();
    }

    private PriorityQueue<PurchaserModel> getPurchaserModelsBasedOnAFR(int financingTerm, Set<PurchaserModel> validPurchasersBasedOnCreditor) {
        PriorityQueue<PurchaserModel> validPurchasersWithAFR = new PriorityQueue<>(
                Comparator.comparing(PurchaserModel::getActualFinancingRate)
        );

        validPurchasersBasedOnCreditor.forEach(purchaser -> {
            purchaser.setFinancingTermInDays(financingTerm);
            BigDecimal actualFinancingRate = calculateActualFinancingRate(purchaser);
            purchaser.setActualFinancingRate(actualFinancingRate);
            BigDecimal maxFinancingRateInBpsOfCreditor = purchaser.getMaxFinancingRateInBpsOfCreditor();
            if (actualFinancingRate.compareTo(maxFinancingRateInBpsOfCreditor) <= 0) {
                validPurchasersWithAFR.add(purchaser);
            }
        });
        return validPurchasersWithAFR;
    }

    private BigDecimal calculateActualFinancingRate(PurchaserModel purchaserModel) {
        BigDecimal mft = BigDecimal.valueOf(purchaserModel.getFinancingTermInDays());
        BigDecimal actualFinancingRate = (purchaserModel.getAnnualRateInBps().multiply(mft))
                .divide(BigDecimal.valueOf(360), BPS_SCALE, RoundingMode.HALF_EVEN);
        return actualFinancingRate;
    }

    private Set<PurchaserModel> getValidPurchasersWhereCreditorExist(Creditor creditor, List<Purchaser> purchasers) {
        Set<PurchaserModel> validPurchasers = new HashSet<>();
        for (Purchaser purchaser : purchasers) {
            purchaser.getPurchaserFinancingSettings().stream()
                    .filter(pfs -> pfs.getCreditor().equals(creditor))
                    .findFirst()
                    .ifPresent(pfs -> validPurchasers.add(PurchaserModel.builder()
                            .purchaser(purchaser)
                            .annualRateInBps(pfs.getAnnualRateInBps())
                            .maxFinancingRateInBpsOfCreditor(creditor.getMaxFinancingRateInBps())
                            .build()));
        }
        return validPurchasers;
    }

    FinancingResult calculateAndPersistFinancingResults(Invoice invoice, PurchaserModel purchaserModel, Creditor creditor) {
        BigDecimal financingRate = purchaserModel.getActualFinancingRate();
        BigDecimal purchaserInterest = getPurchaserInterest(invoice, financingRate);
        BigDecimal earlyPaymentAmount = calculateEarlyPaymentAmount(invoice.getValueInCents(), purchaserInterest);

        FinancingResult financingResult = FinancingResult.builder()
                .purchaser(purchaserModel.getPurchaser())
                .creditor(creditor)
                .financingTermInDays(purchaserModel.getFinancingTermInDays())
                .actualFinancingRate(financingRate)
                .earlyPaymentAmount(earlyPaymentAmount)
                .build();

        return financingResult;
    }

    private static BigDecimal getPurchaserInterest(Invoice invoice, BigDecimal financingRate) {
        return financingRate.multiply(BigDecimal.valueOf(0.0001)).multiply(BigDecimal.valueOf(invoice.getValueInCents()));
    }

    private int calculateFinancingTerm(LocalDate maturityDate) {
        LocalDate currentDate = LocalDate.now();
        return (int) (maturityDate.toEpochDay() - currentDate.toEpochDay());
    }

    private BigDecimal calculateEarlyPaymentAmount(long invoiceAmount, BigDecimal purchaserInterest) {
        return BigDecimal.valueOf(invoiceAmount).subtract(purchaserInterest);
    }
}
