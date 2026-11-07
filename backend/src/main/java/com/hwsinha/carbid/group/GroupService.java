package com.hwsinha.carbid.group;

import com.hwsinha.carbid.bid.Bid;
import com.hwsinha.carbid.bid.BidRepository;
import com.hwsinha.carbid.car.Car;
import com.hwsinha.carbid.car.CarRepository;
import com.hwsinha.carbid.common.PageResponse;
import com.hwsinha.carbid.group.dto.CreateGroupRequest;
import com.hwsinha.carbid.group.dto.GroupDetailResponse;
import com.hwsinha.carbid.group.dto.GroupResponse;
import com.hwsinha.carbid.group.dto.GroupSummaryResponse;
import com.hwsinha.carbid.user.User;
import com.hwsinha.carbid.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final BuyGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    @Transactional
    public GroupResponse createGroup(Long userId,
                                     CreateGroupRequest request) {
        Car car= carRepository.findById(request.carId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        User creator= userRepository.findById(userId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        BuyGroup group=new BuyGroup();
        group.setCar(car);
        group.setCity(request.city());
        group.setMinMembers(request.minMembers());
        group.setDeadline(request.deadline());
        group.setCreatedBy(creator);
        group.setStatus(GroupStatus.OPEN);

        BuyGroup savedGroup=groupRepository.save(group);

        GroupMember member=new GroupMember();
        member.setGroup(savedGroup);
        member.setUser(creator);
        memberRepository.save(member);

        return toResponse(savedGroup);
    }

    @Transactional
    public GroupResponse joinGroup(Long groupId, Long userId) {
        BuyGroup group=groupRepository.findById(groupId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        User user=userRepository.findById(userId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (group.getStatus()!= GroupStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Group is not open");
        }
        if(group.getDeadline().isBefore(Instant.now())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Deadline is passed");
        }
        if(memberRepository.existsByGroupIdAndUserId(groupId,userId)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "already a member");
        }
        GroupMember member=new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        memberRepository.saveAndFlush(member);
        if (memberRepository.countByGroupId(groupId)>= group.getMinMembers()){
            group.setStatus(GroupStatus.BIDDING);
            group.setBiddingDeadline(Instant.now().plus(Duration.ofHours(48)));
        }
        return toResponse(group);
    }

    @Transactional(readOnly = true)
    public PageResponse<GroupSummaryResponse> listGroups(String city, GroupStatus status, Pageable pageable) {
        return toSummaryPage(groupRepository.findByCityIgnoreCaseAndStatus(city, status, pageable));
    }

    @Transactional(readOnly = true)
    public PageResponse<GroupSummaryResponse> listMyGroups(Long userId, Pageable pageable) {
        return toSummaryPage(groupRepository.findJoinedByUserId(userId, pageable));
    }

    @Transactional(readOnly = true)
    public GroupDetailResponse getGroup(Long id) {
        BuyGroup group=groupRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        Car car=group.getCar();
        long memberCount=memberRepository.countByGroupId(group.getId());

        Optional<Bid> lowest=bidRepository.findTopByGroupIdOrderByPriceAsc(id);
        BigDecimal winningPrice=null;
        BigDecimal lowestPrice=null;
        String showRoom=null;
        Bid winner=group.getWinningBid();
        if (lowest.isPresent()) {
            lowestPrice=lowest.get().getPrice();
            if(group.getStatus()==GroupStatus.LOCKED && winner!=null){
                winningPrice=winner.getPrice();
                showRoom=winner.getDealer().getShowroomName();
            }
        }
        return new GroupDetailResponse(id, car.getBrand(), car.getModel(), car.getVariant(),
                car.getExShowroomPrice(),group.getCity(), group.getStatus(), group.getMinMembers(), memberCount,
                group.getDeadline(), group.getBiddingDeadline(), lowestPrice, winningPrice, showRoom);
    }

    private GroupSummaryResponse toSummary(BuyGroup group, long memberCount) {
        Car car= group.getCar();
        return new GroupSummaryResponse(group.getId(), car.getBrand(), car.getModel(),
                car.getVariant(), group.getCity(), group.getStatus(), group.getMinMembers(),
                memberCount, group.getDeadline(), group.getBiddingDeadline());
    }

    private GroupResponse toResponse(BuyGroup group) {
        return  new GroupResponse(
                group.getId(),
                group.getCar().getId(),
                group.getCity(),
                group.getStatus(),
                group.getMinMembers(),
                group.getDeadline()
        );
    }
    private PageResponse<GroupSummaryResponse> toSummaryPage(Page<BuyGroup> page) {
        List<Long> ids= page.getContent().stream().map(BuyGroup::getId).toList();
        Map<Long,Long> counts=ids.isEmpty()
                ? Map.of()
                : memberRepository.countByGroupIds(ids).stream()
                .collect(Collectors.toMap(GroupMemberCount::getGroupId, GroupMemberCount::getCnt));
        List<GroupSummaryResponse> content= page.getContent().stream()
                .map(g-> toSummary(g, counts.getOrDefault(g.getId(), 0L)))
                .toList();

        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
