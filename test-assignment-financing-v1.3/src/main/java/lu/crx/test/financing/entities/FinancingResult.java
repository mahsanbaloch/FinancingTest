package lu.crx.test.financing.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private Creditor creditor;

    @ManyToOne(optional = false)
    private Purchaser purchaser;

    @Basic(optional = false)
    private int financingTermInDays;

    @Basic(optional = false)
    private BigDecimal actualFinancingRate;

    @Basic(optional = false)
    private BigDecimal earlyPaymentAmount;
}
