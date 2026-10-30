package com.hwsinha.carbid.bid;

import com.hwsinha.carbid.bid.dto.BidResponse;
import com.hwsinha.carbid.bid.dto.PlaceBidRequest;
import com.hwsinha.carbid.dealer.Dealer;
import com.hwsinha.carbid.dealer.DealerRepository;
import com.hwsinha.carbid.group.BuyGroup;
import com.hwsinha.carbid.group.BuyGroupRepository;
import com.hwsinha.carbid.group.GroupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final BuyGroupRepository buyGroupRepository;
    private final DealerRepository dealerRepository;

    @Transactional
    public BidResponse placeBid(Long groupId, PlaceBidRequest request) {
        BuyGroup group = buyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Group not found"));
        Dealer dealer = dealerRepository.findById(request.dealerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dealer not found"));
        if (group.getStatus() != GroupStatus.BIDDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Group is not accepting bids");
        }
        if (group.getBiddingDeadline() == null || group.getBiddingDeadline().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bidding deadline has passed");
        }
        if (!dealer.getCity().equalsIgnoreCase(group.getCity())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Dealer is not in this group's city");
        }
        Optional<Bid> lowest = bidRepository.findTopByGroupIdOrderByPriceAsc(groupId);
        if (lowest.isPresent() && request.price().compareTo(lowest.get().getPrice()) >= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bid must be lower than the current lowest bid");
        }
            Bid bid = new Bid();
            bid.setGroup(group);
            bid.setDealer(dealer);
            bid.setPrice(request.price());
            bid.setNotes(request.notes());

            Bid savedBid = bidRepository.save(bid);
            return toResponse(savedBid);
        }
    private BidResponse toResponse(Bid bid) {
        return new BidResponse(bid.getId(), bid.getGroup().getId(), bid.getDealer().getId(),
                bid.getPrice(), bid.getNotes(), bid.getCreatedAt());
    }
}
