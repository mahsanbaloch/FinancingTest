package lu.crx.test.financing.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A creditor is a company that shipped some goods to the {@link Debtor}, issued an {@link Invoice} for the shipment
 * and is waiting for this invoice to be paid by the debtor.
 */
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Creditor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false)
    private String name;

    /**
     * Maximum acceptable financing rate for this creditor.
     */
    @Basic(optional = false)
    private BigDecimal maxFinancingRateInBps;

}
