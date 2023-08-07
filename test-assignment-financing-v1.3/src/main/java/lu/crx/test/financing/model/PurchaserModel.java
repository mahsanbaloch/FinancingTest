package lu.crx.test.financing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lu.crx.test.financing.entities.Purchaser;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@EqualsAndHashCode
public class PurchaserModel {

    private Purchaser purchaser;

    private BigDecimal annualRateInBps;

    private BigDecimal actualFinancingRate;

    private BigDecimal maxFinancingRateInBpsOfCreditor;

    private int financingTermInDays;
}
