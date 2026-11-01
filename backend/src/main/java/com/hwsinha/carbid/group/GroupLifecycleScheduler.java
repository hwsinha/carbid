package com.hwsinha.carbid.group;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupLifecycleScheduler {

    private final BuyGroupRepository groupRepository;
    private final GroupLifecycleService lifecycleService;

    @Scheduled(fixedDelay = 60_000)
    public void closeExpiredGroups() {
        Instant now = Instant.now();
        List<BuyGroup> due = new ArrayList<>();
        due.addAll(groupRepository.findByStatusAndDeadlineBefore(GroupStatus.OPEN, now));
        due.addAll(groupRepository.findByStatusAndBiddingDeadlineBefore(GroupStatus.BIDDING, now));

        for (BuyGroup group : due) {
            try {
                lifecycleService.closeGroup(group.getId());
            } catch (Exception e) {
                log.error("Failed to close group {}", group.getId(), e);
            }
        }
    }
}