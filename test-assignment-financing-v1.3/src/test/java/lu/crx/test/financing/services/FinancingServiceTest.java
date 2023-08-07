package lu.crx.test.financing.services;

import lu.crx.test.financing.entities.Creditor;
import lu.crx.test.financing.entities.FinancingResult;
import lu.crx.test.financing.entities.Invoice;
import lu.crx.test.financing.entities.Purchaser;
import lu.crx.test.financing.model.PurchaserModel;
import lu.crx.test.financing.repository.FinancingResultRepository;
import lu.crx.test.financing.repository.InvoiceRepository;
import lu.crx.test.financing.repository.PurchaserRepository;
import lu.crx.test.financing.utils.InvoiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FinancingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PurchaserRepository purchaserRepository;

    @Mock
    private FinancingResultRepository financingResultRepository;

    @InjectMocks
    private FinancingService financingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRunFinancingAlgorithm_NoInvoices() {

    }

    @Test
    void testRunFinancingAlgorithm_ProcessInvoices() {

    }

    @Test
    void testProcessInvoice_Successful() {

    }

    @Test
    void testProcessInvoice_NoValidPurchasers() {

    }


}
