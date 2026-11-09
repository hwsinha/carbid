package com.hwsinha.carbid.group;

import com.hwsinha.carbid.bid.Bid;
import com.hwsinha.carbid.bid.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupLifecycleService {

    private final BuyGroupRepository buyGroupRepository;
    private final BidRepository bidRepository;

    @Transactional
    public void closeGroup(Long id) {
        BuyGroup group = buyGroupRepository.findByIdForUpdate(id).orElse(null);
        if (group == null) {
            return;
        }
        Instant now = Instant.now();

        if (group.getStatus() == GroupStatus.OPEN
                && group.getDeadline().isBefore(now)) {
            group.setStatus(GroupStatus.EXPIRED);
            log.info("Group {} expired: join deadline passed before reaching minimum members", id);

        } else if (group.getStatus() == GroupStatus.BIDDING
                && group.getBiddingDeadline() != null
                && group.getBiddingDeadline().isBefore(now)) {

            Optional<Bid> lowest = bidRepository.findTopByGroupIdOrderByPriceAsc(id);
            if (lowest.isPresent()) {
                group.setWinningBid(lowest.get());
                group.setStatus(GroupStatus.LOCKED);
                log.info("Group {} locked, winning bid {} at price {}",
                        id, lowest.get().getId(), lowest.get().getPrice());
            } else {
                group.setStatus(GroupStatus.EXPIRED);
                log.info("Group {} expired: bidding ended with no bids", id);
            }
        }
    }
}