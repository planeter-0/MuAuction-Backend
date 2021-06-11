package icu.planeter.muauction.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Planeter
 * @description: SellRecord
 * @date 2021/6/11 20:56
 * @status dev
 */
@Data
@NoArgsConstructor
@Entity
public class SellRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private Long buyerId;
    // Does the buyer receive the item
    // Transfer funds after receiving goods
    private boolean isReceived;

    @OneToOne
    @JoinColumn(name = "bid_id", referencedColumnName = "id")
    private Bid bid;

    public SellRecord(Long sellerId, Long buyerId, boolean isReceived) {
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.isReceived = isReceived;
    }
}
