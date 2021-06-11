package icu.planeter.muauction.service;

import icu.planeter.muauction.entity.Bid;

import java.util.List;

public interface BidService {
    /**
     * Check whether the unfrozen balance is enough to pay the price
     * @param price price
     * @param itemId item id
     * @param address address
     * @param comment comment
     * @return status of bid 0 -->Item have been sold, 2 --> BalanceNotEnough, 1 --> Success
     */
    int bid(double price, Long itemId, String address, String comment);

    /**
     * Get current user's bids
     * @return List<Bid>
     */
    List<Bid> getMine();

    /**
     * Get bids of an item
     * @param itemId item id
     * @return List<Bid>
     */
    List<Bid> getByItemId(Long itemId);

    /**
     * Set field "active" to false.
     * Tyt to unfreeze a bids' funds
     * if they haven't inactivate before,namely cancel.
     * @param bidId item id
     * @return successful or not
     */
    boolean cancel(Long bidId);
//    /**
//     * Tyt to unfreeze other bids' funds if they haven't inactivate before,namely cancel.
//     * @return
//     */
//    void InactivateBids(List<Long> bidIds);
}
